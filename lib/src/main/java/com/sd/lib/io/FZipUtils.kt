package com.sd.lib.io

import java.io.*
import java.util.zip.ZipInputStream

object FZipUtils {
    /**
     * 解压压缩包[zip]到目录[dir]
     */
    @JvmStatic
    fun unzip(zip: File?, dir: File?): Boolean {
        if (zip == null || !zip.exists()) return false
        return try {
            unzip(FileInputStream(zip), dir)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 解压到目录[dir]
     */
    @JvmStatic
    fun unzip(inputStream: InputStream, dir: File?): Boolean {
        if (dir == null) return false
        if (dir.exists() && dir.isFile) throw IllegalArgumentException("file should be a directory")

        val zipInputStream = if (inputStream is ZipInputStream) inputStream else ZipInputStream(inputStream)
        var fileOutputStream: FileOutputStream? = null
        try {
            var zipEntry = zipInputStream.nextEntry
            while (zipEntry != null) {
                val file = File(dir, zipEntry.name)
                if (zipEntry.isDirectory) {
                    // 文件夹
                    if (file.exists() && file.isFile) {
                        if (!file.delete()) return false
                    }
                    if (!file.exists() && !file.mkdirs()) {
                        return false
                    }
                } else {
                    // 文件
                    val parentFile = file.parentFile
                    if (parentFile != null && !parentFile.exists()) {
                        if (!parentFile.mkdirs()) return false
                    }
                    fileOutputStream = FileOutputStream(file)
                    FIOUtils.copy(zipInputStream, fileOutputStream)
                    fileOutputStream.close()
                }

                zipInputStream.closeEntry()
                zipEntry = zipInputStream.nextEntry
            }
            return true
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            FIOUtils.closeQuietly(zipInputStream)
            FIOUtils.closeQuietly(fileOutputStream)
        }
        return false
    }
}