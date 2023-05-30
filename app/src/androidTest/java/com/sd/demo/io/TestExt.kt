package com.sd.demo.io

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sd.lib.io.fExtAddDot
import com.sd.lib.io.fExt
import com.sd.lib.io.fExtRemoveDot
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class TestExt {
    @Test
    fun testExt() {
        assertEquals("mp3", "hello.mp3".fExt())
        assertEquals("mp3", ".mp3".fExt())

        assertEquals("", "mp3".fExt())
        assertEquals("", "".fExt())

        assertEquals("mp3", "".fExt("mp3"))
        assertEquals("mp3", "".fExt(".mp3"))
        assertEquals("mp3", "".fExt("...mp3"))
    }

    @Test
    fun testExtAddDot() {
        assertEquals(".mp3", "mp3".fExtAddDot())
        assertEquals(".mp3", ".mp3".fExtAddDot())
        assertEquals("", "".fExtAddDot())
    }

    @Test
    fun testExtRemoveDot() {
        assertEquals("mp3", "mp3".fExtRemoveDot())
        assertEquals("mp3", ".mp3".fExtRemoveDot())
        assertEquals("", "".fExtRemoveDot())
    }
}