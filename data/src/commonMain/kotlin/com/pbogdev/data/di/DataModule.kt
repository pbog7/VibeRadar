package com.pbogdev.data.di

import com.pbogdev.data.network.ApiService
import com.pbogdev.data.network.ApiServiceImpl
import com.pbogdev.data.network.httpLogger
import com.pbogdev.data.repository.ExampleRepositoryImpl
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
import org.koin.dsl.module


val dataModule = module {
    single {
        HttpClient {
            install(DefaultRequest) {
                url(BuildKonfig.BASE_URL)
            }
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                    }
                )
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
    single<ApiService> { ApiServiceImpl(get()) }
    single<ExampleRepository> { ExampleRepositoryImpl(get()) }
}
