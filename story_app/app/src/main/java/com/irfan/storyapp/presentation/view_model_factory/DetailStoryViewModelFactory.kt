package com.irfan.storyapp.presentation.view_model_factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.irfan.storyapp.Injection
import com.irfan.storyapp.data.repository.DetailStoryRepository
import com.irfan.storyapp.presentation.view_model.DetailStoryViewModel

class DetailStoryViewModelFactory private constructor(private val detailStoryRepository: DetailStoryRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailStoryViewModel::class.java)) {
            return DetailStoryViewModel(detailStoryRepository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }

    companion object {
        @Volatile
        private var instance: DetailStoryViewModelFactory? = null
        fun getInstance(token: String?): DetailStoryViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: DetailStoryViewModelFactory(Injection.provideDetailStoryRepository(token))
            }.also { instance = it }
    }
}