package com.pbogdev.aimatchmakingengine

import org.koin.dsl.module

actual val aiPlatformModule = module {
    single<VibeTextEmbedder> {
        IOSVibeTextEmbedder()
    }
}
