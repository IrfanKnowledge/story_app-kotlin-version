package com.irfan.storyapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.liveData
import com.google.gson.Gson
import com.irfan.storyapp.common.MyLogger
import com.irfan.storyapp.common.ResultState
import com.irfan.storyapp.common.SingleEvent
import com.irfan.storyapp.data.datasource.ApiService
import com.irfan.storyapp.data.model.ErrorResponse
import com.irfan.storyapp.domain.entity.ResponseEntity
import com.irfan.storyapp.domain.entity.story.StoryEntity

class DetailStoryRepository private constructor(private val apiService: ApiService) {
    private val _detailStoryResult =
        MediatorLiveData<ResultState<ResponseEntity<StoryEntity?>?>>(ResultState.Initial)
    val detailStoryResult: LiveData<ResultState<ResponseEntity<StoryEntity?>?>> = _detailStoryResult

    fun getDetailStory(id: String) {
        val detailStoryLiveData: LiveData<ResultState<ResponseEntity<StoryEntity?>?>> = liveData {
            MyLogger.d(TAG, "getDetailStory, status: loading")
            emit(ResultState.Loading)

            try {
                val response = apiService.getDetailStory(id)
                val responseCode = response.code()
                val responseBody = response.body()
                val responseErrorBodyString = response.errorBody()?.string()

                MyLogger.d(TAG, "getDetailStory, onTry, responseCode: $responseCode")

                if (response.isSuccessful) {
                    MyLogger.d(TAG, "getDetailStory, onTry, responseBody: $responseBody")
                    MyLogger.d(TAG, "getDetailStory, onTry, message: ${responseBody?.message}")
                    MyLogger.d(TAG, "getDetailStory, onTry, status: hasData")
                    emit(ResultState.HasData(responseBody?.toEntity()))
                } else {
                    val responseErrorBody =
                        Gson().fromJson(responseErrorBodyString, ErrorResponse::class.java)
                            .toEntity()
                    val message = responseErrorBody.message?.peekContent() ?: ""

                    MyLogger.d(TAG, "getDetailStory, onTry, message: $message")
                    MyLogger.d(TAG, "getDetailStory, onTry, status: error")
                    emit(ResultState.Error(SingleEvent(message)))
                }

            } catch (e: Exception) {
                val message = e.message.toString()

                MyLogger.d(TAG, "getDetailStory, onException, message: $message")
                MyLogger.d(TAG, "getDetailStory, status: error")
                emit(ResultState.Error(SingleEvent(message)))
            }
        }

        _detailStoryResult.addSource(detailStoryLiveData) {
            _detailStoryResult.value = it
        }
    }

    companion object {
        const val TAG = "DetailStoryRepository"

        @Volatile
        private var instance: DetailStoryRepository? = null
        fun getInstance(apiService: ApiService): DetailStoryRepository =
            instance ?: synchronized(this) {
                instance ?: DetailStoryRepository(apiService)
            }.also { instance = it }
    }
}