package com.pbogdev.data.crypto

import com.pbogdev.domain.models.CustomResult


interface CryptographyEngine {
    /**
     * @param payloadAsString The stringified JSON vibe.
     * @param geohash The target location string (e.g., "sr2ym").
     * @param expiresAtEpochMillis The expiration of the beacon in epoch milliseconds
     * @return CustomResult containing the Base64 encoded string.
     */
    suspend fun encrypt(payloadAsString: String, geohash: String, expiresAtEpochMillis: Long): CustomResult<String>

    suspend fun decrypt(encryptedBase64: String, geohash: String, expiresAtEpochMillis: Long): CustomResult<String>
}