package com.pbogdev.domain.models

enum class MatchTier {
    NO_MATCH, POOR_MATCH, GOOD_MATCH, GREAT_MATCH;
    companion object {
        // Centralized business logic for evaluating ANY score
        fun fromScore(score: Float): MatchTier = when {
            score >= 0.80f -> GREAT_MATCH
            score >= 0.65f -> GOOD_MATCH
            score >= 0.50f -> POOR_MATCH
            else -> NO_MATCH
        }
    }
}