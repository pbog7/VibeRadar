package com.pbogdev.data.network

import com.pbogdev.data.network.dto.ExampleDto
import com.pbogdev.data.network.response.ExampleResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class ApiServiceImpl(private val httpClient: HttpClient) : ApiService {
    // temporary before Koin is implemented, after that this will be injected in the constructor
    override suspend fun getExamples(): ExampleResponse {
        return ExampleResponse(ExampleDto(httpClient.get("posts/1").body()))
    }

}