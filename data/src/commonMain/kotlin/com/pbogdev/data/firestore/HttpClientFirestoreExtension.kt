package com.pbogdev.data.firestore

import com.pbogdev.core.dispatcherProvider.DispatcherProvider
import com.pbogdev.data.firestore.wrapperModels.FirestoreDocument
import com.pbogdev.data.firestore.wrapperModels.FirestoreDocumentDto
import com.pbogdev.data.firestore.wrapperModels.QueryResponseItem
import com.pbogdev.data.utils.appJson
import com.pbogdev.data.utils.safeDecodeFromString
import com.pbogdev.data.utils.safeEncodeToString
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
    dispatcherProvider: DispatcherProvider,
    crossinline buildQuery: FirestoreQueryBuilder.() -> Unit
): List<FirestoreDocument<T>> { // <-- Look how clean this return type is now!

    val builder = FirestoreQueryBuilder(collection)
    builder.buildQuery()

    val requestBody =
        appJson.safeEncodeToString(dispatcherProvider = dispatcherProvider, value = builder.build())

    val url =
        "https://firestore.googleapis.com/v1/projects/$projectId/databases/(default)/documents:runQuery"

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
    val parsedList = appJson.safeDecodeFromString<List<QueryResponseItem<T>>>(
        dispatcherProvider = dispatcherProvider,
        string = responseText
    )

    // 4. Return the pure, clean list. No wrappers.
    return parsedList.mapNotNull { it.document }.map { doc ->
        FirestoreDocument(
            id = doc.name?.substringAfterLast("/") ?: "",
            data = doc.fields
        )
    }
}

suspend inline fun <reified T> HttpClient.createFirestoreDocument(
    projectId: String,
    collection: String,
    documentFields: T
) {
    val url =
        "https://firestore.googleapis.com/v1/projects/$projectId/databases/(default)/documents/$collection"

    val requestWrapper = FirestoreDocumentDto(
        name = null,
        fields = documentFields
    )

    val response = this.post(url) {
        contentType(ContentType.Application.Json)
        setBody(requestWrapper)
    }

    if (!response.status.isSuccess()) {
        val responseText = response.bodyAsText()
        throw IllegalStateException("Firestore network error: ${response.status} - $responseText")
    }
}