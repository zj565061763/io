package com.sd.lib.io

import java.io.File
import java.util.concurrent.atomic.AtomicInteger

/**
 * 临时目录
 */
fun fDirTemp(): IDir {
    return fDir(fFilesDir("f_dir_temp"))
}

/**
 * 保存Uri的目录
 */
fun fDirUri(): IDir {
    return fDir(fFilesDir("f_dir_uri"))
}

fun fDir(dir: File): IDir {
    val finalDir = if (dir.exists()) {
        if (dir.isDirectory) dir else dir.parentFile
    } else {
        dir
    }
    return FDir(finalDir)
}

interface IDir {
    /**
     * 在当前文件夹下创建一个新文件
     */
    fun newFile(ext: String?): File?

    /**
     * 操作当前文件夹
     */
    fun <T> modify(block: (dir: File?) -> T): T

    /**
     * 删除当前目录以及下面的所有文件
     */
    fun delete()
}

private class FDir(dir: File) : IDir {
    private val _dir = dir
    private val _directory: IDir
        get() = InternalDir.open(_dir)

    override fun newFile(ext: String?): File? {
        return _directory.newFile(ext)
    }

    override fun <T> modify(block: (dir: File?) -> T): T {
        return _directory.modify(block)
    }

    override fun delete() {
        _directory.delete()
    }

    init {
        addCount(_dir)
    }

    protected fun finalize() {
        removeCount(_dir)
    }

    companion object {
        private val _counterHolder: MutableMap<String, AtomicInteger> = hashMapOf()

        private fun addCount(directory: File) {
            synchronized(InternalDir.Companion) {
                val path = directory.absolutePath
                val counter = _counterHolder[path] ?: AtomicInteger(0).also {
                    _counterHolder[path] = it
                }
                counter.incrementAndGet()
            }
        }

        private fun removeCount(directory: File) {
            synchronized(InternalDir.Companion) {
                val path = directory.absolutePath
                val counter = _counterHolder[path] ?: error("Directory was not found $path")
                counter.decrementAndGet().let {
                    if (it <= 0) {
                        _counterHolder.remove(path)
                        InternalDir.close(directory)
                    }
                }
            }
        }
    }
}

private class InternalDir private constructor(dir: File) : IDir {
    private val _dir = dir

    override fun newFile(ext: String?): File? {
        return modify { it?.fNewFile(ext) }
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
        return if (_dir.fCreateDir()) _dir else null
    }

    companion object {
        private val _instanceHolder: MutableMap<String, InternalDir> = hashMapOf()

        fun open(directory: File): IDir {
            return synchronized(this@Companion) {
                val path = directory.absolutePath
                _instanceHolder[path] ?: InternalDir(directory).also {
                    _instanceHolder[path] = it
                }
            }
        }

        fun close(directory: File) {
            synchronized(this@Companion) {
                val path = directory.absolutePath
                _instanceHolder.remove(path)
            }
        }
    }
}