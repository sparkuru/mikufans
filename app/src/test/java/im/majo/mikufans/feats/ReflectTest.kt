package im.majo.mikufans.feats

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ReflectTest {
    @Test
    fun `selects constructors by argument count and compatible types`() {
        assertEquals("empty", (ConstructorFixture::class.java.newInstanceOrNull() as ConstructorFixture).value)
        assertEquals("name:7", (ConstructorFixture::class.java.newInstanceOrNull("name", 7) as ConstructorFixture).value)
        assertNull(ConstructorFixture::class.java.newInstanceOrNull("name"))
        assertNull(ConstructorFixture::class.java.newInstanceOrNull(7, "name"))
    }

    private class ConstructorFixture {
        val value: String

        constructor() {
            value = "empty"
        }

        constructor(name: String, count: Int) {
            value = "$name:$count"
        }
    }
}
