package com.sd.demo.io

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.security.MessageDigest

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class TestMD5 {
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