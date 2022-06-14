package com.sd.lib.io.dir

import android.content.Context
import java.io.File

/**
 * 临时缓存目录
 */
object FDirTemp {
    private val dir = FFilesDir("f_dir_temp")

    /**
     * 删除目录
     */
    @JvmStatic
    fun delete(context: Context) {
        dir.delete(context)
    }

    /**
     * 创建文件
     */
    @JvmStatic
    fun newFile(ext: String?, context: Context): File? {
        return dir.newFile(ext, context)
    }
}