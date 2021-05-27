package com.sd.lib.io.dir

import android.content.Context
import android.net.Uri
import com.sd.lib.io.uri.FUriUtils
import java.io.File

/**
 * [Uri]文件保存目录
 */
object FDirUri {
    private val DIR by lazy { FDir("f_dir_uri") }

    /**
     * 返回目录
     */
    @JvmStatic
    private fun get(context: Context): File? {
        return DIR.get(context)
    }

    /**
     * 删除目录
     */
    @JvmStatic
    fun delete(context: Context) {
        DIR.delete(context)
    }

    /**
     * 保存[uri]到目录下
     */
    @JvmStatic
    fun saveUri(uri: Uri?, context: Context): File? {
        return DIR.lock {
            val dir = get(context)
            FUriUtils.saveToDir(uri, dir, context)
        }
    }
}