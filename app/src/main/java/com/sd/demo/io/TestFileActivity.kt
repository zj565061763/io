package com.sd.demo.io

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sd.lib.io.FFileUtils
import com.sd.lib.io.FZipUtils
import java.io.File

class TestFileActivity : AppCompatActivity() {
    val TAG = TestFileActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_file)
    }

    override fun onResume() {
        super.onResume()
        testFile()
    }

    private fun testFile() {
        val cacheDir = FFileUtils.getCacheDir("my_cache", this)
        val filesDir = FFileUtils.getFilesDir("my_files", this)

        val cacheZip = File(filesDir, "cacheZip.zip")
        FZipUtils.zip(cacheDir, cacheZip)
        FZipUtils.unzip(cacheZip, filesDir)
    }

    override fun onDestroy() {
        super.onDestroy()
        val cacheDir = FFileUtils.getCacheDir("my_cache", this)
        val filesDir = FFileUtils.getFilesDir("my_files", this)

        FFileUtils.delete(cacheDir)
        FFileUtils.delete(filesDir)
    }
}