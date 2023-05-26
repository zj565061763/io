package com.sd.demo.io

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.sd.lib.io.fCacheDir
import com.sd.lib.io.fCheckDir
import com.sd.lib.io.fCheckFile
import com.sd.lib.io.fCopyToDir
import com.sd.lib.io.fCopyToFile
import com.sd.lib.io.fDelete
import com.sd.lib.io.fFilesDir
import com.sd.lib.io.fMoveToFile
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
class TestFile {
    private val _context get() = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun testCheckDir() {
        val file = File(File(_context.externalCacheDir, "testCheckDir"), "aaa")

        assertEquals(true, file.fCheckFile())
        assertEquals(true, file.exists() && file.isFile)

        assertEquals(true, file.fCheckDir())
        assertEquals(true, file.exists() && file.isDirectory)
    }

    @Test
    fun testCheckFile() {
        val file = File(File(_context.getExternalFilesDir(null), "testCheckFile"), "aaa")

        assertEquals(true, file.fCheckDir())
        assertEquals(true, file.exists() && file.isDirectory)

        assertEquals(true, file.fCheckFile())
        assertEquals(true, file.exists() && file.isFile)
    }

    @Test
    fun testCopyToDir() {
        val cacheDir = fCacheDir("my_cache")
        val filesDir = fFilesDir("my_files")
        assertEquals(true, cacheDir.exists())
        assertEquals(true, filesDir.exists())

        File(File(cacheDir, "deep"), "deep.txt").apply {
            this.fCheckFile()
            this.writeText("hello world")
        }

        val copyDir = File(filesDir, "copyToDir")
        val copyResult = cacheDir.fCopyToDir(copyDir)
        val copyFile = File(File(copyDir, "deep"), "deep.txt")

        assertEquals(true, copyResult)
        assertEquals(true, copyFile.exists())
        assertEquals("hello world", copyFile.readText())
    }

    @Test
    fun testCopyToFile() {
        val cacheDir = fCacheDir("my_cache")
        val filesDir = fFilesDir("my_files")
        assertEquals(true, cacheDir.exists())
        assertEquals(true, filesDir.exists())

        val file = File(cacheDir, "hello.txt").apply {
            this.writeText("hello world")
        }
        val copyFile = File(File(filesDir, "copyFile"), "hello.txt")
        val copyResult = file.fCopyToFile(copyFile)

        assertEquals(true, copyResult)
        assertEquals(true, copyFile.exists())
        assertEquals("hello world", copyFile.readText())
    }

    @Test
    fun testMoveToFile() {
        val cacheDir = fCacheDir("my_cache")
        val filesDir = fFilesDir("my_files")
        assertEquals(true, cacheDir.exists())
        assertEquals(true, filesDir.exists())

        val file = File(cacheDir, "move.txt").apply {
            this.writeText("hello world")
        }
        val moveFile = File(File(filesDir, "moveFile"), "move.txt")
        val moveResult = file.fMoveToFile(moveFile)

        assertEquals(true, moveResult)
        assertEquals(false, file.exists())
        assertEquals(true, moveFile.exists())
        assertEquals("hello world", moveFile.readText())
    }
}