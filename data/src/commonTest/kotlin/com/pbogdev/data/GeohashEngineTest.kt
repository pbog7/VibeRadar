package com.pbogdev.data

import com.pbogdev.data.location.GeohashEngine
import com.pbogdev.data.location.GeohashEngineImpl
import com.pbogdev.domain.models.Direction.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GeohashEngineTest {

    private val engine: GeohashEngine = GeohashEngineImpl()

    @Test
    fun `test standard encoding - Skopje, Karpos coordinates`() {
        val hash = engine.encode(42.0, 21.4, 5)
        assertEquals(5, hash.length)
        assertEquals(expected = "srrny", actual = hash, message = "Standard encoding failed to provide correct geohash")
    }
    @Test
    fun `test variable precision encoding`() {
        // High precision test (9 characters) to ensure the bitwise loop scales perfectly
        val hashHigh = engine.encode(42.0, 21.4, 9)
        assertEquals(9, hashHigh.length)
        assertTrue(hashHigh.startsWith("srrny"), "High precision must maintain the parent prefix")
    }
    // --- MID-GRID ADJACENCY TESTS ---

    @Test
    fun `test mid grid adjacency - top`() {
        assertEquals("sx8e", engine.getAdjacent("sx8d", TOP), "Top mid-grid neighbor failed to calculate standard Z-order shift")
    }

    @Test
    fun `test mid grid adjacency - bottom`() {
        assertEquals("sx89", engine.getAdjacent("sx8d", BOTTOM), "Bottom mid-grid neighbor failed to calculate standard Z-order shift")
    }

    @Test
    fun `test mid grid adjacency - left`() {
        assertEquals("sx86", engine.getAdjacent("sx8d", LEFT), "Left mid-grid neighbor failed to calculate standard Z-order shift")
    }

    @Test
    fun `test mid grid adjacency - right`() {
        assertEquals("sx8f", engine.getAdjacent("sx8d", RIGHT), "Right mid-grid neighbor failed to calculate standard Z-order shift")
    }

    // --- RECURSIVE BOUNDARY TESTS ---

    @Test
    fun `test boundary crossing - right wall`() {
        // 'f' is right edge. Parent "sx8" -> "sx9". Child 'f' wraps left to '4'.
        assertEquals("sx94", engine.getAdjacent("sx8f", RIGHT), "Right wall recursive boundary shift failed")
    }

    @Test
    fun `test boundary crossing - left wall (Double Recursion)`() {
        // '4' is left edge. Parent '8' is ALSO left edge.
        // "sx" -> "sw", '8' wraps to 'x', '4' wraps to 'f'.
        assertEquals("srxf", engine.getAdjacent("sx84", LEFT), "Left wall double-recursive boundary shift failed")
    }

    @Test
    fun `test boundary crossing - top wall`() {
        // 'z' is top edge. Parent "sx8" -> "sxb". Child 'z' wraps bottom to 'b'.
        assertEquals("sxbb", engine.getAdjacent("sx8z", TOP), "Top wall recursive boundary shift failed")
    }

    @Test
    fun `test boundary crossing - bottom wall`() {
        // '0' is bottom edge. Parent "sx8" -> "sx2". Child '0' wraps top to 'p'.
        assertEquals("sx2p", engine.getAdjacent("sx80", BOTTOM), "Bottom wall recursive boundary shift failed")
    }

    @Test
    fun `test exact encoding - origin snaps to north east`() {
        // 0.0, 0.0 mathematically snaps to the North/East quadrant (s0000)
        val centerHash = engine.encode(0.0, 0.0, 5)
        assertEquals(
            "s0000",
            centerHash,
            "Center hash at 0.0, 0.0 must strict-snap to the North-East quadrant due to >= boundary rules"
        )
    }

    @Test
    fun `test hemisphere wrap - crossing the Prime Meridian`() {
        // Hardcode the known origin center to isolate the getAdjacent logic
        val centerHash = "s0000"

        // Moving Left (West) crosses the Prime Meridian.
        val leftHash = engine.getAdjacent(centerHash, LEFT)
        assertEquals("ebpbp", leftHash, "Failed to wrap correctly across the Prime Meridian")
    }

    @Test
    fun `test hemisphere wrap - crossing the Equator`() {
        // Hardcode the known origin center to isolate the getAdjacent logic
        val centerHash = "s0000"

        // Moving Bottom (South) crosses the Equator.
        val bottomHash = engine.getAdjacent(centerHash, BOTTOM)
        assertEquals("kpbpb", bottomHash, "Failed to wrap correctly across the Equator")
    }

    @Test
    fun `test nine box generation rules`() {
        // Generate the 9-box grid for a generic point
        val grid = engine.getNineBoxGrid(42.0, 21.4, 5)

        // Rule 1: Must generate exactly 9 boxes
        assertEquals(9, grid.size, "Must return exactly 9 hashes")

        // Rule 2: All boxes must be strictly unique (no duplicate strings)
        val uniqueHashes = grid.toSet()
        assertEquals(9, uniqueHashes.size, "All 9 hashes must be strictly unique")

        // Rule 3: All boxes must retain the exact precision length
        assertTrue(grid.all { it.length == 5 }, "All neighbors must match center precision")

        // Rule 4: The original center must be included in the list
        assertTrue(grid.contains("srrny"), "The 9-box grid must contain the center coordinate")
    }
}