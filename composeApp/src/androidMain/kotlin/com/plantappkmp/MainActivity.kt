package com.plantappkmp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.plantappkmp.framework.app.AppRoot

/**
 * The app's single Activity, and deliberately almost empty: everything it
 * shows comes from `framework.app`, which is the only package that knows the
 * whole graph.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // The start destination depends on a flag read from disk. Holding the
        // splash until it resolves is what stops onboarding flashing up for a
        // frame in front of a user who already finished it. (iOS gets the same
        // effect for free: its launch screen stays up until the first frame,
        // and `AppRoot` draws nothing until the route is known.)
        var isReady = false
        splashScreen.setKeepOnScreenCondition { !isReady }

        setContent {
            AppRoot(
                onFinish = ::finish,
                onReady = { isReady = true },
            )
        }
    }
}
