package com.pbogdev.data.location

import com.pbogdev.domain.models.Direction
import com.pbogdev.domain.models.Direction.*


class GeohashEngineImpl : GeohashEngine {

    private val base32 = "0123456789bcdefghjkmnpqrstuvwxyz"
    private val bits = intArrayOf(16, 8, 4, 2, 1)

    private val neighbors = mapOf(
        RIGHT to arrayOf("bc01fg45238967deuvhjyznpkmstqrwx", "p0r21436x8zb9dcf5h7kjnmqesgutwvy"),
        LEFT to arrayOf("238967debc01fg45kmstqrwxuvhjyznp", "14365h7k9dcfesgujnmqp0r2twvyx8zb"),
        TOP to arrayOf("p0r21436x8zb9dcf5h7kjnmqesgutwvy", "bc01fg45238967deuvhjyznpkmstqrwx"),
        BOTTOM to arrayOf("14365h7k9dcfesgujnmqp0r2twvyx8zb", "238967debc01fg45kmstqrwxuvhjyznp")
    )

    private val borders = mapOf(
        RIGHT to arrayOf("bcfguvyz", "prxz"),
        LEFT to arrayOf("0145hjnp", "028b"),
        TOP to arrayOf("prxz", "bcfguvyz"),
        BOTTOM to arrayOf("028b", "0145hjnp")
    )

    override fun encode(latitude: Double, longitude: Double, precision: Int): String {
        var isEven = true
        val lat = doubleArrayOf(-90.0, 90.0)
        val lon = doubleArrayOf(-180.0, 180.0)
        var bit = 0
        var ch = 0
        val geohash = StringBuilder()

        while (geohash.length < precision) {
            if (isEven) {
                val mid = (lon[0] + lon[1]) / 2
                if (longitude >= mid) {
                    ch = ch or bits[bit]
                    lon[0] = mid
                } else {
                    lon[1] = mid
                }
            } else {
                val mid = (lat[0] + lat[1]) / 2
                if (latitude >= mid) {
                    ch = ch or bits[bit]
                    lat[0] = mid
                } else {
                    lat[1] = mid
                }
            }
            isEven = !isEven
            if (bit < 4) {
                bit++
            } else {
                geohash.append(base32[ch])
                bit = 0
                ch = 0
            }
        }
        return geohash.toString()
    }

    override fun getAdjacent(hash: String, direction: Direction): String {
        if (hash.isEmpty()) return ""
        val lastChar = hash.last().lowercaseChar()
        val type = hash.length % 2 // 0 for Even, 1 for Odd
        var base = hash.dropLast(1)

        // Recursive border crossing
        if (borders[direction]!![type].contains(lastChar)) {
            base = getAdjacent(base, direction)
        }

        val index = neighbors[direction]!![type].indexOf(lastChar)
        return base + base32[index]
    }

    override fun getNineBoxGrid(latitude: Double, longitude: Double, precision: Int): List<String> {
        val center = encode(latitude, longitude, precision)

        val top = getAdjacent(center, TOP)
        val bottom = getAdjacent(center, BOTTOM)
        val right = getAdjacent(center, RIGHT)
        val left = getAdjacent(center, LEFT)

        return listOf(
            center,
            top,
            bottom,
            right,
            left,
            getAdjacent(left, TOP),    // topLeft
            getAdjacent(right, TOP),   // topRight
            getAdjacent(left, BOTTOM), // bottomLeft
            getAdjacent(right, BOTTOM) // bottomRight
        )
    }
}