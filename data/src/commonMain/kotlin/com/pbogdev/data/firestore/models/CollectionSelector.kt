package com.pbogdev.data.firestore.models

import kotlinx.serialization.Serializable

@Serializable
 data class CollectionSelector(
    val collectionId: String
)