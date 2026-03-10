package com.pbogdev.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class ProfileDto(
    @SerialName("id")
    val id: String,
    @SerialName("likes")
    val likes: String,
    @SerialName("dislikes")
    val dislikes: String?,
    @SerialName("likes_vector")
    val likesVector: FloatArray,
    @SerialName("dislikes_vector")
    val dislikesVector: FloatArray?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as ProfileDto

        return id == other.id && likes == other.likes && dislikes == other.dislikes
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + dislikes.hashCode()
        result = 31 * result + likes.hashCode()
        return result
    }
}
