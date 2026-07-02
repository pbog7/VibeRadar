package com.pbogdev.data.firestore.wrapperModels

import kotlinx.serialization.Serializable

@Serializable
 data class Filter(
    val fieldFilter: FieldFilter
)