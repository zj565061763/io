package com.sd.lib.io

import android.content.Context
import com.sd.lib.ctx.fContext
import java.io.File
import java.io.IOException
import java.util.UUID

/**
 * 获取缓存目录下的[name]目录，如果[name]为空则获取缓存目录，
 * 缓存目录优先获取[Context.getExternalCacheDir]，如果不存在则获取[Context.getCacheDir]，
 * 如果获取失败则抛出异常[IllegalStateException]
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
    return if (ret.fMakeDirs()) ret else error("cache dir is unavailable")
}

/**
 * 获取文件目录下的[name]目录，如果[name]为空则获取文件目录，
 * 文件目录优先获取[Context.getExternalFilesDir]，如果不存在则获取[Context.getFilesDir]，
 * 如果获取失败则抛出异常[IllegalStateException]
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
    return if (ret.fMakeDirs()) ret else error("files dir is unavailable")
}

/**
 * 在当前[File]目录下创建一个扩展名为[ext]的文件并返回，
 * 如果当前[File]是文件，则抛出异常[IllegalArgumentException]
 * @return 创建的文件，null-创建失败
 */
fun File?.fNewFile(ext: String): File? {
    if (this == null) return null
    if (this.isFile) throw IllegalArgumentException("this file should not be a file")
    val dotExt = ext.fExtAddDot()
    while (true) {
        val filename = UUID.randomUUID().toString() + dotExt
        val file = this.resolve(filename)
        if (file.exists()) {
            continue
        } else {
            return file.takeIf { it.fCreateFile() }
        }
    }
}

/**
 * 把当前[File]拷贝到[target]目录下，由[overwrite]决定是否覆盖，
 * 如果当前[File]==[target]则抛出异常[IllegalArgumentException]，
 * 如果当前[File]是文件，则拷贝到[target]目录下，
 * 如果当前[File]是目录，则拷贝目录下的所有文件到[target]目录下
 * @return true-成功；false-失败
 */
@JvmOverloads
fun File?.fCopyToDir(
    target: File?,
    overwrite: Boolean = true,
): Boolean {
    try {
        if (this == null || target == null) return false
        if (!this.exists()) return false
        if (this == target) throw IllegalArgumentException("this should not be target")
        if (!target.fMakeDirs()) return false
        return if (this.isDirectory) {
            this.copyRecursively(target = target, overwrite = overwrite)
        } else {
            val file = target.resolve(this.name)
            this.fCopyToFile(target = file, overwrite = overwrite)
        }
    } catch (e: IOException) {
        e.printStackTrace()
        return false
    }
}

/**
 * 把当前[File]拷贝到[target]，由[overwrite]决定是否覆盖，
 * 如果当前[File]是目录则抛出异常[IllegalArgumentException]，
 * 如果当前[File]==[target]则抛出异常[IllegalArgumentException]
 * @return true-成功；false-失败
 */
@JvmOverloads
fun File?.fCopyToFile(
    target: File?,
    overwrite: Boolean = true,
): Boolean {
    try {
        if (this == null || target == null) return false
        if (!this.exists()) return false
        if (this.isDirectory) throw IllegalArgumentException("this should not be a directory")
        if (this == target) throw IllegalArgumentException("this should not be target")
        if (target.exists()) {
            if (overwrite) target.deleteRecursively() else return false
        }
        target.parentFile?.mkdirs()
        this.inputStream().use { input ->
            target.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return true
    } catch (e: IOException) {
        e.printStackTrace()
        return false
    }
}

/**
 * 把当前[File]移动到[target]，由[overwrite]决定是否覆盖，
 * 如果当前[File]是目录则抛出异常[IllegalArgumentException]
 * @return true-成功；false-失败
 */
@JvmOverloads
fun File?.fMoveToFile(
    target: File?,
    overwrite: Boolean = true,
): Boolean {
    if (this == null || target == null) return false
    if (!this.exists()) return false
    if (this.isDirectory) throw IllegalArgumentException("this should not be a directory")
    if (this == target) return true
    if (target.exists()) {
        if (overwrite) target.deleteRecursively() else return false
    }
    target.parentFile?.mkdirs()
    return this.renameTo(target)
}

/**
 * 创建新文件，如果已存在文件或者目录则删除后创建新文件
 * @return 文件是否存在
 */
fun File?.fCreateNewFile(): Boolean {
    this?.deleteRecursively()
    return this.fCreateFile()
}

/**
 * 创建文件，如果已存在文件则直接返回true，如果不存在则创建文件，
 * 如果已存在并且是目录则删除该目录后创建新文件
 * @return 文件是否存在
 */
fun File?.fCreateFile(): Boolean {
    try {
        if (this == null) return false
        if (this.isFile) return true
        if (this.isDirectory) this.deleteRecursively()
        this.parentFile?.mkdirs()
        return this.createNewFile()
    } catch (e: IOException) {
        e.printStackTrace()
        return false
    }
}

/**
 * 创建目录，如果已存在目录则直接返回true，如果不存在则创建目录，
 * 如果已存在并且是文件则删除该文件后创建新目录
 * @return 目录是否存在
 */
fun File?.fMakeDirs(): Boolean {
    if (this == null) return false
    if (this.isDirectory) return true
    if (this.isFile) this.delete()
    return this.mkdirs()
}

/**
 * 当前文件或者目录的大小(byte)
 */
fun File?.fSize(): Long {
    if (this == null) return 0
    if (this.isFile) return this.length()
    if (this.isDirectory) {
        return this.walkBottomUp().fold(0) { acc, it ->
            acc + (if (it.isFile) it.length() else 0)
        }
    }
    return 0
}