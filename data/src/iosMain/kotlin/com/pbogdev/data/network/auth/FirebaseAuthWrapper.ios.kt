package com.pbogdev.data.network.auth

import cocoapods.FirebaseAuth.FIRAuth
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.suspendCancellableCoroutine

@OptIn(ExperimentalForeignApi::class)
actual class FirebaseAuthWrapper actual constructor() {
    private val auth = FIRAuth.auth()

    actual suspend fun signInAnonymously(): String? = suspendCancellableCoroutine { continuation ->
        auth.signInAnonymouslyWithCompletion { result, error ->
            if (continuation.isActive) {
                // If Apple returns no error, we grab the UID. Otherwise, null.
                val uid = if (error == null) result?.user()?.uid() else null
                continuation.resumeWith(Result.success(uid))
            }
        }
    }

    actual fun getCurrentUid(): String? {
        return auth.currentUser()?.uid()
    }

    actual suspend fun getIdToken(): String? = suspendCancellableCoroutine { continuation ->
        val user = auth.currentUser()
        if (user == null) {
            continuation.resumeWith(Result.success(null))
            return@suspendCancellableCoroutine
        }

        // false = use cached token if valid; true = force network refresh
        user.getIDTokenForcingRefresh(false) { token, error ->
            if (continuation.isActive) {
                // If Apple returns no error, we grab the JWT. Otherwise, null.
                val finalToken = if (error == null) token else null
                continuation.resumeWith(Result.success(finalToken))
            }
        }
    }
}