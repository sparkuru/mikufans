package im.majo.mikufans

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import io.github.libxposed.service.XposedService
import io.github.libxposed.service.XposedServiceHelper
import java.util.concurrent.atomic.AtomicBoolean

object ModuleRemotePreferences : XposedServiceHelper.OnServiceListener {
    private const val TAG = "BBZQ"

    private val registered = AtomicBoolean(false)
    @Volatile private var appContext: Context? = null
    @Volatile private var service: XposedService? = null

    fun init(context: Context) {
        appContext = context.applicationContext ?: context
        if (registered.compareAndSet(false, true)) {
            XposedServiceHelper.registerListener(this)
        }
    }

    fun attach(context: Context, prefs: SharedPreferences) {
        init(context)
        syncSnapshot(prefs)
    }

    override fun onServiceBind(service: XposedService) {
        this.service = service
        appContext?.let { context ->
            syncSnapshot(context.moduleSettingsPreferences())
        }
    }

    override fun onServiceDied(service: XposedService) {
        if (this.service === service) this.service = null
    }

    @Synchronized
    fun syncSnapshot(prefs: SharedPreferences) {
        val values = prefs.all
        withRemoteEditor { editor ->
            editor.clear()
            values.forEach { (key, value) -> editor.putValue(key, value) }
        }
    }

    @Synchronized
    fun applyOperations(operations: List<PreferenceOperation>) {
        if (operations.isEmpty()) return
        withRemoteEditor { editor ->
            operations.forEach { operation ->
                when (operation) {
                    PreferenceOperation.Clear -> editor.clear()
                    is PreferenceOperation.Remove -> editor.remove(operation.key)
                    is PreferenceOperation.Put -> editor.putValue(operation.key, operation.value)
                }
            }
        }
    }

    @Synchronized
    fun setBlockUpdateEnabled(context: Context, enabled: Boolean): Boolean = runCatching {
        val remote = service?.getRemotePreferences(ModuleSettings.PREFS_NAME) ?: return@runCatching false
        val local = context.moduleSettingsPreferences()
        val previous = local.getBoolean(
            ModuleSettings.KEY_BLOCK_UPDATE_ENABLED,
            ModuleSettings.DEFAULT_BLOCK_UPDATE_ENABLED,
        )
        saveUpdateBlockingPreference(
            enabled = enabled,
            previous = previous,
            writeLocal = { value ->
                local.edit().putBoolean(ModuleSettings.KEY_BLOCK_UPDATE_ENABLED, value).commit()
            },
            writeRemote = { value ->
                remote.edit().putBoolean(ModuleSettings.KEY_BLOCK_UPDATE_ENABLED, value).commit()
            },
        )
    }.getOrDefault(false)

    private fun withRemoteEditor(block: (SharedPreferences.Editor) -> Unit) {
        val currentService = service ?: return
        runCatching {
            val editor = currentService.getRemotePreferences(ModuleSettings.PREFS_NAME).edit()
            block(editor)
            editor.commit()
        }.onFailure {
            Log.w(TAG, "sync remote preferences failed: ${it.javaClass.simpleName}: ${it.message}")
        }
    }

    private fun SharedPreferences.Editor.putValue(key: String, value: Any?) {
        when (value) {
            null -> remove(key)
            is Boolean -> putBoolean(key, value)
            is Int -> putInt(key, value)
            is Long -> putLong(key, value)
            is Float -> putFloat(key, value)
            is String -> putString(key, value)
            is Set<*> -> putStringSet(key, value.map { it.toString() }.toSet())
            is List<*> -> putStringSet(key, value.map { it.toString() }.toSet())
            else -> putString(key, value.toString())
        }
    }

    private fun Context.moduleSettingsPreferences(): SharedPreferences =
        getSharedPreferences(ModuleSettings.PREFS_NAME, Context.MODE_PRIVATE)
}

internal fun saveUpdateBlockingPreference(
    enabled: Boolean,
    previous: Boolean,
    writeLocal: (Boolean) -> Boolean,
    writeRemote: (Boolean) -> Boolean,
): Boolean {
    fun writeSafely(writer: (Boolean) -> Boolean, value: Boolean): Boolean =
        runCatching { writer(value) }.getOrDefault(false)

    if (enabled) {
        if (!writeSafely(writeLocal, true)) {
            writeSafely(writeLocal, previous)
            return false
        }
        if (writeSafely(writeRemote, true)) return true
        // A failed remote write may have taken effect; confirm it is disabled before restoring local false.
        if (writeSafely(writeRemote, previous)) writeSafely(writeLocal, previous)
        return false
    }
    if (!writeSafely(writeRemote, false)) {
        writeSafely(writeRemote, previous)
        return false
    }
    if (writeSafely(writeLocal, false)) return true
    if (writeSafely(writeLocal, previous)) writeSafely(writeRemote, previous)
    return false
}

sealed interface PreferenceOperation {
    data object Clear : PreferenceOperation
    data class Remove(val key: String) : PreferenceOperation
    data class Put(val key: String, val value: Any?) : PreferenceOperation
}
