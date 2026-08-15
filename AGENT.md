# AGENT.md

Operating rules for working in this repository. Read this before changing anything.

## What this project is

The Plant App case as a Kotlin Multiplatform app: Android + iOS from one Compose Multiplatform
module (`composeApp`), Koin for DI, Ktor for HTTP, Preferences DataStore for the single persisted
flag. It is a port of the native Android build in `../PlantAppAndroidMVI` and deliberately keeps
that project's architecture — the differences are listed in `README.md` and none of them are
stylistic.

## Commands

```bash
./gradlew :composeApp:installDebug            # Android, onto a running device/emulator
./gradlew :composeApp:allTests                # shared tests on the iOS simulator
./gradlew :composeApp:testDebugUnitTest       # the same tests on the JVM
./gradlew :composeApp:detekt                  # every source set
./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64   # fastest check that commonMain is platform-free
```

Run the iOS app from Xcode (`iosApp/iosApp.xcodeproj`, scheme `iosApp`).

## Dependency rules — never violate

The module boundaries the Android build gets from Gradle are package boundaries here. Nothing
enforces them but review, so they matter more, not less:

- `domain` imports nothing but Kotlin and other `domain`. No Ktor, no Compose, no Koin, no
  platform types.
- `data` may see `domain` and `platform`, never `presentation`.
- `presentation` may see `domain`, `core`, and its own feature package. **A feature package never
  imports another feature package.**
- `framework/app` and `di` are the only places allowed to know that more than one feature exists.
- `core/designsystem` speaks Props and `TextResource`, never domain entities.

## MVI — how every screen works

1. `XScreenState` — one flat immutable data class, plus derived `val`s for anything the UI asks.
2. `XScreenEvent` — a sealed interface where **each case reduces itself** (`fun reduce(old): New`).
   There is no central `when`, and adding a case must never mean editing one.
3. `XScreenStateStore` — `DefaultStateStore<State, Event>` with the initial state.
4. `XViewModel : BasicViewModel<State, Event>` — holds the store and an injected `XNavigator`,
   calls use cases, sends events. It never formats a string and never touches navigation types.
5. `XScreenProps` + `mapStateToProps(state, callbacks)` — a **pure, non-`@Composable`** function
   that decides everything the UI shows. This is where presentation logic is tested.
6. `XScreen` — `BasicScreen(viewModel) { XContent(mapStateToProps(...)) }`. Content composables
   take Props and nothing else.

`reduce` must stay pure: `copy` only, no I/O, no clock reads, no logging.

### Concurrency

- Use `viewModelScope`; structured concurrency cancels in-flight work when the screen goes away.
- "Droppable" work (load, refresh) is guarded with `if (job?.isActive == true) return`.
- "Restartable" work (search) is a `MutableStateFlow` + `debounce` + `collectLatest`.
- Navigation that must not double-fire goes through `launchNavigationOnce`.

### Adding a screen

State → events → store → navigator interface → ViewModel → Props + `mapStateToProps` → Screen →
route constant in `AppRoutes` → destination in `AppNavHost` → `NavigatorImpl` in `Navigators.kt` →
bindings in `di/Modules.kt` (feature module for the ViewModel and store, `navigationModule` for the
navigator). Tests for the reducer and `mapStateToProps` land in `commonTest`.

## Multiplatform rules

- New code goes in `commonMain` by default. Reach for `expect`/`actual` only when the platform API
  genuinely differs, and keep the `expect` surface as small as the app needs (see
  `PlatformBackHandler`, `platformHttpEngine`, `platformLogger`).
- `linkDebugFrameworkIosSimulatorArm64` is the cheapest way to catch an Android-only API that
  compiled fine for Android — run it before claiming shared code works.
- Resources are `Res.string.x` / `Res.drawable.x` / `Res.font.x` from `com.plantappkmp.resources`,
  each accessor imported explicitly. Never add an Android `res/` string for UI copy.
- `commonTest` runs on Kotlin/Native too: no MockK (use the fakes in `commonTest/.../testing`), no
  JUnit annotations (`kotlin.test`), and **no commas inside backticked test names** — Kotlin/Native
  rejects them.

## Compose rules

- Every colour, size, radius and text style comes from `AppTheme.*`. A literal `16.dp` or
  `Color(0xFF…)` in a screen is a bug.
- Content composables take a single Props parameter. No ViewModel below the screen function.
- `@Composable` functions that need window size use `LocalWindowInfo` + `LocalDensity`.
- Previews use `@Preview` from `org.jetbrains.compose.ui.tooling.preview` and wrap content in
  `DayNightPreview { }` so both schemes render in one frame.

## Testing

- Reducers: one test per event case, asserting the whole state transition.
- `mapStateToProps`: one test per branch — every message, every visibility flag.
- Repositories and use cases: one test per result case, using the fakes.
- Never assert on an exception escaping a repository; failures are values.

## Never introduce

Hilt or any JVM-only DI, Retrofit/OkHttp in shared code, `android.*` imports outside `androidMain`,
a `UiState` sealed hierarchy in place of flat state, a central reducer `when`, one-shot event
channels for navigation, a detekt baseline.
