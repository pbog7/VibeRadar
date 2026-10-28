package com.pbogdev.data.firestore.wrapperModels

import kotlinx.serialization.Serializable

@Serializable
 data class CollectionSelector(
    val collectionId: String
)