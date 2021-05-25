package com.sd.demo.io

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.sd.lib.io.FFileUtils
import com.sd.lib.io.FIOUtils
import com.sd.lib.io.FTempDir
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

        FFileUtils.createFile(File(cacheDir, "createFile"))
        FFileUtils.createFile(File(filesDir, "createFile"))

        val helloFile = File(cacheDir, "hello.txt")
        helloFile.writeText("hello world!!!")

        val copyFile = File(filesDir, "helloCopy.txt")
        FFileUtils.copyFile(helloFile, copyFile)
        helloFile.inputStream().use { fis ->
            File(filesDir, "ioHelloCopy.txt").outputStream().use { fos ->
                FIOUtils.copy(fis, fos) {
                    Log.i(TAG, "onBytesCopied:${it}")
                    false
                }
            }
        }

        val moveFile = File(cacheDir, "move.txt")
        moveFile.writeText("move!!!")
        FFileUtils.moveFile(moveFile, File(filesDir, "moved.txt"))

        val deepDir = File(cacheDir, "deep")
        FFileUtils.checkDir(deepDir)
        val deepFile = File(deepDir, "deep.txt")
        deepFile.writeText("deep content")

        val cacheCopyDir = File(filesDir, "cacheCopy")
        FFileUtils.copy(cacheDir, cacheCopyDir)
        val cacheCopyDirSize = FFileUtils.getSize(cacheCopyDir)
        Log.i(TAG, "cacheCopy size:${cacheCopyDirSize} format:${FFileUtils.formatSize(cacheCopyDirSize)}")

        FTempDir.newFile("mp3", this)
        FTempDir.newFile("mp3", this)
        FTempDir.newFile("mp4", this)
        FTempDir.newFile(null, this)
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