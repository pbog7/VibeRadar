package com.pbogdev.data.network

import com.pbogdev.data.firestore.models.StringValue
import com.pbogdev.data.firestore.models.TimestampValue
import com.pbogdev.data.firestore.queryFirestore
import com.pbogdev.data.network.dto.EncryptedBeaconDTO
import com.pbogdev.data.network.dto.ExampleDto
import com.pbogdev.data.network.response.ExampleResponse
import com.pbogdev.viberadar.data.BuildKonfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
private data class NetworkEncryptedBeacon(
    @SerialName("payload") val payload: StringValue,
    @SerialName("geohash") val geohash: StringValue,
    @SerialName("expiresAt") val expiresAt: TimestampValue
)

class ApiServiceImpl(private val httpClient: HttpClient) : ApiService {
    // temporary before Koin is implemented, after that this will be injected in the constructor
    override suspend fun getExamples(): ExampleResponse {
        return ExampleResponse(ExampleDto(httpClient.get("posts/1").body()))
    }

    override suspend fun getBeaconsByGeohashes(geohashes: Set<String>): List<EncryptedBeaconDTO> {
        val rawDocuments =  httpClient.queryFirestore<NetworkEncryptedBeacon>(BuildKonfig.FIREBASE_PROJECT_ID, BEACON_COLLECTION_ID) {
            whereIn("geohash", geohashes)
        }
        return rawDocuments.map { doc ->
            EncryptedBeaconDTO(
                documentId = doc.id,
                payloadBase64 = doc.data.payload.stringValue,
                geohash = doc.data.geohash.stringValue,
                expiresAt = Instant.parse(doc.data.expiresAt.timeStampValue).epochSeconds
            )
        }
    }

}