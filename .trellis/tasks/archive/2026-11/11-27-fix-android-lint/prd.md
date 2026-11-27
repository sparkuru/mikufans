# 修复 Android Lint 存量问题

## 目标

处理 Android 项目的 Lint 错误和警告，使检查能够提供可用反馈，同时保持宿主兼容性、用户设置和已有功能。

## 背景

- 2026-11-27 重新执行 `./hako env JAVA_TOOL_OPTIONS=-Duser.home=/app/.devhome gradle --offline --no-daemon lintDebug`，结果为 79 个错误、48 个警告：78 个 `NewApi`、1 个 `GestureBackNavigation`、36 个 `UnusedResources`、6 个 `PrivateApi`、3 个 `DiscouragedApi`，以及各 1 个 `SetTextI18n`、`ExportedContentProvider` 和 `ChromeOsAbiSupport`。
- 修复开始时项目声明 `minSdk=24`，本地 libxposed API 102、service 102 与 service interfaces 的 AAR 均声明 `minSdk=26`；原清单显式覆盖 service 的库版本要求。用户曾将最低版本调整为 API 27，随后明确改为与依赖一致的 Android 8.0 / API 26，并要求 libxposed API 102 及以上。
- 目标宿主为 Bilibili International 3.20.4；更新拦截任务的背景和设备验证见[归档调查记录](../11-27-handle-forced-update/research/update-investigation.md)。

## 需求与约束

- 核查每类错误和警告的原因，修复真实问题；对于有证据表明必须保留的警告，记录具体位置、影响和处理决定。
- 应用最低版本调整为 Android 8.0 / API 26，模块要求 libxposed API 102 及以上；不再支持在 Android 7.x 上安装。保留目标宿主、现有功能和用户设置。
- 清理不再需要的库最低 SDK 覆盖与旧版本限制提示，保留 API 26–28 所需的真实兼容路径。README 内容由用户自行修改。
- 保留 Provider 的调用方限制、持久化设置、第三方声明和用户数据；不得依靠批量屏蔽、降低检查范围、创建基线或删除功能制造通过结果。
- 本任务只处理 Lint 报告及其必要的兼容性和验证工作；不处理其他已规划的功能任务。

## 验收条件

- [x] 重新运行 `lintDebug` 并核对完整结果，逐项消除错误；警告得到修复，或对有证据的剩余项记录具体原因与处理决定。
- [x] 对涉及 API 版本、返回手势、Provider 或宿主 hook 的变更验证兼容性与原有行为，记录实际设备、版本和未覆盖场景。初轮 Android 11 / 15 / 16 的设备验证、API 27 包的 Android 11 / 16 回归及最终 API 26 包的 Android 11 回归已完成；API 26–29、Android 16 之后的系统及合法宿主 Provider 读取等仍未覆盖，用户已接受此前的验证范围，详见[验证记录](research/lint-verification.md)。
- [x] Android debug 构建及相关单元测试通过，并对修复后的 Lint 结果进行复核；不隐藏失败或扩大与任务无关的改动。
- [x] 完成所需的设计、实施计划和项目上下文，按 Trellis 流程开始修复并记录检查结果。
- [x] 构建产物的清单声明 minSdk 26，模块元数据声明最低 libxposed API 102；相关用户界面、项目规范和任务记录一致，README 由用户自行处理。
- [x] 在新最低版本配置下重新通过 Lint、单元测试和 debug 构建，并验证 APK 清单与可用 Android 设备；明确记录 Android 8.0–10 尚未实测。
