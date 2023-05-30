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
    private val _dirSource = fCacheDir("file_source")

    @Test
    fun testGetKeyFile() {
        val file = _dir.getKeyFile("testGetKeyFile.txt")!!
        assertEquals(true, file.fCreateFile())
        assertEquals(true, file.exists())

        file.writeText("testGetKeyFile")
        assertEquals("testGetKeyFile", file.readText())

        assertEquals(true, file.delete())
        assertEquals(false, file.exists())
    }

    @Test
    fun testGetKeyTempFile() {
        val file = _dir.getKeyTempFile("testGetKeyTempFile.txt")!!
        assertEquals(true, file.fCreateFile())
        assertEquals(true, file.exists())

        file.writeText("testGetKeyTempFile")
        assertEquals("testGetKeyTempFile", file.readText())

        assertEquals(true, file.delete())
        assertEquals(false, file.exists())
    }

    @Test
    fun testCopyFile() {
        val file = _dirSource.resolve("file.txt").apply {
            this.fCreateFile()
            this.writeText("testCopyFile")
        }

        _dir.copyFile(file).let { copyFile ->
            assertEquals(true, copyFile.exists())
            assertEquals("file.txt", copyFile.name)
            assertEquals(false, copyFile == file)
            assertEquals("testCopyFile", copyFile.readText())
        }

        _dir.copyFile(file, "copy_file").let { copyFile ->
            assertEquals(true, copyFile.exists())
            assertEquals("copy_file.txt", copyFile.name)
            assertEquals(false, copyFile == file)
            assertEquals("testCopyFile", copyFile.readText())
        }
    }
}