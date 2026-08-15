package com.plantappkmp.data.home.mapper

import com.plantappkmp.data.home.dto.CategoryDto
import com.plantappkmp.data.home.dto.QuestionDto
import com.plantappkmp.domain.home.entities.Category
import com.plantappkmp.domain.home.entities.Question

/**
 * DTO → entity conversions, applied at the repository boundary.
 *
 * Nullable wire fields collapse to safe defaults here, so no entity, ViewModel
 * or composable downstream has to reason about nulls from the API.
 */
internal fun CategoryDto.toEntity(): Category = Category(
    id = id,
    title = title.orEmpty(),
    imageUrl = image?.url.orEmpty(),
    rank = rank ?: 0,
)

internal fun QuestionDto.toEntity(): Question = Question(
    id = id,
    title = title.orEmpty(),
    subtitle = subtitle.orEmpty(),
    imageUrl = imageUri.orEmpty(),
    articleUrl = uri.orEmpty(),
    order = order ?: 0,
)

/**
 * Sorted by the API's own `rank`, which the payload does not guarantee.
 *
 * Named for its element type rather than overloading one `toEntities`: the two
 * would erase to the same JVM signature.
 */
internal fun List<CategoryDto>.toCategoryEntities(): List<Category> =
    map { it.toEntity() }.sortedBy { it.rank }

/** Sorted by `order`, for the same reason. */
internal fun List<QuestionDto>.toQuestionEntities(): List<Question> =
    map { it.toEntity() }.sortedBy { it.order }
