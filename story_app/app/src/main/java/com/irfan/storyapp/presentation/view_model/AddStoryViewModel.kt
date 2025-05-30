package com.irfan.storyapp.presentation.view_model

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.irfan.storyapp.common.MyLogger
import com.irfan.storyapp.data.repository.AddStoryRepository
import java.io.File

class AddStoryViewModel (private val addStoryRepository: AddStoryRepository) : ViewModel() {
    var currentImageUri: Uri? = null

    fun getAddStoryResult() = addStoryRepository.addStoryResult

    fun addStory(imageFile: File, description: String) {
        addStoryRepository.addStory(imageFile, description)
    }

    override fun onCleared() {
        super.onCleared()
        MyLogger.d(TAG, "onCleared: AddStoryViewModel destroyed")
        addStoryRepository.clearAddStoryResult()
    }

    companion object {
        const val TAG = "AddStoryViewModel"
    }
}