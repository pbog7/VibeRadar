package com.pbogdev.domain.models

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class Beacon(
    /**
     * NOTE: Do not set this manually when creating a NEW beacon in the UI layer.
     * It defaults to a time-ordered UUID v7. Only pass this explicitly when
     * mapping from the database or in test classes.
     */
    val beaconId: String = Uuid.generateV7().toString(),
    val profile: Profile? = null,
    val vibeVector: FloatArray,
    val vibe: String,
    val expiresAt: Long
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Beacon

        return beaconId == other.beaconId && profile == other.profile && expiresAt == other.expiresAt && vibe == other.vibe
    }

    override fun hashCode(): Int {
        var result = expiresAt.hashCode()
        result = 31 * result + beaconId.hashCode()
        result = 31 * result + profile.hashCode()
        return result
    }
}