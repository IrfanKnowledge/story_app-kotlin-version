package com.irfan.storyapp.data.model

import com.google.gson.annotations.SerializedName
import com.irfan.storyapp.common.SingleEvent
import com.irfan.storyapp.domain.entity.ErrorResponseEntity

data class ErrorResponse(

    @field:SerializedName("error")
    val error: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null,
) {
    fun toEntity(): ErrorResponseEntity {
        val message = SingleEvent(this.message.toString())
        return ErrorResponseEntity(
            error = error,
            message = message,
        )
    }
}
