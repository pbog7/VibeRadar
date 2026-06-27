package com.pbogdev.viberadar.di

import com.pbogdev.domain.usecase.GetExamplesUseCase
import com.pbogdev.domain.usecase.GetNearbyBeaconsUseCase
import com.pbogdev.domain.usecase.SaveGeohashUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val domainModule = module {
    factoryOf(::GetExamplesUseCase)
    factoryOf(::GetNearbyBeaconsUseCase)
    factoryOf(::SaveGeohashUseCase)
}