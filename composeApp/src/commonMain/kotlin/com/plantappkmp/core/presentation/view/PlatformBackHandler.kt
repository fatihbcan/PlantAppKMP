package com.plantappkmp.core.presentation.view

import androidx.compose.runtime.Composable

/**
 * Intercepts the platform's own "go back" gesture while [onBack] is the
 * frontmost handler.
 *
 * Android has a system back button and a predictive-back gesture, and the app
 * must consume both — routing them through the ViewModel is the whole point of
 * [com.plantappkmp.core.presentation.viewmodel.SystemBackHandler].
 *
 * iOS has no equivalent: there is no global back affordance to intercept, and
 * the interactive swipe belongs to the navigation host. The actual is a no-op
 * there, which is why every screen still draws its own back control.
 */
@Composable
expect fun PlatformBackHandler(enabled: Boolean = true, onBack: () -> Unit)
