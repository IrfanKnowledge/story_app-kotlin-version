package com.irfan.storyapp.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.irfan.storyapp.common.MyLogger
import com.irfan.storyapp.data.datasource.ApiService
import com.irfan.storyapp.data.datasource.home_datasource.HomePagingSource
import com.irfan.storyapp.domain.entity.story.StoryEntity

class HomeRepository private constructor(private val apiService: ApiService) {
    /**
     * location have 2 option:
     *  - 0 = get all stories
     *  - 1 = get all stories with location
     */
    fun getStories(pageSize: Int, location: Int? = null): LiveData<PagingData<StoryEntity>> {
        MyLogger.d(TAG, "getStories, status: start")
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
            ),
            pagingSourceFactory = {
                HomePagingSource(apiService, location)
            }
        ).liveData
    }

    companion object {
        const val TAG = "HomeRepository"

        @Volatile
        private var instance: HomeRepository? = null
        fun getInstance(apiService: ApiService): HomeRepository =
            instance ?: synchronized(this) {
                instance ?: HomeRepository(apiService)
            }.also { instance = it }
    }
}