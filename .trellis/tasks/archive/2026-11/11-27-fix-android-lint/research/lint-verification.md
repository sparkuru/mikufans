# Android Lint 修复与验证记录

## 范围与结果

- 修复前执行 `./hako env JAVA_TOOL_OPTIONS=-Duser.home=/app/.devhome gradle --offline --no-daemon lintDebug`：79 个错误、48 个警告。
- 初轮修复后、最低版本仍为 API 24 时执行 `./hako env JAVA_TOOL_OPTIONS=-Duser.home=/app/.devhome gradle --offline --no-daemon lintDebug testDebugUnitTest assembleDebug`：成功，Lint 为 0 个错误、11 个警告；18 项 JVM 单元测试通过。报告位于构建产物 `app/build/reports/lint-results-debug.txt`，不会作为长期证据保留。
- 用户先要求最低 Android 8.1 / API 27，随后纠正为与依赖一致的 Android 8.0 / API 26，并保持 libxposed API 102+。本地 libxposed API、service 与 service interfaces 102 AAR 均声明 `minSdk=26`，因此新的应用最低版本不需要 `tools:overrideLibrary`。`META-INF/xposed/module.prop` 声明 `minApiVersion=102`、`targetApiVersion=102`；[libxposed 官方说明](https://libxposed.github.io/api/io/github/libxposed/api/package-summary.html)区分最低 API 与目标 API，目标值并非最高版本。
- API 27 阶段执行 `./hako env JAVA_TOOL_OPTIONS=-Duser.home=/app/.devhome gradle --offline --no-daemon --rerun-tasks lintDebug testDebugUnitTest assembleDebug` 成功；Lint 为 0 个错误、12 个警告，18 项 JVM 测试通过。`aapt dump badging` 对当时 APK 报告 `sdkVersion:'27'`，包内 `module.prop` 为 `minApiVersion=102`。仅 libxposed API 102 实测，102 以上的框架版本尚未验证。
- 最终调整至 API 26 后执行 `./hako env JAVA_TOOL_OPTIONS=-Duser.home=/app/.devhome gradle --offline --no-daemon lintDebug testDebugUnitTest assembleDebug` 成功，Lint 为 0 个错误、12 个警告；随后用 `:app:testDebugUnitTest --rerun` 实际重跑 18 项 JVM 测试，全部通过。最终 APK 的 `aapt dump badging` 报告 `sdkVersion:'26'`，包内 `module.prop` 声明 `minApiVersion=102`。
- 最低版本改变后，Lint 提示 `mipmap-anydpi-v26` 的版本限定已过时，故将图标原样移至 `mipmap-anydpi`。移动后的第一次增量构建资源合并失败；`--rerun-tasks` 完整重做后成功，APK 内存在图标资源。
- 对 `PackageLoadedParam.getDefaultClassLoader()`，上游 [接口源码](https://github.com/libxposed/api/blob/master/api/src/main/java/io/github/libxposed/api/XposedModuleInterface.java) 标注 API 29；低于 29 时在 application 类本身无 class loader 的情况下使用 `Context.classLoader`。设置页的返回适配参考 [Android 官方文档](https://developer.android.com/guide/navigation/custom-back/predictive-back-gesture)；仅旧版回调的 `GestureBackNavigation` 进行定点抑制，API 33+ 实际注册平台返回回调。
- 删除了 35 个经名称逐一搜索确认无源码或资源引用的旧字符串；同名的 SharedPreferences 键保留。全部三个 `getIdentifier` 调用都读取宿主包资源。另删除未被清单引用的旧 `xposed_scope` 数组：原数组与 `META-INF/xposed/scope.list` 内容一致，本项目使用 API 102 的静态 scope；[LSPosed 的旧 scope 约定](https://github.com/LSPosed/LSPosed/wiki/Module-Scope) 需要清单中的 `xposedscope` 元数据，而本项目未声明该元数据。

## 保留的 12 条警告

这些警告未被全局抑制或加入基线，仍会显示在 Lint 报告中。

| 类别 | 具体位置与决定 |
| --- | --- |
| `DiscouragedPrivateApi` × 5、`PrivateApi` × 2 | `BbzqModule.kt` 通过反射取得 `Application.attach` 并取得 `ActivityThread.currentApplication`，分别用于现有的宿主 attach hook 与应用已启动时的补启动；`ModuleSettingsBridge.kt` 的 `ActivityThread.currentApplication` 用于现有的宿主上下文回退。两个 `currentApplication` 位置分别同时产生一条 `PrivateApi`。`CommentPictureHook.kt` 反射 `View.mListenerInfo` 以取得原有监听器；`MineProfileHook.kt` 反射 `BaseDexClassLoader.pathList`，用于按类名发现宿主的备用路径。最低版本变为 27 后 Lint 新报告后者；改写或删除该路径可能影响宿主版本兼容，因此保留现有行为。反射依赖非 SDK 接口，未来 Android 或厂商实现仍可能失效，不能将 Android 11 的验证外推到其他系统。 |
| `DiscouragedApi` × 3 | `ChronosPromotionHook.kt` 按名称取宿主字符串、`CommentPictureHook.kt` 查宿主 `more_button` ID、`FreeCopyHook.kt` 查宿主弹窗主题；这些值属于运行时宿主包，不能替换成模块编译期 `R` 值。保留并接受版本、资源更名及性能风险。 |
| `ExportedContentProvider` × 1 | `AndroidManifest.xml` 导出的 `ModuleSettingsProvider` 承载跨进程设置读取。`call()` 在处理任何方法前检查 Binder UID 对应的包名是否为模块或既有宿主允许列表；其他 CRUD 方法不提供设置数据。关闭导出或要求模块签名权限会改变现有跨进程契约，因此保留该警告。此次非授权 shell 调用被 `SecurityException` 拒绝；被允许的宿主读取未在本机证实。 |
| `ChromeOsAbiSupport` × 1 | `app/build.gradle.kts` 沿用现有 `arm64-v8a` ABI 策略；没有证据表明本次应扩展 x86_64 支持或改变发布产物。ChromeOS/x86_64 未验证，保留告警。 |

## 设备验证

- Android 11 / API 30 开发板：目标宿主 `com.bilibili.app.in` 版本 3.20.4、versionCode 8230800；使用已有稳定 debug 签名执行 `adb install -r`，保留应用数据。设置主页及隐藏功能页可显示；隐藏功能页按系统返回回到设置主页，主页返回退出。设置页内容未被状态栏遮挡。
- 在同一开发板重启宿主并采集 `BBZQ` 日志：Vector 3111 加载 API 102，主进程 runtime 启动，`Setting` 报 `entries=12`，`BlockUpdate installed, methods=2`，且启动期间出现 `BlockUpdate intercepted START_UP`。这些记录验证实际初始化和更新拦截路径，并不证明所有 hook 或所有宿主页面功能都已运行。
- Android 15 / API 35 测试手机：安装同一 debug 包，设置隐藏功能页通过屏幕左边缘返回手势回到设置主页；再次返回后离开设置 Activity。截图显示设置页及文字可见；没有宿主 3.20.4，未在该设备测试 hook。
- Android 16 / API 36 `product:PLR110`：宿主版本 3.20.4 / 8230800；对已安装的模块执行同签名 `adb install -r`，保留数据。隐藏功能页通过左边缘手势返回设置主页，再次手势退出；随后重启宿主，在其设置页观察到 hook 注入的“高级设置 / BBZQ 设置”，点击后进入模块的 `SettingsActivity`。Vector 3094 的日志确认主进程载入 API 102 并开始初始化，下载进程完成 runtime 初始化；主进程未捕获到最终初始化汇总日志，因此不据此声称所有 hook 已完成安装。
- 上述三台设备的初轮验证使用 `minSdk=24` 的包。最低版本首次调整后，以同一 `minSdk=27` 包在 Android 11 开发板及 Android 16 / PLR110 上分别执行同签名 `adb install -r`，两台设备均报告安装后的 minSdk 为 27。开发板重启目标宿主后，Vector API 102 日志显示设置入口 hook 注册 12 个方法、更新拦截注册两个方法，并实际拦截一次 `START_UP`；PLR110 从隐藏功能页侧滑成功回到主页，主页当时显示“需 Android 8.1 及以上”。Android 15 没有复测该包。
- 最终 `minSdk=26` 包在 Android 11 开发板同签名原位安装成功，系统记录 `minSdk=26`，设置页显示“需 Android 8.0 及以上”。重启目标宿主后，Vector 3111 / API 102 日志显示 `Setting entries=12`、`BlockUpdate installed, methods=2`，并实际拦截一次 `START_UP`。这验证了 API 30 设备上的最终包，不代表 Android 8.0 / API 26 已实测。
- 非授权 shell 运行 `content call --uri content://im.majo.mikufans.settings --method contains --arg __lint_probe_missing__`，结果为 `SecurityException: Caller is not allowed to access module settings`。用 `run-as` 调用系统 `content` 命令因该命令缺少 `ACCESS_CONTENT_PROVIDERS_EXTERNALLY` 而被系统拒绝，不能用该结果判断模块自身是否获准。
- Android 11 宿主直接解析模块 Provider 仍出现既有的 `Unknown authority`；Android 16 的目标宿主也记录 `Failed to find provider info`。前一任务已记录框架远端偏好回退。此次没有改动 Provider 或偏好同步实现，也未证实合法宿主能直接读写 Provider。
- 最新支持范围不包括 Android API 24–25。此前未覆盖 Android API 26–29 的运行时分支、API 28–29 刘海设备、Android 16 之后系统、热重载及逐个 feature hook 的完整行为；无相应已连接设备或本地模拟器。API 36 已验证上述返回手势及宿主入口，但未验证预测返回动画的全部交互；不能将现有设备结果或日志中的工厂计数当作所有场景通过。[Vector 上游](https://github.com/JingMatrix/Vector)声明最低 Android 8.1；尚未验证 Android 8.0 上兼容 libxposed API 102 的其他框架。

## 交付状态

初轮设备验证使用的旧 `minSdk=24` Debug APK 的 SHA-256 为 `b88610be5cdd3dd8c064e922f53fed247d04ac100c9297bdee6718c99ec5b670`；中间 `minSdk=27` Debug APK 为 `39cda0ed21aa92819e0ff77cda740a8886a21d2cf9ced8d83c8daa5bf6402c43`；最终 `minSdk=26` Debug APK 为 `e8dd26743b5cd1dbd69d52a660a7881dc52fd2dbd88e61374559ab8eaa8796eb`。截图和经标签过滤的临时日志位于 `/tmp`，不含在提交候选中。

项目 review gate 评定为 `human-required`，最初原因是 API 24–29、Android 16+ 返回路径和合法宿主访问 Provider 等重要场景尚未实测。2026-11-27 用户选择“接受现有验证并记录限制”，随后提供 PLR110 补测 Android 16；其余未覆盖场景仍未因此通过。独立检查未发现确定的功能回归；用户明确授权提交和归档，源码提交为 `91247d872d60fde7300f913c2deafb02d1a55d51`。
