package com.sd.lib.io.dir

import android.content.Context
import com.sd.lib.io.FFileUtils
import java.io.File

/**
 * 在[Context.getExternalFilesDir]下创建指定名称的目录，如果失败则尝试在[Context.getFilesDir]下创建
 */
class FFilesDir(dirName: String) {
    private val _dirName = dirName

    init {
        require(dirName.isNotEmpty()) { "dirName is empty" }
    }

    /**
     * 返回目录
     */
    fun get(context: Context): File {
        synchronized(_dirName) {
            return FFileUtils.getFilesDir(_dirName, context)
        }
    }

    /**
     * 删除目录
     */
    fun delete(context: Context) {
        synchronized(_dirName) {
            val dir = get(context)
            FFileUtils.delete(dir)
        }
    }

    /**
     * 创建文件
     */
    fun newFile(ext: String?, context: Context): File? {
        synchronized(_dirName) {
            val dir = get(context)
            return FFileUtils.newFileUnderDir(dir, ext)
        }
    }

    /**
     * 同步操作
     */
    fun <R> lock(block: () -> R): R {
        synchronized(_dirName) {
            return block()
        }
    }
}