package com.pbogdev.data.firestore.wrapperModels

import kotlinx.serialization.Serializable

@Serializable
data class FirestoreDocument<T>(
    val id:String,
    val data:T
)