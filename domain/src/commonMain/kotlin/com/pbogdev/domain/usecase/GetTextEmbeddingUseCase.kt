package com.pbogdev.domain.usecase

import com.pbogdev.domain.models.CustomResult
import com.pbogdev.domain.textEmbedder.VibeTextEmbedder
import com.pbogdev.domain.usecase.base.BaseUseCase

class GetTextEmbeddingUseCase(private val textEmbedder: VibeTextEmbedder) :
    BaseUseCase<FloatArray, GetTextEmbeddingUseCase.Params> {

    override suspend fun invoke(params: Params): CustomResult<FloatArray> =
        textEmbedder.embed(
            params.text
        )

    data class Params(val text: String)
}