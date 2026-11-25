package im.majo.mikufans.feats

import android.content.Context
import android.content.SharedPreferences
import im.majo.mikufans.ModuleSettingsBridge
import kotlin.LazyThreadSafetyMode
import im.majo.mikufans.feats.hook.BottomBarHook
import im.majo.mikufans.feats.hook.AutoLikeHook
import im.majo.mikufans.feats.hook.AccessKeyHook
import im.majo.mikufans.feats.hook.ChronosPromotionHook
import im.majo.mikufans.feats.hook.DownloadThreadHook
import im.majo.mikufans.feats.hook.TeenagersModeHook
import im.majo.mikufans.feats.hook.TryFreeQualityHook
import im.majo.mikufans.feats.hook.FreeCopyHook
import im.majo.mikufans.feats.hook.HomeRecommendAdHook
import im.majo.mikufans.feats.hook.HomeRecommendAutoRefreshHook
import im.majo.mikufans.feats.hook.HomeComponentHideHook
import im.majo.mikufans.feats.hook.HomeTopBarPurifyHook
import im.majo.mikufans.feats.hook.RewardAdHook
import im.majo.mikufans.feats.hook.SettingHook
import im.majo.mikufans.feats.hook.ShareHook
import im.majo.mikufans.feats.hook.SkipVideoAdHook
import im.majo.mikufans.feats.hook.SkipVideoAdProgressHook
import im.majo.mikufans.feats.hook.SplashAdHook
import im.majo.mikufans.feats.hook.StoryPlayerAdHook
import im.majo.mikufans.feats.hook.VideoCommentHook
import im.majo.mikufans.feats.hook.CommentPictureHook
import im.majo.mikufans.feats.hook.VideoDetailBannerAdHook
import im.majo.mikufans.feats.hook.MineProfileHook
import io.github.libxposed.api.XposedInterface

object RoamingRuntime {
    fun isProcessSupported(packageName: String, processName: String): Boolean =
        resolveProcessScope(packageName, processName) != ProcessScope.UNSUPPORTED

    fun start(
        xposed: XposedInterface,
        packageName: String,
        processName: String,
        application: Context,
        classLoader: ClassLoader,
        log: (String, Throwable?) -> Unit,
    ) {
        val env = RoamingEnv(
            xposed = xposed,
            packageName = packageName,
            processName = processName,
            hostContext = application.applicationContext ?: application,
            classLoader = classLoader,
            logger = log,
        )
        val processScope = resolveProcessScope(packageName, processName)

        env.log("BBZQ runtime starting for $packageName/$processName")
        if (processScope == ProcessScope.UNSUPPORTED) {
            env.log("BBZQ runtime skipped for unsupported process $processName")
            return
        }

        ModuleSettingsBridge.attach(env.hostContext, xposed)

        val hooks = when (processScope) {
            ProcessScope.WEB -> listOf(
                ::ShareHook,
                ::RewardAdHook,
            )

            ProcessScope.DOWNLOAD -> listOf(
                ::DownloadThreadHook,
            )

            ProcessScope.MAIN -> listOf(
                ::SettingHook,
                ::SplashAdHook,
                ::ShareHook,
                ::FreeCopyHook,
                ::BottomBarHook,
                ::HomeComponentHideHook,
                ::HomeRecommendAdHook,
                ::HomeRecommendAutoRefreshHook,
                ::HomeTopBarPurifyHook,
                ::StoryPlayerAdHook,
                ::VideoDetailBannerAdHook,
                ::TryFreeQualityHook,
                ::ChronosPromotionHook,
                ::SkipVideoAdHook,
                ::SkipVideoAdProgressHook,
                ::RewardAdHook,
                ::AutoLikeHook,
                ::AccessKeyHook,
                ::TeenagersModeHook,
                ::VideoCommentHook,
                ::CommentPictureHook,
                ::MineProfileHook,
            )
            ProcessScope.UNSUPPORTED -> emptyList()
        }

        hooks.forEach { factory ->
            val hook = factory(env)
            runCatching { hook.startHook() }
                .onFailure { env.log("Hook failed: ${hook.javaClass.simpleName}", it) }
        }

        env.log("BBZQ runtime installed ${hooks.size} hook(s)")
    }

    private fun resolveProcessScope(packageName: String, processName: String): ProcessScope {
        val normalizedProcessName = processName.ifBlank { packageName }
        return when {
            normalizedProcessName == packageName -> ProcessScope.MAIN
            normalizedProcessName.endsWith(":web") -> ProcessScope.WEB
            normalizedProcessName.endsWith(":download") -> ProcessScope.DOWNLOAD
            else -> ProcessScope.UNSUPPORTED
        }
    }

    private enum class ProcessScope {
        MAIN,
        WEB,
        DOWNLOAD,
        UNSUPPORTED,
    }
}

class RoamingEnv(
    val xposed: XposedInterface,
    val packageName: String,
    val processName: String,
    val hostContext: Context,
    val classLoader: ClassLoader,
    private val logger: (String, Throwable?) -> Unit,
) {
    val prefs: SharedPreferences
        get() = ModuleSettingsBridge.instance

    val moduleContext: Context? by lazy(LazyThreadSafetyMode.NONE) {
        runCatching {
            hostContext.createPackageContext(MODULE_PACKAGE, Context.CONTEXT_IGNORE_SECURITY)
        }
            .onFailure { throwable ->
                logger("Failed to create module context for $MODULE_PACKAGE", throwable)
            }
            .getOrNull()
    }

    fun log(message: String, throwable: Throwable? = null) {
        logger(message, throwable)
    }

    companion object
}

private const val MODULE_PACKAGE = "im.majo.mikufans"

abstract class BaseRoamingHook(
    protected val env: RoamingEnv,
) {
    protected val xposed: XposedInterface
        get() = env.xposed

    protected val classLoader: ClassLoader
        get() = env.classLoader

    protected val prefs: SharedPreferences
        get() = env.prefs

    protected fun log(message: String, throwable: Throwable? = null) {
        env.log(message, throwable)
    }

    abstract fun startHook()
}

