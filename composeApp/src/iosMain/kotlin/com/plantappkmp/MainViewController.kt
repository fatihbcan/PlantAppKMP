package com.plantappkmp

import androidx.compose.ui.window.ComposeUIViewController
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.plantappkmp.di.initKoin
import com.plantappkmp.framework.app.AppRoot
import com.plantappkmp.platform.datastore.PREFERENCES_FILE_NAME
import com.plantappkmp.platform.datastore.createPreferencesDataStore
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask
import platform.UIKit.UIViewController

/**
 * The iOS entry point, called from `ContentView.swift`.
 *
 * Koin is started here rather than in an `AppDelegate` so the Swift side stays
 * a three-line wrapper: this function is the only symbol it needs.
 */
// PascalCase by convention: Swift calls this as `MainViewControllerKt
// .MainViewController()`, and it reads there as a type-like factory.
@Suppress("FunctionNaming")
fun MainViewController(): UIViewController {
    startKoinOnce()
    return ComposeUIViewController {
        AppRoot(
            // There is no "finish the app" on iOS — an app leaves the screen
            // when the user says so, not when a screen asks. Back from the
            // root simply stays put.
            onFinish = {},
            onReady = {},
        )
    }
}

/**
 * Guarded by a flag rather than by asking Koin whether it is running: a second
 * `startKoin` throws, and SwiftUI is free to build this controller again (a
 * scene reconnecting, a preview) inside a process that already has a graph.
 */
private var isKoinStarted = false

private fun startKoinOnce() {
    if (isKoinStarted) return
    isKoinStarted = true
    initKoin(
        module {
            single<DataStore<Preferences>> {
                createPreferencesDataStore { "${documentsDirectory()}/$PREFERENCES_FILE_NAME" }
            }
        },
    )
}

/** Documents rather than Caches: the onboarding flag has to survive a purge. */
@OptIn(ExperimentalForeignApi::class)
private fun documentsDirectory(): String {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )
    return requireNotNull(documentDirectory?.path)
}
