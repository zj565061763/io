package com.sd.demo.io

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sd.lib.io.fCacheDir
import com.sd.lib.io.fCreateNewFile
import com.sd.lib.io.fDir
import com.sd.lib.io.fExt
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
class TestDir {
    private val _dir by lazy { fCacheDir("test_dir").fDir() }

    @Test
    fun testGetKeyFile() {
        _dir.getKeyFile("testGetKeyFile.txt").let { file ->
            assertEquals(true, file.fCreateNewFile())
            assertEquals(true, file.isFile)
            file.writeText("testGetKeyFile")
        }
        _dir.getKeyFile("testGetKeyFile.txt").let { file ->
            assertEquals("testGetKeyFile", file.readText())
        }
    }

    @Test
    fun testCopyFile() {
        val source = fCacheDir("file_source").fNewFile("txt")!!.apply {
            this.writeText("testCopyFile")
        }

        _dir.copyFile(source).let { file ->
            assertEquals(true, file.isFile)
            assertEquals(source.name, file.name)
            assertEquals(false, file == source)
            assertEquals("testCopyFile", file.readText())
        }

        _dir.copyFile(source, "copy_file").let { file ->
            assertEquals(true, file.isFile)
            assertEquals("copy_file.txt", file.name)
            assertEquals(false, file == source)
            assertEquals("testCopyFile", file.readText())
        }
    }

    @Test
    fun testTakeFile() {
        fCacheDir("file_source").fNewFile("txt")!!.apply {
            this.writeText("testTakeFile")
        }.let { source ->
            _dir.takeFile(source).let { file ->
                assertEquals(true, file.isFile)
                assertEquals(source.name, file.name)
                assertEquals(false, file == source)
                assertEquals("testTakeFile", file.readText())
            }
        }

        fCacheDir("file_source").fNewFile("txt")!!.apply {
            this.writeText("testTakeFile")
        }.let { source ->
            _dir.takeFile(source, "take_file").let { file ->
                assertEquals(true, file.isFile)
                assertEquals("take_file.txt", file.name)
                assertEquals(false, file == source)
                assertEquals("testTakeFile", file.readText())
            }
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
        _dir.deleteFile()

        kotlin.run {
            repeat(10) {
                _dir.newFile("aaa")!!.also { assertEquals(true, it.isFile) }
                _dir.newFile("bbb")!!.also { assertEquals(true, it.isFile) }
            }
            _dir.deleteFile { it.name.fExt() == "aaa" }.also { assertEquals(10, it) }
            _dir.deleteFile { it.name.fExt() == "bbb" }.also { assertEquals(10, it) }
        }

        kotlin.run {
            repeat(5) { index ->
                _dir.newFile("mp$index")!!.also { assertEquals(true, it.isFile) }
            }
            repeat(5) {
                _dir.newFile("")!!.also { assertEquals(true, it.isFile) }
            }
            _dir.deleteFile().also { assertEquals(10, it) }
        }

        kotlin.run {
            _dir.newFile("")!!.also { assertEquals(true, it.isFile) }
            _dir.newFile("")!!.also { assertEquals(true, it.isFile) }
            _dir.deleteFile { it.name.fExt() == "" }.also { assertEquals(2, it) }
        }
    }
}