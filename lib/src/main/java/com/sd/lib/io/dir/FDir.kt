package com.sd.lib.io.dir

import android.content.Context
import com.sd.lib.io.FFileUtils
import java.io.File

internal class FDir {
    private val _dir: String

    constructor(dir: String) {
        require(dir.isNotEmpty()) { "dir is empty" }
        _dir = dir
    }

    /**
     * 同步操作
     */
    fun <R> lock(block: () -> R): R {
        synchronized(_dir) {
            return block()
        }
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
}