package com.pbogdev.aimatchmakingengine

import com.pbogdev.aimatchmakingengine.VibeTextEmbedder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

abstract class VibeTextEmbedderTest {

    abstract fun getVibeTextEmbedder(): VibeTextEmbedder

    @Test
    fun testInitialization() = runTest {
        val vibeTextEmbedder = getVibeTextEmbedder()

        vibeTextEmbedder.initialize()
        assertTrue(vibeTextEmbedder.isInitialized)
    }

    @Test
    fun testEmbed() = runTest {
        val vibeTextEmbedder = getVibeTextEmbedder()
        val result = vibeTextEmbedder.embed("I want to go hiking")
        assertNotNull(result)
        assertEquals(512,result.size)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testClose() = runTest {
        val vibeTextEmbedder = getVibeTextEmbedder()
        vibeTextEmbedder.initialize()
        vibeTextEmbedder.close()
        assertFalse(vibeTextEmbedder.isInitialized)
    }
}