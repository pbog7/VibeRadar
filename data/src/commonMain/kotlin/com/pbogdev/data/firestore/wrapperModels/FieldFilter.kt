package com.pbogdev.data.firestore.wrapperModels

import kotlinx.serialization.Serializable

@Serializable
 data class FieldFilter(
    val field: FieldReference,
    val op: String,
    val value: ArrayValueWrapper
)