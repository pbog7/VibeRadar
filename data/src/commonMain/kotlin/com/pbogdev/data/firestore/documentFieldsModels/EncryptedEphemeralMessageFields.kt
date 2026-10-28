package com.pbogdev.data.firestore.documentFieldsModels

import com.pbogdev.data.firestore.wrapperModels.StringValue
import com.pbogdev.data.firestore.wrapperModels.TimestampValue
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EncryptedEphemeralMessageFields(
    @SerialName("encryptedMessagePayload")
    val encryptedMessagePayload: StringValue,
    @SerialName("encryptedMatchmakingBeaconPayload")
    val encryptedMatchmakingBeaconPayload: StringValue,
    @SerialName("expiresAt")
    val expiresAt: TimestampValue,
    @SerialName("expiresAtEpochMillis")
    val expiresAtEpochMillis: StringValue,
    @SerialName("senderUid")
    val senderUid: StringValue,
    @SerialName("senderBeaconId")
    val senderBeaconId: StringValue,
    @SerialName("recipientUid")
    val recipientUid: StringValue,
    @SerialName("recipientBeaconId")
    val recipientBeaconId: StringValue,
)
