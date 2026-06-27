package com.pbogdev.data.firestore

import com.pbogdev.data.firestore.models.FirestoreDocument
import com.pbogdev.data.firestore.models.QueryResponseItem
import com.pbogdev.data.network.appJson
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*


/**
 * A lightweight extension to query Firestore using a standard Ktor HttpClient.
 * Throws an Exception if the network fails or Google returns an error.
 */
suspend inline fun <reified T> HttpClient.queryFirestore(
    projectId: String,
    collection: String,
    crossinline buildQuery: FirestoreQueryBuilder.() -> Unit
): List<FirestoreDocument<T>> { // <-- Look how clean this return type is now!

    val builder = FirestoreQueryBuilder(collection)
    builder.buildQuery()

    val requestBody = appJson.encodeToString(builder.build())

    val url = "https://firestore.googleapis.com/v1/projects/$projectId/databases/(default)/documents:runQuery"

    // 1. If there is no internet, Ktor natively throws an IOException here.
    val response = this.post(url) {
        contentType(ContentType.Application.Json)
        setBody(requestBody)
    }

    val responseText = response.bodyAsText()

    // 2. If Google sends back a 400 or 500 error, we throw our own exception.
    if (!response.status.isSuccess()) {
        throw IllegalStateException("Firestore network error: ${response.status} - $responseText")
    }

    // 3. If parsing fails, kotlinx.serialization throws a SerializationException here.
    val parsedList = appJson.decodeFromString<List<QueryResponseItem<T>>>(responseText)

    // 4. Return the pure, clean list. No wrappers.
    return parsedList.mapNotNull { it.document }.map { doc ->
        FirestoreDocument(
            id = doc.name?.substringAfterLast("/") ?: "",
            data = doc.fields
        )
    }
}