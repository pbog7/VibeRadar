package com.pbogdev.data.di

import androidx.lifecycle.get
import com.pbogdev.data.local.LocationManagerImpl
import com.pbogdev.data.location.GeohashEngine
import com.pbogdev.data.location.GeohashEngineImpl
import com.pbogdev.domain.LocationManager
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal expect val platformDatastoreModule: Module

val localDataModule: Module = module {

    includes(platformDatastoreModule)
    singleOf(::GeohashEngineImpl) bind GeohashEngine::class
    singleOf(::LocationManagerImpl) bind LocationManager::class
}