package com.sd.lib.io

import android.content.Context
import java.io.File

object FTempDir {
    /**
     * 返回临时目录
     */
    @JvmStatic
    fun get(context: Context): File? {
        return FFileUtils.getFilesDir("f_temp_dir", context)
    }

    /**
     * 创建临时文件
     */
    @JvmStatic
    @Synchronized
    fun newFile(ext: String?, context: Context): File? {
        val dir = get(context)
        return FFileUtils.newFileUnderDir(dir, ext)
    }

    /**
     * 删除临时文件目录
     */
    @JvmStatic
    @Synchronized
    fun delete(context: Context) {
        val dir = get(context)
        FFileUtils.delete(dir)
    }

    /**
     * 缓存文件夹大小（字节）
     */
    @JvmStatic
    @Synchronized
    fun getSize(context: Context): Long {
        val dir = get(context)
        return FFileUtils.getSize(dir)
    }
}