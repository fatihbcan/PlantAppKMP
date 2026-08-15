package com.plantappkmp.data.home.datasource

import com.plantappkmp.data.home.dto.CategoriesResponseDto
import com.plantappkmp.data.home.dto.CategoryDto
import com.plantappkmp.data.home.dto.QuestionDto
import com.plantappkmp.platform.network.ApiClient
import com.plantappkmp.platform.network.get

/**
 * The two endpoints of the case API.
 *
 * The Android build declares these as a Retrofit interface; here they are two
 * calls through [ApiClient], which is also what handles the servers's habit of
 * labelling JSON as `text/plain` (see `ApiClient`) and the translation of any
 * failure into an `AppException`. So this class carries the paths and nothing
 * else.
 */
internal interface HomeRemoteDataSource {
    suspend fun fetchCategories(): List<CategoryDto>

    suspend fun fetchQuestions(): List<QuestionDto>
}

internal class HomeRemoteDataSourceImpl(
    private val api: ApiClient,
) : HomeRemoteDataSource {

    override suspend fun fetchCategories(): List<CategoryDto> =
        api.get<CategoriesResponseDto>(CATEGORIES_PATH).data

    override suspend fun fetchQuestions(): List<QuestionDto> =
        api.get<List<QuestionDto>>(QUESTIONS_PATH)

    private companion object {
        const val CATEGORIES_PATH = "getCategories"
        const val QUESTIONS_PATH = "getQuestions"
    }
}
