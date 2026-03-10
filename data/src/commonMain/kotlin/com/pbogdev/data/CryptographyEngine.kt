package com.pbogdev.data

import com.pbogdev.domain.models.CustomResult


interface CryptographyEngine {
    /**
     * @param payloadAsString The stringified JSON vibe.
     * @param geohash The target location string (e.g., "sr2ym").
     * @param timeWindow The UTC epoch string (e.g., "2026-03-10-UTC").
     * @return CustomResult containing the Base64 encoded string.
     */
    suspend fun encrypt(payloadAsString: String, geohash: String, timeWindow: String): CustomResult<String>

    suspend fun decrypt(encryptedBase64: String, geohash: String, timeWindow: String): CustomResult<String>
}