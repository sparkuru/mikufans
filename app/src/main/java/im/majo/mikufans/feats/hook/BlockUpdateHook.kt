package im.majo.mikufans.feats.hook

import android.app.Activity
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import im.majo.mikufans.ModuleSettings
import im.majo.mikufans.ModuleSettingsBridge
import im.majo.mikufans.feats.BaseRoamingHook
import im.majo.mikufans.feats.RoamingEnv
import im.majo.mikufans.feats.from
import im.majo.mikufans.feats.hookBefore
import java.lang.reflect.Method
import java.lang.reflect.Modifier

class BlockUpdateHook(env: RoamingEnv) : BaseRoamingHook(env) {
    private val updatePreference = UpdateBlockingPreference {
        runCatching {
            ModuleSettingsBridge.instance.getBooleanFromAvailableSources(
                ModuleSettings.KEY_BLOCK_UPDATE_ENABLED,
                ModuleSettings.DEFAULT_BLOCK_UPDATE_ENABLED,
            )
        }.onFailure { log("BlockUpdate setting read failed", it) }
            .getOrNull()
            .also { if (it == null) log("BlockUpdate setting unavailable; leaving host behavior unchanged") }
    }

    override fun startHook() {
        if (env.packageName != TARGET_PACKAGE || env.processName != TARGET_PACKAGE) return
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            log("BlockUpdate skipped: Android 8.0 or newer is required")
            return
        }
        if (!isSupportedVersion()) return

        val updater = UPDATER_CLASS.from(classLoader)
        val action = ACTION_CLASS.from(classLoader)
        val callback = CALLBACK_CLASS.from(classLoader)
        if (updater == null || action == null || callback == null) {
            log("BlockUpdate skipped: target classes unavailable")
            return
        }

        val dispatch = findVoidInstanceMethod(updater, "a", Activity::class.java, action, callback)
        val manual = findVoidInstanceMethod(updater, "b", Activity::class.java, callback)
        val onError = runCatching { callback.getMethod("onError", String::class.java) }.getOrNull()
        if (dispatch == null || manual == null || onError == null) {
            log("BlockUpdate skipped: target method signatures unavailable")
            return
        }

        var installed = 0
        runCatching {
            env.hookBefore(dispatch) { param ->
                val kind = (param.args.getOrNull(1) as? Enum<*>)?.name ?: return@hookBefore
                if (kind !in BLOCKED_ACTIONS || !updatePreference.isEnabled()) return@hookBefore
                param.result = null
                notifyBlocked(param.args.getOrNull(0) as? Activity, param.args.getOrNull(2), onError, kind == MANUAL)
                log("BlockUpdate intercepted $kind")
            }
            installed++
            env.hookBefore(manual) { param ->
                if (!updatePreference.isEnabled()) return@hookBefore
                param.result = null
                notifyBlocked(param.args.getOrNull(0) as? Activity, param.args.getOrNull(1), onError, true)
                log("BlockUpdate intercepted manual check")
            }
            installed++
        }.onFailure { log("BlockUpdate hook registration failed, installed=$installed", it) }
        if (installed == 2) log("BlockUpdate installed, methods=$installed")
    }

    @Suppress("DEPRECATION")
    private fun isSupportedVersion(): Boolean {
        val info = runCatching {
            env.hostContext.packageManager.getPackageInfo(TARGET_PACKAGE, 0)
        }.getOrElse {
            log("BlockUpdate skipped: cannot read host version", it)
            return false
        }
        val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            info.longVersionCode
        } else {
            info.versionCode.toLong()
        }
        if (info.versionName == TARGET_VERSION && versionCode == TARGET_VERSION_CODE) return true
        log("BlockUpdate skipped: unsupported host version")
        return false
    }

    private fun notifyBlocked(activity: Activity?, callback: Any?, onError: Method, manual: Boolean) {
        if (callback == null && (!manual || activity == null)) return
        Handler(Looper.getMainLooper()).post {
            if (callback != null) {
                runCatching { onError.invoke(callback, BLOCKED_NOTICE) }
                    .onFailure { log("BlockUpdate callback failed", it) }
            }
            if (manual && activity != null && !activity.isFinishing && !activity.isDestroyed) {
                runCatching { Toast.makeText(activity, BLOCKED_NOTICE, Toast.LENGTH_SHORT).show() }
                    .onFailure { log("BlockUpdate notice failed", it) }
            }
        }
    }

    private companion object {
        const val TARGET_PACKAGE = "com.bilibili.app.in"
        const val TARGET_VERSION = "3.20.4"
        const val TARGET_VERSION_CODE = 8230800L
        const val UPDATER_CLASS = "com.bilibili.gripper.update.l\$a"
        const val ACTION_CLASS = "com.bilibili.gripper.api.updater.UpdaterActionType"
        const val CALLBACK_CLASS = "ar3.c"
        const val MANUAL = "MANUAL"
        const val BLOCKED_NOTICE = "已屏蔽应用内更新检查"
        val BLOCKED_ACTIONS = setOf("START_UP", MANUAL, "FORCE")
    }
}

internal fun findVoidInstanceMethod(type: Class<*>, name: String, vararg parameters: Class<*>): Method? =
    runCatching { type.getDeclaredMethod(name, *parameters) }.getOrNull()
        ?.takeIf { it.returnType == Void.TYPE && !Modifier.isStatic(it.modifiers) }

internal class UpdateBlockingPreference(private val readEnabled: () -> Boolean?) {
    private var disabledForProcess = false

    @Synchronized
    fun isEnabled(): Boolean {
        if (disabledForProcess) return false
        val enabled = runCatching(readEnabled).getOrNull() ?: return false
        if (!enabled) disabledForProcess = true
        return enabled
    }
}
