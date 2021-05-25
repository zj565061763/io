package com.sd.demo.io

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.sd.lib.io.FExtUtils
import com.sd.lib.io.FFileUtils
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

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
    fun testCopyFile() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        val cacheDir = FFileUtils.getCacheDir("my_cache", context)
        val filesDir = FFileUtils.getFilesDir("my_files", context)

        val helloFile = File(cacheDir, "hello.txt").apply {
            this.writeText("hello world!!!")
        }
        val copyFile = File(filesDir, "helloCopy.txt")
        val copyResult = FFileUtils.copyFile(helloFile, copyFile)

        assertEquals(true, copyResult)
        assertEquals(true, copyFile.exists())
        assertEquals("hello world!!!", copyFile.readText())
    }

    @Test
    fun testExt() {
        assertEquals("exe", FExtUtils.getExt("WeChatSetup.exe"))
        assertEquals("mp3", FExtUtils.getExt(".mp3"))

        assertEquals("", FExtUtils.getExt("exe"))
        assertEquals("", FExtUtils.getExt(null))
        assertEquals("", FExtUtils.getExt(""))

        assertEquals(".mp4", FExtUtils.completeExt("mp4"))
        assertEquals(".mp3", FExtUtils.completeExt(".mp3"))
        assertEquals("", FExtUtils.completeExt(null))
        assertEquals("", FExtUtils.completeExt(""))
    }
}