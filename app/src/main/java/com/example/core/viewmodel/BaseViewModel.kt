package com.example.core.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.dispatchers.DefaultDispatcherProvider
import com.example.core.dispatchers.DispatcherProvider
import com.example.core.logger.DebugLogger
import com.example.core.logger.Logger
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

abstract class BaseViewModel(
    protected val dispatchers: DispatcherProvider = DefaultDispatcherProvider(),
    protected val logger: Logger = DebugLogger()
) : ViewModel() {

    protected val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        logger.e(this::class.java.simpleName, "Unhandled coroutine exception", throwable)
        onCoroutineError(throwable)
    }

    protected fun launchOnIO(block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch(dispatchers.io + exceptionHandler) {
            block()
        }
    }

    protected open fun onCoroutineError(throwable: Throwable) {
        // Default error handling hook for child view models
    }
}
