# Android Development and Validation

## Toolchain and first setup

The Android module is `app/`. `app/build.gradle.kts` currently sets compile/target SDK 37, build-tools 37.0.0, minSdk 26 (Android 8.0), arm64-v8a, and Java/Kotlin target 21. The module requires libxposed API 102 or later. The Gradle wrapper is 9.4.1. The root `hako` runs a pinned Gradle/JDK 21 container as the host UID/GID; Docker and a reachable daemon are host prerequisites. The project root is mounted at `/app` and SDK/Gradle caches live in ignored `.devhome/`.

From the repository root, `./hako bash scripts/setup-android-sdk.sh` installs the specified Android SDK components and accepts their required licenses. It may download the verified command-line-tools archive and other SDK components. Then run `./hako gradle --no-daemon assembleDebug`; the APK is in `app/build/outputs/apk/debug/`. If all dependencies and SDK components are cached, `./hako gradle --offline --no-daemon assembleDebug` also works. Missing cache, Docker permission, network, or SDK license is a concrete blocker, not a passed build. Do not run the setup script merely to inspect the project.

There is no previewable web service, listener, or `preview.sh`: this project produces a native APK. Do not invent a server or `.env` file for this module. `HAKO_IMAGE`, `HAKO_PLATFORM`, and `HAKO_SCOPE` are optional wrapper inputs; a nonempty `HAKO_SERVICE` is rejected. Container access still depends on the active sandbox and Docker permissions.

## Configuration and signing

`app/build.gradle.kts` resolves each release signing value in this order inside the Gradle process: corresponding `RELEASE_*` environment variable, Gradle property, then `keystore.properties`. The property names are `releaseStoreFile`, `releaseStorePassword`, `releaseKeyAlias`, and `releaseKeyPassword`; the corresponding environment names are `RELEASE_STORE_FILE`, `RELEASE_STORE_PASSWORD`, `RELEASE_KEY_ALIAS`, and `RELEASE_KEY_PASSWORD`. The local properties file and `/signing` are ignored. The `hako` script does not automatically forward those host environment variables into the container. Never print or commit signing values, and do not claim a signed release is validated without its actual signing configuration. No release keys are needed for the debug build.

For repeatable local debug installs, use `./hako env JAVA_TOOL_OPTIONS=-Duser.home=/app/.devhome gradle --offline --no-daemon assembleDebug` when dependencies are cached. The JVM then keeps its debug keystore under ignored `.devhome/.android/`; without a persistent JVM home, separate container builds can create different debug signatures and Android will reject an in-place update. Preserve any existing keystore and do not commit it.

For a configuration change, inspect the real consumer and ignore rules, preserve existing local values, and document any new secret-free example and restart or rebuild action. If an existing local configuration needs new keys, append only missing keys, preserve comments, quoting, formatting and empty values, and make a repeat run idempotent. Report renamed or removed keys rather than deleting user values silently. Never treat an empty existing value as permission to overwrite it. Do not add dotenv solely for symmetry with web projects.

## Validation profile

- For task policy or documentation changes, validate relevant Trellis task context with `python3 .trellis/scripts/task.py validate <task>` and run `git diff --check`; verify referenced paths and loader inputs.
- For Android source or build changes, run the debug build above and relevant existing focused checks. JVM tests live under `app/src/test`; run `./hako gradle --offline --no-daemon testDebugUnitTest` when dependencies are cached. No `app/src/androidTest` suite currently exists. Add meaningful focused regression coverage when a task warrants it and record its real command.
- For behavior inside the host, identify the device with `adb devices -l`, use the appropriate serial with `adb -s <serial>`, confirm host package/version and enabled module scope, exercise the changed setting or feature, restart the affected host process when needed, and check the resulting behavior and scoped `BBZQ` logs. Preserve user data and avoid logging tokens or preferences. `AGENTS.md` lists authorized device categories; availability must be checked at execution time.
- Native UI changes follow [ui-validation.md](ui-validation.md). A build alone proves neither the host hook nor UI interaction. For unavailable devices, host versions, private services, or credentials, report the exact missing coverage and use the review gate.
- The existing `.github/workflows/ci.yml` explicitly installs SDK/build-tools 35 while the app requests 37. CI behavior has not been verified by this policy; inspect and reconcile that mismatch in a relevant CI/build task rather than treating CI as an established pass. Release builds also need actual signing evidence.

For any new previewable service, first document its own start/stop, readiness, configuration, listener and published addresses, and test it using a project-owned lifecycle. Do not expose ports, rewrite existing configuration, or assume host accessibility without evidence.
