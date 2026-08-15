package com.plantappkmp.data.onboarding.repository

import com.plantappkmp.data.onboarding.dto.SubscriptionPlanDto
import com.plantappkmp.domain.onboarding.data.CompleteOnboardingResult
import com.plantappkmp.domain.onboarding.data.GetPlansResult
import com.plantappkmp.domain.onboarding.data.OnboardingStatusResult
import com.plantappkmp.domain.onboarding.entities.BillingPeriod
import com.plantappkmp.platform.datastore.StorageException
import com.plantappkmp.testing.FakeOnboardingLocalDataSource
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.test.Test
import kotlinx.coroutines.test.runTest

/** One test per branch of every result the repository can return. */
class OnboardingRepositoryImplTest {

    private val local = FakeOnboardingLocalDataSource()
    private val repository = OnboardingRepositoryImpl(local)

    @Test
    fun `readStatus reports completed when the flag is set`() = runTest {
        local.onReadCompleted = { true }

        repository.readStatus() shouldBe OnboardingStatusResult.Completed
    }

    @Test
    fun `readStatus reports pending when the flag is unset`() = runTest {
        local.onReadCompleted = { false }

        repository.readStatus() shouldBe OnboardingStatusResult.Pending
    }

    @Test
    fun `readStatus reports unavailable rather than throwing when storage fails`() = runTest {
        local.onReadCompleted = { throw StorageException("unreadable") }

        repository.readStatus().shouldBeInstanceOf<OnboardingStatusResult.Unavailable>()
    }

    @Test
    fun `markCompleted writes the flag and reports success`() = runTest {
        repository.markCompleted() shouldBe CompleteOnboardingResult.Success
        local.writeCompletedCallCount shouldBe 1
    }

    @Test
    fun `markCompleted reports failure rather than throwing when the write fails`() = runTest {
        local.onWriteCompleted = { throw StorageException("read only") }

        repository.markCompleted().shouldBeInstanceOf<CompleteOnboardingResult.Failure>()
    }

    @Test
    fun `getPlans maps the catalogue to entities`() = runTest {
        local.onReadPlans = {
            listOf(
                SubscriptionPlanDto(
                    "yearly",
                    "yearly",
                    "$529.99",
                    trialDays = 3,
                    discountPercent = 50,
                ),
            )
        }

        val result = repository.getPlans()

        result.shouldBeInstanceOf<GetPlansResult.Success>()
        result.plans.single().period shouldBe BillingPeriod.YEARLY
        result.plans.single().hasDiscount shouldBe true
    }

    @Test
    fun `getPlans reports failure rather than throwing when the source fails`() = runTest {
        local.onReadPlans = { throw StorageException("gone") }

        repository.getPlans().shouldBeInstanceOf<GetPlansResult.Failure>()
    }
}
