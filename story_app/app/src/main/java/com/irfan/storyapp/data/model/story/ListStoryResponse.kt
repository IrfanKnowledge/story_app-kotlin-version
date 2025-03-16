package com.irfan.storyapp.data.model.story

import com.google.gson.annotations.SerializedName
import com.irfan.storyapp.domain.entity.ResponseEntity
import com.irfan.storyapp.domain.entity.story.StoryEntity

data class ListStoryResponse(

    @field:SerializedName("error")
    val error: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("listStory")
    val listStory: List<ListStoryItem>? = null,
) {
    fun toEntity(): ResponseEntity<List<StoryEntity>> {
        return ResponseEntity(
            error = error,
            message = null,
            data = listStory?.map { it.toEntity()},
        );
    }
}

data class ListStoryItem(

    @field:SerializedName("photoUrl")
    val photoUrl: String? = null,

    @field:SerializedName("createdAt")
    val createdAt: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("lon")
    val lon: Number? = null,

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("lat")
    val lat: Number? = null,
) {
    fun toEntity(): StoryEntity {
        return StoryEntity(
            id = id,
            name = name,
            description = description,
            photoUrl = photoUrl,
            createdAt = createdAt,
            lat = lat,
            lon = lon,
        )
    }
}
