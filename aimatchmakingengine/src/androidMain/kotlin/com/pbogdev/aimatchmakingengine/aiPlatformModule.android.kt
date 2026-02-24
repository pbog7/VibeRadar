package com.pbogdev.aimatchmakingengine

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val aiPlatformModule = module {
    single<VibeTextEmbedder> { AndroidVibeTextEmbedder(androidContext()) }
}