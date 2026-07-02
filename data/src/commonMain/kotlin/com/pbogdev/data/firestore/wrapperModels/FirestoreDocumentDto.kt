package com.pbogdev.data.firestore.wrapperModels

import kotlinx.serialization.Serializable

@Serializable
data class FirestoreDocumentDto<T>(
    val name:String?,
    val fields:T
)