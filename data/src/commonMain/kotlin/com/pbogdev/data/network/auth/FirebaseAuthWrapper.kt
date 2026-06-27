package com.pbogdev.data.network.auth

// shared/src/commonMain/kotlin/com/viberadar/beacon/auth/FirebaseAuthWrapper.kt

expect class FirebaseAuthWrapper() {
    suspend fun signInAnonymously(): String?
    fun getCurrentUid(): String?
    suspend fun getIdToken(): String?
}