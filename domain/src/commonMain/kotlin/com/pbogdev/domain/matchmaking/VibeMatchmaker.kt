package com.pbogdev.domain.matchmaking

import com.pbogdev.domain.models.Beacon

interface VibeMatchmaker {
    fun calculateCosineSimilarity(
        vector1: FloatArray,
        vector2: FloatArray,
        isNormalized: Boolean = true
    ): Float

    fun compareBeacon(myBeacon: Beacon, discoveredBeacon: Beacon): MatchmakingResult
}