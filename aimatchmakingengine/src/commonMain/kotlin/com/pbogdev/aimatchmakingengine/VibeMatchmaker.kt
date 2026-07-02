package com.pbogdev.aimatchmakingengine

import com.pbogdev.domain.models.Beacon
import com.pbogdev.domain.models.MatchmakingResult

interface VibeMatchmaker {
    fun calculateCosineSimilarity(
        vector1: FloatArray,
        vector2: FloatArray,
        isNormalized: Boolean = true
    ): Float

    fun compareBeacon(myBeacon: Beacon, discoveredBeacon: Beacon): MatchmakingResult
}