package com.pbogdev.data.firestore.wrapperModels

import kotlinx.serialization.Serializable

@Serializable
data class ArrayValue(
    val values: List<StringValue>
)