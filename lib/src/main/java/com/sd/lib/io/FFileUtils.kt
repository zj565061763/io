package com.sd.lib.io

import android.content.Context
import android.os.Environment
import java.io.File
import java.text.DecimalFormat

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
     * 获得cache下的[name]目录
     */
    @JvmStatic
    fun getCacheDir(name: String, context: Context): File? {
        val externalDir = File(context.externalCacheDir, name)
        if (checkDir(externalDir)) return externalDir

        val dir = File(context.cacheDir, name)
        return if (checkDir(dir)) dir else null
    }

    /**
     * 获得files下的[name]目录
     */
    @JvmStatic
    fun getFilesDir(name: String, context: Context): File? {
        val externalDir = context.getExternalFilesDir(name)
        if (checkDir(externalDir)) return externalDir

        val dir = File(context.filesDir, name)
        return if (checkDir(dir)) dir else null
    }

    /**
     * 在文件夹下[dir]下创建一个扩展名为[ext]的文件
     */
    @JvmStatic
    fun newFileUnderDir(dir: File?, ext: String?): File? {
        if (!checkDir(dir)) return null

        val finalExt = FExtUtils.completeExt(ext)
        var current = System.currentTimeMillis()
        while (true) {
            val filename = current.toString() + finalExt
            val file = File(dir, filename)
            if (file.exists()) {
                current++
                continue
            } else {
                return if (createFile(file)) file else null
            }
        }
    }

    /**
     * 拷贝
     */
    @JvmStatic
    fun copy(source: File?, target: File?): Boolean {
        if (source == null || target == null) return false
        if (source == target) return true
        return try {
            source.copyRecursively(target, overwrite = true)
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

        return try {
            source.copyTo(temp, overwrite = true)
            return moveFile(temp, target)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
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
        if (file.exists()) return true
        return try {
            file.mkdirs()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 检查文件是否存在，不存在则尝试创建
     */
    @JvmStatic
    fun createFile(file: File?): Boolean {
        if (file == null) return false
        if (file.exists()) return true
        return try {
            file.createNewFile()
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
    fun formatSize(byteSize: Long, df: DecimalFormat = DecimalFormat("#.00")): String {
        return if (byteSize <= 0) {
            "0.00B"
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