package com.irfan.storyapp.common

sealed class ResultState<out R> private constructor() {
    data object Initial : ResultState<Nothing>()
    data object Loading : ResultState<Nothing>()
    data object NoData : ResultState<Nothing>()
    data class HasData<out T>(val data: T) : ResultState<T>()
    data class Error(val error: SingleEvent<String>) : ResultState<Nothing>()
}