package com.pbogdev.data.location

import com.pbogdev.domain.models.Direction

interface GeohashEngine {
    /**
     * Converts fuzzed hardware coordinates into a standard Geohash string.
     * @param precision Defaults to 5 (~4.9km x 4.9km grid) for "Blind Relay" privacy.
     */
    fun encode(latitude: Double, longitude: Double, precision: Int = 5): String

    /**
     * Calculates the adjacent Geohash string in a specific direction.
     */
    fun getAdjacent(hash: String, direction: Direction): String

    /**
     * Generates the 9-box grid (current location + 8 neighbors) for optimized Firestore 'IN' querying.
     */
    fun getNineBoxGrid(latitude: Double, longitude: Double, precision: Int = 5): List<String>
}