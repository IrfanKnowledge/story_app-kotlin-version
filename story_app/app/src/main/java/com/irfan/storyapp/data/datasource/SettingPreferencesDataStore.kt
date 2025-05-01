package com.irfan.storyapp.data.datasource

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.irfan.storyapp.common.MyLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingPreferencesDataStore private constructor(private val dataStore: DataStore<Preferences>){

    fun getToken(): Flow<String> {
        MyLogger.d(TAG, "getToken: start")
        return dataStore.data.map { preferences ->
            preferences[TOKEN] ?: ""
        }
    }

    suspend fun saveToken(token: String) {
        MyLogger.d(TAG, "saveToken: start")
        dataStore.edit { preferences ->
            preferences[TOKEN] = token
            MyLogger.d(TAG, "saveToken: end")
        }
    }

    suspend fun deleteToken() {
        MyLogger.d(TAG, "deleteToken: start")
        dataStore.edit { preferences ->
            preferences.remove(TOKEN)
            MyLogger.d(TAG, "deleteToken: end")
        }
    }

    companion object {
        const val TAG = "SettingPreferencesDataStore"

        private val TOKEN = stringPreferencesKey("token")

        @Volatile
        private var instance: SettingPreferencesDataStore? = null

        fun getInstance(dataStore: DataStore<Preferences>): SettingPreferencesDataStore {
            return instance ?: synchronized(this) {
                instance ?: SettingPreferencesDataStore(dataStore)
            }.also { instance = it }
        }
    }
}