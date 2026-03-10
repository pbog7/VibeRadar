package com.pbogdev.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BeaconDto(
    @SerialName("beacon_id")
    val beaconId: String,
    @SerialName("profile")
    val profile: ProfileDto,
    @SerialName("vibe_vector")
    val vibeVector: FloatArray,
    @SerialName("timestamp")
    val timestamp: Long
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as BeaconDto

        return beaconId == other.beaconId && profile == other.profile && timestamp == other.timestamp
    }

    override fun hashCode(): Int {
        var result = timestamp.hashCode()
        result = 31 * result + beaconId.hashCode()
        result = 31 * result + profile.hashCode()
        return result
    }
}