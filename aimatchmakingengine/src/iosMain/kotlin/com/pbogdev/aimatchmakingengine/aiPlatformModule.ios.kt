package com.pbogdev.aimatchmakingengine

import com.pbogdev.domain.textEmbedder.VibeTextEmbedder
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val aiPlatformModule = module {
    singleOf(::IOSVibeTextEmbedder) bind VibeTextEmbedder::class
}
