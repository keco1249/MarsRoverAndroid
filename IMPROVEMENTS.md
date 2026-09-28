# Areas for improvement

A review of what this codebase does today, and what a longer-lived (not take-home-scoped) version
of it should add. None of this is implemented — it's a punch list, ranked roughly by how much it
matters.

## Fixed during this pass (for context)

These were real bugs found while auditing the project, not style nits, so they were corrected
rather than just logged here:

- **A trust-all TLS `X509TrustManager`** was wired into the image-loading `OkHttpClient`, disabling
  certificate *and* hostname validation for every image request. It's gone — `mars.nasa.gov`
  serves images over a normally-trusted certificate, so there was never a reason for it, and
  shipping it would have been a real MITM vulnerability in a "best practices" submission.
- **`assembleRelease` didn't work at all**: `okhttp-logging-interceptor` was `debugImplementation`
  but `NetworkModule` (a main-sourceset file compiled into both variants) referenced
  `HttpLoggingInterceptor` unconditionally, so release compilation failed outright.
- **The `include` query parameter was being sent as a literal parameter *name***
  (`@Query("rover,camera") includes: String = "rover,camera"` instead of `@Query("include")`), so
  the API never actually received the `include=rover,camera` hint the docs describe.
- A `NetworkModule.provideImageLoader()` provider was dead code — nothing ever injected it, since
  Coil resolves its singleton `ImageLoader` via `MarsRoversApplication`'s own
  `SingletonImageLoader.Factory`, not through Hilt.
- Several dead DTO packages (`data/api/camera`, `data/api/rover` (singular), an unused
  `RoversDto.kt`), a fully-commented-out file, an unused `NavigationIconState` sealed class, an
  unused custom `Typography` value, and the default `colors.xml` template leftovers were removed.

## Worth doing next

1. **Enable shrinking for release.** `optimization { enable = false }` is set deliberately here —
   turning on R8/resource shrinking without writing and testing keep rules for
   kotlinx.serialization's reflection-based serializers, Retrofit's dynamic proxy, and Hilt's
   generated components risks a release build that compiles but crashes at runtime, which is worse
   than a large APK for a take-home submission. A real next step: add the keep rules, enable
   `isMinifyEnabled`/`isShrinkResources`, and verify against the full test suite (the debug/release
   APKs are currently 66MB/49MB largely because of this).
2. **Use a dedicated release signing key.** Reusing the debug keystore for `release` (see the
   README) is fine for handing over a runnable APK here, but a real release pipeline needs a
   securely-stored, non-debug key — via Play App Signing or a CI secret, never committed.
3. **Move the debug-only `HttpLoggingInterceptor` into a build-variant source set**
   (`src/debug/.../NetworkModule.kt` providing it, `src/release` not) instead of gating it with
   `if (BuildConfig.DEBUG)` at runtime. That keeps the interceptor class out of the release binary
   entirely rather than just unused.
4. **Sealed UI state instead of flags.** `HomeScreenViewState`/`RoverSelectionViewState` use
   `isLoading: Boolean` + `errorMessage: String?` side by side, which allows representing
   impossible combinations (loading *and* errored at once). A `sealed interface UiState { Loading,
   Success(...), Error(...) }` per screen would make invalid states unrepresentable and is the more
   idiomatic unidirectional-data-flow shape.
5. **Differentiate error causes for the user.** Every failure — no connectivity, a 5xx, a 4xx —
   collapses into the same "Unable to load rovers." string. Splitting "you're offline" from "the
   server had a problem" (and adding a retry action) would meaningfully improve the experience.
6. **Offline support / persistence.** `RoverRepositoryImpl` only caches rovers in an in-memory
   `ConcurrentHashMap` for the life of the process. A Room-backed cache would survive process death
   and let the Home screen render instantly on a cold start with stale-while-revalidate semantics.
7. **Localize user-facing strings.** Error messages and a few UI labels are hardcoded Kotlin string
   literals rather than `strings.xml` resources, so the app can't be localized without a code
   change.
8. **CI.** There's no pipeline running `./gradlew test lint connectedCheck` on push — worth adding
   before this grows past a single contributor.
9. **Static analysis.** No ktlint/detekt/Android Lint baseline is configured beyond the AGP
   defaults; worth adding for consistent formatting and to catch issues like the ones fixed above
   automatically next time.
10. **Compose testing API.** `createAndroidComposeRule` triggers a deprecation warning pointing at
    a newer `androidx.compose.ui.test.junit4.v2` API (which switches the test dispatcher from
    `UnconfinedTestDispatcher` to `StandardTestDispatcher`). Worth migrating once that API
    stabilizes, since it changes how tests need to synchronize with coroutines.
11. **Module boundaries.** Everything lives in a single `:app` module. If this grows past two
    screens, splitting into `:core:network`, `:core:data`, `:feature:home`, `:feature:rover` would
    enforce dependency direction and cut incremental build times.
12. **Accessibility pass.** Images and icons have `contentDescription`s already, but there's been
    no dedicated pass for touch-target sizing (the camera-list chevron in particular) or color
    contrast (`TextSecondary` on white).
13. **Version control.** The project isn't currently a git repository. Before handing this off as a
    "link to a personal GitHub project" (per the challenge's own submission options), initialize
    git, commit in reviewable chunks, and double-check `local.properties` — which currently holds a
    live-looking API key — is excluded (it already is, via `.gitignore`, but it's worth confirming
    the key never ends up in a commit made before `.gitignore` was in place).
