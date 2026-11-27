<p align="center">
  <img src="./assets/mikufans-logo.png" alt="Mikufans logo" width="320">
</p>

基于 BBZQ (https://github.com/HSSkyBoy/BBZQ) v1.0.3（15ed308）；非官方维护分支

由于 [上游后续停止适配旧国际版](https://github.com/HSSkyBoy/BBZQ/commit/d3738c1), 本项目尝试继续维护对 `com.bilibili.app.in` 3.20.4 的支持, 并按需适配功能

原 v1.0.3 分支完整 readme 请阅读：[v1.0.3-readme](./v1.0.3-readme.md)

## requirements

- Android 8.0(API 26)+
- libxposed API 102+, like vector

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

## refers

1. upstream, https://github.com/HSSkyBoy/BBZQ.git
2. 第三方 bilibili intl v3.20.4 fix, https://github.com/xiaojiuwo233/BiliFix.git
3. modern libxposed, https://github.com/JingMatrix/Vector.git

## license

项目使用木兰公共许可证，第 2 版（Mulan PubL v2）

完整授权见 [license](https://license.coscl.org.cn/MulanPubL-2.0)
