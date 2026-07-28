package com.pbogdev.domain.models

sealed class CustomError : Exception() {
    data class EmptyList(override val message: String = "Empty list") : CustomError()
    data class UnknownError(override val message: String = "Something went wrong") : CustomError()
    data class NetworkError(override val message: String = "Something went wrong") : CustomError()
    data class EncryptionError(override val message: String? = "Encryption error"): CustomError()
    data class DecryptionError(override val message: String?,  override val cause: Throwable? = null):CustomError()
    data class GeohashNotStored(override val message: String = "Geohash has not been stored"): CustomError()
    data class BlankTextError(override val message: String = "Text is blank"): CustomError()

    data class EmbeddingError(override val message: String = "Embedding error"): CustomError()

    data class TextEmbedderInitializationError(override val message: String = "TextEmbedder failed to initialize"):
        CustomError()
    data class SignInError(override val message: String = "Anonymous authentication sign in has failed"):
        CustomError()
    data class GetTokenError(override val message: String ="Get token failed, token is either invalid or does not exist"):
        CustomError()
    data class UserNotLoggedIn (override val message: String ="User is not logged in"): CustomError()

    data class BeaconNotStored(override val message: String = "Beacon has not been stored"): CustomError()

}