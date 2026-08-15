package com.plantappkmp.presentation.home.model

import com.plantappkmp.core.presentation.mvi.DefaultStateStore

internal class HomeScreenStateStore :
    DefaultStateStore<HomeScreenState, HomeScreenEvent>(
        initialState = HomeScreenState.initial(),
    )
