package com.irfan.storyapp.presentation.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.irfan.storyapp.data.repository.HomeRepository
import com.irfan.storyapp.domain.entity.story.StoryEntity

class HomeViewModel(homeRepository: HomeRepository) : ViewModel() {
    val listStory: LiveData<PagingData<StoryEntity>> =
        homeRepository.getStories(5).cachedIn(viewModelScope)

    companion object {
        const val TAG = "HomeViewModel"
    }
}