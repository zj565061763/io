package com.sd.demo.io

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sd.lib.io.fDotExt
import com.sd.lib.io.fGetExt
import com.sd.lib.io.fNoneDotExt
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
    fun testGetExt() {
        assertEquals("mp3", "hello.mp3".fGetExt())
        assertEquals("mp3", ".mp3".fGetExt())

        assertEquals("", "mp3".fGetExt())
        assertEquals("", "".fGetExt())

        assertEquals("mp3", "".fGetExt("mp3"))
        assertEquals("mp3", "".fGetExt(".mp3"))
        assertEquals("mp3", "".fGetExt("...mp3"))
    }

    @Test
    fun testDotExt() {
        assertEquals(".mp3", "mp3".fDotExt())
        assertEquals(".mp3", ".mp3".fDotExt())
        assertEquals("", "".fDotExt())
    }

    @Test
    fun testNoneDotExt() {
        assertEquals("mp3", "mp3".fNoneDotExt())
        assertEquals("mp3", ".mp3".fNoneDotExt())
        assertEquals("", "".fNoneDotExt())
    }
}