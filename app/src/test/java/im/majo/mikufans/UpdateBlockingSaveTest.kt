package im.majo.mikufans

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UpdateBlockingSaveTest {
    @Test
    fun `does not save a disabled setting if the remote write fails`() {
        var local = true
        val saved = saveUpdateBlockingPreference(
            enabled = false,
            previous = true,
            writeLocal = { local = it; true },
            writeRemote = { value -> value },
        )

        assertFalse(saved)
        assertTrue(local)
    }

    @Test
    fun `restores the disabled local value if enabling cannot reach remote settings`() {
        var local = false
        val saved = saveUpdateBlockingPreference(
            enabled = true,
            previous = false,
            writeLocal = { local = it; true },
            writeRemote = { value -> !value },
        )

        assertFalse(saved)
        assertFalse(local)
    }

    @Test
    fun `restores the remote value if saving the disabled local value fails`() {
        var remote = true
        var localAttempts = 0
        val saved = saveUpdateBlockingPreference(
            enabled = false,
            previous = true,
            writeLocal = { ++localAttempts > 1 },
            writeRemote = { remote = it; true },
        )

        assertFalse(saved)
        assertTrue(remote)
    }

    @Test
    fun `persists both values when both writes succeed`() {
        var local = true
        var remote = true
        val saved = saveUpdateBlockingPreference(
            enabled = false,
            previous = true,
            writeLocal = { local = it; true },
            writeRemote = { remote = it; true },
        )

        assertTrue(saved)
        assertFalse(local)
        assertFalse(remote)
    }

    @Test
    fun `enables both values when both writes succeed`() {
        var local = false
        var remote = false
        val saved = saveUpdateBlockingPreference(
            enabled = true,
            previous = false,
            writeLocal = { local = it; true },
            writeRemote = { remote = it; true },
        )

        assertTrue(saved)
        assertTrue(local)
        assertTrue(remote)
    }

    @Test
    fun `restores a first write that changes memory before reporting failure`() {
        var local = false
        var attempts = 0
        val saved = saveUpdateBlockingPreference(
            enabled = true,
            previous = false,
            writeLocal = { value -> local = value; ++attempts > 1 },
            writeRemote = { true },
        )

        assertFalse(saved)
        assertFalse(local)
    }

    @Test
    fun `restores both writes when the second changes memory before reporting failure`() {
        var local = true
        var remote = true
        var localAttempts = 0
        val saved = saveUpdateBlockingPreference(
            enabled = false,
            previous = true,
            writeLocal = { value -> local = value; ++localAttempts > 1 },
            writeRemote = { remote = it; true },
        )

        assertFalse(saved)
        assertTrue(local)
        assertTrue(remote)
    }

    @Test
    fun `retains the enabled local value if remote rollback cannot be confirmed`() {
        var local = false
        var remote = false
        val saved = saveUpdateBlockingPreference(
            enabled = true,
            previous = false,
            writeLocal = { local = it; true },
            writeRemote = { value -> if (value) remote = true; false },
        )

        assertFalse(saved)
        assertTrue(local)
        assertTrue(remote)
    }

    @Test
    fun `restores remote if its disabled write changes value but reports failure`() {
        var local = true
        var remote = true
        var remoteAttempts = 0
        val saved = saveUpdateBlockingPreference(
            enabled = false,
            previous = true,
            writeLocal = { local = it; true },
            writeRemote = { value -> remote = value; ++remoteAttempts > 1 },
        )

        assertFalse(saved)
        assertTrue(local)
        assertTrue(remote)
    }

    @Test
    fun `restores the local setting value when a remote write throws after changing state`() {
        var local = true
        var remote = false
        val saved = saveUpdateBlockingPreference(
            enabled = false,
            previous = true,
            writeLocal = { local = it; true },
            writeRemote = { value ->
                remote = value
                if (!value) error("remote result unavailable")
                true
            },
        )

        assertFalse(saved)
        assertTrue(local)
        assertTrue(remote)
    }

    @Test
    fun `keeps remote disabled when local rollback cannot be confirmed`() {
        var local = true
        var remote = true
        val saved = saveUpdateBlockingPreference(
            enabled = false,
            previous = true,
            writeLocal = { value -> if (!value) local = false; false },
            writeRemote = { remote = it; true },
        )

        assertFalse(saved)
        assertFalse(local)
        assertFalse(remote)
    }
}
