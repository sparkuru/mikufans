package im.majo.mikufans.feats.hook

import android.content.Context
import im.majo.mikufans.feats.BaseRoamingHook
import im.majo.mikufans.feats.RoamingEnv
import im.majo.mikufans.feats.hookBefore

/** Device-only fixture; this source belongs exclusively to the temporary test build. */
class ForceUpdateFixtureHook(env: RoamingEnv) : BaseRoamingHook(env) {
    override fun startHook() {
        if (env.packageName != "com.bilibili.app.in" || env.processName != env.packageName) return
        val infoType = classLoader.loadClass("tv.danmaku.bili.update.model.BiliUpgradeInfo")
        val readCache = classLoader.loadClass("ir3.a").getDeclaredMethod("o", Context::class.java)
        env.hookBefore(readCache) { param ->
            val info = infoType.getDeclaredConstructor().newInstance()
            infoType.getMethod("setTitle", String::class.java).invoke(info, "Mikufans 测试强制更新")
            infoType.getMethod("setContent", String::class.java).invoke(info, "本地测试样例，请勿下载")
            infoType.getMethod("setVersion", String::class.java).invoke(info, "99.0.0-test")
            infoType.getMethod("setVersion_code", Int::class.javaPrimitiveType).invoke(info, 99999999)
            infoType.getMethod("setUpgrade_type", Int::class.javaPrimitiveType).invoke(info, 2)
            infoType.getMethod("setUrl", String::class.java).invoke(info, "https://example.invalid/mikufans-test.apk")
            param.result = info
            log("ForceUpdateFixture returned controlled forced update")
        }
        log("ForceUpdateFixture installed")
    }
}
