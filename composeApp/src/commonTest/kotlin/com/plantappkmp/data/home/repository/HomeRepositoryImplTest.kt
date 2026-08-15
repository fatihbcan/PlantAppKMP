package com.plantappkmp.data.home.repository

import com.plantappkmp.data.home.datasource.HomeRemoteDataSource
import com.plantappkmp.data.home.dto.CategoryDto
import com.plantappkmp.data.home.dto.QuestionDto
import com.plantappkmp.domain.home.data.GetCategoriesResult
import com.plantappkmp.domain.home.data.GetQuestionsResult
import com.plantappkmp.platform.network.AppException
import com.plantappkmp.testing.FakeHomeRemoteDataSource
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.test.Test
import kotlinx.coroutines.test.runTest

/**
 * One test per branch of both result types. Nothing here asserts that an
 * exception escapes, because nothing may: the point of the repository is that
 * every failure arrives as a value.
 */
class HomeRepositoryImplTest {

    private val remote = FakeHomeRemoteDataSource()
    private val repository = HomeRepositoryImpl(remote)

    @Test
    fun `getCategories maps and sorts on success`() = runTest {
        remote.onFetchCategories = {
            listOf(
                CategoryDto(id = 1, title = "second", rank = 2),
                CategoryDto(id = 2, title = "first", rank = 1),
            )
        }

        val result = repository.getCategories()

        result.shouldBeInstanceOf<GetCategoriesResult.Success>()
        result.categories.map { it.title } shouldBe listOf("first", "second")
    }

    @Test
    fun `getCategories turns a transport failure into the network case`() = runTest {
        remote.onFetchCategories = { throw AppException.Network() }

        repository.getCategories().shouldBeInstanceOf<GetCategoriesResult.Error.Network>()
    }

    @Test
    fun `getCategories carries the status code through the server case`() = runTest {
        remote.onFetchCategories = { throw AppException.Server(statusCode = 503) }

        val result = repository.getCategories()

        result.shouldBeInstanceOf<GetCategoriesResult.Error.Server>()
        result.statusCode shouldBe 503
    }

    @Test
    fun `getCategories turns a malformed body into the parse case`() = runTest {
        remote.onFetchCategories = { throw AppException.Parse() }

        repository.getCategories().shouldBeInstanceOf<GetCategoriesResult.Error.Parse>()
    }

    @Test
    fun `getCategories falls back to the unknown case`() = runTest {
        remote.onFetchCategories = { throw AppException.Unknown() }

        repository.getCategories().shouldBeInstanceOf<GetCategoriesResult.Error.Unknown>()
    }

    @Test
    fun `getQuestions maps and sorts on success`() = runTest {
        remote.onFetchQuestions = {
            listOf(
                QuestionDto(id = 1, title = "b", order = 2),
                QuestionDto(id = 2, title = "a", order = 1),
            )
        }

        val result = repository.getQuestions()

        result.shouldBeInstanceOf<GetQuestionsResult.Success>()
        result.questions.map { it.title } shouldBe listOf("a", "b")
    }

    @Test
    fun `getQuestions turns a transport failure into the network case`() = runTest {
        remote.onFetchQuestions = { throw AppException.Network() }

        repository.getQuestions().shouldBeInstanceOf<GetQuestionsResult.Error.Network>()
    }

    @Test
    fun `getQuestions carries the status code through the server case`() = runTest {
        remote.onFetchQuestions = { throw AppException.Server(statusCode = 500) }

        val result = repository.getQuestions()

        result.shouldBeInstanceOf<GetQuestionsResult.Error.Server>()
        result.statusCode shouldBe 500
    }

    @Test
    fun `getQuestions turns a malformed body into the parse case`() = runTest {
        remote.onFetchQuestions = { throw AppException.Parse() }

        repository.getQuestions().shouldBeInstanceOf<GetQuestionsResult.Error.Parse>()
    }

    @Test
    fun `getQuestions falls back to the unknown case`() = runTest {
        remote.onFetchQuestions = { throw AppException.Unknown() }

        repository.getQuestions().shouldBeInstanceOf<GetQuestionsResult.Error.Unknown>()
    }
}
