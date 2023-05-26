package com.sd.demo.io

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sd.lib.io.dir.ext.FDirTemp
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class TestDir {
    @Test
    fun testDirTemp() {
        assertEquals(true, FDirTemp.newFile("mp3").exists())
        assertEquals(true, FDirTemp.newFile(".mp3").exists())
        assertEquals(true, FDirTemp.newFile(null).exists())
        assertEquals(true, FDirTemp.newFile("").exists())
    }
}