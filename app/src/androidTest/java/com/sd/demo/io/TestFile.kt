package com.sd.demo.io

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.sd.lib.io.fCacheDir
import com.sd.lib.io.fCopyToDir
import com.sd.lib.io.fCopyToFile
import com.sd.lib.io.fCreateFile
import com.sd.lib.io.fCreateNewFile
import com.sd.lib.io.fFilesDir
import com.sd.lib.io.fMakeDirs
import com.sd.lib.io.fMoveToFile
import com.sd.lib.io.fNewFile
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
    fun testMakeDirs() {
        val file = _context.externalCacheDir!!.resolve("testMakeDirs").resolve("aaa")
        assertEquals(false, file.exists())

        assertEquals(true, file.fCreateFile())
        assertEquals(true, file.isFile)

        assertEquals(true, file.fMakeDirs())
        assertEquals(true, file.isDirectory)
    }

    @Test
    fun testCreateFile() {
        val file = _context.externalCacheDir!!.resolve("testCreateFile").resolve("aaa")
        assertEquals(false, file.exists())

        assertEquals(true, file.fMakeDirs())
        assertEquals(true, file.isDirectory)

        assertEquals(true, file.fCreateFile())
        assertEquals(true, file.isFile)
    }

    @Test
    fun testNewFile() {
        val file = fCacheDir("my_cache").fNewFile("txt")!!
        assertEquals(true, file.isFile)
        assertEquals("txt", file.extension)
    }

    @Test
    fun testMoveToFile() {
        val fromFile = fCacheDir("my_cache").fNewFile("txt")!!.apply {
            this.writeText("hello world")
        }

        val toFile = fFilesDir("my_files").resolve("moveFile").fNewFile("txt")!!
        assertEquals(true, fromFile.fMoveToFile(toFile))

        assertEquals(false, fromFile.exists())
        assertEquals(true, toFile.isFile)
        assertEquals("hello world", toFile.readText())
    }

    @Test
    fun testCopyToFile() {
        val fromFile = fCacheDir("my_cache").resolve("hello.txt").apply {
            this.fCreateNewFile()
            this.writeText("hello world")
            assertEquals(true, this.isFile)
        }

        val toFile = fFilesDir("my_files").resolve("copyFile").resolve("hello.txt")
        assertEquals(true, fromFile.fCopyToFile(toFile))

        assertEquals(true, fromFile.isFile)
        assertEquals(true, toFile.isFile)
        assertEquals("hello world", toFile.readText())
    }

    @Test
    fun testCopyToDir() {
        val fromDir = fCacheDir("my_cache")
        fromDir.resolve("deep").resolve("deep.txt").apply {
            this.fCreateNewFile()
            this.writeText("hello world")
        }

        val toDir = fFilesDir("my_files").resolve("copyToDir")
        assertEquals(true, fromDir.fCopyToDir(toDir))

        val file = toDir.resolve("deep").resolve("deep.txt")
        assertEquals(true, file.isFile)
        assertEquals("hello world", file.readText())
    }
}