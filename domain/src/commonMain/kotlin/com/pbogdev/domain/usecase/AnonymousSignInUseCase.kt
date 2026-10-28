package com.pbogdev.domain.usecase

import com.pbogdev.domain.auth.AnonymousAuthenticator
import com.pbogdev.domain.models.CustomResult
import com.pbogdev.domain.usecase.base.BaseUseCaseNoParams

class AnonymousSignInUseCase(private val anonymousAuthenticator: AnonymousAuthenticator) :
    BaseUseCaseNoParams<Unit> {
    override suspend fun invoke(): CustomResult<Unit> =
        anonymousAuthenticator.signInAnonymously()
}