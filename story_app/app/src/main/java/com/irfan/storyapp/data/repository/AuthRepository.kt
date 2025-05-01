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
import com.irfan.storyapp.domain.entity.auth.UserEntity

class AuthRepository private constructor(private val apiService: ApiService) {
    private val _signUpResult =
        MediatorLiveData<ResultState<ResponseEntity<Unit>?>>(ResultState.Initial)
    val signUpResult: LiveData<ResultState<ResponseEntity<Unit>?>> = _signUpResult

    private val _signInResult =
        MediatorLiveData<ResultState<ResponseEntity<UserEntity?>?>>(ResultState.Initial)
    val signInResult: LiveData<ResultState<ResponseEntity<UserEntity?>?>> = _signInResult

    fun signUp(name: String, email: String, password: String) {
        val signUpLiveData: LiveData<ResultState<ResponseEntity<Unit>?>> = liveData {
            MyLogger.d(TAG, "signUp, status: loading")
            emit(ResultState.Loading)

            try {
                val response = apiService.register(name, email, password)
                val responseCode = response.code()
                val responseBody = response.body()
                val responseErrorBodyString = response.errorBody()?.string()

                MyLogger.d(TAG, "signUp, onTry, responseCode: $responseCode")

                if (response.isSuccessful) {
                    MyLogger.d(TAG, "signUp, onTry, responseBody: $responseBody")
                    MyLogger.d(TAG, "signUp, onTry, message: ${responseBody?.message}")
                    MyLogger.d(TAG, "signUp, onTry, status: hasData")
                    emit(ResultState.HasData(responseBody?.toEntity()))
                } else {
                    val responseErrorBody =
                        Gson().fromJson(responseErrorBodyString, ErrorResponse::class.java)
                            .toEntity()
                    val message = responseErrorBody.message?.peekContent() ?: ""

                    MyLogger.d(TAG, "signUp, onTry, message: $message")
                    MyLogger.d(TAG, "signUp, onTry, status: error")
                    emit(ResultState.Error(SingleEvent(message)))
                }
            } catch (e: Exception) {
                val message = e.message.toString()

                MyLogger.d(TAG, "signUp, status: error")
                emit(ResultState.Error(SingleEvent(message)))
            }
        }

        _signUpResult.addSource(signUpLiveData) {
            _signUpResult.value = it
        }
    }

    fun signIn(email: String, password: String) {
        val signInLiveData: LiveData<ResultState<ResponseEntity<UserEntity?>?>> = liveData {
            MyLogger.d(TAG, "signIn, status: loading")
            emit(ResultState.Loading)

            try {
                val response = apiService.login(email, password)
                val responseCode = response.code()
                val responseBody = response.body()
                val responseErrorBodyString = response.errorBody()?.string()

                MyLogger.d(TAG, "signIn, onTry, responseCode: $responseCode")

                if (response.isSuccessful) {
                    MyLogger.d(TAG, "signIn, onTry, responseBody: $responseBody")
                    MyLogger.d(TAG, "signIn, onTry, message: ${responseBody?.message}")
                    MyLogger.d(TAG, "signIn, onTry, status: hasData")
                    emit(ResultState.HasData(responseBody?.toEntity()))
                } else {
                    val responseErrorBody =
                        Gson().fromJson(responseErrorBodyString, ErrorResponse::class.java)
                            .toEntity()
                    val message = responseErrorBody.message?.peekContent() ?: ""

                    MyLogger.d(TAG, "signIn, onTry, message: $message")
                    MyLogger.d(TAG, "signIn, onTry, status: error")
                    emit(ResultState.Error(SingleEvent(message)))
                }

            } catch (e: Exception) {
                val message = e.message.toString()

                MyLogger.d(TAG, "signIn, onException, message: $message")
                MyLogger.d(TAG, "signIn, status: error")
                emit(ResultState.Error(SingleEvent(message)))
            }
        }

        _signInResult.addSource(signInLiveData) {
            _signInResult.value = it
        }
    }

    fun setStateSignIn(state: ResultState<ResponseEntity<UserEntity?>?>) {
        _signInResult.value = state
    }

    companion object {
        const val TAG = "AuthRepository"

        @Volatile
        private var instance: AuthRepository? = null
        fun getInstance(apiService: ApiService): AuthRepository =
            instance ?: synchronized(this) {
                instance ?: AuthRepository(apiService)
            }.also { instance = it }
    }
}