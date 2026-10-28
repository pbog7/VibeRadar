package com.pbogdev.data.firestore.wrapperModels

import kotlinx.serialization.Serializable


@Serializable
 data class QueryRequest(
    val structuredQuery: StructuredQuery
)

