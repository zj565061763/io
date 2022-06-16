package com.sd.lib.io.dir

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
    fun get(): File {
        synchronized(_dirName) {
            return if (_isCacheDir) {
                FFileUtils.getCacheDir(_dirName)
            } else {
                FFileUtils.getFilesDir(_dirName)
            }
        }
    }

    /**
     * 删除目录
     */
    fun delete() {
        synchronized(_dirName) {
            FFileUtils.delete(get())
        }
    }

    /**
     * 创建文件
     */
    fun newFile(ext: String?): File {
        synchronized(_dirName) {
            return FFileUtils.newFileUnderDir(get(), ext)
        }
    }

    /**
     * 同步操作
     */
    fun <R> lock(block: (dir: File) -> R): R {
        synchronized(_dirName) {
            return block(get())
        }
    }
}