package com.pbogdev.data

import com.pbogdev.data.network.dto.ExampleDto
import com.pbogdev.domain.models.ExampleModel


fun ExampleDto.toExampleModel() = ExampleModel(
    example = example
)

