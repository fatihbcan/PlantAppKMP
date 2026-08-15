package com.plantappkmp.presentation.onboarding.paywall.model

import com.plantappkmp.core.presentation.mvi.DefaultStateStore

internal class PaywallScreenStateStore :
    DefaultStateStore<PaywallScreenState, PaywallScreenEvent>(
        initialState = PaywallScreenState.initial(),
    )
