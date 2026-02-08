package com.pbogdev.data.network

import com.pbogdev.data.network.dto.ExampleDto
import com.pbogdev.data.network.response.ExampleResponse
import com.pbogdev.viberadar.BuildKonfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class ApiServiceImpl() : ApiService {
    // temporary before Koin is implemented, after that this will be injected in the constructor
    val httpClient = HttpClient {
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
    override suspend fun getExamples(): ExampleResponse {
        return ExampleResponse(ExampleDto(httpClient.get("posts/1").body()))
    }

}