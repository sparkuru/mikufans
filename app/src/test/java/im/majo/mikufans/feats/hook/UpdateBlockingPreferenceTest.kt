package im.majo.mikufans.feats.hook

import im.majo.mikufans.readAvailableBoolean
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class UpdateBlockingPreferenceTest {
    @Test
    fun `a stale remote value does not override a confirmed disabled setting in the same instance`() {
        var providerValue: Boolean? = true
        val readSources = { readAvailableBoolean(provider = { providerValue }, remote = { true }) }
        val preference = UpdateBlockingPreference(readSources)

        assertTrue(preference.isEnabled())
        providerValue = false
        assertFalse(preference.isEnabled())
        providerValue = null
        assertFalse(preference.isEnabled())
        assertTrue(UpdateBlockingPreference(readSources).isEnabled())
    }

    @Test
    fun `fails open when settings cannot be read and retries later`() {
        var sourceAvailable = false
        val preference = UpdateBlockingPreference {
            if (sourceAvailable) true else error("settings unavailable")
        }

        assertFalse(preference.isEnabled())
        sourceAvailable = true
        assertTrue(preference.isEnabled())
    }

    @Test
    fun `accepts only a matching void instance update method`() {
        val type = UpdateMethods::class.java

        assertNotNull(findVoidInstanceMethod(type, "valid", String::class.java))
        assertNull(findVoidInstanceMethod(type, "missing", String::class.java))
        assertNull(findVoidInstanceMethod(type, "valid", Int::class.javaPrimitiveType!!))
        assertNull(findVoidInstanceMethod(type, "returnsValue", String::class.java))
        assertNull(findVoidInstanceMethod(type, "staticMethod", String::class.java))
    }

    private class UpdateMethods {
        fun valid(value: String) = Unit
        fun returnsValue(value: String) = value

        companion object {
            @JvmStatic fun staticMethod(value: String) = Unit
        }
    }
}
