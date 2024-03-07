package com.sd.demo.io

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sd.lib.io.fCacheDir
import com.sd.lib.io.uri.fFileName
import com.sd.lib.io.uri.fFileProviderAuthority
import com.sd.lib.io.uri.fToFile
import com.sd.lib.io.uri.fToUri
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class TestUri {
    @Test
    fun testUri() {
        val dirName = "my_uri"
        val filename = "urifile.txt"

        val file = fCacheDir(dirName).resolve(filename).apply {
            this.writeText("hello world")
        }

        val fileUri = file.fToUri()

        assertEquals(filename, fileUri.fFileName())
        assertEquals(
            "content://${fFileProviderAuthority()}/external-cache-path/${dirName}/${filename}",
            fileUri.toString()
        )

        val saveFile = fileUri.fToFile()!!
        assertEquals("hello world", saveFile.readText())
    }
}