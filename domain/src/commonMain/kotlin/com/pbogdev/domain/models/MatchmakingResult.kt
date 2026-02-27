package com.pbogdev.domain.models

data class MatchmakingResult(
    val likesMatchScore: Float,
    val dislikesMatchScore: Float?,
    val vibeMatchScore:Float,
    val overallMatchScore: Float
){
    val likesMatchTier: MatchTier
        get() = MatchTier.fromScore(likesMatchScore)

    val dislikesMatchTier: MatchTier?
        get() = dislikesMatchScore?.let { MatchTier.fromScore(it) }

    val overallMatchTier: MatchTier
        get() = MatchTier.fromScore(overallMatchScore)
}
