package com.sd.demo.io

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.sd.lib.io.FExtUtils
import com.sd.lib.io.FFileUtils
import com.sd.lib.io.FTempDir
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
        assertEquals("mp3", FExtUtils.getExt("hello.mp3"))
        assertEquals("mp3", FExtUtils.getExt(".mp3"))

        assertEquals("", FExtUtils.getExt("mp3"))
        assertEquals("", FExtUtils.getExt(null))
        assertEquals("", FExtUtils.getExt(""))

        assertEquals("", FExtUtils.getExt("mp3", null))
        assertEquals("", FExtUtils.getExt(null, ""))
        assertEquals("", FExtUtils.getExt(null, "."))
        assertEquals("mp3", FExtUtils.getExt("", "mp3"))
        assertEquals("mp3", FExtUtils.getExt("", ".mp3"))

        assertEquals(".mp4", FExtUtils.fullExt("mp4"))
        assertEquals(".mp3", FExtUtils.fullExt(".mp3"))
        assertEquals("", FExtUtils.fullExt(null))
        assertEquals("", FExtUtils.fullExt(""))
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
        val copyFile = File(File(filesDir, "copyFile"), "hello.txt")
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
        val moveFile = File(File(filesDir, "moveFile"), "move.txt")
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

        val copyDir = File(filesDir, "copyToDir")
        val copyResult = FFileUtils.copyToDir(cacheDir, copyDir)
        val copyFile = File(File(copyDir, "deep"), "deep.txt")

        assertEquals(true, copyResult)
        assertEquals(true, copyFile.exists())
        assertEquals("hello world", copyFile.readText())
    }

    @Test
    fun testUri() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        val dirName = "my_uri"
        val cacheDir = FFileUtils.getCacheDir(dirName, context)!!

        val filename = "urifile.txt"
        val file = File(cacheDir, filename).apply {
            this.writeText("hello world")
        }
        val fileUri = FUriUtils.fileToUri(file, context)

        val expectedString = "content://${context.packageName}.${FFileProvider::class.java.simpleName.toLowerCase()}/external_path" +
                "/Android/data/${context.packageName}/cache/${dirName}/${filename}"
        assertEquals(expectedString, fileUri.toString())
        assertEquals(filename, FUriUtils.getName(fileUri, context))

        val tempDir = FTempDir.get(context)
        val copyFile = FUriUtils.saveToDir(fileUri, tempDir, context)
        assertEquals(true, copyFile!!.exists())
        assertEquals("hello world", copyFile.readText())
    }
}