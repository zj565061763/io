package com.sd.demo.io

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.sd.lib.io.fCacheDir
import com.sd.lib.io.fCopyToDir
import com.sd.lib.io.fCopyToFile
import com.sd.lib.io.fCreateDir
import com.sd.lib.io.fCreateFile
import com.sd.lib.io.fFilesDir
import com.sd.lib.io.fMoveToFile
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class TestFile {
    private val _context get() = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun testCreateDir() {
        val file = _context.externalCacheDir!!.resolve("testCreateDir").resolve("aaa")

        assertEquals(true, file.fCreateFile())
        assertEquals(true, file.exists() && file.isFile)

        assertEquals(true, file.fCreateDir())
        assertEquals(true, file.exists() && file.isDirectory)
    }

    @Test
    fun testCreateFile() {
        val file = _context.externalCacheDir!!.resolve("testCreateFile").resolve("aaa")

        assertEquals(true, file.fCreateDir())
        assertEquals(true, file.exists() && file.isDirectory)

        assertEquals(true, file.fCreateFile())
        assertEquals(true, file.exists() && file.isFile)
    }

    @Test
    fun testCopyToDir() {
        val cacheDir = fCacheDir("my_cache")
        val filesDir = fFilesDir("my_files")
        assertEquals(true, cacheDir.exists())
        assertEquals(true, filesDir.exists())

        cacheDir.resolve("deep").resolve("deep.txt").apply {
            this.fCreateFile()
            this.writeText("hello world")
        }

        val copyDir = filesDir.resolve("copyToDir")
        val copyResult = cacheDir.fCopyToDir(copyDir)
        assertEquals(true, copyResult)

        val copyFile = copyDir.resolve("deep").resolve("deep.txt")
        assertEquals("hello world", copyFile.readText())
    }

    @Test
    fun testCopyToFile() {
        val cacheDir = fCacheDir("my_cache")
        val filesDir = fFilesDir("my_files")
        assertEquals(true, cacheDir.exists())
        assertEquals(true, filesDir.exists())

        val file = cacheDir.resolve("hello.txt").apply {
            this.fCreateFile()
            this.writeText("hello world")
        }
        val copyFile = filesDir.resolve("copyFile").resolve("hello.txt")
        val copyResult = file.fCopyToFile(copyFile)

        assertEquals(true, copyResult)
        assertEquals("hello world", copyFile.readText())
    }

    @Test
    fun testMoveToFile() {
        val cacheDir = fCacheDir("my_cache")
        val filesDir = fFilesDir("my_files")
        assertEquals(true, cacheDir.exists())
        assertEquals(true, filesDir.exists())

        val file = cacheDir.resolve("move.txt").apply {
            this.fCreateFile()
            this.writeText("hello world")
        }
        val moveFile = filesDir.resolve("moveFile").resolve("move.txt")
        val moveResult = file.fMoveToFile(moveFile)

        assertEquals(true, moveResult)
        assertEquals(false, file.exists())
        assertEquals(true, moveFile.exists())
        assertEquals("hello world", moveFile.readText())
    }
}