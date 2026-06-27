package com.pbogdev.data.network.auth

// shared/src/androidMain/kotlin/com/viberadar/beacon/auth/FirebaseAuthWrapper.kt

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.suspendCancellableCoroutine

actual class FirebaseAuthWrapper actual constructor() {
    private val auth = FirebaseAuth.getInstance()

    actual suspend fun signInAnonymously(): String? = suspendCancellableCoroutine { continuation ->
        auth.signInAnonymously().addOnCompleteListener { task ->
            if (continuation.isActive) {
                val uid = if (task.isSuccessful) task.result?.user?.uid else null
                continuation.resumeWith(Result.success(uid))
            }
        }
    }

    actual fun getCurrentUid(): String? {
        return auth.currentUser?.uid
    }

    actual suspend fun getIdToken(): String? = suspendCancellableCoroutine { continuation ->
        val user = auth.currentUser
        if (user == null) {
            continuation.resumeWith(Result.success(null))
            return@suspendCancellableCoroutine
        }

        user.getIdToken(false).addOnCompleteListener { task ->
            if (continuation.isActive) {
                val token = if (task.isSuccessful) task.result?.token else null
                continuation.resumeWith(Result.success(token))
            }
        }
    }
}