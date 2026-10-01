package com.pbogdev.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class MatchmakingResultDTO(
    val likesMatchScore: Float,
    val dislikesMatchScore: Float?,
    val vibeMatchScore:Float,
    val overallMatchScore: Float
)