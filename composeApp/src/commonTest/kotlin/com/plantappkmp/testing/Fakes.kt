package com.plantappkmp.testing

import com.plantappkmp.data.home.datasource.HomeRemoteDataSource
import com.plantappkmp.data.home.dto.CategoryDto
import com.plantappkmp.data.home.dto.QuestionDto
import com.plantappkmp.data.onboarding.datasource.OnboardingLocalDataSource
import com.plantappkmp.data.onboarding.dto.SubscriptionPlanDto
import com.plantappkmp.domain.home.data.GetCategoriesResult
import com.plantappkmp.domain.home.data.GetQuestionsResult
import com.plantappkmp.domain.home.repository.HomeRepository
import com.plantappkmp.domain.onboarding.data.CompleteOnboardingResult
import com.plantappkmp.domain.onboarding.data.GetPlansResult
import com.plantappkmp.domain.onboarding.data.OnboardingStatusResult
import com.plantappkmp.domain.onboarding.repository.OnboardingRepository

/**
 * Hand-written doubles for the four collaborators the tests need to steer.
 *
 * The Android build of this app uses MockK. MockK is JVM-only, and these tests
 * run on iOS too — so each fake is a `suspend` lambda per method, which covers
 * everything the mocks were asked to do: return a value, throw, or take time.
 * `coVerify` becomes a call counter.
 */
internal class FakeHomeRemoteDataSource : HomeRemoteDataSource {
    var onFetchCategories: suspend () -> List<CategoryDto> = { emptyList() }
    var onFetchQuestions: suspend () -> List<QuestionDto> = { emptyList() }

    override suspend fun fetchCategories(): List<CategoryDto> = onFetchCategories()

    override suspend fun fetchQuestions(): List<QuestionDto> = onFetchQuestions()
}

internal class FakeHomeRepository : HomeRepository {
    var onGetCategories: suspend () -> GetCategoriesResult =
        { GetCategoriesResult.Success(emptyList()) }
    var onGetQuestions: suspend () -> GetQuestionsResult =
        { GetQuestionsResult.Success(emptyList()) }

    override suspend fun getCategories(): GetCategoriesResult = onGetCategories()

    override suspend fun getQuestions(): GetQuestionsResult = onGetQuestions()
}

internal class FakeOnboardingLocalDataSource : OnboardingLocalDataSource {
    var onReadCompleted: suspend () -> Boolean = { false }
    var onWriteCompleted: suspend () -> Unit = {}
    var onReadPlans: suspend () -> List<SubscriptionPlanDto> = { emptyList() }

    var writeCompletedCallCount = 0
        private set

    override suspend fun readCompleted(): Boolean = onReadCompleted()

    override suspend fun writeCompleted() {
        writeCompletedCallCount++
        onWriteCompleted()
    }

    override suspend fun readPlans(): List<SubscriptionPlanDto> = onReadPlans()
}

internal class FakeOnboardingRepository : OnboardingRepository {
    var onReadStatus: suspend () -> OnboardingStatusResult = { OnboardingStatusResult.Pending }
    var onMarkCompleted: suspend () -> CompleteOnboardingResult =
        { CompleteOnboardingResult.Success }
    var onGetPlans: suspend () -> GetPlansResult = { GetPlansResult.Success(emptyList()) }

    override suspend fun readStatus(): OnboardingStatusResult = onReadStatus()

    override suspend fun markCompleted(): CompleteOnboardingResult = onMarkCompleted()

    override suspend fun getPlans(): GetPlansResult = onGetPlans()
}
