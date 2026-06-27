package com.pbogdev.homescreen

import androidx.compose.foundation.text.input.TextFieldState
import com.pbogdev.domain.models.Beacon


data class HomeViewState(
    val vibe: TextFieldState = TextFieldState(),
    val nearbyBeacons: List<Beacon>? = null,
)
