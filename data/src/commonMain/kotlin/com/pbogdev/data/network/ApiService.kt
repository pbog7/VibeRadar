package com.pbogdev.data.network

import com.pbogdev.data.firestore.documentFieldsModels.EncryptedBeaconFields
import com.pbogdev.data.firestore.documentFieldsModels.EncryptedEphemeralMessageFields
import com.pbogdev.data.network.dto.EncryptedBeaconDTO
import com.pbogdev.data.network.dto.EncryptedMessageDTO
import com.pbogdev.data.network.response.ExampleResponse


interface ApiService {
    suspend fun getExamples(): ExampleResponse
    suspend fun getBeaconsByGeohashes(geohashes: Set<String>): List<EncryptedBeaconDTO>

    suspend fun uploadBeacon(fields: EncryptedBeaconFields, beaconId: String)

    suspend fun uploadEphemeralMessage(fields: EncryptedEphemeralMessageFields, messageId: String)

    suspend fun getMessages(userId: String):List<EncryptedMessageDTO>

    suspend fun deleteMessage(documentId: String)

    suspend fun deleteBeacon(documentId: String)

}