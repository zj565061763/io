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
fun File?.fNewFile(ext: String): File? {
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
        if (!target.fCreateFile(overwrite = true)) return false
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
        if (this == target) return true
        if (!target.fCreateFile(overwrite = overwrite)) return false
        return this.renameTo(target)
    } catch (e: Exception) {
        return e.libThrowOrReturn { false }
    }
}

/**
 * 检查文件是否存在，如果不存在则尝试创建，如果已存在则根据[overwrite]来决定是否覆盖，默认覆盖
 * @return 当前文件是否存在
 */
@JvmOverloads
fun File?.fCreateFile(overwrite: Boolean = true): Boolean {
    try {
        if (this == null) return false
        if (!this.exists()) return this.parentFile.fCreateDir() && this.createNewFile()
        if (overwrite) {
            this.fDelete()
        } else {
            if (this.isFile) return true
            this.deleteRecursively()
        }
        return this.parentFile.fCreateDir() && this.createNewFile()
    } catch (e: Exception) {
        return e.libThrowOrReturn { false }
    }
}

/**
 * 检查文件夹是否存在，如果不存在则尝试创建，如果已存在并且是文件则删除该文件并尝试创建文件夹
 * @return 当前文件夹是否存在
 */
fun File?.fCreateDir(): Boolean {
    try {
        if (this == null) return false
        if (!this.exists()) return this.mkdirs()
        if (this.isDirectory) return true
        if (this.isFile) this.delete()
        return this.mkdirs()
    } catch (e: Exception) {
        return e.libThrowOrReturn { false }
    }
}

/**
 * 删除文件或者目录
 */
fun File?.fDelete(): Boolean {
    try {
        if (this == null) return false
        if (!this.exists()) return false
        return if (this.isFile) {
            this.delete()
        } else {
            this.deleteRecursively()
        }
    } catch (e: Exception) {
        return e.libThrowOrReturn { false }
    }
}