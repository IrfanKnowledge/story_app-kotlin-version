package com.irfan.storyapp.common

import android.util.Log
import com.irfan.storyapp.BuildConfig

object MyLogger {
    fun d(tag: String, message: String) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, message)
        }
    }
}