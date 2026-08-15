package com.plantappkmp.core.util.logging

import android.util.Log

internal actual fun platformLogger(): Logger = AndroidLogger

private object AndroidLogger : Logger {
    override fun debug(message: String) {
        Log.d(LOG_TAG, message)
    }

    override fun warn(message: String, cause: Throwable?) {
        Log.w(LOG_TAG, message, cause)
    }

    override fun error(message: String, cause: Throwable?) {
        Log.e(LOG_TAG, message, cause)
    }
}
