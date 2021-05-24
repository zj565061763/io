package com.sd.demo.io

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.sd.lib.io.FExtUtils
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.sd.demo.io", appContext.packageName)
    }

    @Test
    fun testExt() {
        assertEquals("exe", FExtUtils.getExt("WeChatSetup.exe"))
        assertEquals("mp3", FExtUtils.getExt(".mp3"))

        assertEquals("", FExtUtils.getExt("exe"))
        assertEquals("", FExtUtils.getExt(null))
        assertEquals("", FExtUtils.getExt(""))

        assertEquals(".mp4", FExtUtils.completeExt("mp4"))
        assertEquals("", FExtUtils.completeExt(null))
        assertEquals("", FExtUtils.completeExt(""))
    }
}