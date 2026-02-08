package com.pbogdev.data.network

import com.pbogdev.data.network.response.ExampleResponse


interface ApiService {
    suspend fun getExamples(): ExampleResponse

}