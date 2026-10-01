package com.pbogdev.domain.repository

import com.pbogdev.domain.matchmaking.MatchmakingBeacon
import com.pbogdev.domain.models.ActiveConnection
import com.pbogdev.domain.models.CustomResult
import com.pbogdev.domain.models.EphemeralMessage
import kotlinx.coroutines.flow.Flow

interface EphemeralMessageRepository {
    suspend fun sendMessage(ephemeralMessage: EphemeralMessage,matchmakingBeacon: MatchmakingBeacon): CustomResult<Unit>

    suspend fun syncActiveConnections(): CustomResult<Unit>

    fun observeActiveConnections(): Flow<List<ActiveConnection>>

    suspend fun deleteMessage(messageId: String, beaconId: String): CustomResult<Unit>

    suspend fun decryptMessage(encryptedMessage: String, senderBeaconId: String, expiresAtEpochMillis: Long): CustomResult<EphemeralMessage>
}