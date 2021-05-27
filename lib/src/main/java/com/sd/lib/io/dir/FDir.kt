package com.sd.lib.io.dir

import android.content.Context
import com.sd.lib.io.FFileUtils
import java.io.File

class FDir {
    private val _dir: String

    constructor(dir: String) {
        if (dir.isEmpty()) throw IllegalArgumentException("dir is empty")
        _dir = dir
    }

    /**
     * 返回目录
     */
    fun get(context: Context): File? {
        return lock {
            FFileUtils.getFilesDir(_dir, context)
        }
    }

    /**
     * 删除目录
     */
    fun delete(context: Context) {
        lock {
            val dir = get(context)
            FFileUtils.delete(dir)
        }
    }

    /**
     * 创建文件
     */
    fun newFile(ext: String?, context: Context): File? {
        val dir = get(context)
        return FFileUtils.newFileUnderDir(dir, ext)
    }

    /**
     * 同步操作
     */
    fun <R> lock(block: () -> R): R {
        synchronized(_dir) {
            return block()
        }
    }
}