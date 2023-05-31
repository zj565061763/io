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
        val source = fCacheDir("file_source").resolve("file.txt").apply {
            this.fCreateFile()
            this.writeText("testCopyFile")
        }

        _dir.copyFile(source).let { file ->
            assertEquals(true, file.exists())
            assertEquals("file.txt", file.name)
            assertEquals(false, file == source)
            assertEquals("testCopyFile", file.readText())
        }

        _dir.copyFile(source, "copy_file").let { file ->
            assertEquals(true, file.exists())
            assertEquals("copy_file.txt", file.name)
            assertEquals(false, file == source)
            assertEquals("testCopyFile", file.readText())
        }
    }

    @Test
    fun testTakeFile() {
        val source = fCacheDir("file_source").resolve("file.txt").apply {
            this.fCreateFile()
            this.writeText("testTakeFile")
        }

        _dir.takeFile(source).let { file ->
            assertEquals(true, file.exists())
            assertEquals("file.txt", file.name)
            assertEquals(false, file == source)
            assertEquals("testTakeFile", file.readText())
        }
    }

    @Test
    fun testNewFile() {
        val file = _dir.newFile("txt")!!.also {
            assertEquals(true, it.exists())
            it.writeText("testNewFile")
        }

        assertEquals("testNewFile", file.readText())
    }

    @Test
    fun testDeleteFile() {
        _dir.deleteFile(null)

        kotlin.run {
            repeat(10) {
                _dir.newFile("txt")!!.also { assertEquals(true, it.exists()) }
            }
            _dir.deleteFile("txt").also { assertEquals(10, it) }
        }

        kotlin.run {
            repeat(5) {
                _dir.newFile("mp$it")!!.also { assertEquals(true, it.exists()) }
            }

            _dir.newFile("")!!.also { assertEquals(true, it.exists()) }
            _dir.newFile("")!!.also { assertEquals(true, it.exists()) }

            _dir.deleteFile("").also { assertEquals(2, it) }
            _dir.deleteFile(null).also { assertEquals(5, it) }
        }
    }
}