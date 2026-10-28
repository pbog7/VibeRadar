package com.pbogdev.core.di

import com.pbogdev.core.dispatcherProvider.AppDispatcherProvider
import com.pbogdev.core.dispatcherProvider.DispatcherProvider
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreModule = module {
    singleOf(::AppDispatcherProvider) bind DispatcherProvider::class
}