package com.pbogdev.sharedui.components.feedbackSheet

data class FeedbackConfig(
    val feedbackType: FeedbackType,
    val title: String,
    val message: String,
    val primaryActionLabel: String? = null,
    val onPrimaryAction: (() -> Unit)? = null
)