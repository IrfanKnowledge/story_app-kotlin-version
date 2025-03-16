package com.irfan.storyapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.asLiveData
import com.irfan.storyapp.common.ResultState
import com.irfan.storyapp.data.datasource.SettingPreferencesDataStore

class SettingRepository private constructor(private val settingPreferencesDataStore: SettingPreferencesDataStore) {
    private val _getTokenResult = MediatorLiveData<ResultState<String>>(ResultState.Initial)
    val getTokenResult: LiveData<ResultState<String>> = _getTokenResult

    private val _saveTokenResult = MediatorLiveData<ResultState<String>>(ResultState.Initial)
    val saveTokenResult: LiveData<ResultState<String>> = _saveTokenResult

    private val _deleteTokenResult = MediatorLiveData<ResultState<Unit>>(ResultState.Initial)
    val deleteTokenResult: LiveData<ResultState<Unit>> = _deleteTokenResult

    fun getToken() {
        _getTokenResult.value = ResultState.Loading
        val tokenLiveData = settingPreferencesDataStore.getToken().asLiveData()

        _getTokenResult.addSource(tokenLiveData) {
            if (it.isEmpty()) {
                _getTokenResult.value = ResultState.NoData
            } else {
                _getTokenResult.value = ResultState.HasData(it)
            }
        }
    }

    suspend fun saveToken(token: String) {
        _saveTokenResult.value = ResultState.Loading
        settingPreferencesDataStore.saveToken(token)
        _saveTokenResult.value = ResultState.HasData(token)
    }

    fun setStateSaveToken(state: ResultState<String>) {
        _saveTokenResult.value = state
    }

    suspend fun deleteToken() {
        _deleteTokenResult.value = ResultState.Loading
        settingPreferencesDataStore.deleteToken()
        _deleteTokenResult.value = ResultState.HasData(Unit)
    }

    fun setStateDeleteToken(state: ResultState<Unit>) {
        _deleteTokenResult.value = state
    }

    companion object {
        @Volatile
        private var instance: SettingRepository? = null
        fun getInstance(settingPreferencesDataStore: SettingPreferencesDataStore): SettingRepository =
            instance ?: synchronized(this) {
                instance ?: SettingRepository(settingPreferencesDataStore)
            }.also { instance = it }
    }
}