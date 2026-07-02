package com.pbogdev.data.network

import com.pbogdev.core.dispatcherProvider.DispatcherProvider
import com.pbogdev.data.firestore.createFirestoreDocument
import com.pbogdev.data.firestore.documentFieldsModels.EncryptedBeaconFields
import com.pbogdev.data.firestore.queryFirestore
import com.pbogdev.data.network.dto.EncryptedBeaconDTO
import com.pbogdev.data.network.dto.ExampleDto
import com.pbogdev.data.network.response.ExampleResponse
import com.pbogdev.viberadar.data.BuildKonfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlin.time.Instant


class ApiServiceImpl(
    private val httpClient: HttpClient,
    private val dispatcherProvider: DispatcherProvider
) : ApiService {

    override suspend fun getExamples(): ExampleResponse {
        return ExampleResponse(ExampleDto(httpClient.get("posts/1").body()))
    }

    override suspend fun getBeaconsByGeohashes(geohashes: Set<String>): List<EncryptedBeaconDTO> {
        val rawDocuments = httpClient.queryFirestore<EncryptedBeaconFields>(
            dispatcherProvider = dispatcherProvider,
            projectId = BuildKonfig.FIREBASE_PROJECT_ID,
            collection = BEACON_COLLECTION_ID
        ) {
            whereIn("geohash", geohashes)
        }
        return rawDocuments.map { doc ->
            EncryptedBeaconDTO(
                documentId = doc.id,
                payloadBase64 = doc.data.payload.stringValue,
                geohash = doc.data.geohash.stringValue,
                expiresAtEpochMillis = Instant.parse(doc.data.expiresAt.timeStampValue).toEpochMilliseconds(),
                senderUid = doc.data.senderUid.stringValue
            )
        }
    }

    override suspend fun uploadBeacon(fields: EncryptedBeaconFields) {
        httpClient.createFirestoreDocument(
            projectId = BuildKonfig.FIREBASE_PROJECT_ID,
            collection = BEACON_COLLECTION_ID,
            documentFields = fields
        )
    }

}