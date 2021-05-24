package com.sd.lib.io

import android.content.Context
import com.sd.lib.io.FFileUtils.delete
import com.sd.lib.io.FFileUtils.getFilesDir
import com.sd.lib.io.FFileUtils.newFileUnderDir
import java.io.File

object FTempDir {
    /**
     * 创建临时文件
     */
    @JvmStatic
    @Synchronized
    fun newFile(ext: String?, context: Context): File? {
        val dir = getTempDir(context)
        return newFileUnderDir(dir, ext!!)
    }

    /**
     * 删除临时文件目录
     */
    @JvmStatic
    @Synchronized
    fun delete(context: Context) {
        val dir = getTempDir(context)
        delete(dir)
    }

    /**
     * 返回临时目录
     */
    private fun getTempDir(context: Context): File? {
        return getFilesDir("f_temp_dir", context)
    }
}