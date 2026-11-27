# 实施与验证计划

1. 保存本轮 `lintDebug` 基线统计，按 API、返回手势、资源和其他警告定位真实调用路径与兼容性边界。
2. 修复可安全保持行为的 API 版本和返回导航问题；对 hook 类型边界、宿主 class loader 与 insets 写入能证明前置条件的代码或有针对性的验证。保留宿主流程及设置数据。
3. 逐项审查警告；修复真实问题，保留必要警告时在任务记录中说明位置、证据与风险。不得通过批量屏蔽或删除功能达成通过。
4. 运行项目缓存可用的 `./hako env JAVA_TOOL_OPTIONS=-Duser.home=/app/.devhome gradle --offline --no-daemon lintDebug testDebugUnitTest assembleDebug`，检查完整 Lint 结果及差异。
5. 在可用 Android 设备上验证受影响的设置界面、返回操作和目标宿主 hook；检查 Provider 授权行为，记录设备与任何未覆盖的 API 版本或场景。保留现有用户数据。
6. 独立复核验收、代码差异、测试与残余风险，按项目 review gate 记录是否需要人工信号；更新任务证据和 mainline，不越过既有变更及单独的提交授权。
7. 按用户后续明确决定将 minSdk 调整为 27，检查 libxposed API 102+ 元数据并清除冗余覆盖和旧版本文案；更新共享规范，再重新运行第 4–6 步，并提供可手动加入 README 的描述。
8. 按用户最新决定将 minSdk 修正为 26，保持 libxposed API 102+，更新应用提示与共享规范，重新执行构建、测试和 APK 元数据验证；明确说明 API 26 设备与支持该系统的 API 102 框架尚未验证。
