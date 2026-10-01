package com.pbogdev.ephemeralmessaging.messagingScreen

import com.pbogdev.domain.matchmaking.MatchmakingBeacon
import com.pbogdev.domain.models.Beacon
import com.pbogdev.domain.models.EphemeralMessage

data class EphemeralMessagingViewState(
    val targetMatchmakingBeacon: MatchmakingBeacon? = null,
    val messageState: MessageState = MessageState.DRAFTING,
    val receivedEphemeralMessage: EphemeralMessage? = null,
    val encryptedEphemeralMessage: String? = null,
    val myBeacon: Beacon? = null
)