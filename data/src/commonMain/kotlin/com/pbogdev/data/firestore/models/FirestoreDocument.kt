package com.pbogdev.data.firestore.models

import kotlinx.serialization.Serializable

@Serializable
data class FirestoreDocument<T>(
    val id:String,
    val data:T
)