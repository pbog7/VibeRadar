package com.pbogdev.domain.usecase.base

import com.pbogdev.domain.models.CustomResult

interface BaseUseCaseNoParams<out T> {

    suspend operator fun invoke(): CustomResult<T>
}