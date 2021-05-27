package com.sd.lib.io.dir

import android.content.Context
import com.sd.lib.io.FFileUtils
import java.io.File

/**
 * 临时缓存目录
 */
object FDirTemp {
    private val DIR by lazy { FDir("f_dir_temp") }

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
     * 创建文件
     */
    @JvmStatic
    fun newFile(ext: String?, context: Context): File? {
        val dir = get(context)
        return FFileUtils.newFileUnderDir(dir, ext)
    }
}