package com.pbogdev.domain.repository

import com.pbogdev.domain.models.CustomResult
import com.pbogdev.domain.models.ExampleModel

interface ExampleRepository {
    suspend fun getExamples(): CustomResult<List<ExampleModel>>
}
