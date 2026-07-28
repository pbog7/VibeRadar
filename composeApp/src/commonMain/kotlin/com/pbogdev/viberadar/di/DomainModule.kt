package com.pbogdev.viberadar.di

import com.pbogdev.domain.usecase.AnonymousSignInUseCase
import com.pbogdev.domain.usecase.GetExamplesUseCase
import com.pbogdev.domain.usecase.GetNearbyBeaconsUseCase
import com.pbogdev.domain.usecase.GetTextEmbeddingUseCase
import com.pbogdev.domain.usecase.GetUserBeaconUseCase
import com.pbogdev.domain.usecase.SaveGeohashUseCase
import com.pbogdev.domain.usecase.SetUserBeaconUseCase
import com.pbogdev.domain.usecase.UploadBeaconUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val domainModule = module {
    factoryOf(::GetExamplesUseCase)
    factoryOf(::GetNearbyBeaconsUseCase)
    factoryOf(::SaveGeohashUseCase)
    factoryOf(::UploadBeaconUseCase)
    factoryOf(::GetTextEmbeddingUseCase)
    factoryOf(::AnonymousSignInUseCase)
    factoryOf(::GetUserBeaconUseCase)
    factoryOf(::SetUserBeaconUseCase)
}