package com.sd.demo.io

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.sd.lib.io.fCacheDir
import com.sd.lib.io.uri.FFileProvider
import com.sd.lib.io.uri.fFileName
import com.sd.lib.io.uri.fToFile
import com.sd.lib.io.uri.fToUri
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
class TestUri {
    private val _context get() = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun testUri() {
        val dirName = "my_uri"
        val cacheDir = fCacheDir(dirName)

        val filename = "urifile.txt"
        val file = File(cacheDir, filename).apply {
            this.writeText("hello world")
        }

        val fileUri = file.fToUri()

        val expectedString = "content://${FFileProvider.getAuthority()}/external-cache-path" +
                "/${dirName}/${filename}"
        val uriString = fileUri.toString()

        assertEquals(expectedString, uriString)
        assertEquals(filename, fileUri.fFileName())

        val saveFile = fileUri.fToFile()
        assertEquals(true, saveFile!!.exists())
        assertEquals("hello world", saveFile.readText())
    }
}