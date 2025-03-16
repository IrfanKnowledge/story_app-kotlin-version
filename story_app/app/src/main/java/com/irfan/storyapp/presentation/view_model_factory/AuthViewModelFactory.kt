package com.irfan.storyapp.presentation.view_model_factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.irfan.storyapp.Injection
import com.irfan.storyapp.data.repository.AuthRepository
import com.irfan.storyapp.presentation.view_model.SignInViewModel
import com.irfan.storyapp.presentation.view_model.SignUpViewModel

class AuthViewModelFactory private constructor(private val authRepository: AuthRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignUpViewModel::class.java)) {
            return SignUpViewModel(authRepository) as T
        }

        if (modelClass.isAssignableFrom(SignInViewModel::class.java)) {
            return SignInViewModel(authRepository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }

    companion object {
        @Volatile
        private var instance: AuthViewModelFactory? = null
        fun getInstance(token: String?): AuthViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: AuthViewModelFactory(Injection.provideAuthRepository(token))
            }.also { instance = it }
    }
}