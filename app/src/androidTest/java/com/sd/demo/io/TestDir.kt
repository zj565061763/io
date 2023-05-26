package com.sd.demo.io

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sd.lib.io.fDirTemp
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
    @Test
    fun testDirTemp() {
        fDirTemp().newFile("mp3") {
            assertEquals(true, it?.exists())
        }

        fDirTemp().newFile(".mp3") {
            assertEquals(true, it?.exists())
        }

        fDirTemp().newFile("") {
            assertEquals(true, it?.exists())
        }

        fDirTemp().newFile(null) {
            assertEquals(true, it?.exists())
        }
    }
}