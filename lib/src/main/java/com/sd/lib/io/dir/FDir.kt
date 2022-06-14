package com.sd.lib.io.dir

import android.content.Context
import com.sd.lib.io.FFileUtils
import java.io.File

open class FDir(
    dirName: String,
    isCacheDir: Boolean = false,
) {
    private val _dirName = dirName
    private val _isCacheDir = isCacheDir

    init {
        require(dirName.isNotEmpty()) { "dirName is empty" }
    }

    /**
     * 返回目录
     */
    fun get(context: Context): File {
        synchronized(_dirName) {
            return if (_isCacheDir) {
                FFileUtils.getCacheDir(_dirName, context)
            } else {
                FFileUtils.getFilesDir(_dirName, context)
            }
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