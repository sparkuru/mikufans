# Android and LSPosed Conventions

These conventions describe the current implementation and identify the checks needed when changing it. Target-host behavior still requires evidence from the relevant APK and device; a successful module build alone does not demonstrate a working hook.

## Layout and user interface

The Android application is under `app/`. Kotlin application, settings, and bridge code lives in `app/src/main/java/im/majo/mikufans/`; shared hook and reflection helpers are in its `feats/` directory, and individual feature hooks are in `feats/hook/`. Android resources are under `app/src/main/res/`, and libxposed metadata is under `app/src/main/resources/META-INF/xposed/`.

The current settings UI uses native Android Views. [`SettingsActivity.kt`](../../../app/src/main/java/im/majo/mikufans/SettingsActivity.kt) owns the activity and navigation, while [`SettingsContentFactory.kt`](../../../app/src/main/java/im/majo/mikufans/SettingsContentFactory.kt) builds page content. Extend those paths for a task that changes this UI; do not assume a web frontend or Compose implementation exists. User-facing strings also have Android resource definitions in `app/src/main/res/values/strings.xml`.

## Module, process, and hook registration

[`java_init.list`](../../../app/src/main/resources/META-INF/xposed/java_init.list) names `im.majo.mikufans.BbzqModule`; [`module.prop`](../../../app/src/main/resources/META-INF/xposed/module.prop) specifies libxposed API 102 and static scope. [`scope.list`](../../../app/src/main/resources/META-INF/xposed/scope.list) lists four target packages, including `com.bilibili.app.in`. Treat this as configured scope, not proof that every feature is compatible with every listed host.

[`BbzqModule.kt`](../../../app/src/main/java/im/majo/mikufans/BbzqModule.kt) checks target package and process, starts the runtime after application attachment or through the current application, and uses atomic guards against repeated startup. When changing supported targets, review the scope file, `BbzqModule` target set, [`ModuleSettingsProvider.kt`](../../../app/src/main/java/im/majo/mikufans/ModuleSettingsProvider.kt) caller allowlist, and [`AndroidManifest.xml`](../../../app/src/main/AndroidManifest.xml) package queries together.

[`RoamingRuntime.kt`](../../../app/src/main/java/im/majo/mikufans/feats/RoamingRuntime.kt) places factories in main, `:web`, or `:download` process lists and ignores unsupported processes. Feature hooks extend `BaseRoamingHook` and implement `startHook()`; [`ShareHook.kt`](../../../app/src/main/java/im/majo/mikufans/feats/hook/ShareHook.kt) shows registration in both main and web scopes and counts resolved methods. Put a new hook in the process where its target actually runs, and reuse [`Reflect.kt`](../../../app/src/main/java/im/majo/mikufans/feats/Reflect.kt) and [`Api102Hook.kt`](../../../app/src/main/java/im/majo/mikufans/feats/Api102Hook.kt) where they fit.

Reflection helpers may return `null`, and individual method helpers may return zero when a target is absent. Those values are observations, not evidence that the intended behavior is installed. Obfuscated class names and method signatures are host-version-sensitive: inspect the actual 3.20.4 APK when relevant, record the matched target and process, and exercise the affected feature on an appropriate device.

## Settings and cross-process data

[`ModuleSettings.kt`](../../../app/src/main/java/im/majo/mikufans/ModuleSettings.kt) defines the `bbzq_settings` preference name, keys, defaults, and typed accessors. For example, `isSkipSplashAdEnabled` reads its boolean key with default `true`. Reuse these definitions in UI and hook code instead of introducing competing key strings or defaults. Persisted keys, types, set encodings, page identifiers, provider method names, and Bundle fields are compatibility-sensitive; trace existing readers and writers before changing them.

`SettingsActivity` wraps its private `SharedPreferences` in [`ReadableModulePreferences.kt`](../../../app/src/main/java/im/majo/mikufans/ReadableModulePreferences.kt), which records editor operations and delegates remote synchronization to [`ModuleRemotePreferences.kt`](../../../app/src/main/java/im/majo/mikufans/ModuleRemotePreferences.kt). Hooks access `env.prefs`, supplied by [`ModuleSettingsBridge.kt`](../../../app/src/main/java/im/majo/mikufans/ModuleSettingsBridge.kt). The bridge reads remote preferences and the content provider; when both return values, provider values win duplicate keys. Its nonempty cache and provider retry logic use a five-second interval, with fallback defaults when neither source supplies values.

The bridge editor's `commit()` invokes `apply()` and returns `true`; provider writes use preference `apply()`, so that return value does not establish end-to-end durable persistence. Verify a setting change across the actual process boundary and restart when that behavior is in scope. The provider authorizes a caller when a package associated with its UID matches the module or allowlist; preserve and test that boundary when changing access. [`AccessKeyRepository.kt`](../../../app/src/main/java/im/majo/mikufans/AccessKeyRepository.kt) can retain a validated access key: never include preference dumps, access keys, tokens, or private account values in logs or task evidence.

No application SQL, ORM, or migration convention has been established by the current source. Follow the actual preferences and bridge contracts above rather than inventing a database layer.

## Errors and verification

`RoamingRuntime` wraps each constructed hook's `startHook()` call in `runCatching`, logs failures, and continues with later hooks. Factory construction happens outside that catch. The runtime's final installed count is the number of factories selected, not a confirmation that every target method was found or every hook succeeded. `BbzqModule` logs ordinary runtime messages at INFO and messages with throwables at WARN using the `BBZQ` tag; remote synchronization failures also use a warning.

`Api102Hook` sets callback interception to `ExceptionMode.PASSTHROUGH`, so initialization isolation does not imply that exceptions during later hook callbacks are isolated. Handle expected failures at the actual boundary, retain actionable context without secrets, and avoid reporting a skipped or missing target as success. For behavior changes, combine the applicable build checks with host version, module enablement and scope, the changed user action, resulting state, and relevant sanitized logs; document anything that could not be verified.
