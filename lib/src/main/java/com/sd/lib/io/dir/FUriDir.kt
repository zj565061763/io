package com.sd.lib.io.dir

import android.content.Context
import android.net.Uri
import com.sd.lib.io.FFileUtils
import com.sd.lib.io.uri.FUriUtils
import java.io.File

/**
 * [Uri]文件保存目录
 */
object FUriDir {
    val LOCK = FUriDir::class.java

    /**
     * 返回目录
     */
    @JvmStatic
    private fun get(context: Context): File? {
        synchronized(LOCK) {
            return FFileUtils.getFilesDir("f_uri_dir", context)
        }
    }

    /**
     * 删除目录
     */
    @JvmStatic
    fun delete(context: Context) {
        synchronized(LOCK) {
            val dir = get(context)
            FFileUtils.delete(dir)
        }
    }

    /**
     * 保存[uri]到目录下
     */
    @JvmStatic
    fun saveUri(uri: Uri?, context: Context): File? {
        synchronized(LOCK) {
            val dir = get(context)
            return FUriUtils.saveToDir(uri, dir, context)
        }
    }
}