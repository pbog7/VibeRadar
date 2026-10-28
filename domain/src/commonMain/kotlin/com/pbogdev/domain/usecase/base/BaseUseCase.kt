package com.pbogdev.domain.usecase.base

import com.pbogdev.domain.models.CustomResult

interface BaseUseCase<out T, in Params> {

    suspend operator fun invoke(params:Params): CustomResult<T>
}