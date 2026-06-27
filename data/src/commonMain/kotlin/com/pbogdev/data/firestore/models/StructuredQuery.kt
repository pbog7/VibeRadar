package com.pbogdev.data.firestore.models

import kotlinx.serialization.Serializable

@Serializable
 data class StructuredQuery(
    val from: List<CollectionSelector>,
    val where: Filter
)