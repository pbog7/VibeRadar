package com.pbogdev.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EncryptedBeaconDTO(
    @SerialName("documentId")
    val documentId: String,
    @SerialName("payload")
    val payloadBase64: String,
    @SerialName("geohash")
    val geohash: String,
    @SerialName("expiresAt")
    val expiresAt: Long
)
