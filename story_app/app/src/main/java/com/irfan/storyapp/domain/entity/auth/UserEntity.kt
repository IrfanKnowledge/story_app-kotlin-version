package com.irfan.storyapp.domain.entity.auth

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserEntity(
    val userId: String? = null,
    val name: String? = null,
    val token: String? = null
) : Parcelable