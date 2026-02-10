package com.pbogdev.viberadar.di

import com.pbogdev.data.di.dataModule
import com.pbogdev.homescreen.di.homeScreenModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(
        dataModule,
        domainModule,
        homeScreenModule
        // add other modules here
    )
}