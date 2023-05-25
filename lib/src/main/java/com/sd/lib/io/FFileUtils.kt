package com.sd.lib.io

import android.content.Context
import android.os.Environment
import com.sd.lib.ctx.fContext
import java.io.File
import java.text.DecimalFormat
import java.util.UUID

object FFileUtils {
    const val KB = 1024L
    const val MB = 1024 * KB
    const val GB = 1024 * MB

    /**
     * 外部存储是否存在
     */
    @JvmStatic
    fun isExternalStorageMounted(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

    /**
     * 优先获取外部files下的[name]目录，如果获取失败则获取内部files下的[name]目录
     */
    @JvmStatic
    fun getFilesDir(name: String): File {
        val context = fContext
        val externalDir = context.getExternalFilesDir(name)
        if (checkDir(externalDir)) return externalDir!!
        return File(context.filesDir, name).also {
            checkDir(it)
        }
    }

    /**
     * 在文件夹下[dir]下创建一个扩展名为[ext]的文件
     */
    @JvmStatic
    fun newFileUnderDir(dir: File, ext: String?): File {
        checkDir(dir)
        val fullExt = ext.fFullExt()
        while (true) {
            val filename = UUID.randomUUID().toString() + fullExt
            val file = File(dir, filename)
            if (file.exists()) {
                continue
            } else {
                createFile(file)
                return file
            }
        }
    }

    /**
     * 如果[source]是文件，则拷贝到[dir]目录下，
     * 如果[source]是目录，则拷贝目录下的所有文件到[dir]目录下
     */
    @JvmStatic
    fun copyToDir(source: File?, dir: File?): Boolean {
        if (source == null || dir == null) return false
        if (source == dir) return true
        if (dir.exists() && !dir.isDirectory) {
            delete(dir)
        }
        return try {
            if (source.isDirectory) {
                source.copyRecursively(dir, overwrite = true)
            } else {
                val targetFile = File(dir, source.name)
                copyFile(source, targetFile)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 拷贝文件
     */
    @JvmStatic
    fun copyFile(source: File?, target: File?): Boolean {
        if (source == null || !source.exists()) return false
        if (target == null) return false
        if (source.isDirectory) throw IllegalArgumentException("source should not be a directory")
        if (source == target) return true

        val temp = File(target.absolutePath + ".temp")
        delete(temp)

        try {
            source.copyTo(temp, overwrite = true)
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

        return moveFile(temp, target)
    }

    /**
     * 移动文件
     */
    @JvmStatic
    fun moveFile(source: File?, target: File?): Boolean {
        if (source == null || !source.exists()) return false
        if (target == null) return false

        delete(target)
        if (!checkDir(target.parentFile)) return false

        return try {
            source.renameTo(target)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 删除文件或者目录
     */
    @JvmStatic
    fun delete(file: File?): Boolean {
        if (file == null) return false
        return try {
            file.deleteRecursively()
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    /**
     * 检查文件夹是否存在，如果不存在则尝试创建
     */
    @JvmStatic
    fun checkDir(file: File?): Boolean {
        if (file == null) return false
        return try {
            if (file.exists()) true else file.mkdirs()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 检查文件的父文件夹是否存在，如果不存在则尝试创建
     */
    fun checkParentDir(file: File?): Boolean {
        if (file == null) return false
        val parent = file.parentFile ?: return true
        return checkDir(parent)
    }

    /**
     * 检查文件是否存在，不存在则尝试创建
     */
    @JvmStatic
    fun createFile(file: File?): Boolean {
        if (file == null) return false
        return try {
            if (file.exists()) true else file.createNewFile()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 返回[file]的大小（字节）
     */
    @JvmStatic
    fun getSize(file: File?): Long {
        if (file == null || !file.exists()) return 0
        return file.walkBottomUp().fold(0) { acc, it ->
            acc + (if (it.isFile) it.length() else 0)
        }
    }

    /**
     * 返回格式化的字符串
     */
    @JvmStatic
    @JvmOverloads
    fun formatSize(byteSize: Long, df: DecimalFormat = DecimalFormat("#.0")): String {
        return if (byteSize <= 0) {
            "0.0B"
        } else if (byteSize < KB) {
            df.format(byteSize.toDouble()) + "B"
        } else if (byteSize < MB) {
            df.format(byteSize.toDouble() / KB) + "KB"
        } else if (byteSize < GB) {
            df.format(byteSize.toDouble() / MB) + "MB"
        } else {
            df.format(byteSize.toDouble() / GB) + "GB"
        }
    }
}

/**
 * 获取缓存目录下的[name]目录，如果name为空则获取缓存目录，
 * 缓存目录优先获取[Context.getExternalCacheDir]，如果不存在则获取[Context.getCacheDir]
 */
@JvmOverloads
fun fCacheDir(name: String? = null): File {
    val context = fContext
    val dir = context.externalCacheDir ?: context.cacheDir
    val ret = if (name.isNullOrEmpty()) {
        dir
    } else {
        File(dir, name)
    }
    return ret.also {
        it.checkDir()
    }
}

/**
 * 检查文件夹是否存在，如果不存在则尝试创建
 */
fun File?.checkDir(): Boolean {
    if (this == null) return false
    try {
        if (!this.exists()) return this.mkdirs()
        if (this.isDirectory) return true
        this.delete()
        return this.mkdirs()
    } catch (e: Exception) {
        e.printStackTrace()
        return false
    }
}
