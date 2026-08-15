package com.plantappkmp.presentation.onboarding.paywall.viewmodel

import androidx.lifecycle.viewModelScope
import com.plantappkmp.core.presentation.viewmodel.BasicViewModel
import com.plantappkmp.domain.onboarding.data.CompleteOnboardingResult
import com.plantappkmp.domain.onboarding.data.GetPlansResult
import com.plantappkmp.domain.onboarding.entities.SubscriptionPlan
import com.plantappkmp.domain.onboarding.usecase.CompleteOnboardingUseCase
import com.plantappkmp.domain.onboarding.usecase.GetSubscriptionPlansUseCase
import com.plantappkmp.presentation.onboarding.paywall.model.PaywallScreenEvent
import com.plantappkmp.presentation.onboarding.paywall.model.PaywallScreenState
import com.plantappkmp.presentation.onboarding.paywall.model.PaywallScreenStateStore
import com.plantappkmp.presentation.onboarding.paywall.navigation.PaywallNavigator
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Owns the paywall: the plan catalogue, the selection, and the one action that
 * actually ends onboarding.
 *
 * Both the close button and a completed "purchase" record completion, so a
 * user who subscribes is not sent back through onboarding either.
 */
internal class PaywallViewModel(
    stateStore: PaywallScreenStateStore,
    override val navigator: PaywallNavigator,
    private val getPlans: GetSubscriptionPlansUseCase,
    private val completeOnboarding: CompleteOnboardingUseCase,
) : BasicViewModel<PaywallScreenState, PaywallScreenEvent>(stateStore) {

    /**
     * `bloc_concurrency`'s `droppable()`, by hand.
     *
     * Bloc processes events concurrently unless a transformer says otherwise,
     * so the Flutter build declares one per I/O event. Here the equivalent is
     * an explicit job guard — a mashed retry button must not fan out into N
     * in-flight loads. Structured concurrency then gives back what the Flutter
     * version cannot have: `viewModelScope` cancels the request for real when
     * the screen goes away, rather than letting it land on a closed sink.
     */
    private var loadJob: Job? = null

    init {
        load()
    }

    fun onRetryClick() = load()

    fun onPlanClick(planId: String) {
        if (planId == state.value.selectedPlanId) return
        sendEvent(PaywallScreenEvent.PlanSelected(planId))
    }

    /**
     * There is no billing backend in this case, so a "purchase" resolves
     * immediately and its only durable effect is completing onboarding.
     */
    fun onSubscribeClick() {
        if (!state.value.canSubmit) return
        finishOnboarding()
    }

    /** The control the case brief names as the end of the onboarding flow. */
    fun onCloseClick() = finishOnboarding()

    private fun load() {
        if (loadJob?.isActive == true) return
        loadJob = viewModelScope.launch {
            sendEvent(PaywallScreenEvent.LoadStarted)
            when (val result = getPlans()) {
                is GetPlansResult.Success -> sendEvent(
                    PaywallScreenEvent.PlansLoaded(
                        plans = result.plans,
                        defaultPlanId = result.plans.defaultId(),
                    ),
                )

                is GetPlansResult.Failure -> sendEvent(PaywallScreenEvent.PlansLoadFailed)
            }
        }
    }

    private fun finishOnboarding() {
        launchNavigationOnce {
            sendEvent(PaywallScreenEvent.SubmissionStarted)
            when (completeOnboarding()) {
                is CompleteOnboardingResult.Success -> Unit
                // The flag could not be persisted. Let the user through anyway:
                // trapping someone in onboarding is the worse failure. The
                // reducer records it so a test can assert the branch was taken
                // and so it reaches the log, but nothing renders it — the
                // screen that would show it is being torn down.
                is CompleteOnboardingResult.Failure ->
                    sendEvent(PaywallScreenEvent.CompletionFailed)
            }
            navigator.home()
            true
        }
    }
}

/**
 * The plan the design preselects: the discounted one, falling back to the
 * first on offer.
 */
private fun List<SubscriptionPlan>.defaultId(): String? =
    firstOrNull { it.hasDiscount }?.id ?: firstOrNull()?.id
