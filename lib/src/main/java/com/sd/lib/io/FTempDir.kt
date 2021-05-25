package com.sd.lib.io

import android.content.Context
import java.io.File

object FTempDir {
    /**
     * 创建临时文件
     */
    @JvmStatic
    @Synchronized
    fun newFile(ext: String?, context: Context): File? {
        val dir = getTempDir(context)
        return FFileUtils.newFileUnderDir(dir, ext)
    }

    /**
     * 删除临时文件目录
     */
    @JvmStatic
    @Synchronized
    fun delete(context: Context) {
        val dir = getTempDir(context)
        FFileUtils.delete(dir)
    }

    /**
     * 缓存文件夹大小（字节）
     */
    @JvmStatic
    @Synchronized
    fun getSize(context: Context): Long {
        val dir = getTempDir(context)
        return FFileUtils.getSize(dir)
    }

    /**
     * 返回临时目录
     */
    private fun getTempDir(context: Context): File? {
        return FFileUtils.getFilesDir("f_temp_dir", context)
    }
}