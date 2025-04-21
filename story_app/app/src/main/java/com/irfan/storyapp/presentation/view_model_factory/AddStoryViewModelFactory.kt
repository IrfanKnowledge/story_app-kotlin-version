package com.irfan.storyapp.presentation.view_model_factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.irfan.storyapp.Injection
import com.irfan.storyapp.data.repository.AddStoryRepository
import com.irfan.storyapp.presentation.view_model.AddStoryViewModel

class AddStoryViewModelFactory private constructor(private val addStoryRepository: AddStoryRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddStoryViewModel::class.java)) {
            return AddStoryViewModel(addStoryRepository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }

    companion object {
        @Volatile
        private var instance: AddStoryViewModelFactory? = null
        fun getInstance(token: String?): AddStoryViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: AddStoryViewModelFactory(Injection.provideAddStoryRepository(token))
            }.also { instance = it }
    }
}