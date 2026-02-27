package com.pbogdev.aimatchmakingengine

import com.pbogdev.domain.models.MatchmakingResult
import com.pbogdev.domain.models.Beacon
import kotlin.math.sqrt

class VibeMatchmaker {
    /**
     * Calculates the cosine similarity between two high-dimensional vectors.
     * @param vector1 The first vector.
     * @param vector2 The second vector.
     * @param isNormalized If true, assumes vectors are L2 normalized, optimizing
     * the calculation to a pure dot product. Defaults to true.
     * @return A float between -1.0 (perfectly dissimilar) and 1.0 (perfectly similar).
     */
    fun calculateCosineSimilarity(
        vector1: FloatArray,
        vector2: FloatArray,
        isNormalized: Boolean = true
    ): Float {
        require(vector1.size == vector2.size) {
            "Vectors must be the same length. Vector A: ${vector1.size}, Vector B: ${vector2.size}"
        }

        var dotProduct = 0.0f

        if (isNormalized) {
            // FAST PATH: If already normalized, ||A|| and ||B|| are 1.0.
            // Cosine similarity is exactly equal to the dot product.
            for (i in vector1.indices) {
                dotProduct += vector1[i] * vector2[i]
            }
        } else {
            // STANDARD PATH: Calculate dot product and magnitudes in one pass
            var normA = 0.0f
            var normB = 0.0f

            for (i in vector1.indices) {
                val a = vector1[i]
                val b = vector2[i]

                dotProduct += a * b
                normA += a * a
                normB += b * b
            }

            // Prevent division by zero if a zero-vector is passed
            if (normA == 0.0f || normB == 0.0f) return 0.0f

            dotProduct /= (sqrt(normA) * sqrt(normB))
        }
        return dotProduct.coerceIn(-1.0f, 1.0f)
    }

    fun compareBeacon(myBeacon: Beacon, discoveredBeacon: Beacon): MatchmakingResult {
        val vibeSim = calculateCosineSimilarity(myBeacon.vibeVector, discoveredBeacon.vibeVector)
        val likesSim = calculateCosineSimilarity(myBeacon.profile.likesVector, discoveredBeacon.profile.likesVector)

        var dislikesModifier = 0f

        val myBeaconDislikes = myBeacon.profile.dislikesVector
        val discoveredBeaconDislikes = discoveredBeacon.profile.dislikesVector

        if (discoveredBeaconDislikes != null) {
            dislikesModifier -= (DEALBREAKER_PENALTY * calculateCosineSimilarity(myBeacon.profile.likesVector, discoveredBeaconDislikes))
        }
        if (myBeaconDislikes != null) {
            dislikesModifier -= (DEALBREAKER_PENALTY * calculateCosineSimilarity(myBeaconDislikes, discoveredBeacon.profile.likesVector))
        }
        if (myBeaconDislikes != null && discoveredBeaconDislikes != null) {
            dislikesModifier += (HATER_BONUS * calculateCosineSimilarity(myBeaconDislikes, discoveredBeaconDislikes))
        }

        val baseScore = (vibeSim * VIBE_WEIGHT) + (likesSim * LIKES_WEIGHT)
        val overallScore = (baseScore + dislikesModifier).coerceIn(0f, 1f)

        return MatchmakingResult(
            likesMatchScore = likesSim,
            dislikesMatchScore = dislikesModifier,
            vibeMatchScore = vibeSim,
            overallMatchScore = overallScore
        )
    }
}