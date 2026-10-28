package com.pbogdev.domain.matchmaking

import com.pbogdev.domain.models.Beacon

data class MatchmakingBeacon(
    val beacon: Beacon,
    val matchResult: MatchmakingResult
)