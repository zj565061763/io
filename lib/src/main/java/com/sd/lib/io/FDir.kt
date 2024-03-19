package com.sd.lib.io

import android.net.Uri
import com.sd.lib.io.uri.fFileName
import com.sd.lib.io.uri.fSaveToFile
import java.io.File
import java.security.MessageDigest

interface FDir {
    /**
     * 获取[key]对应的文件，如果key有扩展名，则返回的文件名包括[key]的扩展名
     */
    fun getKeyFile(key: String): File

    /**
     * 把[file]文件拷贝到当前目录，如果[file]是目录则抛出异常[IllegalArgumentException]
     * @param filename 不为空-使用该文件名；为空-使用[file]的文件名
     * @param overwrite true-如果目标文件存在则覆盖该文件；false-不覆盖拷贝失败
     * @return 拷贝成功-返回拷贝后的文件；拷贝失败-返回原文件[file]
     */
    fun copyFile(
        file: File,
        filename: String? = null,
        overwrite: Boolean = true,
    ): File

    /**
     * 把[file]文件移动到当前目录，如果[file]是目录则抛出异常[IllegalArgumentException]
     * @param filename 不为空-使用该文件名；为空-使用[file]的文件名
     * @param overwrite true-如果目标文件存在则覆盖该文件；false-不覆盖移动失败
     * @return 移动成功-返回移动后的文件；移动失败-返回原文件[file]
     */
    fun takeFile(
        file: File,
        filename: String? = null,
        overwrite: Boolean = true,
    ): File

    /**
     * 在当前目录下创建并返回一个扩展名为[ext]的新文件
     */
    fun newFile(ext: String): File?

    /**
     * 保存[uri]的内容，并返回对应的文件
     */
    fun saveUri(uri: Uri?): File?

    /**
     * 删除当前目录下的文件
     * @param block 遍历文件，返回true则删除该文件
     * @return 返回删除的文件数量
     */
    fun deleteFile(block: ((File) -> Boolean)? = null): Int

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
    fun <T> modify(block: (dir: File) -> T): T

    companion object {
        /**
         * 返回[directory]对应的[FDir]，如果[directory]是一个文件则抛出异常[IllegalArgumentException]
         */
        @JvmStatic
        fun get(directory: File): FDir {
            if (directory.isFile) throw IllegalArgumentException("directory should not be a file")
            return DirImpl(directory)
        }
    }
}

private class DirImpl(dir: File) : FDir {
    private val _dir = dir

    override fun getKeyFile(key: String): File {
        val ext = key.fExt()
        return modify { dir ->
            dir.resolve(fMd5(key) + ext.fExtAddDot())
        }
    }

    override fun copyFile(
        file: File,
        filename: String?,
        overwrite: Boolean,
    ): File {
        if (file.isDirectory) throw IllegalArgumentException("file should not be a directory")
        return modify { dir ->
            if (file.isFile) {
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
        if (file.isDirectory) throw IllegalArgumentException("file should not be a directory")
        return modify { dir ->
            if (file.isFile) {
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
        return modify { dir -> dir.fNewFile(ext) }
    }

    override fun saveUri(uri: Uri?): File? {
        if (uri == null) return null
        val ext = uri.fFileName().fExt()
        return modify { dir ->
            dir.fNewFile(ext).takeIf { uri.fSaveToFile(it) }
        }
    }

    override fun deleteFile(block: ((File) -> Boolean)?): Int {
        return listFiles { files ->
            var count = 0
            for (item in files) {
                if (block == null || block(item)) {
                    if (item.deleteRecursively()) count++
                }
            }
            count
        }
    }

    override fun <T> listFiles(block: (files: Array<File>) -> T): T {
        return modify { dir ->
            val files = dir.listFiles() ?: emptyArray()
            block(files)
        }
    }

    override fun size(): Long {
        return modify { dir -> dir.fSize() }
    }

    override fun <T> modify(block: (dir: File) -> T): T {
        _dir.fMakeDirs()
        return block(_dir)
    }
}

private fun fMd5(input: String): String {
    val md5Bytes = MessageDigest.getInstance("MD5").digest(input.toByteArray())
    return buildString {
        for (byte in md5Bytes) {
            val hex = Integer.toHexString(0xff and byte.toInt())
            if (hex.length == 1) append("0")
            append(hex)
        }
    }
}