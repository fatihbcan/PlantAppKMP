package com.plantappkmp.core.util.logging

import platform.Foundation.NSLog

internal actual fun platformLogger(): Logger = IosLogger

/**
 * `NSLog` rather than `println`: it is what Xcode's console and the device
 * log both pick up, so a build running from a phone is still readable.
 */
private object IosLogger : Logger {
    override fun debug(message: String) {
        NSLog("$LOG_TAG D: %s", message)
    }

    override fun warn(message: String, cause: Throwable?) {
        NSLog("$LOG_TAG W: %s %s", message, cause?.stackTraceToString().orEmpty())
    }

    override fun error(message: String, cause: Throwable?) {
        NSLog("$LOG_TAG E: %s %s", message, cause?.stackTraceToString().orEmpty())
    }
}
