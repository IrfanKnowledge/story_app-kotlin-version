package com.irfan.storyapp.domain.entity.story

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StoryEntity(
    val id: String? = null,
    val name: String? = null,
    val description: String? = null,
    val photoUrl: String? = null,
    val createdAt: String? = null,
    val lat: Double? = null,
    val lon: Double? = null,
) : Parcelable
