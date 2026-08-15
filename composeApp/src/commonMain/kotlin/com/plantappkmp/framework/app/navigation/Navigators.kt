package com.plantappkmp.framework.app.navigation

import com.plantappkmp.core.presentation.navigation.BasicNavigator
import com.plantappkmp.core.presentation.navigation.NavigationDirections
import com.plantappkmp.core.presentation.navigation.NavigationManager
import com.plantappkmp.presentation.home.navigation.HomeNavigator
import com.plantappkmp.presentation.onboarding.intro.navigation.IntroNavigator
import com.plantappkmp.presentation.onboarding.paywall.navigation.PaywallNavigator

internal class IntroNavigatorImpl(
    private val navigationManager: NavigationManager,
    basicNavigator: DefaultBasicNavigator,
) : IntroNavigator, BasicNavigator by basicNavigator {

    override fun paywall() =
        navigationManager.navigateTo(NavigationDirections(AppRoutes.PAYWALL))
}

internal class PaywallNavigatorImpl(
    private val navigationManager: NavigationManager,
    basicNavigator: DefaultBasicNavigator,
) : PaywallNavigator, BasicNavigator by basicNavigator {

    /**
     * The case's one genuinely stateful navigation rule: a user who completes
     * onboarding must never re-enter it.
     *
     * Clearing the stack rather than popping the intro route: the paywall has
     * two ways in. From onboarding the stack is intro → paywall, but home's
     * premium strip opens it again later, where the stack is home → paywall
     * and there is no intro route to pop up to. Naming intro would leave that
     * second case pushing a *second* home on top of the paywall; emptying the
     * stack lands on one home either way, and the system back gesture leaves
     * the app rather than walking back into the paywall.
     */
    override fun home() = navigationManager.navigateTo(
        NavigationDirections(
            route = AppRoutes.HOME,
            isClearingBackStack = true,
            isSingleTop = true,
        ),
    )
}

internal class HomeNavigatorImpl(
    private val navigationManager: NavigationManager,
    basicNavigator: DefaultBasicNavigator,
) : HomeNavigator, BasicNavigator by basicNavigator {

    override fun paywall() =
        navigationManager.navigateTo(NavigationDirections(AppRoutes.PAYWALL))
}
