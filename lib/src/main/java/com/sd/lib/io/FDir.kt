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

/**
 * 把[dir]转为[IDir]
 */
fun fDir(dir: File): IDir {
    val finalDir = if (dir.exists()) {
        if (dir.isDirectory) dir else error("dir should not be a file")
    } else {
        dir
    }
    return FDir(finalDir)
}

interface IDir {
    /**
     * 在当前文件夹下创建一个新文件
     */
    fun newFile(ext: String): File?

    /**
     * 删除当前目录以及下面的所有文件
     */
    fun delete(): Boolean

    /**
     * 操作当前文件夹
     */
    fun <T> modify(block: (dir: File?) -> T): T
}

private class FDir(dir: File) : IDir {
    private val _dir = dir
    private val _directory: IDir
        get() = InternalDir.open(_dir)

    override fun newFile(ext: String): File? {
        return _directory.newFile(ext)
    }

    override fun delete(): Boolean {
        return _directory.delete()
    }

    override fun <T> modify(block: (dir: File?) -> T): T {
        return _directory.modify(block)
    }

    init {
        addCount(_dir)
    }

    protected fun finalize() {
        removeCount(_dir)
    }

    companion object {
        private val sCounterHolder: MutableMap<String, AtomicInteger> = hashMapOf()

        private fun addCount(directory: File) {
            synchronized(InternalDir.Companion) {
                val path = directory.absolutePath
                val counter = sCounterHolder[path] ?: AtomicInteger(0).also {
                    sCounterHolder[path] = it
                }
                counter.incrementAndGet()
            }
        }

        private fun removeCount(directory: File) {
            synchronized(InternalDir.Companion) {
                val path = directory.absolutePath
                val counter = sCounterHolder[path] ?: error("Directory was not found $path")
                counter.decrementAndGet().let {
                    if (it <= 0) {
                        sCounterHolder.remove(path)
                        InternalDir.close(directory)
                    }
                }
            }
        }
    }
}

private class InternalDir private constructor(dir: File) : IDir {
    private val _dir = dir

    override fun newFile(ext: String): File? {
        return modify { it.fNewFile(ext) }
    }

    override fun delete(): Boolean {
        return modify { it.fDelete() }
    }

    @Synchronized
    override fun <T> modify(block: (dir: File?) -> T): T {
        return block(createDir())
    }

    private fun createDir(): File? {
        return if (_dir.fCreateDir()) _dir else null
    }

    companion object {
        private val sInstanceHolder: MutableMap<String, InternalDir> = hashMapOf()

        fun open(directory: File): IDir {
            return synchronized(this@Companion) {
                val path = directory.absolutePath
                sInstanceHolder[path] ?: InternalDir(directory).also {
                    sInstanceHolder[path] = it
                }
            }
        }

        fun close(directory: File) {
            synchronized(this@Companion) {
                val path = directory.absolutePath
                sInstanceHolder.remove(path)
            }
        }
    }
}