
package com.pbogdev.domain.usecase

import com.pbogdev.domain.models.CustomResult
import com.pbogdev.domain.models.ExampleModel
import com.pbogdev.domain.repository.ExampleRepository

class GetExamplesUseCase(private val exampleRepository: ExampleRepository): BaseUseCaseNoParams<List<ExampleModel>> {
    override suspend fun invoke(): CustomResult<List<ExampleModel>> =
       exampleRepository.getExamples()
}
