package com.plantappkmp.core.util.logging

/**
 * Logging as an interface so the pure-Kotlin layers can report without naming
 * a platform log API. The real implementation is bound in the composition root
 * and differs per platform — `Log` on Android, `NSLog` on iOS.
 */
interface Logger {
    fun debug(message: String)
    fun warn(message: String, cause: Throwable? = null)
    fun error(message: String, cause: Throwable? = null)
}

internal expect fun platformLogger(): Logger

internal const val LOG_TAG = "PlantAppKMP"
