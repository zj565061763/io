package com.sd.lib.io.dir.ext

import com.sd.lib.io.dir.FFilesDir
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
    fun delete() {
        dir.delete()
    }

    /**
     * 创建文件
     */
    @JvmStatic
    fun newFile(ext: String?): File {
        return dir.newFile(ext)
    }
}