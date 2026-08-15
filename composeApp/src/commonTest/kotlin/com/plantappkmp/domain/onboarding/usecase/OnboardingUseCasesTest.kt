package com.plantappkmp.domain.onboarding.usecase

import com.plantappkmp.domain.onboarding.data.CompleteOnboardingResult
import com.plantappkmp.domain.onboarding.data.GetPlansResult
import com.plantappkmp.domain.onboarding.data.OnboardingStatusResult
import com.plantappkmp.domain.onboarding.entities.BillingPeriod
import com.plantappkmp.domain.onboarding.entities.SubscriptionPlan
import com.plantappkmp.testing.FakeOnboardingRepository
import io.kotest.matchers.shouldBe
import kotlin.test.Test
import kotlinx.coroutines.test.runTest

/**
 * One test per branch of each result type. Adding a failure mode to a result
 * without adding a test here leaves a reachable path unexercised.
 */
class OnboardingUseCasesTest {

    private val repository = FakeOnboardingRepository()

    @Test
    fun `GetOnboardingStatus returns completed when the flag is set`() = runTest {
        repository.onReadStatus = { OnboardingStatusResult.Completed }

        GetOnboardingStatusUseCase(repository)() shouldBe OnboardingStatusResult.Completed
    }

    @Test
    fun `GetOnboardingStatus returns pending when the flag is unset`() = runTest {
        repository.onReadStatus = { OnboardingStatusResult.Pending }

        GetOnboardingStatusUseCase(repository)() shouldBe OnboardingStatusResult.Pending
    }

    @Test
    fun `GetOnboardingStatus surfaces an unreadable flag rather than guessing`() = runTest {
        val cause = IllegalStateException("disk")
        repository.onReadStatus = { OnboardingStatusResult.Unavailable(cause) }

        val result = GetOnboardingStatusUseCase(repository)()

        result shouldBe OnboardingStatusResult.Unavailable(cause)
    }

    @Test
    fun `CompleteOnboarding reports success`() = runTest {
        repository.onMarkCompleted = { CompleteOnboardingResult.Success }

        CompleteOnboardingUseCase(repository)() shouldBe CompleteOnboardingResult.Success
    }

    @Test
    fun `CompleteOnboarding reports a write failure`() = runTest {
        val cause = IllegalStateException("read only")
        repository.onMarkCompleted = { CompleteOnboardingResult.Failure(cause) }

        CompleteOnboardingUseCase(repository)() shouldBe CompleteOnboardingResult.Failure(cause)
    }

    @Test
    fun `GetSubscriptionPlans returns the catalogue`() = runTest {
        val plans = listOf(SubscriptionPlan("monthly", BillingPeriod.MONTHLY, "$2.99"))
        repository.onGetPlans = { GetPlansResult.Success(plans) }

        GetSubscriptionPlansUseCase(repository)() shouldBe GetPlansResult.Success(plans)
    }

    @Test
    fun `GetSubscriptionPlans reports a failure`() = runTest {
        repository.onGetPlans = { GetPlansResult.Failure() }

        GetSubscriptionPlansUseCase(repository)() shouldBe GetPlansResult.Failure()
    }
}
