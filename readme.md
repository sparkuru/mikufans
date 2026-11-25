
基于 BBZQ (https://github.com/HSSkyBoy/BBZQ) v1.0.3（15ed308）；非官方维护分支

由于 [上游后续停止适配旧国际版](https://github.com/HSSkyBoy/BBZQ/commit/d3738c1), 本项目尝试继续维护对 `com.bilibili.app.in` 3.20.4 的支持, 并按需适配功能

原 v1.0.3 分支完整 readme 请阅读：[v1.0.3-readme](./v1.0.3-readme.md)

## features

- 用于区分官方 bbzq, 应用包名由 `io.github.bbzq` 更改为 `im.majo.mikufans`
- all bbzq v1.0.3 features
- fix
  - [ ] 短链分享清理
  - [ ] 去更新检测, 适配下发强制更新行为
- new
  - 功能
    - [ ] 账号内合法付费表情包适配支持
    - [ ] 提取动态消息中的完整链接: up 动态推广里的第三方链接比如 tb、jd 等站内的商品页, 一般会在跳转时强制登录账号, 这边的做法是点击链接后直接复制到剪贴板
  - 隐私
    - [ ] 去遥测

## development

使用 Docker 运行 Android 开发命令，无需在宿主机安装 JDK、Gradle 或 Android SDK。首次初始化会下载 SDK 并接受所需的 Android SDK 许可协议，缓存保存在不受版本控制的 `.devhome/` 中。

```bash
./hako bash scripts/setup-android-sdk.sh
./hako gradle --no-daemon assembleDebug
```

Debug APK 位于 `app/build/outputs/apk/debug/`。Release 构建可运行 `./hako gradle --no-daemon assembleRelease`，需要预先配置签名参数。

## refers

1. upstream, https://github.com/HSSkyBoy/BBZQ.git
2. 第三方 bilibili intl v3.20.4 fix, https://github.com/xiaojiuwo233/BiliFix.git
3. modern libxposed, https://github.com/JingMatrix/Vector.git

## license

项目使用木兰公共许可证，第 2 版（Mulan PubL v2）

完整授权见 [license](https://license.coscl.org.cn/MulanPubL-2.0)