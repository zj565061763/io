package com.sd.lib.io

import android.content.Context
import java.io.File

/**
 * 临时缓存目录
 */
object FTempDir {
    /**
     * 返回目录
     */
    @JvmStatic
    fun get(context: Context): File? {
        synchronized(FTempDir::class.java) {
            return FFileUtils.getFilesDir("f_temp_dir", context)
        }
    }

    /**
     * 创建文件
     */
    @JvmStatic
    fun newFile(ext: String?, context: Context): File? {
        val dir = get(context)
        return FFileUtils.newFileUnderDir(dir, ext)
    }

    /**
     * 删除目录
     */
    @JvmStatic
    fun delete(context: Context) {
        synchronized(FTempDir::class.java) {
            val dir = get(context)
            FFileUtils.delete(dir)
        }
    }
}