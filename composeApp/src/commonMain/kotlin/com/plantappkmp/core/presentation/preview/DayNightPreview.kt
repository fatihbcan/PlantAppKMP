package com.plantappkmp.core.presentation.preview

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.plantappkmp.core.designsystem.theme.AppTheme

/**
 * Renders a component in both schemes at once. A hardcoded colour is caught
 * the moment someone opens the preview pane, which is the cheapest possible
 * dark-mode test.
 *
 * The Android build gets this from two `@Preview` annotations differing in
 * `uiMode`. Compose Multiplatform's `@Preview` has no `uiMode`, so the two
 * schemes are stacked inside one preview instead — same signal, one frame.
 */
@Composable
fun DayNightPreview(content: @Composable () -> Unit) {
    Column {
        AppTheme(isDarkTheme = false) { content() }
        AppTheme(isDarkTheme = true) { content() }
    }
}
