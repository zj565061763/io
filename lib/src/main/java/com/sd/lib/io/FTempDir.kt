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
        return FFileUtils.getFilesDir("f_temp_dir", context)
    }

    /**
     * 创建文件
     */
    @JvmStatic
    @Synchronized
    fun newFile(ext: String?, context: Context): File? {
        val dir = get(context)
        return FFileUtils.newFileUnderDir(dir, ext)
    }

    /**
     * 删除文件目录
     */
    @JvmStatic
    @Synchronized
    fun delete(context: Context) {
        val dir = get(context)
        FFileUtils.delete(dir)
    }

    /**
     * 文件夹大小（字节）
     */
    @JvmStatic
    @Synchronized
    fun getSize(context: Context): Long {
        val dir = get(context)
        return FFileUtils.getSize(dir)
    }
}