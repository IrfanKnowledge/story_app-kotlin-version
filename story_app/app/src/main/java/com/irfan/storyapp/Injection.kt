package com.irfan.storyapp

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.irfan.storyapp.data.datasource.ApiConfig
import com.irfan.storyapp.data.datasource.SettingPreferencesDataStore
import com.irfan.storyapp.data.repository.AuthRepository
import com.irfan.storyapp.data.repository.DetailStoryRepository
import com.irfan.storyapp.data.repository.HomeRepository
import com.irfan.storyapp.data.repository.SettingRepository

object Injection {
    fun provideAuthRepository(token: String?): AuthRepository {
        val apiService = ApiConfig.getApiService(token)

        return AuthRepository.getInstance(apiService)
    }

    fun provideSettingRepository(dataStore: DataStore<Preferences>): SettingRepository {
        val settingPreferencesDataStore = SettingPreferencesDataStore.getInstance(dataStore)

        return SettingRepository.getInstance(settingPreferencesDataStore)
    }

    fun provideHomeRepository(token: String?): HomeRepository {
        val apiService = ApiConfig.getApiService(token)

        return HomeRepository.getInstance(apiService)
    }

    fun provideDetailStoryRepository(token: String?): DetailStoryRepository {
        Log.d(TAG, "provideDetailStoryRepository, token: $token")
        val apiService = ApiConfig.getApiService(token)

        return DetailStoryRepository.getInstance(apiService)
    }

    const val TAG = "Injection"
}