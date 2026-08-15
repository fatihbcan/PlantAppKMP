package com.plantappkmp.presentation.onboarding.intro.navigation

import com.plantappkmp.core.presentation.navigation.BasicNavigator

/** Where the intro pages can go. Implemented in the composition root. */
interface IntroNavigator : BasicNavigator {
    fun paywall()
}
