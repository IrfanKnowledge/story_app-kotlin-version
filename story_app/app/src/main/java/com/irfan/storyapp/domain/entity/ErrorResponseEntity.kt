package com.irfan.storyapp.domain.entity

import com.irfan.storyapp.common.SingleEvent

data class ErrorResponseEntity(
    val error: Boolean? = null,
    val message: SingleEvent<String>? = null,
)
