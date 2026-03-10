package com.pbogdev.data

import com.pbogdev.domain.models.CustomError
import com.pbogdev.domain.models.CustomResult
import com.pbogdev.viberadar.data.BuildKonfig
//import com.viberadar.data.config.BuildKonfig
import dev.whyoleg.cryptography.BinarySize.Companion.bits
import dev.whyoleg.cryptography.CryptographyProvider
import dev.whyoleg.cryptography.algorithms.AES
import dev.whyoleg.cryptography.algorithms.HKDF
import dev.whyoleg.cryptography.algorithms.SHA256
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class CryptographyEngineImpl(
    private val provider: CryptographyProvider = CryptographyProvider.Default
) : CryptographyEngine {

    @OptIn(ExperimentalEncodingApi::class)
    override suspend fun encrypt(payloadAsString: String, geohash: String, timeWindow: String): CustomResult<String> {
        return try {
            val cipher = deriveAesGcmCipher(geohash, timeWindow)

            val encryptedBytes = cipher.encrypt(payloadAsString.encodeToByteArray())

            CustomResult.Success(Base64.encode(encryptedBytes))
        } catch (e: Exception) {
            CustomResult.Failure(CustomError.EncryptionError(e.message?:"Encryption error"))
        }
    }

    @OptIn(ExperimentalEncodingApi::class)
    override suspend fun decrypt(encryptedBase64: String, geohash: String, timeWindow: String): CustomResult<String> {
        return try {
            val cipher = deriveAesGcmCipher(geohash, timeWindow)

            val decryptedBytes = cipher.decrypt(Base64.decode(encryptedBase64))

            CustomResult.Success(decryptedBytes.decodeToString())
        } catch (e: Exception) {
            CustomResult.Failure(CustomError.DecryptionError(e.message?:"Data tamper detected or incorrect routing metadata", e.cause))
        }
    }

    private suspend fun deriveAesGcmCipher(geohash: String, timeWindow: String): dev.whyoleg.cryptography.operations.Cipher {
        // 1. Initialize the required algorithms
        val hkdf = provider.get(HKDF)
        val aesGcm = provider.get(AES.GCM)

        // 2. Configure the HKDF Derivation Engine
        val kdf = hkdf.secretDerivation(
            digest = SHA256,
            outputSize = 256.bits, // 256 bits required for our AES-GCM cipher
            salt = null, // The interface accepts ByteArray?, null perfectly defaults to the RFC standard empty salt
            info = "$geohash:$timeWindow".encodeToByteArray()
        )

        // 3. Execute the derivation using our Master Secret (IKM)
        val derivedKeyBytes = kdf.deriveSecret(BuildKonfig.APP_SECRET.encodeToByteArray())

        // 4. Decode the raw bytes into a workable AES-GCM Key
        val aesKey = aesGcm.keyDecoder().decodeFromByteString(
            format = AES.Key.Format.RAW,
            byteString = derivedKeyBytes
        )

        // 5. Return the ready-to-use Cipher
        return aesKey.cipher()
    }
}