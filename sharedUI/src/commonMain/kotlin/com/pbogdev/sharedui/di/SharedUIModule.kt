package com.pbogdev.sharedui.di

import org.koin.core.module.Module
import org.koin.dsl.module

internal expect val platformSharedUIModule: Module

val sharedUIModule = module {
    includes(platformSharedUIModule)
}