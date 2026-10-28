package com.pbogdev.data.repository

import com.pbogdev.data.network.ApiService
import com.pbogdev.data.toExampleModel
import com.pbogdev.domain.models.CustomError
import com.pbogdev.domain.models.CustomResult
import com.pbogdev.domain.models.ExampleModel
import com.pbogdev.domain.repository.ExampleRepository

class ExampleRepositoryImpl(private val apiService: ApiService): ExampleRepository {
    override suspend fun getExamples(): CustomResult<List<ExampleModel>> {
        return try {
            val response = apiService.getExamples()
            CustomResult.Success(listOf(response.exampleDto.toExampleModel()))
        } catch (e: Exception){
            println("Exception is $e")
            CustomResult.Failure(CustomError.UnknownError("Exception is ${e.message}"))
        }
    }

}