package com.irfan.storyapp.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.irfan.storyapp.data.datasource.ApiService
import com.irfan.storyapp.data.datasource.home_datasource.HomePagingSource
import com.irfan.storyapp.domain.entity.story.StoryEntity

class HomeRepository private constructor(private val apiService: ApiService) {
    fun getStories(pageSize: Int): LiveData<PagingData<StoryEntity>> {
        Log.d(TAG, "getStories, status: start")
        return Pager(
            config = PagingConfig(
                pageSize = pageSize
            ),
            pagingSourceFactory = {
                HomePagingSource(apiService)
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