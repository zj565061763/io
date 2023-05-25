package com.sd.lib.io

import android.content.Context
import com.sd.lib.ctx.fContext
import java.io.File
import java.text.DecimalFormat
import java.util.UUID

object FFileUtils {
    const val KB = 1024L
    const val MB = 1024 * KB
    const val GB = 1024 * MB

    /**
     * 移动文件
     */
    @JvmStatic
    fun moveFile(source: File?, target: File?): Boolean {
        if (source == null || !source.exists()) return false
        if (target == null) return false

        target.fDelete()
        if (!target.parentFile.fCheckDir()) return false

        return try {
            source.renameTo(target)
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
        it.fCheckDir()
    }
}

/**
 * 获取文件目录下的[name]目录，如果name为空则获取文件目录，
 * 文件目录优先获取[Context.getExternalFilesDir]，如果不存在则获取[Context.getFilesDir]
 */
@JvmOverloads
fun fFilesDir(name: String? = null): File {
    val context = fContext
    val dir = context.getExternalFilesDir(null) ?: context.filesDir
    val ret = if (name.isNullOrEmpty()) {
        dir
    } else {
        File(dir, name)
    }
    return ret.also {
        it.fCheckDir()
    }
}

/**
 * 在文件夹下创建一个扩展名为[ext]的文件
 */
fun File.fNewFile(ext: String?): File {
    this.fCheckDir()
    val fullExt = ext.fFullExt()
    while (true) {
        val filename = UUID.randomUUID().toString() + fullExt
        val file = File(this, filename)
        if (file.exists()) {
            continue
        } else {
            file.fCheckFile()
            return file
        }
    }
}

/**
 * 检查文件是否存在，如果不存在则尝试创建
 * @return true-文件已经存在，或者创建成功
 */
fun File?.fCheckFile(): Boolean {
    return try {
        if (this == null) return false
        if (!this.exists()) return this.createNewFile()
        if (this.isFile) return true
        this.deleteRecursively()
        return this.createNewFile()
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

/**
 * 检查文件夹是否存在，如果不存在则尝试创建
 * @return true-文件夹已经存在，或者创建成功
 */
fun File?.fCheckDir(): Boolean {
    try {
        if (this == null) return false
        if (!this.exists()) return this.mkdirs()
        if (this.isDirectory) return true
        this.delete()
        return this.mkdirs()
    } catch (e: Exception) {
        e.printStackTrace()
        return false
    }
}

/**
 * 如果是文件，则拷贝到[dir]目录下，
 * 如果是目录，则拷贝目录下的所有文件到[dir]目录下
 */
fun File?.fCopyToDir(dir: File?): Boolean {
    if (this == null || dir == null) return false
    if (!this.exists()) return false
    if (!dir.fCheckDir()) return false
    if (this == dir) return true
    return try {
        if (this.isDirectory) {
            this.copyRecursively(dir, overwrite = true)
        } else {
            this.fCopyToFile(File(dir, this.name))
        }
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

/**
 * 拷贝文件
 */
fun File?.fCopyToFile(file: File?): Boolean {
    if (this == null || file == null) return false
    if (!this.exists()) return false
    if (this.isDirectory) error("this should not be a directory")
    if (this == file) return true
    return try {
        this.copyTo(file, overwrite = true)
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

/**
 * 移动文件
 */
fun File?.fMoveToFile(file: File?): Boolean {
    if (this == null || file == null) return false
    if (!this.exists()) return false
    if (this.isDirectory) error("this should not be a directory")
    if (!file.fCheckFile()) return false
    if (this == file) return true
    return try {
        this.renameTo(file)
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

/**
 * 删除文件或者目录
 */
fun File?.fDelete() {
    if (this == null) return
    try {
        if (!this.exists()) return
        if (this.isFile) {
            this.delete()
        } else {
            this.deleteRecursively()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}