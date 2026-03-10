package com.pbogdev.aimatchmakingengine

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AndroidVibeTextEmbedderTest : VibeTextEmbedderTest() {


    override fun getVibeTextEmbedder(): VibeTextEmbedder =
        AndroidVibeTextEmbedder(InstrumentationRegistry.getInstrumentation().targetContext)

    @org.junit.After
    fun waitForBackgroundThreads() {
        // This runs on Android, so we CAN use Java's Thread.sleep!
        // It pauses the "Test Finished" signal for 1 second.
        Thread.sleep(1000)
    }
}