package com.pbogdev.domain.models

sealed class CustomResult<out T> {
    data class Success<T>(val data:T): CustomResult<T>()
    data class Failure(val error: CustomError): CustomResult<Nothing>()
}