package com.sd.demo.io

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.sd.lib.io.FFileUtils
import com.sd.lib.io.dir.ext.FDirTemp
import com.sd.lib.io.dir.ext.FDirUri
import com.sd.lib.io.fFullExt
import com.sd.lib.io.fGetExt
import com.sd.lib.io.uri.FFileProvider
import com.sd.lib.io.uri.FUriUtils
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.security.MessageDigest

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun testExt() {
        assertEquals("mp3", "hello.mp3".fGetExt())
        assertEquals("mp3", ".mp3".fGetExt())

        assertEquals("", "mp3".fGetExt())
        assertEquals("", null.fGetExt())
        assertEquals("", "".fGetExt())

        assertEquals("", "mp3".fGetExt(null))
        assertEquals("", null.fGetExt(""))
        assertEquals("", null.fGetExt("."))
        assertEquals("mp3", "".fGetExt("mp3"))
        assertEquals("mp3", "".fGetExt(".mp3"))
        assertEquals("mp3", "".fGetExt("...mp3"))

        assertEquals(".mp4", "mp4".fFullExt())
        assertEquals(".mp3", ".mp3".fFullExt())
        assertEquals("", null.fFullExt())
        assertEquals("", "".fFullExt())
    }

    @Test
    fun testDirTemp() {
        assertEquals(true, FDirTemp.newFile("mp3").exists())
        assertEquals(true, FDirTemp.newFile(".mp3").exists())
        assertEquals(true, FDirTemp.newFile(null).exists())
        assertEquals(true, FDirTemp.newFile("").exists())
    }

    @Test
    fun testCopyFile() {
        val cacheDir = FFileUtils.getCacheDir("my_cache")
        val filesDir = FFileUtils.getFilesDir("my_files")
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
        val cacheDir = FFileUtils.getCacheDir("my_cache")
        val filesDir = FFileUtils.getFilesDir("my_files")
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
        val cacheDir = FFileUtils.getCacheDir("my_cache")
        val filesDir = FFileUtils.getFilesDir("my_files")
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
        val cacheDir = FFileUtils.getCacheDir(dirName)

        val filename = "urifile.txt"
        val file = File(cacheDir, filename).apply {
            this.writeText("hello world")
        }

        val fileUri = FUriUtils.fileToUri(file)
        val expectedString = "content://${FFileProvider.getAuthority()}/external_path" +
                "/Android/data/${context.packageName}/cache/${dirName}/${filename}"

        assertEquals(expectedString, fileUri.toString())
        assertEquals(filename, FUriUtils.getName(fileUri))

        val saveFile = FDirUri.saveUri(fileUri)
        assertEquals(true, saveFile!!.exists())
        assertEquals("hello world", saveFile.readText())
    }

    @Test
    fun testMd5() {
        val content = "hello world!"
        assertEquals(md5(content), "FC3FF98E8C6A0D3087D515C0473F8677")
    }

    private fun md5(value: String): String {
        val bytes = MessageDigest.getInstance("MD5").digest(value.toByteArray())
        return bytes.joinToString("") { "%02X".format(it) }
    }
}