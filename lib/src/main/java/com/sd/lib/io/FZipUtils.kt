package com.sd.lib.io

import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

/**
 * 把[zip]压缩包解压到[target]目录下
 */
fun File?.fUnzipTo(target: File?): Boolean {
    try {
        if (this == null || !this.exists()) return false
        return FileInputStream(this).fUnzipTo(target)
    } catch (e: Exception) {
        return e.libThrowOrReturn { false }
    }
}

/**
 * 解压到[target]目录下
 */
fun InputStream?.fUnzipTo(target: File?): Boolean {
    try {
        if (this == null || target == null) return false
        if (!target.fCreateDir()) return false
        (if (this is ZipInputStream) this else ZipInputStream(this)).use { inputStream ->
            var entry = inputStream.nextEntry
            while (entry != null) {
                val file = target.resolve(entry.name)
                if (entry.isDirectory) {
                    if (!file.fCreateDir()) return false
                } else {
                    if (!file.fCreateFile()) return false
                    file.outputStream().use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                inputStream.closeEntry()
                entry = inputStream.nextEntry
            }
        }
        return true
    } catch (e: Exception) {
        return e.libThrowOrReturn { false }
    }
}

/**
 * 压缩文件当前文件
 */
@JvmOverloads
fun File?.fZip(filename: String? = null): File? {
    try {
        if (this == null || !this.exists()) return null
        val parentFile = this.parentFile ?: error("parent file is null, call fZipTo() method instead")
        val zipFilename = if (filename.isNullOrEmpty()) {
            this.name + ".zip"
        } else {
            if (filename.endsWith(".zip")) filename else "$filename.zip"
        }
        val zipFile = parentFile.resolve(zipFilename)
        return if (fZipTo(zipFile)) zipFile else null
    } catch (e: Exception) {
        return e.libThrowOrReturn { null }
    }
}

/**
 * 压缩为[zip]，
 * 如果当前是文件则把文件压缩为[zip]，如果当前是文件夹则把文件夹和文件夹下的所有文件压缩为[zip]
 */
fun File?.fZipTo(zip: File?): Boolean {
    try {
        if (this == null || !this.exists()) return false
        return arrayOf<File?>(this).fZipTo(zip)
    } catch (e: Exception) {
        return e.libThrowOrReturn { false }
    }
}

/**
 * 压缩为[zip]
 */
fun Array<File?>?.fZipTo(zip: File?): Boolean {
    try {
        if (this.isNullOrEmpty()) return false
        if (zip == null) return false
        if (!zip.fCreateFile()) return false
        ZipOutputStream(zip.outputStream()).use { output ->
            for (item in this) {
                if (item == null || !item.exists()) return false
                compressFile(item, item.name, output)
            }
        }
        return true
    } catch (e: Exception) {
        return e.libThrowOrReturn { false }
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