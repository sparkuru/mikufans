# Android Lint 修复设计

## 边界与现状

错误最初来自应用最低 API 24 与 libxposed API 102 的 API 26 类型边界、API 29 的宿主 class loader、API 30 的窗口 insets，以及新系统返回导航。用户先要求 Android 8.1 / API 27，随后明确改为 Android 8.0 / API 26，模块要求 libxposed API 102 及以上；这是经过授权的产品兼容性调整，不是为了绕过 Lint 而调整。不能通过全局屏蔽、基线或删除已有功能使检查通过。

## 兼容性设计

- APK 使用 `minSdk=26`，低于 API 26 的系统不再支持安装。缓存中的 libxposed API、service 与 service interfaces 102 AAR 的最低 API 均为 26，因此移除仅为 API 24 配置的 `tools:overrideLibrary`；保留模块元数据中 `minApiVersion=102` 的下限。
- 继续使用 API 26 可用的 hook 反射类型，并同步设置页中针对更新功能的 Android 最低版本提示。Vector 上游声明最低 Android 8.1；Android 8.0 必须依赖支持 API 102 且兼容该系统的其他框架，实际可用性未验证。
- `PackageLoadedParam.getDefaultClassLoader()` 被上游标注为 API 29；在较早系统获取宿主实际 class loader，并保持 application attach、重复加载与热重载时的单次启动语义。验证依据为 [libxposed 上游接口](https://github.com/libxposed/api/blob/master/api/src/main/java/io/github/libxposed/api/XposedModuleInterface.java) 及实际代码。
- 返回事件在新旧 Android 均保留现有的页面跳转与退出语义，依据 [Android 官方返回手势文档](https://developer.android.com/guide/navigation/custom-back/predictive-back-gesture) 实现；窗口 insets 在低版本使用可用 API，在高版本继续覆盖系统栏和刘海。
- Provider 仍需跨进程导出且只允许原允许列表；不要通过移除导出、放宽权限或添加不相容签名权限改变行为。对无法消除的告警按具体原因记录，而非全局隐藏。
- 主机私有资源通过宿主包名动态查找的必要性、反射私有 API 的风险、ABI 策略以及未使用资源逐项审查；保留有实际用途或外部消费契约的资源。

## 验证与回退

先复核完整 Lint 报告，再运行离线 `lintDebug`、`testDebugUnitTest` 和 `assembleDebug`，检查生成 APK 的 minSdk 和模块元数据。在可用设备上记录 Android 版本、宿主版本、设置页返回、模块启动及宿主功能的实测结果；Android 8.0–10 和特定宿主路径若不能覆盖须明确记录。设备安装采用已有相同 debug 签名的原位更新，保留数据；出现签名冲突时不得卸载。每个逻辑组保持独立可审查的变更以便回退。
