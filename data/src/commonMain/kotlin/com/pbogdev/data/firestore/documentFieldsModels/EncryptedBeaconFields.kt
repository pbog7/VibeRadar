package com.pbogdev.data.firestore.documentFieldsModels

import com.pbogdev.data.firestore.wrapperModels.StringValue
import com.pbogdev.data.firestore.wrapperModels.TimestampValue
import kotlinx.serialization.SerialName

import kotlinx.serialization.Serializable

@Serializable
data class EncryptedBeaconFields(
    @SerialName("geohash")
    val geohash: StringValue,
    @SerialName("payload")
    val payload: StringValue,
    @SerialName("expiresAt")
    val expiresAt: TimestampValue,
    @SerialName("expiresAtEpochMillis")
    val expiresAtEpochMillis: StringValue,
    @SerialName("senderUid")
    val senderUid: StringValue
)
