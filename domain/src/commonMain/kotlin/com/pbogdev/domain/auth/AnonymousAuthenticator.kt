package com.pbogdev.domain.auth


import com.pbogdev.domain.models.CustomResult

interface AnonymousAuthenticator {
    suspend fun signInAnonymously(): CustomResult<Unit>
    fun getCurrentUid(): String?
    suspend fun getIdToken(forceRefresh: Boolean = false): CustomResult<String>
}
