package com.irfan.storyapp.data.model.auth

import com.google.gson.annotations.SerializedName
import com.irfan.storyapp.common.SingleEvent
import com.irfan.storyapp.domain.entity.ResponseEntity
import com.irfan.storyapp.domain.entity.auth.UserEntity

data class LoginResponse(

    @field:SerializedName("error")
    val error: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("loginResult")
    val loginResult: LoginResultModel? = null,
) {
    fun toEntity(): ResponseEntity<UserEntity> {
        val message = message?.let { SingleEvent(it) }
        return ResponseEntity(
            error = error,
            message = message,
            data = loginResult?.toEntity(),
        )
    }
}

data class LoginResultModel(

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("userId")
    val userId: String? = null,

    @field:SerializedName("token")
    val token: String? = null,
) {
    fun toEntity(): UserEntity {
        return UserEntity(
            userId = userId,
            name = name,
            token = token,
        )
    }
}
