package com.pbogdev.data.firestore.wrapperModels

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TimestampValue(
    @SerialName("timestampValue")
    val timeStampValue: String
)