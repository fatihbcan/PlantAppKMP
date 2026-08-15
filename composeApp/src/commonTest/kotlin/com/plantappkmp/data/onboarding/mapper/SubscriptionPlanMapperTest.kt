package com.plantappkmp.data.onboarding.mapper

import com.plantappkmp.data.onboarding.dto.SubscriptionPlanDto
import com.plantappkmp.domain.onboarding.entities.BillingPeriod
import io.kotest.matchers.shouldBe
import kotlin.test.Test

/**
 * The cheapest bug-catcher in the app: the mapper is where an upstream change
 * of shape turns into either a safe default or a wrong screen.
 */
class SubscriptionPlanMapperTest {

    @Test
    fun `a monthly plan maps every field`() {
        val entity = SubscriptionPlanDto(
            id = "monthly",
            period = "monthly",
            formattedPrice = "$2.99",
        ).toEntity()

        entity.id shouldBe "monthly"
        entity.period shouldBe BillingPeriod.MONTHLY
        entity.formattedPrice shouldBe "$2.99"
        entity.trialDays shouldBe 0
        entity.discountPercent shouldBe 0
    }

    @Test
    fun `annual is accepted as a spelling of yearly`() {
        val dto = SubscriptionPlanDto(id = "y", period = "annual", formattedPrice = "$1")

        dto.toEntity().period shouldBe BillingPeriod.YEARLY
    }

    @Test
    fun `period matching ignores case`() {
        val dto = SubscriptionPlanDto(id = "y", period = "YEARLY", formattedPrice = "$1")

        dto.toEntity().period shouldBe BillingPeriod.YEARLY
    }

    @Test
    fun `an unrecognised period falls back to monthly rather than throwing`() {
        val dto = SubscriptionPlanDto(id = "x", period = "fortnightly", formattedPrice = "$1")

        dto.toEntity().period shouldBe BillingPeriod.MONTHLY
    }
}
