package com.plantappkmp.domain.onboarding.usecase

import com.plantappkmp.domain.onboarding.data.CompleteOnboardingResult
import com.plantappkmp.domain.onboarding.data.GetPlansResult
import com.plantappkmp.domain.onboarding.data.OnboardingStatusResult
import com.plantappkmp.domain.onboarding.repository.OnboardingRepository

/**
 * Reads whether onboarding has already been completed on this device.
 *
 * The composition root calls this once at startup to pick the start
 * destination, which is this architecture's equivalent of a route guard.
 */
class GetOnboardingStatusUseCase(
    private val repository: OnboardingRepository,
) {
    suspend operator fun invoke(): OnboardingStatusResult = repository.readStatus()
}

/**
 * Marks the onboarding flow finished, so the gate stops routing here.
 *
 * Called when the paywall's close button is tapped — the case defines that
 * tap, not a purchase, as the end of onboarding.
 */
class CompleteOnboardingUseCase(
    private val repository: OnboardingRepository,
) {
    suspend operator fun invoke(): CompleteOnboardingResult = repository.markCompleted()
}

/** Loads the plans offered on the paywall. */
class GetSubscriptionPlansUseCase(
    private val repository: OnboardingRepository,
) {
    suspend operator fun invoke(): GetPlansResult = repository.getPlans()
}
