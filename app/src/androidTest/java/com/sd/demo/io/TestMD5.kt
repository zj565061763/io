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
        val md5 = md5("hello world!")
        assertEquals("FC3FF98E8C6A0D3087D515C0473F8677", md5)
    }
}

private fun md5(value: String): String {
    return MessageDigest.getInstance("MD5")
        .digest(value.toByteArray())
        .joinToString("") { "%02X".format(it) }
}