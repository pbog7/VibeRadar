package com.pbogdev.sharedui.di

import com.pbogdev.sharedui.AndroidSystemActionLauncher
import com.pbogdev.sharedui.components.systemActionLauncher.SystemActionLauncher
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformSharedUIModule: Module = module {
    single<SystemActionLauncher> { AndroidSystemActionLauncher(context = get()) }
}