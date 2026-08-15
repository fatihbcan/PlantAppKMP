package com.plantappkmp.data.onboarding.repository

import com.plantappkmp.data.onboarding.datasource.OnboardingLocalDataSource
import com.plantappkmp.data.onboarding.mapper.toEntity
import com.plantappkmp.domain.onboarding.data.CompleteOnboardingResult
import com.plantappkmp.domain.onboarding.data.GetPlansResult
import com.plantappkmp.domain.onboarding.data.OnboardingStatusResult
import com.plantappkmp.domain.onboarding.repository.OnboardingRepository
import com.plantappkmp.platform.datastore.StorageException

/**
 * Thin translation layer: call the source, map DTOs, turn exceptions into
 * result cases. No business rules live here.
 */
internal class OnboardingRepositoryImpl(
    private val local: OnboardingLocalDataSource,
) : OnboardingRepository {

    override suspend fun readStatus(): OnboardingStatusResult = try {
        if (local.readCompleted()) OnboardingStatusResult.Completed else OnboardingStatusResult.Pending
    } catch (cause: StorageException) {
        OnboardingStatusResult.Unavailable(cause)
    }

    override suspend fun markCompleted(): CompleteOnboardingResult = try {
        local.writeCompleted()
        CompleteOnboardingResult.Success
    } catch (cause: StorageException) {
        CompleteOnboardingResult.Failure(cause)
    }

    override suspend fun getPlans(): GetPlansResult = try {
        GetPlansResult.Success(local.readPlans().map { it.toEntity() })
    } catch (cause: StorageException) {
        GetPlansResult.Failure(cause)
    }
}
