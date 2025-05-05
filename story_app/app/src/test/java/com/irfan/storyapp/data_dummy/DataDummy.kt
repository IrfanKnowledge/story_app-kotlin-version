package com.irfan.storyapp.data_dummy

import com.irfan.storyapp.domain.entity.story.StoryEntity

object DataDummy {
    fun generateDummyListStory(): List<StoryEntity> {
        val items: MutableList<StoryEntity> = arrayListOf()
        for (i in 0..5) {
            val story = StoryEntity(
                "story-$i",
                "name-$i",
                "description-$i",
            )
            items.add(story)
        }
        return items
    }
}