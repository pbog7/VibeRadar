package com.pbogdev.domain.models

data class Beacon(
    val beaconId: String,
    val profile: Profile,
    val vibeVector: FloatArray,
    val timestamp: Long
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Beacon

        return beaconId == other.beaconId && profile == other.profile && timestamp == other.timestamp
    }

    override fun hashCode(): Int {
        var result = timestamp.hashCode()
        result = 31 * result + beaconId.hashCode()
        result = 31 * result + profile.hashCode()
        return result
    }
}