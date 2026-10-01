package com.pbogdev.domain.usecase.base

import kotlinx.coroutines.flow.Flow

interface ObserveBaseUseCaseNoParams<out T> {

     operator fun invoke(): Flow<T>
}