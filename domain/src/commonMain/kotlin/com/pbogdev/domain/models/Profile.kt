package com.pbogdev.domain.models

data class Profile(
    val id: String,
    val likes: String,
    val dislikes: String?,
    val likesVector: FloatArray,
    val dislikesVector: FloatArray?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Profile

        return id == other.id && likes == other.likes && dislikes == other.dislikes
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + dislikes.hashCode()
        result = 31 * result + likes.hashCode()
        return result
    }
}
