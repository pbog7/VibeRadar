package com.pbogdev.data.di

import com.pbogdev.data.crypto.CryptographyEngine
import com.pbogdev.data.crypto.CryptographyEngineImpl
import com.pbogdev.data.dispatcherProvider.AppDispatcherProvider
import com.pbogdev.data.dispatcherProvider.DispatcherProvider
import com.pbogdev.data.network.ApiService
import com.pbogdev.data.network.ApiServiceImpl
import com.pbogdev.data.network.appJson
import com.pbogdev.data.network.httpLogger
import com.pbogdev.data.repository.BeaconRepositoryImpl
import com.pbogdev.data.repository.ExampleRepositoryImpl
import com.pbogdev.domain.repository.BeaconRepository
import com.pbogdev.domain.repository.ExampleRepository
import com.pbogdev.viberadar.data.BuildKonfig
import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module


val dataModule = module {
    includes(localDataModule)
    single {
        HttpClient {
            install(DefaultRequest) {
                url(BuildKonfig.BASE_URL)
            }
            install(ContentNegotiation) {
                json(appJson)
            }
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        httpLogger.i { message }
                    }
                }
                level = LogLevel.ALL
            }

        }
    }
    singleOf(::AppDispatcherProvider) bind DispatcherProvider::class
    singleOf(::ApiServiceImpl) bind ApiService::class
    singleOf(::ExampleRepositoryImpl) bind ExampleRepository::class
    single<CryptographyEngine> { CryptographyEngineImpl() }
    singleOf (::BeaconRepositoryImpl) bind BeaconRepository::class
}
