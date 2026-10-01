package com.pbogdev.data.crypto

import com.pbogdev.core.dispatcherProvider.DispatcherProvider
import com.pbogdev.core.utils.safeResult
import com.pbogdev.domain.models.CustomError
import com.pbogdev.domain.models.CustomResult
import com.pbogdev.domain.models.MessagePayloadType
import com.pbogdev.viberadar.data.BuildKonfig
import dev.whyoleg.cryptography.BinarySize.Companion.bits
import dev.whyoleg.cryptography.CryptographyProvider
import dev.whyoleg.cryptography.algorithms.AES
import dev.whyoleg.cryptography.algorithms.HKDF
import dev.whyoleg.cryptography.algorithms.SHA256
import dev.whyoleg.cryptography.operations.Cipher
import kotlinx.coroutines.withContext
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class CryptographyEngineImpl(
    private val provider: CryptographyProvider = CryptographyProvider.Default,
    private val dispatcherProvider: DispatcherProvider
) : CryptographyEngine {

    @OptIn(ExperimentalEncodingApi::class)
    override suspend fun encryptBeacon(
        payloadAsString: String,
        geohash: String,
        expiresAtEpochMillis: Long
    ): CustomResult<String> =
        safeResult(
            mapException = { e ->
                CustomError.EncryptionError(
                    e.message ?: "Encryption error"
                )
            }
        ) {
            withContext(dispatcherProvider.default) {
                val cipher = deriveAesGcmCipher(geohash, expiresAtEpochMillis)

                val encryptedBytes = cipher.encrypt(payloadAsString.encodeToByteArray())

                CustomResult.Success(Base64.UrlSafe.encode(encryptedBytes))
            }

        }


    @OptIn(ExperimentalEncodingApi::class)
    override suspend fun decryptBeacon(
        encryptedBase64: String,
        geohash: String,
        expiresAtEpochMillis: Long
    ): CustomResult<String> =
        safeResult(
            mapException = { e ->
                CustomError.DecryptionError(
                    e.message ?: "Data tamper detected or incorrect routing metadata", e.cause
                )
            }
        ) {
            withContext(dispatcherProvider.default) {
                val cipher = deriveAesGcmCipher(geohash, expiresAtEpochMillis)

                val decryptedBytes = cipher.decrypt(Base64.UrlSafe.decode(encryptedBase64))

                CustomResult.Success(decryptedBytes.decodeToString())
            }
        }

    override suspend fun encryptMessage(
        payloadAsString: String,
        senderBeaconId: String,
        expiresAtEpochMillis: Long,
        messagePayloadType: MessagePayloadType
    ): CustomResult<String> = safeResult(
        mapException = { e ->
            CustomError.EncryptionError(
                e.message ?: "Encryption error"
            )
        }
    ) {
        withContext(dispatcherProvider.default) {
            val key =  messagePayloadType.name + senderBeaconId
            val cipher = deriveAesGcmCipher(key, expiresAtEpochMillis)

            val encryptedBytes = cipher.encrypt(payloadAsString.encodeToByteArray())

            CustomResult.Success(Base64.UrlSafe.encode(encryptedBytes))
        }

    }

    override suspend fun decryptMessage(
        encryptedBase64: String,
        senderBeaconId: String,
        expiresAtEpochMillis: Long,
        messagePayloadType: MessagePayloadType
    ): CustomResult<String> =
        safeResult(
            mapException = { e ->
                CustomError.DecryptionError(
                    e.message ?: "Data tamper detected or incorrect routing metadata", e.cause
                )
            }
        ) {
            withContext(dispatcherProvider.default) {
                val key = messagePayloadType.name + senderBeaconId
                val cipher = deriveAesGcmCipher(key, expiresAtEpochMillis)

                val decryptedBytes = cipher.decrypt(Base64.UrlSafe.decode(encryptedBase64))

                CustomResult.Success(decryptedBytes.decodeToString())
            }
        }

    /**
     * @param key the geohash string is used as a key for beacon encryption and the targetBeaconId is used for message encryption.
     * @param expiresAtEpochMillis the expiration date in epoch millis is used for both beacon and message encryption
     */
    private suspend fun deriveAesGcmCipher(key: String, expiresAtEpochMillis: Long): Cipher {
        // 1. Initialize the required algorithms
        val hkdf = provider.get(HKDF)
        val aesGcm = provider.get(AES.GCM)

        // 2. Configure the HKDF Derivation Engine
        val kdf = hkdf.secretDerivation(
            digest = SHA256,
            outputSize = 256.bits, // 256 bits required for our AES-GCM cipher
            salt = null, // The interface accepts ByteArray?, null perfectly defaults to the RFC standard empty salt
            info = "$key:$expiresAtEpochMillis".encodeToByteArray()
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