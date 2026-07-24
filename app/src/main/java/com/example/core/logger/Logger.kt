package com.example.core.logger

import android.util.Log

interface Logger {
    fun d(tag: String, message: String)
    fun e(tag: String, message: String, throwable: Throwable? = null)
    fun i(tag: String, message: String)
}

class DebugLogger : Logger {
    override fun d(tag: String, message: String) {
        Log.d("BloomFamily_$tag", message)
    }

    override fun e(tag: String, message: String, throwable: Throwable?) {
        Log.e("BloomFamily_$tag", message, throwable)
    }

    override fun i(tag: String, message: String) {
        Log.i("BloomFamily_$tag", message)
    }
}
