package com.irfan.storyapp.presentation.view_model

import androidx.lifecycle.ViewModel
import com.irfan.storyapp.data.repository.DetailStoryRepository

class DetailStoryViewModel(private val detailStoryRepository: DetailStoryRepository) : ViewModel() {
    fun getDetailStoryResult() = detailStoryRepository.detailStoryResult

    fun fetchDetailStory(id: String) {
        detailStoryRepository.getDetailStory(id)
    }
}