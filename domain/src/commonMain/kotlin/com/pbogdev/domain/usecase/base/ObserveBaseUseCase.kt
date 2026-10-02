package com.pbogdev.domain.usecase.base

import kotlinx.coroutines.flow.Flow

interface ObserveBaseUseCase<out T, in Params> {

     operator fun invoke(params:Params): Flow<T>
}