# AGENTS.md

## Repo shape
- Single Android module `:app` (namespace `com.induztek.protocolos`). The Spring Boot microservices live in a separate repo linked from README; there is no backend source here.
- Entry points: `InduztekApp` (app-scoped repository holders + token provider), `MainActivity` (manual `ViewModelProvider` factories), `navigation/AppNavegacion.kt` (entire NavHost, starts at `Login`). New repositories/ViewModels must be wired in `InduztekApp` **and** `MainActivity`.
- `data/remote/*Api.kt` is the real endpoint contract; README's endpoint list has drifted (e.g. formalizar is `POST` in code, `PATCH` in README; no `/api/equipos/internal/{code}`). DTO-to-domain mapping is in `RemoteMappers.kt`.
- Repositories are remote-only (Retrofit + in-memory `StateFlow` caches). No Room dependency despite README; session state is `SharedPreferences` (`SessionManager`), photos are files in `filesDir` (`ImageStorageHelper`), image uploads go to `POST api/imagenes`.

## Build and verification
- Use the wrapper on Windows: `.\gradlew.bat :app:assembleDebug` and `.\gradlew.bat :app:testDebugUnitTest` (Gradle 8.13, JDK 21, Android SDK 35). `local.properties` is gitignored and machine-specific (`sdk.dir`).
- Historical gotcha: putting the `.txt` test docs in `app/src/main/res/values/` breaks `:app:mergeDebugResources` ("The file name must end with .xml"). They now live in `docs/`; never move non-XML files into `res/values/`.
- Unit-test stack is configured in `app/build.gradle.kts`: JUnit4, `kotlinx-coroutines-test`, MockK, MockWebServer, plus `unitTests.isReturnDefaultValues = true`. Compose UI test deps are `androidTestImplementation` only; there are no `androidTest` sources yet.
- No lint/format/typecheck/CI configuration exists here; don't claim those checks run.
- Unit tests are JUnit4 in `app/src/test/java/com/induztek/protocolos`. `docs/*.txt` are written notes, compiled tests are only the `.kt` files under `app/src/test`.

## Runtime config quirks
- API base URL precedence: `-PINDUZTEK_API_BASE_URL=...` > environment variable > `local.properties` > hardcoded Dev Tunnel URL. It is baked into `BuildConfig.API_BASE_URL` (trailing `/` added) — override via those inputs, not generated code.
- `RetrofitClient` adds `Authorization: Bearer <token>` (provider wired in `InduztekApp.onCreate`) and `X-Tunnel-Skip-Anti-Phishing-Page: true`. Debug builds allow cleartext traffic; release builds do not.
- `settings.gradle.kts` removes `ANDROID_PREFS_ROOT` from the JVM environment via reflection; preserve it when editing build config.
- Role codes P-01–P-04 are defined in `model/PerfilUsuario.kt` (including P-02 Ingeniero); README describes only three roles. Permission expectations are pinned by `AnexosDominioTest`.
