package com.pbogdev.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MatchmakingBeaconDTO(
    @SerialName("beacon")
    val beacon: BeaconDto,
    @SerialName("matchResult")
    val matchResult: MatchmakingResultDTO
)