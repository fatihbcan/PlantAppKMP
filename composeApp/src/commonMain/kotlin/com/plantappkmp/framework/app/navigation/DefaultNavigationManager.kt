package com.plantappkmp.framework.app.navigation

import com.plantappkmp.core.presentation.navigation.BasicNavigator
import com.plantappkmp.core.presentation.navigation.NavigationBackDirections
import com.plantappkmp.core.presentation.navigation.NavigationCommand
import com.plantappkmp.core.presentation.navigation.NavigationDirections
import com.plantappkmp.core.presentation.navigation.NavigationManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
internal class DefaultNavigationManager : NavigationManager {

    // Buffered rather than replayed: a command issued while the Activity is
    // rebuilding must still arrive, but replaying one after a configuration
    // change would navigate twice.
    private val internalCommands = MutableSharedFlow<NavigationCommand>(
        extraBufferCapacity = COMMAND_BUFFER,
    )

    override val commands: SharedFlow<NavigationCommand> = internalCommands.asSharedFlow()

    override fun navigateTo(directions: NavigationDirections) {
        internalCommands.tryEmit(NavigationCommand.NavigateTo(directions))
    }

    override fun navigateBack(directions: NavigationBackDirections?) {
        internalCommands.tryEmit(NavigationCommand.Back(directions))
    }

    override fun finish() {
        internalCommands.tryEmit(NavigationCommand.Finish)
    }

    private companion object {
        const val COMMAND_BUFFER = 8
    }
}

/** `back()`, implemented once, delegated to by every NavigatorImpl. */
internal class DefaultBasicNavigator(
    private val navigationManager: NavigationManager,
) : BasicNavigator {
    override fun back() = navigationManager.navigateBack()
}
