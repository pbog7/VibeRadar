package com.pbogdev.homescreen

import com.pbogdev.domain.matchmaking.MatchmakingBeacon
import com.pbogdev.domain.models.ActiveConnection
import com.pbogdev.domain.models.Beacon
import com.pbogdev.domain.models.Profile


data class HomeViewState(
    val nearbyBeacons: List<MatchmakingBeacon>? = null,
    val profile: Profile? = null,
    val myBeacon: Beacon? = null,
    val radarState: RadarState = RadarState.IDLE,
    val expiresAt: Long? = null,
    val uploadNewBeacon: Boolean = true,
    val activeConnectionsList: List<ActiveConnection> = emptyList(),
)
