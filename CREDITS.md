# Credits

## Data

- **[Mars Vista API](https://marsvista.dev/docs/explorer)** — rover metadata and photo data for
  all screens in this app.
- **NASA / JPL-Caltech** — the rover photographs themselves, served from `mars.nasa.gov` and
  credited "NASA/JPL-Caltech" per the API's `credit` field. The NASA "worm" logotype used in the
  app's top bar is a NASA trademark, used here for a non-commercial coding exercise only.

## Third-party libraries

| Library | Author | License | Used for |
|---|---|---|---|
| [Kotlin](https://kotlinlang.org/) | JetBrains | Apache 2.0 | Language |
| [kotlinx.coroutines](https://github.com/Kotlin/kotlinx.coroutines) | JetBrains | Apache 2.0 | Async/concurrency (view models, parallel rover fetch) |
| [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization) | JetBrains | Apache 2.0 | JSON parsing of API responses |
| [AndroidX Core / Activity / Lifecycle](https://developer.android.com/jetpack/androidx) | Google | Apache 2.0 | Core Android + ViewModel/SavedStateHandle support |
| [Jetpack Compose](https://developer.android.com/jetpack/compose) (UI, Material 3, Navigation) | Google | Apache 2.0 | Entire UI layer, navigation between the two screens |
| [Dagger Hilt](https://dagger.dev/hilt/) | Google | Apache 2.0 | Dependency injection |
| [Retrofit](https://square.github.io/retrofit/) | Square | Apache 2.0 | Type-safe HTTP client for the Mars Vista API |
| [OkHttp](https://square.github.io/okhttp/) | Square | Apache 2.0 | HTTP transport, API-key header injection, request/response logging |
| [Coil](https://coil-kt.github.io/coil/) | Coil Contributors | Apache 2.0 | Async image loading for rover photos |
| [JUnit 4](https://junit.org/junit4/) | JUnit Team | EPL 1.0 | Unit and instrumented test framework |
| [AndroidX Test / Espresso](https://developer.android.com/training/testing) | Google | Apache 2.0 | Instrumented/E2E test infrastructure |
| [OkHttp MockWebServer](https://github.com/square/okhttp/tree/master/mockwebserver) | Square | Apache 2.0 | Integration-testing the Retrofit/OkHttp stack against canned HTTP responses |

No dependency here is modified from its published form; all are pulled unmodified from Maven
Central / Google's Maven repository via Gradle.
