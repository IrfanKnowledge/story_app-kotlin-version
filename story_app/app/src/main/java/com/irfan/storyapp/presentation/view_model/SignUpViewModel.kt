package com.irfan.storyapp.presentation.view_model

import android.util.Log
import androidx.lifecycle.ViewModel
import com.irfan.storyapp.data.repository.AuthRepository

class SignUpViewModel(private val authRepository: AuthRepository) : ViewModel() {
    fun getSignUpResult() = authRepository.signUpResult

    fun signUp(name: String, email: String, password: String) {
        Log.d(TAG, "getSignUp: execute")
        authRepository.signUp(name, email, password)
    }

    companion object {
        const val TAG = "SignUpViewModel"
    }
}