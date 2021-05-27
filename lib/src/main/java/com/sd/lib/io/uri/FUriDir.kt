package com.sd.lib.io.uri

import android.content.Context
import android.net.Uri
import com.sd.lib.io.FFileUtils
import java.io.File

/**
 * [Uri]文件保存目录
 */
object FUriDir {
    /**
     * 保存[uri]到目录下
     */
    @JvmStatic
    @Synchronized
    fun saveUri(uri: Uri?, context: Context): File? {
        val dir = get(context)
        return FUriUtils.saveToDir(uri, dir, context)
    }

    /**
     * 删除目录
     */
    @JvmStatic
    @Synchronized
    fun delete(context: Context) {
        val dir = get(context)
        FFileUtils.delete(dir)
    }

    /**
     * 返回目录
     */
    @JvmStatic
    private fun get(context: Context): File? {
        return FFileUtils.getFilesDir("f_uri_dir", context)
    }
}