package com.sd.lib.io

import android.content.Context
import com.sd.lib.ctx.fContext
import java.io.File
import java.util.UUID

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
    return if (ret.fMakeDirs()) ret else error("cache dir is unavailable")
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
    return if (ret.fMakeDirs()) ret else error("files dir is unavailable")
}

/**
 * 在文件夹下创建一个扩展名为[ext]的文件
 */
fun File?.fNewFile(ext: String): File? {
    if (this == null) return null
    if (this.isFile) error("this file should not be a file")
    val dotExt = ext.fExtAddDot()
    while (true) {
        val filename = UUID.randomUUID().toString() + dotExt
        val file = this.resolve(filename)
        if (file.exists()) {
            continue
        } else {
            return file.takeIf { it.fCreateNewFile() }
        }
    }
}

/**
 * 如果是文件，则拷贝到[target]目录下，
 * 如果是目录，则拷贝目录下的所有文件到[target]目录下
 */
fun File?.fCopyToDir(target: File?): Boolean {
    try {
        if (this == null || target == null) return false
        if (!this.exists()) return false
        if (this == target) error("this should not be target")
        if (!target.fMakeDirs()) return false
        return if (this.isDirectory) {
            this.copyRecursively(target = target, overwrite = true)
        } else {
            val file = target.resolve(this.name)
            this.fCopyToFile(target = file, overwrite = true)
        }
    } catch (e: Exception) {
        return e.libThrowOrReturn { false }
    }
}

/**
 * 拷贝文件，如果[target]已存在则由[overwrite]决定是否覆盖
 */
@JvmOverloads
fun File?.fCopyToFile(target: File?, overwrite: Boolean = true): Boolean {
    try {
        if (this == null || target == null) return false
        if (!this.exists()) return false
        if (this.isDirectory) error("this should not be a directory")
        if (this == target) error("this should not be target")
        if (target.exists() && !overwrite) return false
        if (!target.fCreateNewFile()) return false
        this.copyTo(target, overwrite = true)
        return true
    } catch (e: Exception) {
        return e.libThrowOrReturn { false }
    }
}

/**
 * 移动文件，如果[target]已存在则由[overwrite]决定是否覆盖
 */
fun File?.fMoveToFile(target: File?, overwrite: Boolean = true): Boolean {
    try {
        if (this == null || target == null) return false
        if (!this.exists()) return false
        if (this.isDirectory) error("this should not be a directory")
        if (this == target) error("this should not be target")
        if (target.exists() && !overwrite) return false
        if (!target.fCreateNewFile()) return false
        return this.renameTo(target)
    } catch (e: Exception) {
        return e.libThrowOrReturn { false }
    }
}

/**
 * 创建新文件，如果已存在文件或者文件夹则删除后创建新文件
 * @return 文件是否存在
 */
fun File?.fCreateNewFile(): Boolean {
    try {
        if (this == null) return false
        this.fDelete()
        return this.parentFile.fMakeDirs() && this.createNewFile()
    } catch (e: Exception) {
        return e.libThrowOrReturn { false }
    }
}

/**
 * 创建文件，如果已存在文件则直接返回，如果不存在则创建文件，
 * 如果已存在并且是文件夹则删除该文件夹并创建文件
 * @return 文件是否存在
 */
fun File?.fCreateFile(): Boolean {
    try {
        if (this == null) return false
        if (this.isFile) return true
        if (this.isDirectory) this.deleteRecursively()
        return this.parentFile.fMakeDirs() && this.createNewFile()
    } catch (e: Exception) {
        return e.libThrowOrReturn { false }
    }
}

/**
 * 创建文件夹，如果已存在文件夹则直接返回，如果不存在则创建文件夹，
 * 如果已存在并且是文件则删除该文件并创建文件夹
 * @return 文件夹是否存在
 */
fun File?.fMakeDirs(): Boolean {
    if (this == null) return false
    if (this.isDirectory) return true
    if (this.isFile) this.delete()
    return this.mkdirs()
}

/**
 * 删除文件或者目录
 */
fun File?.fDelete(): Boolean {
    if (this == null) return false
    if (this.isFile) return this.delete()
    if (this.isDirectory) return this.deleteRecursively()
    return false
}