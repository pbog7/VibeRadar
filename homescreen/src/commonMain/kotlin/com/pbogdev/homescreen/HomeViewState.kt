package com.pbogdev.homescreen

import androidx.compose.foundation.text.input.TextFieldState
import com.pbogdev.domain.matchmaking.MatchmakingBeacon
import com.pbogdev.domain.models.Beacon
import com.pbogdev.domain.models.CustomError
import com.pbogdev.domain.models.Profile


data class HomeViewState(
    val nearbyBeacons: List<MatchmakingBeacon>? = null,
    val profile: Profile? = null,
    val myBeacon: Beacon? = null,
    val radarState: RadarState = RadarState.IDLE,
    val expiresAt: Long? = null,
    val uploadNewBeacon: Boolean = true
)
