package com.sd.lib.io

import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

/**
 * 把当前[File]解压到[target]目录下
 * @return true-成功；false-失败
 */
fun File?.fUnzipTo(target: File?): Boolean {
    try {
        if (this == null || !this.exists()) return false
        return FileInputStream(this).fUnzipTo(target)
    } catch (e: IOException) {
        e.printStackTrace()
        return false
    }
}

/**
 * 把当前[InputStream]解压到[target]目录下
 * @return true-成功；false-失败
 */
fun InputStream?.fUnzipTo(target: File?): Boolean {
    try {
        if (this == null || target == null) return false
        if (!target.fMakeDirs()) return false
        (if (this is ZipInputStream) this else ZipInputStream(this)).use { inputStream ->
            var entry = inputStream.nextEntry
            while (entry != null) {
                val file = target.resolve(entry.name)
                if (entry.isDirectory) {
                    if (!file.fMakeDirs()) return false
                } else {
                    if (!file.fCreateNewFile()) return false
                    file.outputStream().use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                inputStream.closeEntry()
                entry = inputStream.nextEntry
            }
        }
        return true
    } catch (e: IOException) {
        e.printStackTrace()
        return false
    }
}

/**
 * 压缩当前文件并返回压缩后的文件
 */
@JvmOverloads
fun File?.fZip(filename: String? = null): File? {
    if (this == null || !this.exists()) return null
    val zipFilename = if (filename.isNullOrEmpty()) {
        this.name + ".zip"
    } else {
        if (filename.endsWith(".zip")) filename else "$filename.zip"
    }
    val target = this.resolveSibling(zipFilename)
    return if (fZipTo(target)) target else null
}

/**
 * 压缩当前文件为[target]，
 * 如果当前是文件则把文件压缩为[target]，如果当前是文件夹则把文件夹和文件夹下的所有文件压缩为[target]
 */
fun File?.fZipTo(target: File?): Boolean {
    if (this == null || !this.exists()) return false
    return arrayOf<File?>(this).fZipTo(target)
}

/**
 * 压缩当前文件列表为[target]
 */
fun Array<File?>?.fZipTo(target: File?): Boolean {
    try {
        if (this.isNullOrEmpty()) return false
        if (target == null) return false
        if (!target.fCreateNewFile()) return false
        ZipOutputStream(target.outputStream()).use { outputStream ->
            for (item in this) {
                if (item == null || !item.exists()) continue
                compressFile(
                    file = item,
                    filename = item.name,
                    outputStream = outputStream,
                )
            }
        }
        return true
    } catch (e: IOException) {
        e.printStackTrace()
        return false
    }
}

private fun compressFile(
    file: File,
    filename: String,
    outputStream: ZipOutputStream,
) {
    when {
        file.isFile -> {
            outputStream.putNextEntry(ZipEntry(filename))
            file.inputStream().use { inputStream ->
                inputStream.copyTo(outputStream)
            }
        }

        file.isDirectory -> {
            outputStream.putNextEntry(ZipEntry(filename + File.separator))
            file.listFiles()?.forEach { item ->
                compressFile(
                    file = item,
                    filename = filename + File.separator + item.name,
                    outputStream = outputStream,
                )
            }
        }
    }
}