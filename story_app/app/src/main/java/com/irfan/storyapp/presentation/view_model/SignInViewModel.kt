package com.irfan.storyapp.presentation.view_model

import androidx.lifecycle.ViewModel
import com.irfan.storyapp.common.MyLogger
import com.irfan.storyapp.common.ResultState
import com.irfan.storyapp.data.repository.AuthRepository
import com.irfan.storyapp.domain.entity.ResponseEntity
import com.irfan.storyapp.domain.entity.auth.UserEntity

class SignInViewModel(private val authRepository: AuthRepository) : ViewModel() {
    fun getSignInResult() = authRepository.signInResult

    fun signIn(email: String, password: String) {
        MyLogger.d(TAG, "getSignIn: execute")
        authRepository.signIn(email, password)
    }

    fun setStateSignIn(state: ResultState<ResponseEntity<UserEntity?>?>) {
        authRepository.setStateSignIn(state)
    }

    companion object {
        const val TAG = "SignInViewModel"
    }
}