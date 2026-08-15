package com.plantappkmp.core.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.plantappkmp.resources.Res
import com.plantappkmp.resources.rubik_bold
import com.plantappkmp.resources.rubik_extrabold
import com.plantappkmp.resources.rubik_light
import com.plantappkmp.resources.rubik_medium
import com.plantappkmp.resources.rubik_regular
import com.plantappkmp.resources.rubik_semibold
import org.jetbrains.compose.resources.Font

/**
 * Rubik, the design's typeface, bundled rather than linked so the app renders
 * identically to the Figma frames instead of falling back to the platform's
 * default face — Roboto on Android, San Francisco on iOS.
 *
 * Unlike the Android build, where a `FontFamily` can be built from resource
 * ids at any time, Compose Multiplatform loads fonts inside composition. So
 * the family and the styles built from it are `@Composable` and read through
 * `AppTheme.typography` — the call sites are unchanged.
 */
@Composable
internal fun rubikFontFamily(): FontFamily = FontFamily(
    Font(Res.font.rubik_light, FontWeight.Light),
    Font(Res.font.rubik_regular, FontWeight.Normal),
    Font(Res.font.rubik_medium, FontWeight.Medium),
    Font(Res.font.rubik_semibold, FontWeight.SemiBold),
    Font(Res.font.rubik_bold, FontWeight.Bold),
    Font(Res.font.rubik_extrabold, FontWeight.ExtraBold),
)

/**
 * Named text styles, keyed by role rather than by size.
 *
 * Styles carry no colour: colour comes from [AppColors] at the call site, so
 * the same style works on light, dark and premium surfaces.
 */
@Immutable
data class AppTypography(
    /** Onboarding headline. */
    val displayLg: TextStyle,
    /** Paywall headline. */
    val displayMd: TextStyle,
    /** Home greeting, section headings. */
    val titleLg: TextStyle,
    /** Card titles. */
    val titleMd: TextStyle,
    /** Plan tile titles. */
    val titleSm: TextStyle,
    val bodyLg: TextStyle,
    val bodyMd: TextStyle,
    val bodySm: TextStyle,
    /** Field labels and chips. */
    val label: TextStyle,
    /** Legal copy, image credits. */
    val caption: TextStyle,
    val button: TextStyle,
)

/**
 * The weight the design uses for the emphasised half of a headline.
 *
 * Public because it is a design-system decision applied inside a feature's own
 * `AnnotatedString`, which is the one place a caller legitimately needs a raw
 * weight rather than a whole style.
 */
val HeadlineEmphasisWeight = FontWeight.ExtraBold

// Long by nature: it is eleven declarations of data, and splitting it would
// scatter the type scale across several functions to satisfy a line count.
@Suppress("LongMethod")
@Composable
internal fun appTypography(fontFamily: FontFamily = rubikFontFamily()): AppTypography =
    AppTypography(
        displayLg = TextStyle(
            fontFamily = fontFamily,
            fontSize = 27.sp,
            lineHeight = 34.6.sp,
            fontWeight = FontWeight.Normal,
            letterSpacing = (-0.8).sp,
        ),
        displayMd = TextStyle(
            fontFamily = fontFamily,
            fontSize = 27.sp,
            lineHeight = 32.4.sp,
            fontWeight = FontWeight.Normal,
            letterSpacing = (-0.6).sp,
        ),
        titleLg = TextStyle(
            fontFamily = fontFamily,
            fontSize = 24.sp,
            lineHeight = 28.8.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = (-0.9).sp,
        ),
        titleMd = TextStyle(
            fontFamily = fontFamily,
            fontSize = 16.sp,
            lineHeight = 20.8.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = (-0.2).sp,
        ),
        titleSm = TextStyle(
            fontFamily = fontFamily,
            fontSize = 15.sp,
            lineHeight = 19.5.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = (-0.2).sp,
        ),
        bodyLg = TextStyle(
            fontFamily = fontFamily,
            fontSize = 16.sp,
            lineHeight = 22.4.sp,
            fontWeight = FontWeight.Normal,
            letterSpacing = (-0.3).sp,
        ),
        bodyMd = TextStyle(
            fontFamily = fontFamily,
            fontSize = 14.sp,
            lineHeight = 19.6.sp,
            fontWeight = FontWeight.Normal,
            letterSpacing = (-0.2).sp,
        ),
        bodySm = TextStyle(
            fontFamily = fontFamily,
            fontSize = 12.sp,
            lineHeight = 16.2.sp,
            fontWeight = FontWeight.Normal,
        ),
        label = TextStyle(
            fontFamily = fontFamily,
            fontSize = 13.sp,
            lineHeight = 16.9.sp,
            fontWeight = FontWeight.Medium,
        ),
        caption = TextStyle(
            fontFamily = fontFamily,
            fontSize = 11.sp,
            lineHeight = 14.9.sp,
            fontWeight = FontWeight.Normal,
        ),
        button = TextStyle(
            fontFamily = fontFamily,
            fontSize = 16.sp,
            lineHeight = 19.2.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = (-0.2).sp,
        ),
    )
