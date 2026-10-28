package com.pbogdev.data.crypto

import com.pbogdev.domain.models.CustomResult
import com.pbogdev.domain.models.MessagePayloadType


interface CryptographyEngine {
    /**
     * @param payloadAsString The stringified JSON vibe.
     * @param geohash The target location string (e.g., "sr2ym").
     * @param expiresAtEpochMillis The expiration of the beacon in epoch milliseconds
     * @return CustomResult containing the Base64 encoded string.
     */
    suspend fun encryptBeacon(payloadAsString: String, geohash: String, expiresAtEpochMillis: Long): CustomResult<String>

    suspend fun decryptBeacon(encryptedBase64: String, geohash: String, expiresAtEpochMillis: Long): CustomResult<String>

    suspend fun encryptMessage(payloadAsString: String, senderBeaconId: String, expiresAtEpochMillis: Long, messagePayloadType: MessagePayloadType): CustomResult<String>

    suspend fun decryptMessage(encryptedBase64: String, senderBeaconId: String, expiresAtEpochMillis: Long, messagePayloadType: MessagePayloadType): CustomResult<String>
}