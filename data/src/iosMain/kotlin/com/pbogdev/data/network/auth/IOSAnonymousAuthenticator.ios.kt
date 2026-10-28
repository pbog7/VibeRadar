package com.pbogdev.data.network.auth

import cocoapods.FirebaseAuth.FIRAuth
import com.pbogdev.core.dispatcherProvider.DispatcherProvider
import com.pbogdev.core.utils.safeResult
import com.pbogdev.domain.auth.AnonymousAuthenticator
import com.pbogdev.domain.models.CustomError
import com.pbogdev.domain.models.CustomResult
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

@OptIn(ExperimentalForeignApi::class)
internal class IOSAnonymousAuthenticator(
    private val dispatcherProvider: DispatcherProvider
) : AnonymousAuthenticator {

    private val auth = FIRAuth.auth()

    override suspend fun signInAnonymously(): CustomResult<Unit> {
        return withContext(dispatcherProvider.io) {
            safeResult(mapException = {
                CustomError.SignInError()
            }) {
                suspendCancellableCoroutine { continuation ->
                    auth.signInAnonymouslyWithCompletion { result, error ->
                        if (continuation.isActive) {
                            val uid = result?.user()?.uid()
                            if (error == null && uid != null) {
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
        return auth.currentUser()?.uid()
    }

    override suspend fun getIdToken(forceRefresh: Boolean): CustomResult<String> {
        return withContext(dispatcherProvider.io) {
            safeResult(mapException = {
                CustomError.GetTokenError()
            }) {
                val user = auth.currentUser()
                    ?: return@withContext CustomResult.Failure(CustomError.UserNotLoggedIn())

                suspendCancellableCoroutine { continuation ->
                    user.getIDTokenForcingRefresh(forceRefresh) { token, error ->
                        if (continuation.isActive) {
                            if (error == null && token != null) {
                                continuation.resume(CustomResult.Success(token))
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