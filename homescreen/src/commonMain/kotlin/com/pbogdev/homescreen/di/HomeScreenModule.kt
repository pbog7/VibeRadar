package com.pbogdev.homescreen.di

import com.pbogdev.homescreen.HomeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val homeScreenModule = module {
    viewModelOf(::HomeViewModel)
}
