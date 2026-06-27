package com.pbogdev.data.firestore.models

import kotlinx.serialization.Serializable

@Serializable
 data class FieldReference(
    val fieldPath: String
)