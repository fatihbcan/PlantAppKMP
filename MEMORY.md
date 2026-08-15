# MEMORY.md

Durable context for this repository — the decisions behind the code, and why they are the way they
are. `AGENT.md` says what to do; this says why.

## Project identity

`PlantAppKMP` — the HUBX Plant App case as a Kotlin Multiplatform app. Android + iOS, one
`composeApp` module, Compose Multiplatform UI. Ported from `../PlantAppAndroidMVI` (14-module
native Android build, same case, same design) in August 2026. Both apps are kept: the native one is
the architecture reference, this one is the shared-code answer.

Package / applicationId / bundle id: `com.plantappkmp` — deliberately different from
`com.plantappmvi.android` so both can sit on one device and be compared side by side.

## Architecture in one picture

```
UI (Compose, common)  ──props──  mapStateToProps  ──state──  StateStore  ──event──  ViewModel
                                                                                       │
                                                                              use cases (domain)
                                                                                       │
                                                                            repositories (data)
                                                                                       │
                                                     Ktor / DataStore (platform, expect-actual)
```

Navigation is a bus: a ViewModel calls its own `Navigator` interface, the implementation in
`framework/app` turns that into a `NavigationCommand`, and `AppRoot` — the only place holding a
`NavController` — executes it.

## Key decisions

### 1. One module, not fourteen
The Android build makes layering compiler-enforced through Gradle modules. That does not carry: the
iOS framework exports one module, `internal` stops being a feature boundary, and 14 KMP modules
would multiply source sets and framework wiring for the same three screens. Layers are packages;
each feature keeps its own Koin module so the "who knows about whom" question still has one answer.

### 2. Koin, because Hilt cannot leave the JVM
Bindings live in `di/Modules.kt`, split by layer and feature. `navigationModule` is the one
declaration that names all three features — the same role `FrameworkModule` plays in the Android
build.

### 3. `startKoin` is called by the platform, not by common code
The only binding that needs a platform handle is the `DataStore` file path, so each entry point
(`PlantAppApplication`, `MainViewController`) builds a one-binding module and passes it to
`initKoin`. Nothing else in the graph is platform-aware.

### 4. `ContentNegotiation` is deliberately not installed
The case API returns JSON labelled `content-type: text/plain`. Ktor's plugin dispatches on that
header and would reject every response; Retrofit picked by return type and never noticed. So
`ApiClient` reads `bodyAsText()` and decodes explicitly. Removing that indirection re-breaks the
app, and the failure looks like a parse error, not a config error.

### 5. `AppException` extends `Exception`, not `IOException`
`IOException` is not in common Kotlin. The four cases and the contract ("nothing but an
`AppException` leaves the network package") are unchanged; `apiCall` does the translation the
OkHttp interceptor used to do.

### 6. Typography is composable now
Compose Multiplatform loads fonts inside composition, so `DefaultAppTypography` (a top-level `val`
on Android) became `appTypography()`, called by `AppTheme` and provided through the same
`LocalAppTypography`. Call sites — `AppTheme.typography.titleLg` — did not change.

### 7. The window replaces `LocalConfiguration`
Both places that read screen size (the compact-dimens breakpoint in `AppTheme`, the card width and
column count in `HomeScreen`) use `LocalWindowInfo.current.containerSize` through `LocalDensity`.

### 8. Fakes instead of MockK
MockK is JVM-only and the shared tests run on iOS. `commonTest/.../testing/Fakes.kt` holds four
hand-written doubles, each a `suspend` lambda per method, which covers returning, throwing and
taking time. `coVerify` became a call counter.

### 9. Coil 3 shares the app's Ktor client
`AppRoot` installs a singleton `ImageLoader` built on `KtorNetworkFetcherFactory` with the injected
client — one HTTP stack, one set of timeouts, both platforms.

## Deviations from the Android build worth remembering

- The three dispatcher qualifiers (`@IoDispatcher` and friends) were dropped: they were bound in
  the Android build and injected by nothing. `Logger` survives as an `expect`/`actual`.
- `AppRoot` resolves the `NavController` and the `NavigationManager` itself instead of taking them
  as parameters, because there are two entry points now and neither should know how the root wires.
- `DayNightPreviews` (two `@Preview`s differing in `uiMode`) became `DayNightPreview { }`, a
  composable that stacks both schemes, because CMP's `@Preview` has no `uiMode`.
- detekt runs without the ktlint `detekt-formatting` ruleset, matching the Android project's setup.

## Known debt

- The intro footer clips its consent line: the box is `height(34.dp)` and then applies
  `navigationBarsPadding()` *inside* that height, leaving ~10dp for an 11sp line. Present in the
  Android build too — verified on both — so fixing it should be done in both repos together.
- No iOS app icon artwork (empty 1024 slot), no `TEAM_ID` in `iosApp/Configuration/Config.xcconfig`;
  device builds need one filled in.
- kotest 5.9.1 assertions are used on native. They work today; if a Kotlin bump breaks the klib,
  the fallback is `kotlin.test` assertions.

## For future sessions

Verify shared code with `linkDebugFrameworkIosSimulatorArm64`, not just the Android compile — the
Android target happily accepts `android.*` and `LocalConfiguration` from `commonMain`, and the iOS
link is where that surfaces.
