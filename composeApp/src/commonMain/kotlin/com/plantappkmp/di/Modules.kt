package com.plantappkmp.di

import com.plantappkmp.core.presentation.navigation.NavigationManager
import com.plantappkmp.core.util.logging.Logger
import com.plantappkmp.core.util.logging.platformLogger
import com.plantappkmp.data.home.datasource.HomeRemoteDataSource
import com.plantappkmp.data.home.datasource.HomeRemoteDataSourceImpl
import com.plantappkmp.data.home.repository.HomeRepositoryImpl
import com.plantappkmp.data.onboarding.datasource.OnboardingLocalDataSource
import com.plantappkmp.data.onboarding.datasource.OnboardingLocalDataSourceImpl
import com.plantappkmp.data.onboarding.repository.OnboardingRepositoryImpl
import com.plantappkmp.domain.home.repository.HomeRepository
import com.plantappkmp.domain.home.usecase.GetCategoriesUseCase
import com.plantappkmp.domain.home.usecase.GetHomeContentUseCase
import com.plantappkmp.domain.home.usecase.GetQuestionsUseCase
import com.plantappkmp.domain.onboarding.repository.OnboardingRepository
import com.plantappkmp.domain.onboarding.usecase.CompleteOnboardingUseCase
import com.plantappkmp.domain.onboarding.usecase.GetOnboardingStatusUseCase
import com.plantappkmp.domain.onboarding.usecase.GetSubscriptionPlansUseCase
import com.plantappkmp.framework.app.AppViewModel
import com.plantappkmp.framework.app.navigation.DefaultBasicNavigator
import com.plantappkmp.framework.app.navigation.DefaultNavigationManager
import com.plantappkmp.framework.app.navigation.HomeNavigatorImpl
import com.plantappkmp.framework.app.navigation.IntroNavigatorImpl
import com.plantappkmp.framework.app.navigation.PaywallNavigatorImpl
import com.plantappkmp.platform.datastore.DataStoreKeyValueStore
import com.plantappkmp.platform.datastore.KeyValueStore
import com.plantappkmp.platform.network.ApiClient
import com.plantappkmp.platform.network.appHttpClient
import com.plantappkmp.platform.network.appJson
import com.plantappkmp.platform.network.platformHttpEngine
import com.plantappkmp.presentation.home.model.HomeScreenStateStore
import com.plantappkmp.presentation.home.navigation.HomeNavigator
import com.plantappkmp.presentation.home.viewmodel.HomeViewModel
import com.plantappkmp.presentation.onboarding.intro.model.IntroScreenStateStore
import com.plantappkmp.presentation.onboarding.intro.navigation.IntroNavigator
import com.plantappkmp.presentation.onboarding.intro.viewmodel.IntroViewModel
import com.plantappkmp.presentation.onboarding.paywall.model.PaywallScreenStateStore
import com.plantappkmp.presentation.onboarding.paywall.navigation.PaywallNavigator
import com.plantappkmp.presentation.onboarding.paywall.viewmodel.PaywallViewModel
import io.ktor.client.HttpClient
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/**
 * Infrastructure: the HTTP stack and the key-value store.
 *
 * The `DataStore` itself is not here — it needs a file path only the platform
 * knows, so it comes from the platform module the entry point supplies.
 */
val platformApiModule: Module = module {
    single { appJson() }
    single<HttpClient> { appHttpClient(platformHttpEngine()) }
    single { ApiClient(get(), get()) }

    single<KeyValueStore> { DataStoreKeyValueStore(get()) }
    single<Logger> { platformLogger() }
}

/**
 * Onboarding, top to bottom.
 *
 * The Android build puts these `@Binds` inside the feature's own Gradle module
 * so the implementations can stay `internal` to it. There is one module here,
 * so `internal` no longer draws that line — keeping each feature's bindings in
 * its own declaration is what replaces it.
 */
val onboardingModule: Module = module {
    single<OnboardingLocalDataSource> { OnboardingLocalDataSourceImpl(get()) }
    single<OnboardingRepository> { OnboardingRepositoryImpl(get()) }

    factory { GetOnboardingStatusUseCase(get()) }
    factory { CompleteOnboardingUseCase(get()) }
    factory { GetSubscriptionPlansUseCase(get()) }

    factory { IntroScreenStateStore() }
    factory { PaywallScreenStateStore() }

    viewModelOf(::IntroViewModel)
    viewModelOf(::PaywallViewModel)
}

/** Home, top to bottom. */
val homeModule: Module = module {
    single<HomeRemoteDataSource> { HomeRemoteDataSourceImpl(get()) }
    single<HomeRepository> { HomeRepositoryImpl(get()) }

    factory { GetCategoriesUseCase(get()) }
    factory { GetQuestionsUseCase(get()) }
    factory { GetHomeContentUseCase(get(), get()) }

    factory { HomeScreenStateStore() }

    viewModelOf(::HomeViewModel)
}

/**
 * The composition root's own bindings: the navigation bus, every `Navigator`
 * implementation, and the ViewModel that picks the start destination.
 *
 * This is the one declaration in the app that knows all three features exist —
 * `IntroNavigatorImpl` names the paywall route and `PaywallNavigatorImpl`
 * names home's, which is exactly why they live together and away from the
 * features themselves.
 */
val navigationModule: Module = module {
    single<NavigationManager> { DefaultNavigationManager() }
    factory { DefaultBasicNavigator(get()) }

    factory<IntroNavigator> { IntroNavigatorImpl(get(), get()) }
    factory<PaywallNavigator> { PaywallNavigatorImpl(get(), get()) }
    factory<HomeNavigator> { HomeNavigatorImpl(get(), get()) }

    viewModelOf(::AppViewModel)
}

fun appModules(): List<Module> = listOf(
    platformApiModule,
    navigationModule,
    onboardingModule,
    homeModule,
)
