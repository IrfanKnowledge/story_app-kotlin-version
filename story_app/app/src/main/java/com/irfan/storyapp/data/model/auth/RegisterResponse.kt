package com.irfan.storyapp.data.model.auth

import com.google.gson.annotations.SerializedName
import com.irfan.storyapp.common.SingleEvent
import com.irfan.storyapp.domain.entity.ResponseEntity

data class RegisterResponse(

    @field:SerializedName("error")
    val error: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null,
) {
    fun toEntity(): ResponseEntity<Unit> {
        val message = this.message?.let { SingleEvent(it) }
        return ResponseEntity(
            error = error,
            message = message,
        );
    }
}
