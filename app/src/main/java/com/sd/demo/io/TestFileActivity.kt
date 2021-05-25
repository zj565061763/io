package com.sd.demo.io

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.sd.lib.io.FFileUtils
import com.sd.lib.io.FTempDir
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

        // test copy file
        val helloFile = File(cacheDir, "hello.txt").apply {
            this.writeText("hello world!!!")
        }
        val copyFile = File(filesDir, "helloCopy.txt")
        FFileUtils.copyFile(helloFile, copyFile)

        // test move file
        val moveFile = File(cacheDir, "move.txt").apply {
            this.writeText("move!!!")
        }
        FFileUtils.moveFile(moveFile, File(filesDir, "moved.txt"))

        // test copy dir
        File(File(cacheDir, "deep"), "deep.txt").apply {
            FFileUtils.checkParentDir(this)
            this.writeText("deep content")
        }

        val cacheCopyDir = File(filesDir, "cacheCopy")
        FFileUtils.copy(cacheDir, cacheCopyDir)
        val cacheCopyDirSize = FFileUtils.getSize(cacheCopyDir)
        Log.i(TAG, "cacheCopy size:${cacheCopyDirSize} format:${FFileUtils.formatSize(cacheCopyDirSize)}")

        // test zip
        val cacheZip = File(filesDir, "cacheZip.zip")
        FZipUtils.zip(cacheDir, cacheZip)
        FZipUtils.unzip(cacheZip, File(filesDir, "cacheUnzip"))
    }

    override fun onDestroy() {
        super.onDestroy()
        val cacheDir = FFileUtils.getCacheDir("my_cache", this)
        val filesDir = FFileUtils.getFilesDir("my_files", this)

        FFileUtils.delete(cacheDir)
        FFileUtils.delete(filesDir)
        FTempDir.delete(this)
    }
}