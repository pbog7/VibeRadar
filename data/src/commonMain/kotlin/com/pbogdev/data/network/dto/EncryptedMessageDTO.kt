package com.pbogdev.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EncryptedMessageDTO(
    @SerialName("documentId")
    val documentId: String,
    @SerialName("encryptedMessagePayload")
    val encryptedMessagePayload: String,
    @SerialName("encryptedMatchmakingBeaconPayload")
    val encryptedMatchmakingBeaconPayload: String,
    @SerialName("expiresAtEpochMillis")
    val expiresAtEpochMillis: Long,
    @SerialName("senderUid")
    val senderUid: String,
    @SerialName("senderBeaconId")
    val senderBeaconId: String,
    @SerialName("recipientUid")
    val recipientUid: String,
    @SerialName("recipientBeaconId")
    val recipientBeaconId: String
)

