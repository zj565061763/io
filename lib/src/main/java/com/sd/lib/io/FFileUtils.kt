package com.sd.lib.io

import android.content.Context
import com.sd.lib.ctx.fContext
import java.io.File
import java.text.DecimalFormat
import java.util.UUID

const val FByteKB = 1024L
const val FByteMB = 1024L * FByteKB
const val FByteGB = 1024L * FByteMB
const val FByteTB = 1024L * FByteGB

/**
 * 获取缓存目录下的[name]目录，如果name为空则获取缓存目录，
 * 缓存目录优先获取[Context.getExternalCacheDir]，如果不存在则获取[Context.getCacheDir]
 */
@JvmOverloads
fun fCacheDir(name: String? = null): File {
    val context = fContext
    val dir = context.externalCacheDir ?: context.cacheDir ?: error("cache dir is unavailable")
    val ret = if (name.isNullOrEmpty()) {
        dir
    } else {
        dir.resolve(name)
    }
    return if (ret.fCreateDir()) ret else error("cache dir is unavailable")
}

/**
 * 获取文件目录下的[name]目录，如果name为空则获取文件目录，
 * 文件目录优先获取[Context.getExternalFilesDir]，如果不存在则获取[Context.getFilesDir]
 */
@JvmOverloads
fun fFilesDir(name: String? = null): File {
    val context = fContext
    val dir = context.getExternalFilesDir(null) ?: context.filesDir ?: error("files dir is unavailable")
    val ret = if (name.isNullOrEmpty()) {
        dir
    } else {
        dir.resolve(name)
    }
    return if (ret.fCreateDir()) ret else error("files dir is unavailable")
}

/**
 * 在文件夹下创建一个扩展名为[ext]的文件
 */
fun File?.fNewFile(ext: String?): File? {
    try {
        if (this == null) return null
        if (this.exists() && this.isFile) error("this file should not be a file")
        val fullExt = ext.fDotExt()
        while (true) {
            val filename = UUID.randomUUID().toString() + fullExt
            val file = this.resolve(filename)
            if (file.exists()) {
                continue
            } else {
                return file.takeIf { it.fCreateFile() }
            }
        }
    } catch (e: Exception) {
        return e.libThrowOrReturn { null }
    }
}

/**
 * 如果是文件，则拷贝到[dir]目录下，
 * 如果是目录，则拷贝目录下的所有文件到[dir]目录下
 */
fun File?.fCopyToDir(dir: File?): Boolean {
    try {
        if (this == null || dir == null) return false
        if (!this.exists()) return false
        if (!dir.fCreateDir()) return false
        if (this == dir) return true
        return if (this.isDirectory) {
            this.copyRecursively(dir, overwrite = true)
        } else {
            val file = dir.resolve(this.name)
            this.fCopyToFile(file)
        }
    } catch (e: Exception) {
        return e.libThrowOrReturn { false }
    }
}

/**
 * 拷贝文件
 */
fun File?.fCopyToFile(file: File?): Boolean {
    try {
        if (this == null || file == null) return false
        if (!this.exists()) return false
        if (this.isDirectory) error("this should not be a directory")
        if (this == file) return true
        if (!file.fCreateFile()) return false
        this.copyTo(file, overwrite = true)
        return true
    } catch (e: Exception) {
        return e.libThrowOrReturn { false }
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
    if (!file.fCreateFile()) return false
    return try {
        this.renameTo(file)
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

/**
 * 检查文件是否存在，如果不存在则尝试创建，如果已存在则根据[overwrite]来决定是否覆盖，默认覆盖
 * @return true-创建成功或者文件已经存在
 */
fun File?.fCreateFile(overwrite: Boolean = true): Boolean {
    return try {
        if (this == null) return false
        if (!this.exists()) this.parentFile.fCreateDir() && this.createNewFile()
        if (overwrite) {
            this.fDelete()
        } else {
            if (this.isFile) return true
            this.deleteRecursively()
        }
        return this.parentFile.fCreateDir() && this.createNewFile()
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

/**
 * 检查文件夹是否存在，如果不存在则尝试创建，如果已存在并且是文件则删除该文件并尝试创建文件夹
 * @return true-创建成功或者文件夹已经存在
 */
fun File?.fCreateDir(): Boolean {
    try {
        if (this == null) return false
        if (!this.exists()) return this.mkdirs()
        if (this.isDirectory) return true
        this.fDelete()
        return this.mkdirs()
    } catch (e: Exception) {
        e.printStackTrace()
        return false
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
        this < FByteTB -> df.format(this.toDouble() / FByteGB) + "GB"
        else -> df.format(this.toDouble() / FByteTB) + "TB"
    }
}