package com.irfan.storyapp.presentation.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.irfan.storyapp.common.SingleEvent
import com.irfan.storyapp.data.repository.HomeRepository
import com.irfan.storyapp.domain.entity.story.StoryEntity

class HomeViewModel(homeRepository: HomeRepository) : ViewModel() {
    val listStory: LiveData<PagingData<StoryEntity>> =
        homeRepository.getStories(5, 1).cachedIn(viewModelScope)

    var listStorySnapshot: List<StoryEntity>? = null

    private val _refreshListStory = MutableLiveData<SingleEvent<Unit>>()
    val refreshListStory: LiveData<SingleEvent<Unit>> = _refreshListStory

    var isUpdateListStory: Boolean = false

    fun refreshListStory() {
        _refreshListStory.value = SingleEvent(Unit)
    }

    companion object {
        const val TAG = "HomeViewModel"
    }
}