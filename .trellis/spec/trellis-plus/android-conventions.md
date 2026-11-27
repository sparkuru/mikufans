# Android and LSPosed Conventions

These conventions describe the current implementation and identify the checks needed when changing it. Target-host behavior still requires evidence from the relevant APK and device; a successful module build alone does not demonstrate a working hook.

## Layout and user interface

The Android application is under `app/`. Kotlin application, settings, and bridge code lives in `app/src/main/java/im/majo/mikufans/`; shared hook and reflection helpers are in its `feats/` directory, and individual feature hooks are in `feats/hook/`. Android resources are under `app/src/main/res/`, and libxposed metadata is under `app/src/main/resources/META-INF/xposed/`.

The current settings UI uses native Android Views. [`SettingsActivity.kt`](../../../app/src/main/java/im/majo/mikufans/SettingsActivity.kt) owns the activity and navigation, while [`SettingsContentFactory.kt`](../../../app/src/main/java/im/majo/mikufans/SettingsContentFactory.kt) builds page content. Extend those paths for a task that changes this UI; do not assume a web frontend or Compose implementation exists. User-facing strings also have Android resource definitions in `app/src/main/res/values/strings.xml`.

## Module, process, and hook registration

[`java_init.list`](../../../app/src/main/resources/META-INF/xposed/java_init.list) names `im.majo.mikufans.BbzqModule`; [`module.prop`](../../../app/src/main/resources/META-INF/xposed/module.prop) specifies a minimum libxposed API of 102, a target API of 102, and static scope. The target is not a maximum; API versions above 102 have not been independently verified. [`scope.list`](../../../app/src/main/resources/META-INF/xposed/scope.list) lists four target packages, including `com.bilibili.app.in`. Treat this as configured scope, not proof that every feature is compatible with every listed host.

[`BbzqModule.kt`](../../../app/src/main/java/im/majo/mikufans/BbzqModule.kt) checks target package and process, starts the runtime after application attachment or through the current application, and uses atomic guards against repeated startup. When changing supported targets, review the scope file, `BbzqModule` target set, [`ModuleSettingsProvider.kt`](../../../app/src/main/java/im/majo/mikufans/ModuleSettingsProvider.kt) caller allowlist, and [`AndroidManifest.xml`](../../../app/src/main/AndroidManifest.xml) package queries together.

[`RoamingRuntime.kt`](../../../app/src/main/java/im/majo/mikufans/feats/RoamingRuntime.kt) places factories in main, `:web`, or `:download` process lists and ignores unsupported processes. Feature hooks extend `BaseRoamingHook` and implement `startHook()`; [`ShareHook.kt`](../../../app/src/main/java/im/majo/mikufans/feats/hook/ShareHook.kt) shows registration in both main and web scopes and counts resolved methods. Put a new hook in the process where its target actually runs, and reuse [`Reflect.kt`](../../../app/src/main/java/im/majo/mikufans/feats/Reflect.kt) and [`Api102Hook.kt`](../../../app/src/main/java/im/majo/mikufans/feats/Api102Hook.kt) where they fit.

Reflection helpers may return `null`, and individual method helpers may return zero when a target is absent. Those values are observations, not evidence that the intended behavior is installed. Obfuscated class names and method signatures are host-version-sensitive: inspect the actual 3.20.4 APK when relevant, record the matched target and process, and exercise the affected feature on an appropriate device.

### Scenario: Android API boundaries for hooks and settings navigation

#### 1. Scope / trigger

Apply this contract when changing the libxposed API 102 hook boundary, host class-loader selection, settings Back navigation, or window insets. The application declares `minSdk=26` (Android 8.0) and requires libxposed API 102 or newer. The cached API, service, and service-interface 102 AAR manifests each declare a minimum of API 26; the application does not override their minimum SDK. This minimum does not prove compatibility on API 26–29. Vector's upstream declares Android 8.1 as its own minimum; a framework supporting both Android 8.0 and libxposed API 102 has not yet been verified.

#### 2. Signatures

- `RoamingEnv.hookBefore`, `hookAfter`, and `replace` accept `Executable`; `Method` and `Constructor<*>` are compatible with that type on the app's supported API levels.
- `BbzqModule.onPackageLoaded(PackageLoadedParam)` starts the host runtime once, choosing the application class loader and then the framework default on API 29+ or `Context.classLoader` below 29 when needed.
- `SettingsActivity` routes both its API 33+ `OnBackInvokedCallback` and its older `onBackPressed()` path through the same navigation handler.

#### 3. Contracts

Keep the app minimum at API 26 and the module's libxposed minimum at API 102 unless the user approves a support-policy change. Do not assume the host class loader is always the module class loader or invoke `PackageLoadedParam.getDefaultClassLoader()` below API 29. On API 33+ the activity registers the system Back callback and unregisters it on destruction; on older systems the legacy callback remains necessary. The hidden-features page returns to settings root, while other pages finish. System-bar and display-cutout insets use the API 30 interface on API 30+, the available legacy interfaces on API 26–29, and cutout safety values only on API 28+.

#### 4. Validation and error matrix

| Condition | Required behavior |
| --- | --- |
| Device API below 26 | The package declares API 26 as its install minimum; these systems are outside the supported app range. |
| API 26–28 application class loader is unavailable | Use `Context.classLoader` without calling the API 29 framework method. |
| API 29+ application class loader is unavailable | Use the framework default class loader. |
| API 33+ system Back is invoked | Run the same page-specific action as the legacy Back path. |
| API 26–27 window insets | Use available system-window insets without accessing display-cutout APIs. |

#### 5. Good / base / bad cases

Good: an API 35 or API 36 gesture returns from hidden features to settings root. Base: an API 30 Back event follows the same transition. Bad: treating the dependency's API 26 minimum as proof of a working API 26 framework or device, or globally suppressing `NewApi` instead of guarding the runtime boundary.

#### 6. Tests required

Run `lintDebug`, `testDebugUnitTest`, and `assembleDebug`; inspect the generated APK's minSdk and packaged module metadata. Exercise settings Back and visible insets on available older and newer devices, and verify module startup plus the changed host path on the target host. Record untested API versions, cutout devices, hot reload, and individual hook paths explicitly; an Android 16 edge-gesture result does not prove all predictive animations or API 26–29 compatibility.

#### 7. Wrong vs correct

```kotlin
// Wrong: this framework method is API 29+.
val loader = param.getDefaultClassLoader()

// Correct when the application's own class loader is unavailable.
val loader = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
    param.getDefaultClassLoader()
} else {
    application.classLoader
}
```

## Settings and cross-process data

[`ModuleSettings.kt`](../../../app/src/main/java/im/majo/mikufans/ModuleSettings.kt) defines the `bbzq_settings` preference name, keys, defaults, and typed accessors. For example, `isSkipSplashAdEnabled` reads its boolean key with default `true`. Reuse these definitions in UI and hook code instead of introducing competing key strings or defaults. Persisted keys, types, set encodings, page identifiers, provider method names, and Bundle fields are compatibility-sensitive; trace existing readers and writers before changing them.

`SettingsActivity` wraps its private `SharedPreferences` in [`ReadableModulePreferences.kt`](../../../app/src/main/java/im/majo/mikufans/ReadableModulePreferences.kt), which records editor operations and delegates remote synchronization to [`ModuleRemotePreferences.kt`](../../../app/src/main/java/im/majo/mikufans/ModuleRemotePreferences.kt). Hooks access `env.prefs`, supplied by [`ModuleSettingsBridge.kt`](../../../app/src/main/java/im/majo/mikufans/ModuleSettingsBridge.kt). The bridge reads remote preferences and the content provider; when both return values, provider values win duplicate keys. Its nonempty cache and provider retry logic use a five-second interval, with fallback defaults when neither source supplies values.

The bridge editor's `commit()` invokes `apply()` and returns `true`; provider writes use preference `apply()`, so that return value does not establish end-to-end durable persistence. Verify a setting change across the actual process boundary and restart when that behavior is in scope. The provider authorizes a caller when a package associated with its UID matches the module or allowlist; preserve and test that boundary when changing access. [`AccessKeyRepository.kt`](../../../app/src/main/java/im/majo/mikufans/AccessKeyRepository.kt) can retain a validated access key: never include preference dumps, access keys, tokens, or private account values in logs or task evidence.

### Scenario: update-blocking setting across module and host

#### 1. Scope / trigger

Use this contract when changing the `block_update_enabled` switch or its hook for `com.bilibili.app.in` 3.20.4. Android 11 device testing showed that the host may not resolve the module provider even while libxposed remote preferences work.

#### 2. Signatures

- `ModuleSettings.KEY_BLOCK_UPDATE_ENABLED: String` identifies the Boolean preference; `DEFAULT_BLOCK_UPDATE_ENABLED` is `true`.
- `ModuleSettingsBridge.getBooleanFromAvailableSources(key: String, defValue: Boolean): Boolean?` reads the provider first and then remote preferences; `null` means neither value was obtainable.
- `ModuleRemotePreferences.setBlockUpdateEnabled(context: Context, enabled: Boolean): Boolean` coordinates this switch's local and remote writes. Its result says whether both writes were confirmed, not that two stores are atomic.

#### 3. Contracts

The hook runs only in the target package's main process, version code `8230800` / version name `3.20.4`, on the app's supported Android 8.0 or newer. If the setting is unavailable, it leaves the host method in control. Once a hook instance reads `false`, that instance stays disabled for its lifetime. The settings page says to restart the host and reports when synchronization is unconfirmed; hot reload may create another instance and was not separately verified.

#### 4. Validation and error matrix

| Condition | Required behavior |
| --- | --- |
| Provider available | Use its Boolean, including the configured default for a missing key. |
| Provider unavailable, remote readable | Use the remote Boolean; the Android 11 board exercised this path. |
| Both reads unavailable or throw | Do not block the host update call; log without private data. |
| Setting write cannot be confirmed | Attempt compensation and show the synchronization notice; do not claim an atomic commit. |
| A confirmed `false` was read earlier by this hook instance | Keep update blocking disabled for that instance, even if a later source is stale. |

#### 5. Good / base / bad cases

- Good: disabling the switch writes remote then local, restarting the host restores the original update behavior, and the UI agrees with the saved state.
- Base: an unset, readable preference defaults to enabled and a blocked manual check gives an explicit notice.
- Bad: treating an unavailable source or the bridge's generic fallback as proof of a saved value, or presenting an uncertain two-store write as successful.

#### 6. Tests required

Run `testDebugUnitTest` for source priority, unavailable reads, hook-instance disabled state, and local/remote write and compensation failures. For changed host behavior, verify the setting across a real process restart and use a controlled forced-update object plus logs; a build or missing dialog alone is insufficient. Existing stale snapshots, process death between writes, repeated compensation failures, hot reload, the direct `FORCE` action, and real server responses require explicit evidence before claiming coverage.

#### 7. Wrong vs correct

```kotlin
// Wrong for this hook: a generic fallback can hide unavailable settings.
val enabled = prefs.getBoolean(ModuleSettings.KEY_BLOCK_UPDATE_ENABLED, true)

// Correct: preserve the unavailable state and let the hook fail open.
val enabled = ModuleSettingsBridge.instance.getBooleanFromAvailableSources(
    ModuleSettings.KEY_BLOCK_UPDATE_ENABLED,
    ModuleSettings.DEFAULT_BLOCK_UPDATE_ENABLED,
)
```

No application SQL, ORM, or migration convention has been established by the current source. Follow the actual preferences and bridge contracts above rather than inventing a database layer.

## Errors and verification

`RoamingRuntime` wraps each constructed hook's `startHook()` call in `runCatching`, logs failures, and continues with later hooks. Factory construction happens outside that catch. The runtime's final installed count is the number of factories selected, not a confirmation that every target method was found or every hook succeeded. `BbzqModule` logs ordinary runtime messages at INFO and messages with throwables at WARN using the `BBZQ` tag; remote synchronization failures also use a warning.

`Api102Hook` sets callback interception to `ExceptionMode.PASSTHROUGH`, so initialization isolation does not imply that exceptions during later hook callbacks are isolated. Handle expected failures at the actual boundary, retain actionable context without secrets, and avoid reporting a skipped or missing target as success. For behavior changes, combine the applicable build checks with host version, module enablement and scope, the changed user action, resulting state, and relevant sanitized logs; document anything that could not be verified.
