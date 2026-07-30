package com.pbogdev.data.repository

import co.touchlab.kermit.Logger
import com.pbogdev.core.dispatcherProvider.DispatcherProvider
import com.pbogdev.core.utils.epochMillisToIso
import com.pbogdev.core.utils.safeResult
import com.pbogdev.data.crypto.CryptographyEngine
import com.pbogdev.data.firestore.documentFieldsModels.EncryptedBeaconFields
import com.pbogdev.data.firestore.wrapperModels.StringValue
import com.pbogdev.data.firestore.wrapperModels.TimestampValue
import com.pbogdev.data.network.ApiService
import com.pbogdev.data.network.dto.BeaconDto
import com.pbogdev.data.network.dto.EncryptedBeaconDTO
import com.pbogdev.data.toBeacon
import com.pbogdev.data.toBeaconDto
import com.pbogdev.data.utils.appJson
import com.pbogdev.data.utils.safeDecodeFromString
import com.pbogdev.data.utils.safeEncodeToString
import com.pbogdev.domain.location.GeohashManager
import com.pbogdev.domain.auth.AnonymousAuthenticator
import com.pbogdev.domain.matchmaking.MatchmakingBeacon
import com.pbogdev.domain.matchmaking.VibeMatchmaker
import com.pbogdev.domain.models.Beacon
import com.pbogdev.domain.models.CustomError
import com.pbogdev.domain.models.CustomResult
import com.pbogdev.domain.repository.BeaconRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

class BeaconRepositoryImpl(
    private val geohashManager: GeohashManager,
    private val apiService: ApiService,
    private val cryptoEngine: CryptographyEngine,
    private val dispatcherProvider: DispatcherProvider,
    private val authenticator: AnonymousAuthenticator,
    private val vibeMatchmaker: VibeMatchmaker
) : BeaconRepository {


    override suspend fun getNearbyBeacons(userBeacon: Beacon): CustomResult<List<MatchmakingBeacon>> =
        safeResult {
            // Fetch current geohash grid from Datastore
            val geohashResult = geohashManager.fetchCurrentGeohashGrid()
            if (geohashResult is CustomResult.Failure) {
                return CustomResult.Failure(geohashResult.error)
            } else {
                // Query firestore for nearby encrypted beacons
                val geohashGrid = (geohashResult as CustomResult.Success).data
                val encryptedBeaconDTOList = apiService.getBeaconsByGeohashes(geohashGrid)

                // decrypt encrypted beacons and map them into domain beacons asynchronously
                val beacons = withContext(dispatcherProvider.default) {
                    encryptedBeaconDTOList.map { dto ->
                        Logger.i("DTO geohash:${dto.geohash}, expiresAtEpochMillis:${dto.expiresAtEpochMillis}")
                        async {
                            val decryptedBeacon = encryptedBeaconDtoToBeacon(dto)
                            decryptedBeacon?.let {
                                MatchmakingBeacon(
                                    beacon = decryptedBeacon,
                                    matchResult = vibeMatchmaker.compareBeacon(
                                        myBeacon = userBeacon,
                                        discoveredBeacon = decryptedBeacon
                                    )
                                )

                            }
                        }
                    }.awaitAll()
                        .filterNotNull()
                        .sortedByDescending { it.matchResult.overallMatchScore }
                }

                CustomResult.Success(beacons)
            }
        }

    private suspend fun encryptedBeaconDtoToBeacon(dto: EncryptedBeaconDTO): Beacon? {
        val decryptResult = cryptoEngine.decrypt(
            encryptedBase64 = dto.payloadBase64,
            geohash = dto.geohash,
            expiresAtEpochMillis = dto.expiresAtEpochMillis
        )
        Logger.i("Decrypt result is $decryptResult")
        return if (decryptResult is CustomResult.Success) {
            Logger.i("decryptresult data ${decryptResult.data}")
            val decryptedJson = decryptResult.data
            val beaconDto = appJson.safeDecodeFromString<BeaconDto>(
                dispatcherProvider = dispatcherProvider,
                string = decryptedJson
            )
            beaconDto.toBeacon()
        } else {
            // Skip beacons that fail decryption
            null
        }
    }

    override suspend fun uploadBeacon(beacon: Beacon): CustomResult<Unit> = safeResult {
        val geohashResult = geohashManager.fetchCurrentGeohash()
        if (geohashResult is CustomResult.Failure) {
            return CustomResult.Failure(geohashResult.error)
        } else {
            val userId = authenticator.getCurrentUid()
                ?: return CustomResult.Failure(CustomError.UserNotLoggedIn())
            val geohash = (geohashResult as CustomResult.Success).data
            val encryptBeaconResult = cryptoEngine.encrypt(
                payloadAsString = appJson.safeEncodeToString(
                    dispatcherProvider = dispatcherProvider,
                    value = beacon.toBeaconDto()
                ),
                geohash = geohash,
                expiresAtEpochMillis = beacon.expiresAt
            )
            when (encryptBeaconResult) {
                is CustomResult.Failure -> {
                    return@safeResult CustomResult.Failure(encryptBeaconResult.error)
                }

                is CustomResult.Success -> {
                    apiService.uploadBeacon(
                        EncryptedBeaconFields(
                            geohash = StringValue(geohash),
                            expiresAt = TimestampValue(epochMillisToIso(beacon.expiresAt)),
                            payload = StringValue(encryptBeaconResult.data),
                            expiresAtEpochMillis = StringValue(beacon.expiresAt.toString()),
                            senderUid = StringValue(userId)
                        )
                    )
                }
            }
            CustomResult.Success(Unit)
        }
    }
}