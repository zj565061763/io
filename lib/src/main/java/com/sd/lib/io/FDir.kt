package com.sd.lib.io

import com.sd.lib.closeable.FAutoCloseFactory
import com.sd.lib.io.FDir.Companion.TempExt
import java.io.File

/**
 * 临时目录
 */
fun fDirTemp(): FDir {
    return fFilesDir("f_dir_temp").fDir()
}

/**
 * 保存Uri的目录
 */
fun fDirUri(): FDir {
    return fFilesDir("f_dir_uri").fDir()
}

/**
 * [File]转[FDir]
 */
fun File.fDir(): FDir {
    if (this.isFile) error("this should not be a file")
    return FDir.get(this)
}

interface FDir {
    /**
     * 返回[key]对应的文件，如果key有扩展名，则返回的文件名包括[key]的扩展名
     */
    fun getKeyFile(key: String?): File?

    /**
     * 返回[key]对应的临时文件，扩展名：[TempExt]
     */
    fun getKeyTempFile(key: String?): File?

    /**
     * 把[file]文件拷贝到当前目录，如果[file]是目录则抛出异常[IllegalStateException]
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
     * 把[file]文件移动到当前目录，如果[file]是目录则抛出异常[IllegalStateException]
     * @param filename 如果不为空-使用该文件名；为空-使用[file]的文件名
     * @param overwrite true-如果目标文件存在则覆盖该文件；false-不覆盖移动失败
     * @return 移动成功-返回移动后的文件；移动失败-返回原文件[file]
     */
    fun takeFile(
        file: File,
        filename: String? = null,
        overwrite: Boolean = true,
    ): File

    /**
     * 在当前目录下创建一个新文件
     * @param ext 文件扩展名
     */
    fun newFile(ext: String): File?

    /**
     * 删除当前目录下的文件(临时文件[TempExt]不会被删除)
     * @param block 遍历文件，返回true则删除该文件
     * @return 返回删除的文件数量
     */
    fun deleteFile(block: ((File) -> Boolean)? = null): Int

    /**
     * 删除当前目录下的临时文件[TempExt]
     * @param block 遍历临时文件，返回true则删除该文件
     * @return 返回删除的文件数量
     */
    fun deleteTempFile(block: ((File) -> Boolean)? = null): Int

    /**
     * 操作当前目录的子级
     */
    fun <T> listFiles(block: (files: Array<File>) -> T): T

    /**
     * 当前目录下所有文件的大小
     */
    fun size(): Long

    /**
     * 操作当前目录
     */
    fun <T> modify(block: (dir: File?) -> T): T

    companion object {
        /** 临时文件扩展名 */
        const val TempExt = "temp"

        private val _factory = FAutoCloseFactory(CloseableDir::class.java)

        @JvmStatic
        fun get(directory: File): FDir {
            val path = directory.absolutePath
            return _factory.create(path) { DirImpl(directory) }
        }
    }
}

private interface CloseableDir : FDir, AutoCloseable

private class DirImpl(dir: File) : CloseableDir {
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
        if (file.isDirectory) error("file should not be a directory")
        return modify { dir ->
            if (dir != null && file.isFile) {
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
        if (file.isDirectory) error("file should not be a directory")
        return modify { dir ->
            if (dir != null && file.isFile) {
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

    override fun deleteFile(block: ((File) -> Boolean)?): Int {
        return listFiles { files ->
            var count = 0
            for (item in files) {
                if (item.extension == TempExt) continue
                if (block == null || block(item)) {
                    if (item.deleteRecursively()) count++
                }
            }
            count
        }
    }

    override fun deleteTempFile(block: ((File) -> Boolean)?): Int {
        return listFiles { files ->
            var count = 0
            for (item in files) {
                if (item.extension != TempExt) continue
                if (block == null || block(item)) {
                    if (item.deleteRecursively()) count++
                }
            }
            count
        }
    }

    override fun <T> listFiles(block: (files: Array<File>) -> T): T {
        return modify {
            val files = it?.listFiles() ?: emptyArray()
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
        require(key.isNotEmpty()) { "key is empty" }
        return modify { dir ->
            if (dir != null) {
                val filename = libMD5(key) + ext.fExtAddDot()
                dir.resolve(filename)
            } else {
                null
            }
        }
    }

    override fun close() {
    }
}