package com.plantappkmp.domain.home.repository

import com.plantappkmp.domain.home.data.GetCategoriesResult
import com.plantappkmp.domain.home.data.GetQuestionsResult

/**
 * Read access to the home screen's two collections.
 *
 * No implementation may throw across this boundary; every failure is a case
 * of the operation's result type.
 */
interface HomeRepository {
    suspend fun getCategories(): GetCategoriesResult

    suspend fun getQuestions(): GetQuestionsResult
}
