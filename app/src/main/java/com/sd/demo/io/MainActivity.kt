package com.sd.demo.io

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.sd.lib.io.FFileUtils
import com.sd.lib.io.FIOUtils
import com.sd.lib.io.FTempDir
import com.sd.lib.io.FZipUtils
import java.io.File

class MainActivity : AppCompatActivity() {
    val TAG = MainActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        testFile()
    }

    private fun testFile() {
        val cacheDir = FFileUtils.getCacheDir("my_cache", this)
        val filesDir = FFileUtils.getFilesDir("my_files", this)

        val helloFile = File(cacheDir, "hello.txt").apply {
            this.writeText("hello world!!!")
        }
        val copyFile = File(filesDir, "helloCopy.txt")
        FFileUtils.copyFile(helloFile, copyFile)

        val moveFile = File(cacheDir, "move.txt").apply {
            this.writeText("move!!!")
        }
        FFileUtils.moveFile(moveFile, File(filesDir, "moved.txt"))

        File(File(cacheDir, "deep"), "deep.txt").apply {
            this.writeText("deep content")
        }

        val cacheCopyDir = File(filesDir, "cacheCopy")
        FFileUtils.copy(cacheDir, cacheCopyDir)
        val cacheCopyDirSize = FFileUtils.getSize(cacheCopyDir)
        Log.i(TAG, "cacheCopy size:${cacheCopyDirSize} format:${FFileUtils.formatSize(cacheCopyDirSize)}")

        FTempDir.newFile("mp3", this)
        FTempDir.newFile("mp3", this)
        FTempDir.newFile("mp4", this)
        FTempDir.newFile(null, this)

        val cacheZipFile = File(filesDir, "cacheZip.zip")
        FZipUtils.zip(cacheDir, cacheZipFile)
        FZipUtils.unzip(cacheZipFile, File(filesDir, "cacheUnzip"))
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