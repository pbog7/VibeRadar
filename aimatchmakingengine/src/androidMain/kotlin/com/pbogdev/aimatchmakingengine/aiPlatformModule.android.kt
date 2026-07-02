package com.pbogdev.aimatchmakingengine

import com.pbogdev.domain.textEmbedder.VibeTextEmbedder
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val aiPlatformModule = module {
    single<VibeTextEmbedder> { AndroidVibeTextEmbedder(context = androidContext(), dispatcherProvider = get()) }
}