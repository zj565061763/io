package com.sd.lib.io

import com.sd.lib.io.IDir.Companion.TempExt
import java.io.File
import java.util.concurrent.atomic.AtomicInteger

/**
 * 临时目录
 */
fun fDirTemp(): IDir {
    return fFilesDir("f_dir_temp").fDir()
}

/**
 * 保存Uri的目录
 */
fun fDirUri(): IDir {
    return fFilesDir("f_dir_uri").fDir()
}

/**
 * [File]转[IDir]
 */
fun File.fDir(): IDir {
    if (this.isFile) error("this should not be a file")
    return DirApi(this)
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
     * @param block 遍历文件，返回true则跳过该文件
     * @return 返回删除的文件数量
     */
    fun deleteFile(ext: String?, block: ((File) -> Boolean)? = null): Int

    /**
     * 删除临时文件
     * @param block 遍历临时文件，返回true则跳过该文件
     * @return 返回删除的文件数量
     */
    fun deleteTempFile(block: ((File) -> Boolean)? = null): Int

    /**
     * 操作当前文件夹的子级
     */
    fun <T> listFiles(block: (files: Array<File>?) -> T): T

    /**
     * 当前文件夹下所有文件的大小
     */
    fun size(): Long

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

    override fun deleteFile(ext: String?, block: ((File) -> Boolean)?): Int {
        return _directory.deleteFile(ext, block)
    }

    override fun deleteTempFile(block: ((File) -> Boolean)?): Int {
        return _directory.deleteTempFile(block)
    }

    override fun <T> listFiles(block: (files: Array<File>?) -> T): T {
        return _directory.listFiles(block)
    }

    override fun size(): Long {
        return _directory.size()
    }

    override fun <T> modify(block: (dir: File?) -> T): T {
        return _directory.modify(block)
    }

    override fun hashCode(): Int {
        return _directory.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return if (other is DirApi) _directory == other._directory else false
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
                val name = file.name.fExtRename(filename)
                val target = dir.resolve(name)
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
                val name = file.name.fExtRename(filename)
                val target = dir.resolve(name)
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

    override fun deleteFile(ext: String?, block: ((File) -> Boolean)?): Int {
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
                        if (block != null && block(item)) continue
                        if (item.fDelete()) count++
                    }
                }
                count
            } else {
                0
            }
        }
    }

    override fun deleteTempFile(block: ((File) -> Boolean)?): Int {
        return listFiles { files ->
            if (!files.isNullOrEmpty()) {
                var count = 0
                for (item in files) {
                    if (block != null && block(item)) continue
                    if (item.extension == TempExt) {
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
        return modify {
            val files = it?.listFiles()
            block(files)
        }
    }

    override fun size(): Long {
        return modify { it.fSize() }
    }

    @Synchronized
    override fun <T> modify(block: (dir: File?) -> T): T {
        val directory = if (_dir.fMakeDirs()) _dir else null
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

    override fun hashCode(): Int {
        return _dir.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return if (other is DirImpl) _dir == other._dir else false
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