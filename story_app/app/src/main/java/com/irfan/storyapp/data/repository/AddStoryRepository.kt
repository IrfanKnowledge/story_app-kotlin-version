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
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AddStoryRepository private constructor(private val apiService: ApiService) {
    private val _addStoryResult =
        MediatorLiveData<ResultState<ResponseEntity<Unit?>?>>(ResultState.Initial)
    val addStoryResult: LiveData<ResultState<ResponseEntity<Unit?>?>> = _addStoryResult

    fun addStory(imageFile: File, description: String) {
        val addStoryLiveData: LiveData<ResultState<ResponseEntity<Unit?>?>> = liveData {
            MyLogger.d(TAG, "addStory, status: loading")
            emit(ResultState.Loading)

            try {
                val requestBody = description.toRequestBody("text/plain".toMediaType())

                val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
                val multipartBody = MultipartBody.Part.createFormData(
                    "photo",
                    imageFile.name,
                    requestImageFile
                )

                val response = apiService.addStory(multipartBody, requestBody)
                val responseCode = response.code()
                val responseBody = response.body()
                val responseErrorBodyString = response.errorBody()?.string()

                MyLogger.d(TAG, "addStory, onTry, responseCode: $responseCode")

                if (response.isSuccessful) {
                    MyLogger.apply {
                        d(TAG, "addStory, onTry, responseBody: $responseBody")
                        d(TAG, "addStory, onTry, message: ${responseBody?.message}")
                        d(TAG, "addStory, onTry, status: hasData")
                    }
                    emit(ResultState.HasData(responseBody?.toEntity()))
                } else {
                    val responseErrorBody =
                        Gson().fromJson(responseErrorBodyString, ErrorResponse::class.java)
                            .toEntity()
                    val message = responseErrorBody.message?.peekContent() ?: ""

                    MyLogger.d(TAG, "addStory, onTry, message: $message")
                    MyLogger.d(TAG, "addStory, onTry, status: error")
                    emit(ResultState.Error(SingleEvent(message)))
                }
            } catch (e: Exception) {
                val message = e.message.toString()

                MyLogger.d(TAG, "addStory, onException, message: $message")
                MyLogger.d(TAG, "addStory, status: error")
                emit(ResultState.Error(SingleEvent(message)))
            }
        }

        _addStoryResult.addSource(addStoryLiveData) {
            _addStoryResult.value = it
        }
    }

    fun clearAddStoryResult() {
        _addStoryResult.value = ResultState.Initial
    }

    companion object {
        const val TAG = "AddStoryRepository"

        @Volatile
        private var instance: AddStoryRepository? = null
        fun getInstance(apiService: ApiService): AddStoryRepository =
            instance ?: synchronized(this) {
                instance ?: AddStoryRepository(apiService)
            }.also { instance = it }
    }
}