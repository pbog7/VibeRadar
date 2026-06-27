package com.pbogdev.data.firestore.models

import kotlinx.serialization.Serializable

@Serializable
 data class QueryResponseItem<T>(
    val document: FirestoreDocumentDto<T>? = null
)