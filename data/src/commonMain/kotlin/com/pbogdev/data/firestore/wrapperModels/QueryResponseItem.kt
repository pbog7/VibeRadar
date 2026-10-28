package com.pbogdev.data.firestore.wrapperModels

import kotlinx.serialization.Serializable

@Serializable
 data class QueryResponseItem<T>(
    val document: FirestoreDocumentDto<T>? = null
)