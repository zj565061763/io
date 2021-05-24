package com.sd.lib.io

import android.content.Context
import android.os.Environment
import java.io.File

object FFileUtils {
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
    fun newFileUnderDir(dir: File?, ext: String): File? {
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
                return if (checkFile(file)) file else null
            }
        }
    }

    /**
     * 删除文件或者目录
     */
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
    fun checkFile(file: File?): Boolean {
        if (file == null) return false
        if (file.exists()) return true
        return try {
            file.createNewFile()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}