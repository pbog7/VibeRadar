package com.pbogdev.aimatchmakingengine

import com.pbogdev.domain.matchmaking.VibeMatchmaker
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal expect val aiPlatformModule: Module
val aiMatchmakingModule = module {
    includes(aiPlatformModule)
    singleOf(::DefaultVibeMatchmaker) bind VibeMatchmaker::class
}

