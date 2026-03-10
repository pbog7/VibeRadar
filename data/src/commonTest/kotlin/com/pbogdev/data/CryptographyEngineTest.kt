package com.pbogdev.data

import com.pbogdev.domain.models.CustomResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CryptographyEngineTest {

    private val cryptoEngine: CryptographyEngine = CryptographyEngineImpl()

    // Shared Test Constants
    private val defaultJson =
        """{"profile":{"likes":["coffee"],"currentVibe":"chill"},"timestamp":1710080000}"""
    private val defaultGeohash = "srrny" // Skopje
    private val defaultTimeWindow = "2026-03-10-UTC"

    @Test
    fun `encrypt and decrypt with exact matching metadata returns original payload`() = runTest {
        // Act - Encrypt
        val encryptResult = cryptoEngine.encrypt(defaultJson, defaultGeohash, defaultTimeWindow)
        assertTrue(encryptResult is CustomResult.Success, "Encryption should succeed")
        val encryptedBase64 = encryptResult.data

        // Act - Decrypt
        val decryptResult = cryptoEngine.decrypt(encryptedBase64, defaultGeohash, defaultTimeWindow)
        assertTrue(
            decryptResult is CustomResult.Success,
            "Decryption should succeed with correct metadata"
        )

        // Assert
        assertEquals(defaultJson, decryptResult.data, "Decrypted text must match exactly")
    }

    @Test
    fun `decrypt fails when attempting to read with a different geohash`() = runTest {
        // Arrange
        val encryptedBase64 = (cryptoEngine.encrypt(
            defaultJson,
            defaultGeohash,
            defaultTimeWindow
        ) as CustomResult.Success).data

        // Act - Someone in Berlin tries to decrypt the Skopje vibe
        val maliciousGeohash = "u33dc"
        val decryptResult =
            cryptoEngine.decrypt(encryptedBase64, maliciousGeohash, defaultTimeWindow)

        // Assert
        assertTrue(
            decryptResult is CustomResult.Failure,
            "Decryption MUST fail if the geohash is different"
        )
    }

    @Test
    fun `decrypt fails when attempting to read with a different time window`() = runTest {
        // Arrange
        val encryptedBase64 = (cryptoEngine.encrypt(
            defaultJson,
            defaultGeohash,
            defaultTimeWindow
        ) as CustomResult.Success).data

        // Act - App tries to derive the key using tomorrow's date
        val wrongTimeWindow = "2026-03-11-UTC"
        val decryptResult = cryptoEngine.decrypt(encryptedBase64, defaultGeohash, wrongTimeWindow)

        // Assert
        assertTrue(
            decryptResult is CustomResult.Failure,
            "Decryption MUST fail if the time window is different"
        )
    }

    @Test
    fun `decrypt fails when the ciphertext payload is tampered with`() = runTest {
        // Arrange
        val encryptedBase64 = (cryptoEngine.encrypt(
            defaultJson,
            defaultGeohash,
            defaultTimeWindow
        ) as CustomResult.Success).data

        // Act - Simulate network tampering or MITM attack
        val tamperedBase64 = encryptedBase64.replaceFirst('A', 'B')
        val decryptResult = cryptoEngine.decrypt(tamperedBase64, defaultGeohash, defaultTimeWindow)

        // Assert
        assertTrue(
            decryptResult is CustomResult.Failure,
            "Decryption MUST fail if the AES-GCM Auth Tag detects tampering"
        )
    }
}