package com.pbogdev.data.firestore.wrapperModels

import kotlinx.serialization.Serializable

@Serializable
 data class FieldReference(
    val fieldPath: String
)