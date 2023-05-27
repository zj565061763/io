package com.sd.lib.io

import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

/**
 * 把[zip]压缩包解压到[dir]目录下
 */
fun File?.fUnzipTo(dir: File?): Boolean {
    if (this == null || !this.exists()) return false
    return FileInputStream(this).fUnzipTo(dir)
}

/**
 * 解压到[dir]目录下
 */
fun InputStream?.fUnzipTo(dir: File?): Boolean {
    if (this == null || dir == null) return false
    if (!dir.fCreateDir()) return false
    return try {
        (if (this is ZipInputStream) this else ZipInputStream(this)).use { inputStream ->
            var entry = inputStream.nextEntry
            while (entry != null) {
                val target = File(dir, entry.name)
                if (entry.isDirectory) {
                    if (!target.fCreateDir()) return false
                } else {
                    if (!target.fCreateFile()) return false
                    target.outputStream().use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                inputStream.closeEntry()
                entry = inputStream.nextEntry
            }
        }
        true
    } catch (e: IOException) {
        e.printStackTrace()
        false
    }
}

/**
 * 压缩文件当前文件
 */
fun File?.fZip(filename: String? = null): File? {
    if (this == null || !this.exists()) return null
    val parentFile = this.parentFile ?: error("parent file is null, call fZipTo() method instead")
    val zipFilename = if (filename.isNullOrEmpty()) {
        this.name + ".zip"
    } else {
        if (filename.endsWith(".zip")) filename else "$filename.zip"
    }
    val zipFile = parentFile.resolve(zipFilename)
    return if (fZipTo(zipFile)) zipFile else null
}

/**
 * 压缩为[zip]，
 * 如果当前是文件则把文件压缩为[zip]，如果当前是文件夹则把文件夹和文件夹下的所有文件压缩为[zip]
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
    if (!zip.fCreateFile()) return false
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