package com.sd.demo.io

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sd.lib.io.fCacheDir
import com.sd.lib.io.fCheckDir
import com.sd.lib.io.fFilesDir
import com.sd.lib.io.fUnzipTo
import com.sd.lib.io.fZip
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class TestZip {
    @Test
    fun testZip() {
        val cacheDir = fCacheDir("my_cache").apply {
            this.resolve("testZip_hello.txt").apply {
                this.writeText("hello")
            }
            this.resolve("testZip_world.txt").apply {
                this.writeText("world")
            }
            this.resolve("testZip").apply {
                this.fCheckDir()
                this.resolve("hello world.txt").apply {
                    this.writeText("hello world")
                }
            }
        }

        val zip = cacheDir.fZip()
        assertEquals(true, zip?.exists())

        val filesDir = fFilesDir("my_files")
        assertEquals(true, zip.fUnzipTo(filesDir))

        filesDir.resolve("my_cache").apply {
            this.resolve("testZip_hello.txt").let { file ->
                assertEquals(true, file.exists())
                assertEquals("hello", file.readText())
            }
            this.resolve("testZip_world.txt").let { file ->
                assertEquals(true, file.exists())
                assertEquals("world", file.readText())
            }
            this.resolve("testZip").resolve("hello world.txt").let { file ->
                assertEquals(true, file.exists())
                assertEquals("hello world", file.readText())
            }
        }
    }
}