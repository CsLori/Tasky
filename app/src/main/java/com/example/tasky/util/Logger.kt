package com.example.tasky.util

import com.example.tasky.BuildConfig
import timber.log.Timber

object Logger {

    fun init() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            // There is no prod here
        }
    }

    fun d(message: String, vararg args: Any?) {
        Timber.d(message, *args)
    }

    fun e(throwable: Throwable, message: String, vararg args: Any?) {
        Timber.e(throwable, message, *args)
    }

    fun i(message: String, vararg args: Any?) {
        Timber.i(message, *args)
    }

    fun w(message: String, vararg args: Any?) {
        Timber.w(message, *args)
    }

    fun v(message: String, vararg args: Any?) {
        Timber.v(message, *args)
    }
}