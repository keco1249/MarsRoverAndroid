# Mars Rovers

An Android app for browsing NASA Mars Rover photos via the [Mars Vista API](https://marsvista.dev/docs/explorer).

- **Home** — a tiled list of every rover (name, launch/landing dates, total photo count, and its
  available cameras).
- **Rover detail** — tapping a rover opens its photos for a single day, defaulting to the most
  recent day the rover has photos for. A date picker lets you jump to any other day between the
  rover's landing date and its most recent photo date; the grid paginates as you scroll.

Built with Kotlin and Jetpack Compose. See [CREDITS.md](CREDITS.md) for every third-party library
in use, and [IMPROVEMENTS.md](IMPROVEMENTS.md) for a review of what's here today versus what a
longer-lived version of this app should add.

## Requirements

- Android Studio Ladybug (or newer) / a JDK 17+ toolchain
- Android SDK platform 37, minimum device OS **Android 10 (API 29)**
- A Mars Vista API key — get one at [marsvista.dev](https://marsvista.dev)

## Setup

1. Clone or unzip the project.
2. Open (or create) `local.properties` in the project root and add your API key:

   ```properties
   MARS_API_KEY=your_key_here
   ```

   `local.properties` is git-ignored; the key is read into `BuildConfig.MARS_API_KEY` at build
   time and never committed. Without it, every API request returns 401/403 and both screens will
   show an error state.

## Building & running

### From Android Studio

Open the project, let Gradle sync, select the `app` run configuration, and hit Run. Use the
build-variant switcher to choose `debug` or `release`.

### From the command line

```bash
# Debug build — installable on any device/emulator, no key required to build (only to load data)
./gradlew assembleDebug
# → app/build/outputs/apk/debug/app-debug.apk

# Release build
./gradlew assembleRelease
# → app/build/outputs/apk/release/app-release.apk

# Install straight onto a connected device/emulator
./gradlew installDebug
```

The release build type has minification/shrinking turned off (`optimization { enable = false }`)
so no keep-rules are required for Retrofit/kotlinx.serialization/Hilt reflection — see
[IMPROVEMENTS.md](IMPROVEMENTS.md) for why that's a deliberate trade-off. It's signed with the
local debug keystore (`~/.android/debug.keystore`, auto-created by the Android SDK tooling) so
`assembleRelease` produces an APK you can install directly, without needing a private release
signing key for this exercise.

To install an APK manually:

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## Running the tests

```bash
# Unit + integration tests (JVM, no device needed)
./gradlew testDebugUnitTest

# End-to-end tests (needs a connected device or running emulator)
./gradlew connectedDebugAndroidTest
```

- **Unit tests** (`app/src/test`) cover the DTO→domain mapping, the API-key interceptor, the
  repository's caching/pagination logic, and both view models' loading/error/pagination state
  machines against fakes — no network or Android framework involved.
- **Integration test** (`MarsApiServiceIntegrationTest`) wires up the real Retrofit + OkHttp +
  kotlinx.serialization stack against an in-process `MockWebServer`, so it exercises the actual
  request/response pipeline (query params, headers, JSON parsing) without hitting the network.
- **End-to-end tests** (`app/src/androidTest`) launch the real `MainActivity` with Hilt's test
  support swapping in a fake repository, and drive the UI exactly as a user would: browsing the
  rover list, navigating into a detail screen, and opening the date picker.

## Architecture

- **UI**: Jetpack Compose, one package per screen (`ui/home`, `ui/rover`) plus a `ui/components`
  package for shared pieces. Each screen composable has two entry points — one that resolves its
  `ViewModel` via `hiltViewModel()`, and a stateless one taking a plain view-state data class —
  so previews and UI tests don't need a DI graph.
- **State**: each screen has a single `ViewModel` exposing one `StateFlow<XViewState>`.
- **Data**: `RoverRepository` is the single source of truth for rover/photo data, backed by
  Retrofit (`MarsApiService`) talking to the Mars Vista API; DTOs in `data/api` are mapped to
  plain domain models in `data/model`, which the UI layer further maps to its own UI models.
- **DI**: Dagger Hilt (`di/NetworkModule`, `di/RepositoryModule`).
