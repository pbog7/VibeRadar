package aimatchmakingengine

import com.pbogdev.aimatchmakingengine.VibeMatchmaker
import com.pbogdev.domain.models.Profile
import com.pbogdev.domain.models.Beacon
import kotlin.test.Test
import kotlin.test.assertEquals

class VibeMatchmakerTest {

    private val matchmaker = VibeMatchmaker()

    /**
     * Helper to easily create 512-dimension vectors for testing.
     * We only care about the first few values to verify the math; the rest are 0f.
     */
    private fun vectorOf(vararg values: Float): FloatArray {
        val array = FloatArray(512)
        values.forEachIndexed { index, value ->
            if (index < 512) array[index] = value
        }
        return array
    }
    /**
     * Helper to easily construct Vibe objects for testing.
     */
    private fun createVibe(
        vibeVector: FloatArray,
        likesVector: FloatArray,
        dislikesVector: FloatArray? = null
    ): Beacon {
        return Beacon(
            beaconId = "test_vibe_id",
            profile = Profile(
                id = "test_profile_id",
                likesVector = likesVector,
                dislikesVector = dislikesVector,
                likes = "I like hamburgers",
                dislikes = "I hate walking"
            ),
            vibeVector = vibeVector,
            timestamp = 0L
        )
    }
    @Test
    fun `calculateCosineSimilarity normalized returns exact match with score of 1_0`() {
        // Arrange
        val vector1 = vectorOf(1f, 0f, 0f)
        val vector2 =  vectorOf(1f, 0f, 0f) // Identical direction


        val result = matchmaker.calculateCosineSimilarity(vector1 = vector1, vector2 = vector2, isNormalized = true)

        // Assert
        // Using a small delta for floating-point comparisons
        assertEquals(1.0f, result, 0.0001f)
    }

    @Test
    fun `calculateCosineSimilarity not normalized returns exact match with score of 1_0`() {
        // Arrange
        val vector1 = vectorOf(3f, 4f, 0f)
        val vector2 =  vectorOf(6f, 8f, 0f) // Identical direction


        val result = matchmaker.calculateCosineSimilarity(vector1 = vector1, vector2 = vector2, isNormalized = false)

        // Assert
        // Using a small delta for floating-point comparisons
        assertEquals(1.0f, result, 0.0001f)
    }
    @Test
    fun `compareBeacon returns 1_0 for identical vibes and likes with no dislikes`() {
        // Arrange: Perfect match across the board
        val myVibe = createVibe(
            vibeVector = vectorOf(1f, 0f, 0f),
            likesVector = vectorOf(0f, 1f, 0f)
        )
        val discoveredVibe = createVibe(
            vibeVector = vectorOf(1f, 0f, 0f), // Matches my vibe
            likesVector = vectorOf(0f, 1f, 0f) // Matches my likes
        )

        // Act
        val result = matchmaker.compareBeacon(myVibe, discoveredVibe)

        // Assert: (1.0 * 0.65) + (1.0 * 0.35) = 1.0
        assertEquals(1.0f, result.overallMatchScore, 0.0001f)
        assertEquals(1.0f, result.vibeMatchScore, 0.0001f)
        assertEquals(1.0f, result.likesMatchScore, 0.0001f)
        assertEquals(0.0f, result.dislikesMatchScore!!, 0.0001f)
    }

    @Test
    fun `compareBeacon heavily weights immediate vibe over baseline likes`() {
        // Arrange: Identical vibe, completely opposite baseline likes
        val myVibe = createVibe(
            vibeVector = vectorOf(1f, 0f, 0f),
            likesVector = vectorOf(1f, 0f, 0f)
        )
        val discoveredVibe = createVibe(
            vibeVector = vectorOf(1f, 0f, 0f), // Matches vibe (1.0)
            likesVector = vectorOf(0f, 1f, 0f) // Orthogonal likes (0.0)
        )

        // Act
        val result = matchmaker.compareBeacon(myVibe, discoveredVibe)

        // Assert: (1.0 * 0.65) + (0.0 * 0.35) = 0.65
        assertEquals(0.65f, result.overallMatchScore, 0.0001f)
    }

    @Test
    fun `compareBeacon applies DEALBREAKER_PENALTY when likes clash with dislikes`() {
        // Arrange: Perfect base match, but a severe clash exists
        val myVibe = createVibe(
            vibeVector = vectorOf(1f, 0f, 0f),
            likesVector = vectorOf(0f, 1f, 0f) // I like 'B'
        )
        val discoveredVibe = createVibe(
            vibeVector = vectorOf(1f, 0f, 0f),
            likesVector = vectorOf(0f, 1f, 0f),
            dislikesVector = vectorOf(0f, 1f, 0f) // Target actively dislikes 'B'
        )

        // Act
        val result = matchmaker.compareBeacon(myVibe, discoveredVibe)

        // Assert: Base Score (1.0) - Dealbreaker (1.0 similarity * 0.20) = 0.80
        assertEquals(-0.20f, result.dislikesMatchScore!!, 0.0001f)
        assertEquals(0.80f, result.overallMatchScore, 0.0001f)
    }

    @Test
    fun `compareBeacon applies HATER_BONUS when both users share dislikes`() {
        // Arrange: Poor base match, but identical dislikes
        val myVibe = createVibe(
            vibeVector = vectorOf(1f, 0f, 0f), // My vibe 'A'
            likesVector = vectorOf(0f, 0f, 0f),
            dislikesVector = vectorOf(0f, 0f, 1f) // I hate 'C'
        )
        val discoveredVibe = createVibe(
            vibeVector = vectorOf(0f, 1f, 0f), // Target vibe 'B' (Sim = 0.0)
            likesVector = vectorOf(1f, 0f, 0f), // Target likes 'A' (Sim = 0.0)
            dislikesVector = vectorOf(0f, 0f, 1f) // Target also hates 'C' (Sim = 1.0)
        )

        // Act
        val result = matchmaker.compareBeacon(myVibe, discoveredVibe)

        // Assert: Base Score (0.0) + Hater Bonus (1.0 * 0.10) = 0.10
        assertEquals(0.10f, result.dislikesMatchScore!!, 0.0001f)
        assertEquals(0.10f, result.overallMatchScore, 0.0001f)
    }

    @Test
    fun `compareBeacon clamps negative scores to exactly zero`() {
        // Arrange: Zero base match, AND a dealbreaker penalty
        val myVibe = createVibe(
            vibeVector = vectorOf(1f, 0f, 0f),
            likesVector = vectorOf(0f, 1f, 0f) // I like 'B'
        )
        val discoveredVibe = createVibe(
            vibeVector = vectorOf(0f, 1f, 0f), // Orthogonal vibe
            likesVector = vectorOf(1f, 0f, 0f), // Orthogonal likes
            dislikesVector = vectorOf(0f, 1f, 0f) // Target hates 'B' (Clash!)
        )

        // Act
        val result = matchmaker.compareBeacon(myVibe, discoveredVibe)

        // Assert: Base (0.0) - Dealbreaker (0.20) = -0.20, which must clamp to 0.0
        assertEquals(-0.20f, result.dislikesMatchScore!!, 0.0001f)
        assertEquals(0.0f, result.overallMatchScore, 0.0001f) // Validates coerceIn(0f, 1f)
    }

    @Test
    fun `compareBeacon applies double dealbreaker penalty when both users clash`() {
        // Arrange: Good vibe, but their likes/dislikes are completely crossed
        val myVibe = createVibe(
            vibeVector = vectorOf(1f, 0f, 0f), // Vibe matches (1.0)
            likesVector = vectorOf(0f, 1f, 0f), // I like 'B'
            dislikesVector = vectorOf(0f, 0f, 1f) // I hate 'C'
        )
        val discoveredVibe = createVibe(
            vibeVector = vectorOf(1f, 0f, 0f), // Vibe matches (1.0)
            likesVector = vectorOf(0f, 0f, 1f), // Target likes 'C' (I hate this -> -0.20)
            dislikesVector = vectorOf(0f, 1f, 0f) // Target hates 'B' (I like this -> -0.20)
        )

        // Act
        val result = matchmaker.compareBeacon(myVibe, discoveredVibe)

        // Assert
        // Base Score: (1.0 * 0.65) + (0.0 * 0.35) = 0.65
        // Modifier: -0.20 (My clash) - 0.20 (Target clash) = -0.40
        // Expected Final: 0.65 - 0.40 = 0.25
        assertEquals(-0.40f, result.dislikesMatchScore!!, 0.0001f)
        assertEquals(0.25f, result.overallMatchScore, 0.0001f)
    }

    @Test
    fun `compareBeacon correctly sums HATER_BONUS and DEALBREAKER_PENALTY simultaneously`() {
        // Arrange
        val myVibe = createVibe(
            vibeVector = vectorOf(1f, 0f, 0f),
            likesVector = vectorOf(0f, 1f, 0f), // I like 'B'
            dislikesVector = vectorOf(0f, 0f, 1f) // I hate 'C'
        )
        val discoveredVibe = createVibe(
            vibeVector = vectorOf(1f, 0f, 0f),
            likesVector = vectorOf(0f, 0f, 1f), // Target likes 'C' (Clash! I hate 'C' -> -0.20)
            dislikesVector = vectorOf(0f, 0f, 1f) // Target ALSO hates 'C' (Bonus! -> +0.10)
        )

        // Act
        val result = matchmaker.compareBeacon(myVibe, discoveredVibe)

        // Assert
        // Base Score: (1.0 * 0.65) + (0.0 * 0.35) = 0.65
        // Net Modifier: -0.20 (Clash) + 0.10 (Shared Hate) = -0.10
        // Expected Final: 0.65 - 0.10 = 0.55
        assertEquals(-0.10f, result.dislikesMatchScore!!, 0.0001f)
        assertEquals(0.55f, result.overallMatchScore, 0.0001f)
    }
}