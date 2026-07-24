package com.example.core.result

sealed interface Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>
    data class Error(val exception: Throwable, val message: String? = exception.localizedMessage) : Resource<Nothing>
    data object Loading : Resource<Nothing>
}

sealed interface UiState<out T> {
    data object Idle : UiState<Nothing>
    data object Loading : UiState<Nothing>
    data class Success<out T>(val data: T) : UiState<T>
    data object Empty : UiState<Nothing>
    data class Error(val message: String, val throwable: Throwable? = null) : UiState<Nothing>
}
