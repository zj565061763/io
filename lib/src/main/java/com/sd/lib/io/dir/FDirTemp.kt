package com.sd.lib.io.dir

import android.content.Context
import java.io.File

/**
 * 临时缓存目录
 */
object FDirTemp {
    private val DIR by lazy { FDir("f_dir_temp") }

    /**
     * 删除目录
     */
    @JvmStatic
    fun delete(context: Context) {
        DIR.delete(context)
    }

    /**
     * 创建文件
     */
    @JvmStatic
    fun newFile(ext: String?, context: Context): File? {
        return DIR.newFile(ext, context)
    }
}