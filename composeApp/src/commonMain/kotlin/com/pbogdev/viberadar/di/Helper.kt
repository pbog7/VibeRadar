package com.pbogdev.viberadar.di

import com.pbogdev.aimatchmakingengine.aiMatchmakingModule
import com.pbogdev.core.di.coreModule
import com.pbogdev.data.di.dataModule
import com.pbogdev.homescreen.di.homeScreenModule
import com.pbogdev.sharedui.di.sharedUIModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(
        dataModule,
        domainModule,
        homeScreenModule,
        aiMatchmakingModule,
        coreModule,
        sharedUIModule
        // add other modules here
    )
}