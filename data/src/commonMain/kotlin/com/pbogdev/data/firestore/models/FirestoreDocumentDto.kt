package com.pbogdev.data.firestore.models

import kotlinx.serialization.Serializable

@Serializable
data class FirestoreDocumentDto<T>(
    val name:String?,
    val fields:T
)