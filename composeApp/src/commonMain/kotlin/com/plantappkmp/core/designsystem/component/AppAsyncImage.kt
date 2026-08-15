package com.plantappkmp.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import com.plantappkmp.core.designsystem.theme.AppTheme

/**
 * A remote image with a themed placeholder behind it.
 *
 * The placeholder is a filled surface rather than a spinner: these images sit
 * inside cards in a scrolling list, and a grid of spinners reads as breakage
 * where a grid of empty cards reads as loading.
 *
 * [placeholderColor] is overridable because that fill is only invisible under
 * an image that covers its whole box. Artwork *fitted* into a corner of a card
 * leaves the rest of the box bare, and there the default paints a grey block
 * across the card rather than a placeholder — pass [Color.Transparent].
 *
 * Coil 3 rather than the Android build's Coil 2: same API, and it fetches
 * through the shared Ktor client on both platforms (see `imageLoader`).
 */
@Composable
fun AppAsyncImage(
    url: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    placeholderColor: Color = AppTheme.colors.surfaceMuted,
) {
    AsyncImage(
        model = url,
        contentDescription = null,
        contentScale = contentScale,
        modifier = modifier.background(placeholderColor),
    )
}
