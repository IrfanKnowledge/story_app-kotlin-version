package com.irfan.storyapp.data.model.story

import com.google.gson.annotations.SerializedName
import com.irfan.storyapp.domain.entity.ResponseEntity
import com.irfan.storyapp.domain.entity.story.StoryEntity

data class DetailStoryResponse(

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("story")
	val story: Story? = null
) {
	fun toEntity(): ResponseEntity<StoryEntity> {
		return ResponseEntity(
			error = error,
			message = null,
			data = story?.toEntity(),
		);
	}
}

data class Story(

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
	val lat: Number? = null
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
