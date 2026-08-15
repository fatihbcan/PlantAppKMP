package com.plantappkmp.core.presentation.view

import androidx.compose.runtime.Composable

/**
 * No-op: iOS has no system back gesture for an app to intercept, and the
 * navigation host owns the edge swipe. Screens reach `goBack()` through their
 * own controls instead — the paywall's close button is the only one in this
 * app that needs it.
 */
@Composable
actual fun PlatformBackHandler(enabled: Boolean, onBack: () -> Unit) = Unit
