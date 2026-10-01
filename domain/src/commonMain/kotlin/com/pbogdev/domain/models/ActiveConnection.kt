package com.pbogdev.domain.models

import com.pbogdev.domain.matchmaking.MatchmakingBeacon

data class ActiveConnection(
    val target: MatchmakingBeacon,
    val status: ConnectionStatus,
    val encryptedUnreadMessage: String? = null
)
