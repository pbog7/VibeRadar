package com.pbogdev.data.firestore.models

import kotlinx.serialization.Serializable


@Serializable
 data class QueryRequest(
    val structuredQuery: StructuredQuery
)

