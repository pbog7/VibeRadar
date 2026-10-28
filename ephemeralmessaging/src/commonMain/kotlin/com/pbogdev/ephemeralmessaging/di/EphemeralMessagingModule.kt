package com.pbogdev.ephemeralmessaging.di

import com.pbogdev.ephemeralmessaging.messagingScreen.EphemeralMessagingViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val ephemeralMessagingModule = module {
    viewModelOf(::EphemeralMessagingViewModel)
}