package com.sd.lib.io

import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

object FZipUtils {
    /**
     * 把[zip]压缩包解压到[dir]目录下
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
     * 解压到[dir]目录下
     */
    @JvmStatic
    fun unzip(inputStream: InputStream, dir: File?): Boolean {
        if (dir == null) return false
        if (dir.exists() && dir.isFile) throw IllegalArgumentException("dir should be a directory")

        val zipInputStream = if (inputStream is ZipInputStream) inputStream else ZipInputStream(inputStream)
        try {
            var zipEntry = zipInputStream.nextEntry
            while (zipEntry != null) {
                val target = File(dir, zipEntry.name)
                if (zipEntry.isDirectory) {
                    // 文件夹
                    if (target.exists() && target.isFile) {
                        if (!target.delete()) return false
                    }
                    if (!target.exists() && !target.mkdirs()) {
                        return false
                    }
                } else {
                    // 文件
                    val parentFile = target.parentFile
                    if (parentFile != null && !parentFile.exists()) {
                        if (!parentFile.mkdirs()) return false
                    }
                    target.outputStream().use { outputStream ->
                        zipInputStream.copyTo(outputStream)
                    }
                }

                zipInputStream.closeEntry()
                zipEntry = zipInputStream.nextEntry
            }
            return true
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            zipInputStream.fClose()
        }
        return false
    }
}

/**
 * 压缩为[zip]，
 * 如果当前是文件则把文件压缩为[zip]，如果当前是文件夹则把文件夹下的所有文件压缩为[zip]
 */
fun File?.fZipTo(zip: File?): Boolean {
    if (this == null || !this.exists()) return false
    return arrayOf<File?>(this).fZipTo(zip)
}

/**
 * 压缩为[zip]
 */
fun Array<File?>?.fZipTo(zip: File?): Boolean {
    if (this.isNullOrEmpty()) return false
    if (zip == null) return false
    if (!zip.fCheckFile()) return false
    return try {
        ZipOutputStream(zip.outputStream()).use { output ->
            for (item in this) {
                if (item == null || !item.exists()) return false
                compressFile(item, item.name, output)
            }
        }
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

private fun compressFile(
    file: File,
    filename: String,
    outputStream: ZipOutputStream,
) {
    if (file.isDirectory) {
        outputStream.putNextEntry(ZipEntry(filename + File.separator))
        file.listFiles()?.forEach { item ->
            compressFile(
                file = item,
                filename = filename + File.separator + item.name,
                outputStream = outputStream,
            )
        }
    } else {
        outputStream.putNextEntry(ZipEntry(filename))
        file.inputStream().use { inputStream ->
            inputStream.copyTo(outputStream)
        }
    }
}