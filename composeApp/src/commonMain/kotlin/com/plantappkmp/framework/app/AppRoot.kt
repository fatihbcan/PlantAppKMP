package com.plantappkmp.framework.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor3.KtorNetworkFetcherFactory
import com.plantappkmp.core.designsystem.theme.AppTheme
import com.plantappkmp.core.presentation.navigation.NavigationCommand
import com.plantappkmp.core.presentation.navigation.NavigationManager
import com.plantappkmp.framework.app.navigation.AppNavHost
import io.ktor.client.HttpClient
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

/**
 * The app's whole UI, so each platform's entry point stays a shell: an
 * Activity on Android, a `UIViewController` on iOS.
 *
 * This is also the only place a `NavController` is touched: the navigation bus
 * is collected here and turned into real calls, which is what keeps every
 * ViewModel free of navigation types.
 *
 * The Android build takes the controller and the bus as parameters, because
 * its Activity is where Hilt could inject them. Both are resolved here
 * instead — there are two entry points now, and neither should have to know
 * how the root is wired.
 */
@Composable
fun AppRoot(
    onFinish: () -> Unit,
    onReady: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Coil fetches through the app's own Ktor client rather than standing up a
    // second HTTP stack per platform — one set of timeouts, one engine.
    val httpClient: HttpClient = koinInject()
    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .components { add(KtorNetworkFetcherFactory(httpClient = { httpClient })) }
            .build()
    }

    val navigationManager: NavigationManager = koinInject()
    val navController = rememberNavController()

    // Acquired here rather than taken as a parameter: this ViewModel is an
    // implementation detail of the composition root, and hoisting it into the
    // signature would force it public for no caller's benefit.
    val viewModel: AppViewModel = koinViewModel()
    val startRoute by viewModel.startRoute.collectAsStateWithLifecycle()

    LaunchedEffect(startRoute) {
        if (startRoute != null) onReady()
    }

    LaunchedEffect(navController) {
        navigationManager.commands.collect { command ->
            when (command) {
                is NavigationCommand.NavigateTo -> navController.navigate(
                    command.directions.route,
                ) {
                    val directions = command.directions
                    launchSingleTop = directions.isSingleTop
                    if (directions.isClearingBackStack) {
                        // The graph itself, inclusively — that is what empties
                        // the stack whatever it holds. Naming a route only
                        // works when that route is on it, and the graph's own
                        // start destination is not: it is fixed at launch by
                        // the onboarding gate, so within a session that
                        // finished onboarding it is a route already popped.
                        popUpTo(navController.graph.id) { inclusive = true }
                    } else {
                        directions.popUpToRoute?.let { route ->
                            popUpTo(route) { inclusive = directions.isPopUpToInclusive }
                        }
                    }
                }

                is NavigationCommand.Back -> {
                    val directions = command.directions
                    val popped = if (directions == null) {
                        navController.popBackStack()
                    } else {
                        navController.popBackStack(directions.route, directions.isInclusive)
                    }
                    // Back from the root screen leaves the app rather than
                    // sitting on an empty stack.
                    if (!popped) onFinish()
                }

                NavigationCommand.Finish -> onFinish()
            }
        }
    }

    AppTheme {
        startRoute?.let { route ->
            AppNavHost(
                navController = navController,
                startRoute = route,
                modifier = modifier,
            )
        }
    }
}
