package com.pbogdev.homescreen

import androidx.compose.foundation.text.input.TextFieldState
import com.pbogdev.domain.models.Beacon
import com.pbogdev.domain.models.CustomError
import com.pbogdev.domain.models.Profile


data class HomeViewState(
    val vibe: TextFieldState = TextFieldState(),
    val nearbyBeacons: List<Beacon>? = null,
    val profile: Profile? = null,
    val myBeacon: Beacon? = null,
    val isLoading: Boolean = false
)
