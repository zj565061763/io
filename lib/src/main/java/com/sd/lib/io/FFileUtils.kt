package com.sd.lib.io

import android.content.Context
import com.sd.lib.ctx.fContext
import java.io.File
import java.text.DecimalFormat
import java.util.UUID

const val FByteKB = 1024L
const val FByteMB = 1024 * FByteKB
const val FByteGB = 1024 * FByteMB
const val FByteTB = 1024 * FByteGB

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
        if (!this.exists()) {
            this.parentFile.fCheckDir()
            return this.createNewFile()
        }
        if (this.isFile) return true
        this.deleteRecursively()
        this.parentFile.fCheckDir()
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
    if (!file.fCheckFile()) return false
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
    if (this == file) return true
    if (!file.fCheckFile()) return false
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

/**
 * 返回文件或者文件夹的大小（字节）
 */
fun File?.fSize(): Long {
    if (this == null || !this.exists()) return 0
    return if (this.isFile) {
        this.length()
    } else {
        this.walkBottomUp().fold(0) { acc, it ->
            acc + (if (it.isFile) it.length() else 0)
        }
    }
}

/**
 * 返回格式化的字符串
 */
@JvmOverloads
fun Long.fFormatByteSize(df: DecimalFormat = DecimalFormat("#.0")): String {
    return when {
        this <= 0 -> df.format(0.0) + "B"
        this < FByteKB -> df.format(this.toDouble()) + "B"
        this < FByteMB -> df.format(this.toDouble() / FByteKB) + "KB"
        this < FByteGB -> df.format(this.toDouble() / FByteMB) + "MB"
        else -> df.format(this.toDouble() / FByteGB) + "GB"
    }
}