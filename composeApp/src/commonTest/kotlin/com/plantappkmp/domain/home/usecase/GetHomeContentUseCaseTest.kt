package com.plantappkmp.domain.home.usecase

import com.plantappkmp.domain.home.data.GetCategoriesResult
import com.plantappkmp.domain.home.data.GetQuestionsResult
import com.plantappkmp.domain.home.entities.Category
import com.plantappkmp.domain.home.entities.Question
import com.plantappkmp.testing.FakeHomeRepository
import io.kotest.matchers.shouldBe
import kotlin.test.Test
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest

class GetHomeContentUseCaseTest {

    private val repository = FakeHomeRepository()
    private val useCase = GetHomeContentUseCase(
        getCategories = GetCategoriesUseCase(repository),
        getQuestions = GetQuestionsUseCase(repository),
    )

    @Test
    fun `both endpoints are fetched in parallel — not one after the other`() = runTest {
        // Deliberately slow stubs. An instantly-completing mock never overlaps
        // with anything, so a sequential implementation would pass this test
        // for the wrong reason.
        repository.onGetCategories = {
            delay(ENDPOINT_DELAY_MS)
            GetCategoriesResult.Success(emptyList())
        }
        repository.onGetQuestions = {
            delay(ENDPOINT_DELAY_MS)
            GetQuestionsResult.Success(emptyList())
        }

        val start = currentTime
        useCase()
        val elapsed = currentTime - start

        elapsed shouldBe ENDPOINT_DELAY_MS
    }

    @Test
    fun `a failure in one endpoint leaves the other intact`() = runTest {
        val categories = listOf(Category(1, "Ferns", ""))
        repository.onGetCategories = { GetCategoriesResult.Success(categories) }
        repository.onGetQuestions = { GetQuestionsResult.Error.Network() }

        val content = useCase()

        content.categories shouldBe GetCategoriesResult.Success(categories)
        content.questions shouldBe GetQuestionsResult.Error.Network()
    }

    @Test
    fun `both results come back when both succeed`() = runTest {
        val questions = listOf(Question(1, "Watering", "", "", "", 0))
        repository.onGetCategories = { GetCategoriesResult.Success(emptyList()) }
        repository.onGetQuestions = { GetQuestionsResult.Success(questions) }

        val content = useCase()

        content.questions shouldBe GetQuestionsResult.Success(questions)
        content.categories shouldBe GetCategoriesResult.Success(emptyList())
    }

    private companion object {
        const val ENDPOINT_DELAY_MS = 500L
    }
}
