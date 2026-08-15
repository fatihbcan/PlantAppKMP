package com.plantappkmp.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.plantappkmp.core.designsystem.theme.AppTheme
import com.plantappkmp.core.presentation.preview.DayNightPreview
import com.plantappkmp.core.presentation.resource.TextResource
import com.plantappkmp.core.presentation.resource.asString
import com.plantappkmp.resources.Res
import com.plantappkmp.resources.action_retry
import com.plantappkmp.resources.error_no_connection
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun AppLoader(modifier: Modifier = Modifier) {
    CircularProgressIndicator(
        color = AppTheme.colors.brand,
        strokeWidth = AppTheme.dimens.strokeThick,
        modifier = modifier.size(AppTheme.dimens.spaceXxl),
    )
}

@Immutable
data class ErrorStateProps(
    val message: TextResource,
    val retryText: TextResource = TextResource.fromId(Res.string.action_retry),
    val onRetry: (() -> Unit)? = null,
)

/**
 * The retry affordance is nullable rather than always-present: a section whose
 * failure the user cannot act on should not offer a button that does nothing.
 */
@Composable
fun ErrorState(
    props: ErrorStateProps,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.spaceSm),
        modifier = modifier
            .fillMaxWidth()
            .padding(AppTheme.dimens.spaceLg),
    ) {
        Text(
            text = props.message.asString(),
            style = AppTheme.typography.bodyMd,
            color = AppTheme.colors.danger,
            textAlign = TextAlign.Center,
        )
        props.onRetry?.let { onRetry ->
            TextButton(onClick = onRetry) {
                Text(
                    text = props.retryText.asString(),
                    style = AppTheme.typography.label,
                    color = AppTheme.colors.brand,
                )
            }
        }
    }
}

@Immutable
data class EmptyStateProps(val message: TextResource)

@Composable
fun EmptyState(
    props: EmptyStateProps,
    modifier: Modifier = Modifier,
) {
    Text(
        text = props.message.asString(),
        style = AppTheme.typography.bodyMd,
        color = AppTheme.colors.onCanvasMuted,
        textAlign = TextAlign.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(AppTheme.dimens.spaceLg),
    )
}

@Preview
@Composable
private fun ErrorStatePreview() = DayNightPreview {
    ErrorState(
        ErrorStateProps(
            message = TextResource.fromId(Res.string.error_no_connection),
            onRetry = {},
        ),
    )
}
