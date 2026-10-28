package com.pbogdev.data.firestore.wrapperModels

import kotlinx.serialization.Serializable

@Serializable
 data class StructuredQuery(
    val from: List<CollectionSelector>,
    val where: Filter
)