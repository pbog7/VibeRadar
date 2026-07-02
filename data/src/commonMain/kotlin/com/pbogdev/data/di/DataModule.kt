package com.pbogdev.data.di


import com.pbogdev.data.crypto.CryptographyEngine
import com.pbogdev.data.crypto.CryptographyEngineImpl
import com.pbogdev.data.local.LocationManagerImpl
import com.pbogdev.data.location.GeohashEngine
import com.pbogdev.data.location.GeohashEngineImpl
import com.pbogdev.data.network.ApiService
import com.pbogdev.data.network.ApiServiceImpl
import com.pbogdev.data.network.httpLogger
import com.pbogdev.data.repository.BeaconRepositoryImpl
import com.pbogdev.data.repository.ExampleRepositoryImpl
import com.pbogdev.data.utils.appJson
import com.pbogdev.domain.LocationManager
import com.pbogdev.domain.auth.AnonymousAuthenticator
import com.pbogdev.domain.models.CustomResult
import com.pbogdev.domain.repository.BeaconRepository
import com.pbogdev.domain.repository.ExampleRepository
import com.pbogdev.viberadar.data.BuildKonfig
import dev.whyoleg.cryptography.CryptographyProvider
import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal expect val platformDataModule: Module

val dataModule = module {
    includes(platformDataModule)
    single {

        HttpClient {
            install(DefaultRequest) {
                url(BuildKonfig.BASE_URL)
            }
            install(ContentNegotiation) {
                json(appJson)
            }
            install(plugin = Auth)
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        httpLogger.i { message }
                    }
                }
                level = LogLevel.ALL
            }
            install(Auth) {
                val authenticator: AnonymousAuthenticator = get()
                bearer {
                    loadTokens {
                        val result = authenticator.getIdToken(forceRefresh = false)
                        if (result is CustomResult.Success) {
                            BearerTokens(result.data, "")
                        } else {
                            null
                        }
                    }
                    refreshTokens {
                        val result = authenticator.getIdToken(forceRefresh = true)
                        if (result is CustomResult.Success) {
                            BearerTokens(result.data, "")
                        } else {
                            null
                        }
                    }
                    sendWithoutRequest { request ->
                        true
                    }
                }
            }
        }
    }
    singleOf(::GeohashEngineImpl) bind GeohashEngine::class
    singleOf(::LocationManagerImpl) bind LocationManager::class
    singleOf(::ApiServiceImpl) bind ApiService::class
    singleOf(::ExampleRepositoryImpl) bind ExampleRepository::class
    single<CryptographyEngine> {
        CryptographyEngineImpl(
            provider = CryptographyProvider.Default,
            dispatcherProvider = get()
        )
    }
    singleOf(::BeaconRepositoryImpl) bind BeaconRepository::class
}
