package com.irfan.storyapp.presentation.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.irfan.storyapp.common.MyLogger
import com.irfan.storyapp.common.ResultState
import com.irfan.storyapp.data.repository.SettingRepository
import kotlinx.coroutines.launch

class SettingViewModel(private val settingRepository: SettingRepository) :
    ViewModel() {

    fun getTokenResult() = settingRepository.getTokenResult

    fun fetchToken() {
        MyLogger.d(TAG, "getToken: execute")
        settingRepository.getToken()
    }

    fun getSaveTokenResult() = settingRepository.saveTokenResult

    fun saveToken(token: String) {
        MyLogger.d(TAG, "saveToken: execute")
        viewModelScope.launch {
            settingRepository.saveToken(token)
        }
    }

    fun setStateSaveToken(state: ResultState<String>) {
        settingRepository.setStateSaveToken(state)
    }

    fun getDeleteTokenResult() = settingRepository.deleteTokenResult

    fun deleteToken() {
        MyLogger.d(TAG, "deleteToken: execute")
        viewModelScope.launch {
            settingRepository.deleteToken()
        }
    }

    fun setStateDeleteToken(state: ResultState<Unit>) {
        settingRepository.setStateDeleteToken(state)
    }

    companion object {
        const val TAG = "SettingViewModel"
    }
}