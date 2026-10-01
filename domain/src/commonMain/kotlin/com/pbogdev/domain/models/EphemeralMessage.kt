package com.pbogdev.domain.models

import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Serializable
data class EphemeralMessage(
    val messageId: String = Uuid.generateV7().toString(),
    val recipientUid: String,
    val senderBeaconId: String,
    val recipientBeaconId: String,
    val expiresAt: Long,
    val isOutgoing: Boolean,
    val plainText: String
)
