package com.pbogdev.aimatchmakingengine

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.pbogdev.domain.textEmbedder.VibeTextEmbedder
import com.pbogdev.testcore.TestDispatcherProvider
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AndroidVibeTextEmbedderTest : VibeTextEmbedderTest() {


    override fun getVibeTextEmbedder(): VibeTextEmbedder =
        AndroidVibeTextEmbedder(context = InstrumentationRegistry.getInstrumentation().targetContext, dispatcherProvider = TestDispatcherProvider())

}