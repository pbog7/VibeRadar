package com.pbogdev.data.network

import com.pbogdev.data.network.dto.EncryptedBeaconDTO
import com.pbogdev.data.network.response.ExampleResponse


interface ApiService {
    suspend fun getExamples(): ExampleResponse
    suspend fun getBeaconsByGeohashes(geohashes: Set<String>): List<EncryptedBeaconDTO>

}