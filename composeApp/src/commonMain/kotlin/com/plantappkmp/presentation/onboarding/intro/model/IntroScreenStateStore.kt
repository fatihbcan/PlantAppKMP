package com.plantappkmp.presentation.onboarding.intro.model

import com.plantappkmp.core.presentation.mvi.DefaultStateStore

internal class IntroScreenStateStore :
    DefaultStateStore<IntroScreenState, IntroScreenEvent>(
        initialState = IntroScreenState.initial(),
    )
