package com.sd.lib.io

import com.sd.lib.io.IDir.Companion.TempExt
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
    return DirApi(finalDir)
}

interface IDir {
    /**
     * 返回[key]对应的文件，如果key包括扩展名，则会使用[key]的扩展名
     */
    fun getKeyFile(key: String?): File?

    /**
     * 返回[key]对应的临时文件，扩展名：[TempExt]
     */
    fun getKeyTempFile(key: String?): File?

    /**
     * 把[file]文件拷贝到当前目录
     * @param filename 如果不为空-使用该文件名；为空-使用[file]的文件名
     * @param overwrite true-如果目标文件存在则覆盖该文件；false-不覆盖拷贝失败
     * @return 拷贝成功-返回拷贝后的文件；拷贝失败-返回原文件[file]
     */
    fun copyFile(
        file: File,
        filename: String? = null,
        overwrite: Boolean = true,
    ): File

    /**
     * 把[file]文件移动到当前目录
     * @param filename 如果不为空-使用该文件名；为空-使用[file]的文件名
     * @param overwrite true-如果目标文件存在则覆盖该文件；false-不覆盖拷贝失败
     * @return 移动成功-返回移动后的文件；移动失败-返回原文件[file]
     */
    fun takeFile(
        file: File,
        filename: String? = null,
        overwrite: Boolean = true,
    ): File

    /**
     * 在当前文件夹下创建一个新文件
     */
    fun newFile(ext: String): File?

    /**
     * 删除当前文件夹下的文件（临时文件不会被删除）
     * @param ext 文件扩展名（例如mp3）null-删除所有文件
     * @return 返回删除的文件数量
     */
    fun deleteFile(ext: String?): Int

    /**
     * 操作当前文件夹的子级
     */
    fun <T> listFiles(block: (files: Array<File>?) -> T): T

    /**
     * 操作当前文件夹
     */
    fun <T> modify(block: (dir: File?) -> T): T

    companion object {
        const val TempExt = "temp"
    }
}

private class DirApi(dir: File) : IDir {
    private val _dir = dir
    private val _directory: IDir
        get() = DirImpl.open(_dir)

    override fun getKeyFile(key: String?): File? {
        return _directory.getKeyFile(key)
    }

    override fun getKeyTempFile(key: String?): File? {
        return _directory.getKeyTempFile(key)
    }

    override fun copyFile(
        file: File,
        filename: String?,
        overwrite: Boolean,
    ): File {
        return _directory.copyFile(
            file = file,
            filename = filename,
            overwrite = overwrite,
        )
    }

    override fun takeFile(
        file: File,
        filename: String?,
        overwrite: Boolean,
    ): File {
        return _directory.takeFile(
            file = file,
            filename = filename,
            overwrite = overwrite,
        )
    }

    override fun newFile(ext: String): File? {
        return _directory.newFile(ext)
    }

    override fun deleteFile(ext: String?): Int {
        return _directory.deleteFile(ext)
    }

    override fun <T> listFiles(block: (files: Array<File>?) -> T): T {
        return _directory.listFiles(block)
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
            synchronized(DirImpl.Companion) {
                val path = directory.absolutePath
                val counter = sCounterHolder[path] ?: AtomicInteger(0).also {
                    sCounterHolder[path] = it
                }
                counter.incrementAndGet()
            }
        }

        private fun removeCount(directory: File) {
            synchronized(DirImpl.Companion) {
                val path = directory.absolutePath
                val counter = sCounterHolder[path] ?: error("Directory was not found $path")
                counter.decrementAndGet().let {
                    if (it <= 0) {
                        sCounterHolder.remove(path)
                        DirImpl.close(directory)
                    }
                }
            }
        }
    }
}

private class DirImpl private constructor(dir: File) : IDir {
    private val _dir = dir

    override fun getKeyFile(key: String?): File? {
        if (key.isNullOrEmpty()) return null
        return createKeyFile(
            key = key,
            ext = key.fExt(),
        )
    }

    override fun getKeyTempFile(key: String?): File? {
        if (key.isNullOrEmpty()) return null
        return createKeyFile(
            key = key,
            ext = TempExt,
        )
    }

    override fun copyFile(
        file: File,
        filename: String?,
        overwrite: Boolean,
    ): File {
        return modify { dir ->
            if (dir != null && file.exists()) {
                if (file.isDirectory) error("file should not be a directory")
                val target = dir.resolve(filename.takeUnless { it.isNullOrEmpty() } ?: file.name)
                val success = file.fCopyToFile(target = target, overwrite = overwrite)
                if (success) target else file
            } else {
                file
            }
        }
    }

    override fun takeFile(
        file: File,
        filename: String?,
        overwrite: Boolean,
    ): File {
        return modify { dir ->
            if (dir != null && file.exists()) {
                if (file.isDirectory) error("file should not be a directory")
                val target = dir.resolve(filename.takeUnless { it.isNullOrEmpty() } ?: file.name)
                val success = file.fMoveToFile(target = target, overwrite = overwrite)
                if (success) target else file
            } else {
                file
            }
        }
    }

    override fun newFile(ext: String): File? {
        return modify { it.fNewFile(ext) }
    }

    override fun deleteFile(ext: String?): Int {
        return listFiles { files ->
            if (!files.isNullOrEmpty()) {
                val noneDotExt = if (ext.isNullOrEmpty()) ext else {
                    ext.fExtRemoveDot()
                }

                var count = 0
                for (item in files) {
                    val itemExt = item.extension
                    if (itemExt == TempExt) continue
                    if (noneDotExt == null || noneDotExt == itemExt) {
                        if (item.fDelete()) count++
                    }
                }
                count
            } else {
                0
            }
        }
    }

    override fun <T> listFiles(block: (files: Array<File>?) -> T): T {
        return modify { block(it?.listFiles()) }
    }

    @Synchronized
    override fun <T> modify(block: (dir: File?) -> T): T {
        val directory = if (_dir.fCreateDir()) _dir else null
        return block(directory)
    }

    private fun createKeyFile(
        key: String,
        ext: String,
    ): File? {
        if (key.isEmpty()) return null
        return modify { dir ->
            if (dir != null) {
                val filename = libMD5(key) + ext.fExtAddDot()
                dir.resolve(filename)
            } else {
                null
            }
        }
    }

    companion object {
        private val sInstanceHolder: MutableMap<String, DirImpl> = hashMapOf()

        fun open(directory: File): IDir {
            return synchronized(this@Companion) {
                val path = directory.absolutePath
                sInstanceHolder[path] ?: DirImpl(directory).also {
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