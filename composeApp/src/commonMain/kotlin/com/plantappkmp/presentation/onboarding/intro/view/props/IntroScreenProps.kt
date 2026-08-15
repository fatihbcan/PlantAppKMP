package com.plantappkmp.presentation.onboarding.intro.view.props

import androidx.compose.runtime.Immutable
import com.plantappkmp.core.presentation.resource.TextResource
import com.plantappkmp.presentation.onboarding.intro.model.IntroScreenState
import com.plantappkmp.resources.Res
import com.plantappkmp.resources.app_title
import com.plantappkmp.resources.onboarding_diagnose_cta
import com.plantappkmp.resources.onboarding_diagnose_highlight
import com.plantappkmp.resources.onboarding_diagnose_title
import com.plantappkmp.resources.onboarding_identify_cta
import com.plantappkmp.resources.onboarding_identify_highlight
import com.plantappkmp.resources.onboarding_identify_title
import com.plantappkmp.resources.onboarding_page_indicator
import com.plantappkmp.resources.onboarding_privacy_policy
import com.plantappkmp.resources.onboarding_terms_of_use
import com.plantappkmp.resources.onboarding_welcome_body
import com.plantappkmp.resources.onboarding_welcome_cta
import com.plantappkmp.resources.onboarding_welcome_legal
import com.plantappkmp.resources.onboarding_welcome_title
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/** Which illustration an intro page shows. */
enum class IntroArtwork { WELCOME, IDENTIFY, CARE_GUIDES }

/**
 * One intro page's copy and artwork.
 *
 * The artwork is named by an enum rather than held as a composable, so a page
 * stays plain data that a JVM test can build without a Compose runtime.
 */
@Immutable
internal data class IntroPageProps(
    val title: TextResource,
    val highlight: TextResource?,
    /**
     * The welcome page emphasises the product name with weight only; the two
     * that follow draw the design's hand-drawn stroke as well.
     */
    val isHighlightUnderlined: Boolean,
    val body: TextResource?,
    val cta: TextResource,
    val legal: TextResource?,
    val artwork: IntroArtwork,
)

@Immutable
internal data class IntroScreenProps(
    val pages: ImmutableList<IntroPageProps>,
    val currentPage: Int,
    val showsPageIndicator: Boolean,
    val indicatorIndex: Int,
    val indicatorCount: Int,
    val indicatorLabel: TextResource,
    val underlinedLegalPhrases: ImmutableList<TextResource>,
    val onNextClick: () -> Unit = {},
    val onPageSwiped: (Int) -> Unit = {},
) {
    val currentCta: TextResource get() = pages[currentPage].cta
    val currentLegal: TextResource? get() = pages[currentPage].legal

    companion object {
        fun preview() = mapStateToProps(IntroScreenState.initial())
    }
}

/**
 * All of this screen's presentation logic, in one pure function: which dot is
 * filled, whether dots show at all, and which page's copy the footer and the
 * call to action belong to.
 */
internal fun mapStateToProps(
    state: IntroScreenState,
    onNextClick: () -> Unit = {},
    onPageSwiped: (Int) -> Unit = {},
): IntroScreenProps = IntroScreenProps(
    pages = IntroPages,
    currentPage = state.pageIndex,
    showsPageIndicator = state.showsPageIndicator,
    indicatorIndex = state.indicatorIndex,
    indicatorCount = state.pageCount,
    indicatorLabel = TextResource.fromId(
        Res.string.onboarding_page_indicator,
        state.humanPageNumber,
        state.pageCount,
    ),
    underlinedLegalPhrases = persistentListOf(
        TextResource.fromId(Res.string.onboarding_terms_of_use),
        TextResource.fromId(Res.string.onboarding_privacy_policy),
    ),
    onNextClick = onNextClick,
    onPageSwiped = onPageSwiped,
)

private val IntroPages: ImmutableList<IntroPageProps> = persistentListOf(
    IntroPageProps(
        title = TextResource.fromId(Res.string.onboarding_welcome_title, "PlantApp"),
        highlight = TextResource.fromId(Res.string.app_title),
        isHighlightUnderlined = false,
        body = TextResource.fromId(Res.string.onboarding_welcome_body),
        cta = TextResource.fromId(Res.string.onboarding_welcome_cta),
        legal = TextResource.fromId(Res.string.onboarding_welcome_legal),
        artwork = IntroArtwork.WELCOME,
    ),
    IntroPageProps(
        title = TextResource.fromId(Res.string.onboarding_identify_title),
        highlight = TextResource.fromId(Res.string.onboarding_identify_highlight),
        isHighlightUnderlined = true,
        body = null,
        cta = TextResource.fromId(Res.string.onboarding_identify_cta),
        legal = null,
        artwork = IntroArtwork.IDENTIFY,
    ),
    IntroPageProps(
        title = TextResource.fromId(Res.string.onboarding_diagnose_title),
        highlight = TextResource.fromId(Res.string.onboarding_diagnose_highlight),
        isHighlightUnderlined = true,
        body = null,
        cta = TextResource.fromId(Res.string.onboarding_diagnose_cta),
        legal = null,
        artwork = IntroArtwork.CARE_GUIDES,
    ),
)
