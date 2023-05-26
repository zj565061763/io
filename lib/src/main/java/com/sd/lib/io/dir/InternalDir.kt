package com.sd.lib.io.dir

import com.sd.lib.io.fCheckDir
import com.sd.lib.io.fNewFile
import java.io.File

class InternalDir private constructor(dir: File) : IDir {
    private val _dir = dir

    override fun <T> newFile(ext: String?, block: (File?) -> T): T {
        return modify {
            val newFile = it?.fNewFile(ext)
            block(newFile)
        }
    }

    @Synchronized
    override fun <T> modify(block: (dir: File?) -> T): T {
        return block(checkDir())
    }

    @Synchronized
    override fun delete() {
        _dir.delete()
    }

    private fun checkDir(): File? {
        return if (_dir.fCheckDir()) _dir else null
    }

    companion object {
        private val _cacheHolder: MutableMap<String, InternalDir> = hashMapOf()

        fun open(directory: File): IDir {
            return synchronized(this@Companion) {
                val path = directory.absolutePath
                _cacheHolder[path] ?: InternalDir(directory).also {
                    _cacheHolder[path] = it
                }
            }
        }

        fun close(directory: File) {
            synchronized(this@Companion) {
                val path = directory.absolutePath
                _cacheHolder.remove(path)
            }
        }
    }
}