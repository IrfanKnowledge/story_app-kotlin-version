package com.irfan.storyapp.data.model.story

import com.google.gson.annotations.SerializedName
import com.irfan.storyapp.common.SingleEvent
import com.irfan.storyapp.domain.entity.ResponseEntity

data class AddStoryResponse(

    @field:SerializedName("error")
    val error: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null,
) {
    fun toEntity(): ResponseEntity<Unit> {
        val message = SingleEvent(this.message.toString())
        return ResponseEntity(
            error = error,
            message = message,
            data = Unit,
        )
    }
}
