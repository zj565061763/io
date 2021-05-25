package com.sd.demo.io

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.sd.lib.io.FExtUtils
import com.sd.lib.io.FFileUtils
import com.sd.lib.io.uri.FFileProvider
import com.sd.lib.io.uri.FUriUtils
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

    @Test
    fun testCopyFile() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        val cacheDir = FFileUtils.getCacheDir("my_cache", context)!!
        val filesDir = FFileUtils.getFilesDir("my_files", context)!!
        assertEquals(true, cacheDir.exists())
        assertEquals(true, filesDir.exists())

        val file = File(cacheDir, "hello.txt").apply {
            this.writeText("hello world")
        }
        val copyFile = File(filesDir, "helloCopy.txt")
        val copyResult = FFileUtils.copyFile(file, copyFile)

        assertEquals(true, copyResult)
        assertEquals(true, copyFile.exists())
        assertEquals("hello world", copyFile.readText())
    }

    @Test
    fun testMoveFile() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        val cacheDir = FFileUtils.getCacheDir("my_cache", context)!!
        val filesDir = FFileUtils.getFilesDir("my_files", context)!!
        assertEquals(true, cacheDir.exists())
        assertEquals(true, filesDir.exists())

        val file = File(cacheDir, "move.txt").apply {
            this.writeText("hello world")
        }
        val moveFile = File(filesDir, "moved.txt")
        val moveResult = FFileUtils.moveFile(file, moveFile)

        assertEquals(true, moveResult)
        assertEquals(false, file.exists())
        assertEquals(true, moveFile.exists())
        assertEquals("hello world", moveFile.readText())
    }

    @Test
    fun testCopyToDir() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        val cacheDir = FFileUtils.getCacheDir("my_cache", context)!!
        val filesDir = FFileUtils.getFilesDir("my_files", context)!!
        assertEquals(true, cacheDir.exists())
        assertEquals(true, filesDir.exists())

        File(File(cacheDir, "deep"), "deep.txt").apply {
            FFileUtils.checkParentDir(this)
            this.writeText("hello world")
        }

        val copyDir = File(filesDir, "cacheCopy")
        val copyResult = FFileUtils.copyToDir(cacheDir, copyDir)
        val copyFile = File(File(copyDir, "deep"), "deep.txt")

        assertEquals(true, copyResult)
        assertEquals(true, copyFile.exists())
        assertEquals("hello world", copyFile.readText())
    }

    @Test
    fun testUri() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        val cacheDir = FFileUtils.getCacheDir("my_cache", context)!!

        val file = File(cacheDir, "urifile.txt").apply {
            this.writeText("hello world")
        }
        val fileUri = FUriUtils.fileToUri(file, context)

        val expectedString = "content://${context.packageName}.${FFileProvider::class.java.simpleName.toLowerCase()}/external_path" +
                "/Android/data/${context.packageName}/cache/my_cache/urifile.txt"
        assertEquals(expectedString, fileUri.toString())
    }
}