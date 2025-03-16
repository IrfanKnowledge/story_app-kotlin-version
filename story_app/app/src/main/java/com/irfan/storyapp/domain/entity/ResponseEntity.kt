package com.irfan.storyapp.domain.entity

import com.irfan.storyapp.common.SingleEvent

data class ResponseEntity<out T>(
    val error: Boolean? = null,
    val message: SingleEvent<String>? = null,
    val data: T? = null,
)
