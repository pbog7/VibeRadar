package com.pbogdev.data.network.auth

import com.google.firebase.auth.FirebaseAuth
import com.pbogdev.core.dispatcherProvider.DispatcherProvider
import com.pbogdev.core.utils.safeResult
import com.pbogdev.domain.auth.AnonymousAuthenticator
import com.pbogdev.domain.models.CustomError
import com.pbogdev.domain.models.CustomResult
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

internal class AndroidAnonymousAuthenticator(
    private val dispatcherProvider: DispatcherProvider
) : AnonymousAuthenticator {

    private val auth = FirebaseAuth.getInstance()

    override suspend fun signInAnonymously(): CustomResult<Unit> {
        return withContext(dispatcherProvider.io) {
            safeResult(mapException = {
                CustomError.SignInError()
            }) {
                suspendCancellableCoroutine { continuation ->
                    auth.signInAnonymously().addOnCompleteListener { task ->
                        if (continuation.isActive) {
                            if (task.isSuccessful && task.result?.user?.uid != null) {
                                continuation.resume(CustomResult.Success(Unit))
                            } else {
                                continuation.resume(CustomResult.Failure(CustomError.SignInError()))
                            }
                        }
                    }
                }
            }
        }
    }

    override fun getCurrentUid(): String? {
        return auth.currentUser?.uid
    }

    override suspend fun getIdToken(forceRefresh: Boolean): CustomResult<String> {
        return withContext(dispatcherProvider.io) {
            safeResult(mapException = {
                CustomError.GetTokenError()
            }) {
                val user = auth.currentUser
                    ?: return@withContext CustomResult.Failure(CustomError.UserNotLoggedIn())

                suspendCancellableCoroutine { continuation ->
                    user.getIdToken(forceRefresh).addOnCompleteListener { task ->
                        if (continuation.isActive) {
                            if (task.isSuccessful && task.result?.token != null) {
                                continuation.resume(CustomResult.Success(task.result.token!!))
                            } else {
                                continuation.resume(CustomResult.Failure(CustomError.GetTokenError()))
                            }
                        }
                    }
                }
            }
        }
    }
}