package com.pbogdev.viberadar.di

import com.pbogdev.domain.usecase.GetExamplesUseCase
import org.koin.dsl.module

val domainModule = module {
    single { GetExamplesUseCase(get()) }
}