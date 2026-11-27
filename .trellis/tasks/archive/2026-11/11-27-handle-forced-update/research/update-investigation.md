# 更新行为调查记录

调查和设备验证时间：2026-11-27。基线、静态分析和设备对照分别记录，不能以其中某一项推断未经覆盖的其他场景。

## 目标宿主与设备

- 目标包为 `com.bilibili.app.in` 3.20.4，`versionCode=8230800`；归档 `base.apk` SHA-256 为 `2e4726395b401fb3df72715aed1f13cb60b8674582b320b6ac6e7c5d2fc28aba`。在归档目录执行 `sha256sum -c SHA256SUMS`，六个 APK 均通过。
- Android 11、arm64 的 RK3568 开发板在调查前未安装目标宿主；本次安装了归档中的 base APK 和五个 split APK。初始检查时 `im.majo.mikufans` 未安装，随后安装了本任务构建的模块并配置目标作用域。
- 首次启动后跳过可返回的引导页，未登录状态下首页可加载推荐内容，没有观察到强制更新弹窗。此结果只描述当次启动条件，不能证明任何强制更新路径已被覆盖。
- 在“我的 → 设置 → 关于哔哩哔哩 → 检查更新”点击手动检查，界面提示当前已是最新版本；见 [现场截图](./manual-update-check.png)。该提示不证明服务端返回内容，也不代表模拟了强制更新。
- 应用保留在开发板上，下面记录其后的模块开启、关闭及受控强制更新对照。

## 项目与上游

- 调查开始时，本仓库代码搜索未发现 `BlockUpdateHook`、`UpdateCheck` 或 `LatestVersionException` 对应的更新拦截实现。
- 上游 BBZQ 在提交 [`6b5d960b04485c36f00b33a1abcbf183605f7496` 的更新拦截实现](https://github.com/HSSkyBoy/BBZQ/blob/6b5d960b04485c36f00b33a1abcbf183605f7496/app/src/main/java/io/github/bbzq/feats/hook/system/BlockUpdateHook.kt)中，检查设置后拦截更新检查方法，并尝试处理部分更新辅助入口；其[符号扫描器](https://github.com/HSSkyBoy/BBZQ/blob/6b5d960b04485c36f00b33a1abcbf183605f7496/app/src/main/java/io/github/bbzq/feats/symbol/CommentSystemSymbolScanner.kt#L238)使用目标方法签名和 `UpdateHelper` 辅助方法识别。
- 上游的[开关默认值](https://github.com/HSSkyBoy/BBZQ/blob/6b5d960b04485c36f00b33a1abcbf183605f7496/app/src/main/java/io/github/bbzq/ModuleSettings.kt#L554)为关闭；这是上游事实，尚不是本任务的产品决定。上游当前侧重更新的宿主版本，不能直接据此认定上述符号或方案适用于国际版 3.20.4。

## 静态分析覆盖与限制

- `rectl detect` 仅将该 base APK 识别为 ZIP，报告 `zip_directory_unclassified: directory quota or ZIP64`，自动计划仅包含 triage；这不是 DEX 语义分析。
- `unzip -l base.apk 'classes*.dex'` 列出 29 个 DEX 成员；REA 的 Java/DEX 分析能力尚未在此目标上验证。后续定向回退分析见下文。
- 沙箱内 Docker socket 探测返回 `operation not permitted`；按 REA 提供的限定命令在允许的环境重试后，Docker 版本为 26.1.5，但 `rea/java-kotlin:0.1.0` 镜像确实不存在。即使准备镜像，DEX/APK 语义分析是否可用仍需单独验证。
- REA 会话 `25eebd37-1642-4401-b577-65980596a425` 在 `/tmp/mikufans-update-rea.87iXbP/export-evidence-v2/` 导出现场截图、上游来源和环境检查记录；截图证据 ID 为 `d9f38fb31eff27406028`，上游来源 ID 为 `8bf95b3226ad756a3356`，镜像缺失证据 ID 为 `5728e98e0c2fa25399fa`。导出审阅显示未建立分析预检、语义充分性未验证；没有运行 `up` 或 `analyze`。
- 后续使用目标版本的实际更新路径、受控对象和设备日志验证，不将另一个版本的类名或弹窗消失当作功能成功。

## 3.20.4 更新代码的定向静态分析

- 对已校验的 base APK 做只读 DEX 类表检查，更新组件定义位于 `classes28.dex`；引用相关更新方法的另一个 DEX 为 `classes16.dex`。两者分别从归档中提取到临时目录，SHA-256 为 `2ad821b3e4590b5beac28771485623ca787076e4113ba40579d7d78be02975d6` 和 `008be4390479438735f5979d6ce1fbc3616d4b7d3d3e912ce03093ca3473c01a`。
- REA 当前 Java 适配器的 DEX 语义分析仍未建立，因此以本机 JADX 1.5.3 作受限回退：Gradle 镜像 `sha256:2d34fc215310891039e1b2b7797632de8d59eab54496c134bc2da711ba651c50` 中以非 root、无网络、只读输入、无 capabilities 及资源限额运行。整包加载在 2.3 GiB 堆限制下内存不足；缩小到上述两个 DEX 后得到部分反编译和交叉引用。个别无关方法有 JADX 控制流重构告警，不能把全部输出视作无误源码。
- `tv.danmaku.bili.update.api.q.k(Context, ar3.c)` 发起手动检查，`q.l(Activity, ar3.c)` 在启动流程中先读取缓存的强制更新，再发起常规更新检查；`q.m(Activity)` 读取缓存的强制更新。`zq3.c.c(Context)` 是远端更新信息供应器。
- 交叉引用显示上述入口由 `com.bilibili.gripper.update.l$a` 使用：其 `a(Activity, UpdaterActionType, ar3.c)` 分别派发 `START_UP`、`MANUAL`、`FORCE`，其中 `FORCE` 直接调用 `q.m`；`b(Activity, ar3.c)` 也触发手动检查。回调接口 `ar3.c` 含 `onError(String)`。实际设备命中情况见下文。
- `ir3.a.o(Context)` 读取更新目录中的 `.infofawkes` 缓存，并以 `BiliUpgradeInfo.forceUpgrade()` 判断强制更新；该模型以 `upgrade_type == 2` 表示强制更新。开发板上检查到内部及外部更新缓存目录均为空，可使用独立命名、可移除的测试样例验证，不能覆盖已有文件或触发真实下载。
- 开发板已有并启用了 Vector 2.2（3111），管理器报告 API 102；本次安装的 Mikufans 模块只选择了 `com.bilibili.app.in` 作用域。

## 设备对照和测试样例

- 在目标宿主的主进程中，hook 日志确认安装了两个方法；默认开启和重新开启时均观察到 `START_UP` 命中，手动检查观察到 `MANUAL` 命中及[已屏蔽提示](./manual-update-blocked.png)。关闭开关并重启后，界面仍显示关闭，手动检查恢复[宿主原提示](./manual-update-unblocked.png)。
- 未修改宿主读取人工 `.infofawkes` JSON 时，进入了缓存解析但在 Gson 枚举反射处抛出 `AssertionError` / `NoSuchFieldException: INVALID`，没有得到有效弹窗。测试文件按哈希确认后已从内外部更新目录移除；这个失败样例不能证明强制更新被拦截。
- 为获得有效对照，在 `/tmp` 副本中编译临时模块，额外注册 [force-update-fixture.kt](./force-update-fixture.kt)：仅将 `ir3.a.o(Context)` 的返回替换为目标宿主自己的 `BiliUpgradeInfo`，其中 `upgrade_type=2`、版本号 `99999999`，下载 URL 使用不可解析的 `example.invalid`；未点击下载。该样例注入的是缓存读取之后的对象，不覆盖 JSON 解析或真实服务端响应。
- 同一临时构建、同一对象：关闭拦截时日志显示样例返回，宿主显示[测试强制更新弹窗](./force-update-fixture-disabled.png)；开启拦截时日志显示 `START_UP` 被拦截，未进入样例读取，首页仍可操作。最新设置读取实现也重新执行了关闭、开启对照。`FORCE` 枚举分支有静态入口证据，但未单独从宿主 UI 触发该动作。
- 日志中的关键记录分别为 `BlockUpdate installed, methods=2`、`BlockUpdate intercepted START_UP`、`BlockUpdate intercepted MANUAL` 和关闭时的 `ForceUpdateFixture returned controlled forced update`。强制样例验证的是与最终包相同的 hook 和设置读取逻辑；最终设置同步写入另行在正常包及失败注入包上验证。
- 首页推荐加载及一次视频播放可用；设备未登录，未验证登录流程。开关的无障碍节点具有名称、可点击及可勾选状态；切换到 720×1280 和 1.3 倍字体后目标行仍可读，见[截图](./setting-narrow-large-text.png)，测试后恢复原屏幕尺寸和 1.0 字体缩放。
- Android 11 宿主中直接读取模块 Provider 报 `Unknown authority`；框架远端偏好在本机可用，已实测开关两态。代码优先读取 Provider，失败后读取框架远端值，两者均不可用时放行并记录；一旦读到关闭，该 hook 实例在其存续期间保持关闭，热重载可能创建新实例，尚未单独验证。设置 UI 关闭时先写远端、开启时先写本地，并处理写入失败及补偿失败；同步结果无法确认时提示重试。用临时构建强制设置写入返回失败，开关保持原状态并显示[同步未确认提示](./setting-sync-unconfirmed.png)。两端不能原子提交，已有的旧快照、进程中断或多次补偿失败仍可能造成不一致，不能保证所有故障下跨进程状态一致。
- 在项目容器中 debug 构建和 17 项聚焦单元测试通过；全仓 `lintDebug` 仍报告 79 个错误、48 个警告，本次修改文件未发现 Lint 错误。新增 hook 的 API 26 问题已通过显式 Android 8.0 检查消除；该限制不代表项目其余功能已验证 Android 8.0 以下的兼容性。
- 测试时曾发现普通容器构建会生成不同的临时 debug 签名；本次使用 `JAVA_TOOL_OPTIONS=-Duser.home=/app/.devhome` 将测试签名保存在忽略的 `.devhome/.android/debug.keystore`，便于同签名替换。只替换了本次新安装的测试模块，并备份及恢复了其测试设置。
- 最终正常包已安装回开发板，其 SHA-256 为 `64a06003bb8dc520569e2cfbd7e7361b19a4346ef79e042083ffc25487bd139f`，与工作区 debug APK 一致；设备上的本次临时缓存与样例文件已清理。
