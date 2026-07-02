package com.pbogdev.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BeaconDto(
    @SerialName("beacon_id")
    val beaconId: String,
    @SerialName("profile")
    val profile: ProfileDto? = null,
    @SerialName("vibe_vector")
    val vibeVector: FloatArray,
    @SerialName("vibe")
    val vibe: String,
    @SerialName("expiresAtEpochMillis")
    val expiresAtEpochMillis: Long
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as BeaconDto

        return beaconId == other.beaconId && profile == other.profile && expiresAtEpochMillis == other.expiresAtEpochMillis && vibe == other.vibe
    }

    override fun hashCode(): Int {
        var result = expiresAtEpochMillis.hashCode()
        result = 31 * result + beaconId.hashCode()
        result = 31 * result + profile.hashCode()
        return result
    }
}