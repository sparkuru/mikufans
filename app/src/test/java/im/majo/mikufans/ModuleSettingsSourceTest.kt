package im.majo.mikufans

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ModuleSettingsSourceTest {
    @Test
    fun `provider value overrides an older remote value`() {
        var remoteReads = 0
        val value = readAvailableBoolean(
            provider = { false },
            remote = { remoteReads++; true },
        )

        assertEquals(false, value)
        assertEquals(0, remoteReads)
    }

    @Test
    fun `uses the remote value when the provider is unavailable`() {
        assertEquals(false, readAvailableBoolean(provider = { null }, remote = { false }))
        assertEquals(true, readAvailableBoolean(provider = { error("unavailable") }, remote = { true }))
    }

    @Test
    fun `reports unavailable when neither source can be read`() {
        assertNull(readAvailableBoolean(provider = { null }, remote = { error("unavailable") }))
    }
}
