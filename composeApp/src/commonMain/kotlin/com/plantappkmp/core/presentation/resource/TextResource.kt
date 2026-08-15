package com.plantappkmp.core.presentation.resource

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import org.jetbrains.compose.resources.PluralStringResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

/**
 * Deferred user-facing text.
 *
 * Props are built by `mapStateToProps`, a plain non-`@Composable` function
 * that cannot call `stringResource()`. This defers resolution to composition,
 * so presentation logic stays testable off-device and previews still render
 * real copy.
 *
 * The Android build of this app carries a `@StringRes Int` here. Compose
 * Multiplatform's resources are typed objects instead ([StringResource]),
 * which is strictly better for this shape: a Props test can assert on the
 * identity of the resource without an Android runtime to resolve it.
 */
@Immutable
sealed interface TextResource {

    @Immutable
    data class Text(val text: String) : TextResource

    @Immutable
    data class Id(val id: StringResource, val formatArgs: List<Any>) : TextResource

    @Immutable
    data class Plural(
        val pluralId: PluralStringResource,
        val count: Int,
        val formatArgs: List<Any>,
    ) : TextResource

    companion object {
        fun fromString(text: String): TextResource = Text(text)

        fun fromId(id: StringResource, vararg args: Any): TextResource = Id(id, args.toList())

        fun fromPlural(id: PluralStringResource, count: Int, vararg args: Any): TextResource =
            Plural(id, count, args.toList())
    }
}

// The spread is required: the resource functions take `vararg Any`, and there
// is no list-taking overload to call instead.
@Suppress("SpreadOperator")
@Composable
fun TextResource.asString(): String = when (this) {
    is TextResource.Text -> text
    is TextResource.Id ->
        if (formatArgs.isEmpty()) {
            stringResource(id)
        } else {
            stringResource(id, *formatArgs.toTypedArray())
        }

    is TextResource.Plural ->
        if (formatArgs.isEmpty()) {
            pluralStringResource(pluralId, count)
        } else {
            pluralStringResource(pluralId, count, *formatArgs.toTypedArray())
        }
}
