package com.pbogdev.data.repository

import co.touchlab.kermit.Logger
import com.pbogdev.core.dispatcherProvider.DispatcherProvider
import com.pbogdev.core.utils.epochMillisToIso
import com.pbogdev.core.utils.safeResult
import com.pbogdev.data.crypto.CryptographyEngine
import com.pbogdev.data.firestore.documentFieldsModels.EncryptedEphemeralMessageFields
import com.pbogdev.data.firestore.wrapperModels.StringValue
import com.pbogdev.data.firestore.wrapperModels.TimestampValue
import com.pbogdev.data.local.database.dao.ActiveConnectionsDao
import com.pbogdev.data.local.database.entities.ActiveConnectionEntity
import com.pbogdev.data.network.ApiService
import com.pbogdev.data.network.dto.EncryptedMessageDTO
import com.pbogdev.data.network.dto.MatchmakingBeaconDTO
import com.pbogdev.data.toActiveConnection
import com.pbogdev.data.toMatchmakingBeacon
import com.pbogdev.data.toMatchmakingBeaconDTO
import com.pbogdev.data.utils.appJson
import com.pbogdev.data.utils.safeDecodeFromString
import com.pbogdev.data.utils.safeEncodeToString
import com.pbogdev.domain.auth.AnonymousAuthenticator
import com.pbogdev.domain.matchmaking.MatchmakingBeacon
import com.pbogdev.domain.models.ActiveConnection
import com.pbogdev.domain.models.ConnectionStatus
import com.pbogdev.domain.models.CustomError
import com.pbogdev.domain.models.CustomResult
import com.pbogdev.domain.models.EphemeralMessage
import com.pbogdev.domain.models.MessagePayloadType
import com.pbogdev.domain.repository.EphemeralMessageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class EphemeralMessageRepositoryImpl(
    private val apiService: ApiService,
    private val cryptoEngine: CryptographyEngine,
    private val dispatcherProvider: DispatcherProvider,
    private val authenticator: AnonymousAuthenticator,
    private val activeConnectionsDao: ActiveConnectionsDao
) : EphemeralMessageRepository {
    override suspend fun sendMessage(
        ephemeralMessage: EphemeralMessage,
        matchmakingBeacon: MatchmakingBeacon
    ): CustomResult<Unit> =
        safeResult {
            val userId = authenticator.getCurrentUid()
                ?: return CustomResult.Failure(CustomError.UserNotLoggedIn())
            val encryptMessageResult = cryptoEngine.encryptMessage(
                payloadAsString = appJson.safeEncodeToString(
                    dispatcherProvider = dispatcherProvider,
                    value = ephemeralMessage
                ),
                senderBeaconId = ephemeralMessage.senderBeaconId,
                expiresAtEpochMillis = matchmakingBeacon.beacon.expiresAt,
                messagePayloadType = MessagePayloadType.EPHEMERAL_MESSAGE
            )
            if (encryptMessageResult is CustomResult.Failure) {
                return@safeResult encryptMessageResult
            }

            // 2. Serialize & Encrypt the MatchmakingBeacon Metadata
            val matchmakingBeaconDtoString = appJson.safeEncodeToString(
                dispatcherProvider = dispatcherProvider,
                value = matchmakingBeacon.toMatchmakingBeaconDTO(userId)
            )
            val encryptMatchmakingBeaconResult = cryptoEngine.encryptMessage(
                payloadAsString = matchmakingBeaconDtoString,
                senderBeaconId = ephemeralMessage.recipientBeaconId,
                expiresAtEpochMillis = matchmakingBeacon.beacon.expiresAt,
                messagePayloadType = MessagePayloadType.MATCHMAKING_BEACON
            )
            if (encryptMatchmakingBeaconResult is CustomResult.Failure) {
                return@safeResult encryptMatchmakingBeaconResult
            }
            apiService.uploadEphemeralMessage(
                fields = EncryptedEphemeralMessageFields(
                    expiresAt = TimestampValue(epochMillisToIso(matchmakingBeacon.beacon.expiresAt)),
                    encryptedMessagePayload = StringValue((encryptMessageResult as CustomResult.Success).data),
                    encryptedMatchmakingBeaconPayload = StringValue((encryptMatchmakingBeaconResult as CustomResult.Success).data),
                    expiresAtEpochMillis = StringValue(matchmakingBeacon.beacon.expiresAt.toString()),
                    senderUid = StringValue(userId),
                    recipientUid = StringValue(ephemeralMessage.recipientUid),
                    recipientBeaconId = StringValue(ephemeralMessage.recipientBeaconId),
                    senderBeaconId = StringValue(ephemeralMessage.senderBeaconId)
                ),
                messageId = ephemeralMessage.messageId
            )
            activeConnectionsDao.upsertActiveConnection(
                ActiveConnectionEntity(
                    beaconId = matchmakingBeacon.beacon.beaconId,
                    senderUid = userId,
                    matchmakingBeaconDtoString = matchmakingBeaconDtoString,
                    encryptedMessagePayload = encryptMessageResult.data,
                    connectionStatus = ConnectionStatus.IDLE.name
                )
            )
            CustomResult.Success(Unit)

        }

    override suspend fun syncActiveConnections(): CustomResult<Unit> = safeResult {
        val userId = authenticator.getCurrentUid()
            ?: return CustomResult.Failure(CustomError.UserNotLoggedIn())
        withContext(dispatcherProvider.default) {
            val localEntitiesMap =
                activeConnectionsDao.getActiveConnectionsSnapshot().associateBy { it.beaconId }
            val encryptedMessageDTOList = apiService.getMessages(userId)
            val entitiesToUpsert = encryptedMessageDTOList.mapNotNull { dto ->
                val existingEntity = localEntitiesMap[dto.senderBeaconId]
                if (existingEntity != null) {
                    if (existingEntity.encryptedMessagePayload != dto.encryptedMessagePayload) {
                        existingEntity.copy(
                            encryptedMessagePayload = dto.encryptedMessagePayload,
                            connectionStatus = ConnectionStatus.UNREAD.name
                        )
                    } else {
                        null
                    }
                } else {
                    encryptedMessageDtoToActiveConnectionEntity(dto)
                }
            }
            if (entitiesToUpsert.isNotEmpty()) {
                activeConnectionsDao.upsertActiveConnections(entitiesToUpsert)
            }

        }
        CustomResult.Success(Unit)
    }

    override fun observeActiveConnections(): Flow<List<ActiveConnection>> =
        activeConnectionsDao.observeActiveConnections().map { entities ->
            entities.map {
                it.toActiveConnection()
            }
        }


    override suspend fun deleteMessage(messageId: String, beaconId: String): CustomResult<Unit> =
        safeResult {
            val activeConnectionEntity =
                activeConnectionsDao.getActiveConnectionByBeaconId(beaconId)
            activeConnectionEntity?.let {
                activeConnectionsDao.updateActiveConnection(it.copy(encryptedMessagePayload = null))
            }
            apiService.deleteMessage(messageId)
            CustomResult.Success(Unit)
        }

    override suspend fun decryptMessage(
        encryptedMessage: String,
        senderBeaconId: String,
        expiresAtEpochMillis: Long
    ): CustomResult<EphemeralMessage> = safeResult {
        val decryptResult = cryptoEngine.decryptMessage(
            encryptedBase64 = encryptedMessage,
            senderBeaconId = senderBeaconId,
            expiresAtEpochMillis = expiresAtEpochMillis,
            messagePayloadType = MessagePayloadType.EPHEMERAL_MESSAGE
        )
        return when (decryptResult) {
            is CustomResult.Success -> {
                CustomResult.Success(
                    appJson.safeDecodeFromString<EphemeralMessage>(
                        dispatcherProvider = dispatcherProvider,
                        string = decryptResult.data
                    )
                )
            }

            is CustomResult.Failure -> {
                CustomResult.Failure(decryptResult.error)
            }
        }
    }

    private suspend fun encryptedMessageDtoToActiveConnection(dto: EncryptedMessageDTO): ActiveConnection? {
        val decryptResult = cryptoEngine.decryptMessage(
            encryptedBase64 = dto.encryptedMatchmakingBeaconPayload,
            senderBeaconId = dto.recipientBeaconId,
            expiresAtEpochMillis = dto.expiresAtEpochMillis,
            messagePayloadType = MessagePayloadType.MATCHMAKING_BEACON
        )
        Logger.i("Decrypt result is $decryptResult")
        return if (decryptResult is CustomResult.Success) {
            Logger.i("decryptresult data ${decryptResult.data}")
            ActiveConnection(
                target = appJson.safeDecodeFromString<MatchmakingBeaconDTO>(
                    dispatcherProvider = dispatcherProvider,
                    string = decryptResult.data
                ).toMatchmakingBeacon(),
                status = ConnectionStatus.UNREAD,
                encryptedUnreadMessage = dto.encryptedMessagePayload
            )
        } else {
            // Skip beacons that fail decryption
            null
        }
    }
    private suspend fun encryptedMessageDtoToActiveConnectionEntity(dto: EncryptedMessageDTO): ActiveConnectionEntity? {
        val decryptResult = cryptoEngine.decryptMessage(
            encryptedBase64 = dto.encryptedMatchmakingBeaconPayload,
            senderBeaconId = dto.recipientBeaconId,
            expiresAtEpochMillis = dto.expiresAtEpochMillis,
            messagePayloadType = MessagePayloadType.MATCHMAKING_BEACON
        )
        Logger.i("Decrypt result is $decryptResult")
        return if (decryptResult is CustomResult.Success) {
            Logger.i("decryptresult data ${decryptResult.data}")
            ActiveConnectionEntity(
                beaconId = dto.senderBeaconId,
                senderUid = dto.senderUid,
                encryptedMessagePayload = dto.encryptedMessagePayload,
                matchmakingBeaconDtoString = decryptResult.data,
                connectionStatus = ConnectionStatus.UNREAD.name
            )
        } else {
            // Skip beacons that fail decryption
            null
        }
    }
}