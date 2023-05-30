package com.sd.demo.io

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sd.lib.io.fCacheDir
import com.sd.lib.io.fCreateFile
import com.sd.lib.io.fDir
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
    private val _dir = fDir(fCacheDir("test_dir"))

    @Test
    fun testGetFile() {
        val file = _dir.getFile("testGetFile.txt")!!
        assertEquals(true, file.fCreateFile())
        assertEquals(true, file.exists())

        file.writeText("testGetFile")
        assertEquals("testGetFile", file.readText())

        assertEquals(true, file.delete())
        assertEquals(false, file.exists())
    }
}