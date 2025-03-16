package com.irfan.storyapp.data.datasource.home_datasource

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.gson.Gson
import com.irfan.storyapp.data.datasource.ApiService
import com.irfan.storyapp.data.model.ErrorResponse
import com.irfan.storyapp.data.repository.AuthRepository
import com.irfan.storyapp.domain.entity.story.StoryEntity

class HomePagingSource(private val apiService: ApiService) : PagingSource<Int, StoryEntity>() {

    override fun getRefreshKey(state: PagingState<Int, StoryEntity>): Int? {
        Log.d(TAG, "getRefreshKey, status: start")
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoryEntity> {
        Log.d(TAG, "load, beforeTry, status: start")
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX

            val response = apiService.getStories(position, params.loadSize)
            val responseCode = response.code()
            val responseBody = response.body()
            val responseErrorBodyString = response.errorBody()?.string()

            Log.d(TAG, "load, onTry, responseCode: $responseCode")

            if (response.isSuccessful) {
                Log.d(AuthRepository.TAG, "load, onTry, responseBody: $responseBody")
                Log.d(AuthRepository.TAG, "load, onTry, message: ${responseBody?.message}")
                Log.d(AuthRepository.TAG, "load, onTry, status: hasData")

                LoadResult.Page(
                    data = responseBody?.listStory?.map { it.toEntity() } ?: emptyList(),
                    prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                    nextKey = if (responseBody?.listStory.isNullOrEmpty()) null else position + 1
                )
            } else {
                val responseErrorBody =
                    Gson().fromJson(responseErrorBodyString, ErrorResponse::class.java)
                        .toEntity()
                val message = responseErrorBody.message?.peekContent() ?: ""

                Log.d(AuthRepository.TAG, "load, onTry, message: $message")
                Log.d(AuthRepository.TAG, "load, onTry, status: error")

                LoadResult.Error(Exception(message))
            }
        } catch (e: Exception) {
            val message = e.message.toString()

            Log.d(TAG, "load, onException, message: $message")
            Log.d(TAG, "load, onException, status: error")
            return LoadResult.Error(e)
        }
    }

    private companion object {
        const val TAG = "HomePagingSource"
        const val INITIAL_PAGE_INDEX = 1
    }
}