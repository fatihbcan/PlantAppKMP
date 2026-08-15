package com.plantappkmp.di

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module

/**
 * Starts the graph. Called once per process — from `PlantAppApplication` on
 * Android, from `MainViewController` on iOS — with the one module that cannot
 * be written in common code, because it needs a file path from the platform.
 */
fun initKoin(platformModule: Module): KoinApplication = startKoin {
    modules(appModules() + platformModule)
}
