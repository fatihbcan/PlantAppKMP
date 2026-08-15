pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

rootProject.name = "PlantAppKMP"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

// One module by design. The Android build of this app splits the same code across fourteen
// Gradle modules to make the layering compiler-enforced; here the layers are packages, and the
// iOS framework — which exports one module anyway — is what the split would have to fight.
include(":composeApp")
