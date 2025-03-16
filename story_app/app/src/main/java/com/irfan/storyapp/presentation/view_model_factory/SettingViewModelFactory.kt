package com.irfan.storyapp.presentation.view_model_factory

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.irfan.storyapp.Injection
import com.irfan.storyapp.data.repository.SettingRepository
import com.irfan.storyapp.presentation.view_model.SettingViewModel

@Suppress("UNCHECKED_CAST")
class SettingViewModelFactory private constructor(private val settingRepository: SettingRepository) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingViewModel::class.java)) {
            return SettingViewModel(settingRepository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }

    companion object {
        @Volatile
        private var instance: SettingViewModelFactory? = null
        fun getInstance(dataStore: DataStore<Preferences>): SettingViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: SettingViewModelFactory(Injection.provideSettingRepository(dataStore))
            }.also { instance = it }
    }
}