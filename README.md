# PlantAppKMP — HUBX Case, Kotlin Multiplatform

The Plant App case built once and shipped twice: Android and iOS from one Compose Multiplatform
module, with the same MVI architecture as the native Android build in
[`PlantAppAndroidMVI`](../PlantAppAndroidMVI).

Three screens — onboarding intro (3 pages), paywall, home — over a live categories/questions API
and one persisted onboarding flag.

## Running it

Android:

```bash
./gradlew :composeApp:installDebug
```

iOS — open `iosApp/iosApp.xcodeproj` in Xcode and run, or from the command line:

```bash
xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp -sdk iphonesimulator -destination 'platform=iOS Simulator,name=iPhone 17 Pro' build
```

The Xcode target's first build phase runs `:composeApp:embedAndSignAppleFrameworkForXcode`, so the
Kotlin framework is always rebuilt before the app links against it. `local.properties` needs
`sdk.dir` for the Android side; iOS needs nothing beyond Xcode.

Checks:

```bash
./gradlew :composeApp:allTests :composeApp:testDebugUnitTest :composeApp:detekt
```

`allTests` runs the shared tests on the iOS simulator; `testDebugUnitTest` runs the same 92 tests
on the JVM. Both matter — a test passing on one target says nothing about the other.

## What the app does

- **Intro** — three swipeable pages, headline + artwork + call to action, with a consent line on
  the first page and dots on the rest. Back walks the pages before it leaves the flow.
- **Paywall** — plan catalogue with the discounted plan preselected, feature strip, close control.
  Closing it (or "subscribing", which has no billing backend in this case) is what completes
  onboarding, so it never shows again.
- **Home** — greeting, debounced local search, premium strip, article carousel and a category grid,
  both fetched in parallel and failing independently.

## Layout

One Gradle module, `composeApp`, with the layers the Android build enforces through separate
modules kept as packages:

```
composeApp/src/
├── commonMain/kotlin/com/plantappkmp/
│   ├── core/presentation/     MVI contracts, navigation contracts, TextResource/IconResource,
│   │                          BasicViewModel, BasicScreen
│   ├── core/designsystem/     colours, dimens, shapes, typography, components, icons
│   ├── core/util/             Logger (expect/actual)
│   ├── platform/network/      Ktor client, ApiClient, AppException
│   ├── platform/datastore/    KeyValueStore over Preferences DataStore
│   ├── domain/{onboarding,home}/   entities, sealed results, repository interfaces, use cases
│   ├── data/{onboarding,home}/     DTOs, mappers, data sources, repository implementations
│   ├── presentation/…         one package per screen: model, navigation, view, viewmodel
│   ├── framework/app/         AppRoot, AppNavHost, routes, Navigator implementations
│   └── di/                    Koin modules
├── commonMain/composeResources/    strings, Rubik, the design's webp exports
├── androidMain/               MainActivity, Application, manifest, launcher/splash resources
├── iosMain/                   MainViewController + the iOS actuals
└── commonTest/                92 tests, run on JVM and iOS
```

`iosApp/` is the Xcode wrapper: three Swift files that host the Compose view controller.

## What changed from the Android build, and why

| Android build | Here | Why |
| --- | --- | --- |
| 14 Gradle modules | 1 module, packages | The iOS framework exports one module anyway; `internal` no longer draws feature boundaries, so package discipline and per-feature Koin modules do |
| Hilt | Koin | Hilt is JVM/Android only |
| Retrofit + OkHttp | Ktor (OkHttp / Darwin engines) | Multiplatform HTTP |
| `ErrorInterceptor` | `apiCall` in `ApiClient` | Same four `AppException` cases, no interceptor to hang them on |
| `@StringRes Int` / `@DrawableRes Int` | `StringResource` / `DrawableResource` | Compose Multiplatform resources are typed |
| `LocalConfiguration` | `LocalWindowInfo` + `LocalDensity` | Android-only composition local |
| `BackHandler` | `PlatformBackHandler` expect/actual | iOS has no system back to intercept |
| MockK | hand-written fakes in `commonTest/testing` | MockK cannot run on native |
| DataStore via `Context` delegate | `createPreferencesDataStore(path)` | The platform supplies the file path; everything above it is shared |

Everything else — `ScreenState`/`ScreenEvent.reduce`/`StateStore`, Props + `mapStateToProps`,
per-feature `Navigator` interfaces implemented in the composition root, the navigation bus, the
sealed per-operation results — is the Android code, unchanged.

One genuine platform difference worth naming: the case API answers `content-type: text/plain` with
a JSON body. Retrofit never noticed, because it picks a converter by return type. Ktor's
`ContentNegotiation` dispatches on that header and would refuse every response, so `ApiClient`
reads the body as text and decodes it explicitly.

## Known gaps

- No billing SDK on either platform — the paywall's catalogue is a local list, as in the case.
- No app icon artwork on iOS; the asset catalogue has an empty 1024 slot.
- Only Home has a screen; the other bottom-bar destinations render as unselected, by design.
- The intro footer clips its consent line on both platforms — the footer box is 34dp tall and then
  applies navigation-bar padding inside that. This is inherited from the Android build, which
  behaves identically; fixing it means changing both.
