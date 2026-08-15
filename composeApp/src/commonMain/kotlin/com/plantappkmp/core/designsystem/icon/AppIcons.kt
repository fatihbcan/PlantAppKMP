package com.plantappkmp.core.designsystem.icon

import com.plantappkmp.core.presentation.resource.IconResource
import com.plantappkmp.resources.Res
import com.plantappkmp.resources.ic_chevron_right
import com.plantappkmp.resources.ic_close
import com.plantappkmp.resources.ic_envelope_badge
import com.plantappkmp.resources.ic_feature_detailed
import com.plantappkmp.resources.ic_feature_faster
import com.plantappkmp.resources.ic_feature_unlimited
import com.plantappkmp.resources.ic_nav_diagnose
import com.plantappkmp.resources.ic_nav_garden
import com.plantappkmp.resources.ic_nav_home
import com.plantappkmp.resources.ic_nav_profile
import com.plantappkmp.resources.ic_scan
import com.plantappkmp.resources.ic_search
import com.plantappkmp.resources.ic_speedometer

/**
 * The design's own glyphs, exported from the Figma file.
 *
 * None of these exist in Material's icon set — the viewfinder, the lidded jar,
 * the diagnose shield, the garden leaf and the dial are drawn for this product
 * — and approximating them by eye is exactly what made the bottom bar and the
 * paywall's feature strip read wrong in the first pass.
 *
 * They ship at 1x for the file's 360dp frame, hence the `drawable-mdpi`
 * qualifier: Compose Multiplatform reads the same density buckets Android
 * does, and scales per device from there. A 3x re-export is a straight file
 * swap.
 */
object AppIcons {
    /** Viewfinder around a card — plant identification. */
    val Scan = IconResource(Res.drawable.ic_scan)

    /** Dial — faster processing. */
    val Gauge = IconResource(Res.drawable.ic_speedometer)

    /** Leaf — the "My Garden" destination. */
    val Leaf = IconResource(Res.drawable.ic_nav_garden)

    /** Lidded jar — the "Home" destination. */
    val Pot = IconResource(Res.drawable.ic_nav_home)

    /** Shield with a cross — the "Diagnose" destination. */
    val ShieldPlus = IconResource(Res.drawable.ic_nav_diagnose)

    /** Bust — the "Profile" destination. */
    val Person = IconResource(Res.drawable.ic_nav_profile)

    /** Magnifier — the home search field. */
    val Search = IconResource(Res.drawable.ic_search)

    /** Chevron — the premium strip's affordance. */
    val ChevronRight = IconResource(Res.drawable.ic_chevron_right)

    /** Cross — the paywall's close control, and clearing the search field. */
    val Close = IconResource(Res.drawable.ic_close)

    /**
     * The gilded envelope and its unread counter on the premium strip. Full
     * colour, so it is drawn as exported rather than tinted.
     */
    val EnvelopeBadge = IconResource(Res.drawable.ic_envelope_badge, isTintable = false)

    /**
     * The paywall's three feature tiles. The design ships these as complete
     * marks — tinted ground and glyph together — so they are drawn whole
     * rather than rebuilt from a bare glyph on a box of our own.
     */
    val FeatureUnlimited = IconResource(Res.drawable.ic_feature_unlimited, isTintable = false)
    val FeatureFaster = IconResource(Res.drawable.ic_feature_faster, isTintable = false)
    val FeatureDetailed = IconResource(Res.drawable.ic_feature_detailed, isTintable = false)
}
