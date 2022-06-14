package com.sd.lib.io

import java.io.*
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
            FIOUtils.close(zipInputStream)
        }
        return false
    }

    /**
     * 把[source]压缩为[zip]
     */
    @JvmStatic
    fun zip(source: File?, zip: File?): Boolean {
        if (source == null || !source.exists()) return false
        return zip(arrayOf(source), zip)
    }

    /**
     * 压缩文件
     */
    @JvmStatic
    fun zip(files: Array<File?>?, zip: File?): Boolean {
        if (files == null || files.isEmpty()) return false
        if (zip == null) return false

        if (zip.exists()) {
            if (zip.isDirectory) throw IllegalArgumentException("zip should not be a directory")
            if (!zip.delete()) return false
        }

        val fileParent = zip.parentFile
        if (fileParent != null && !fileParent.exists()) {
            if (!fileParent.mkdirs()) return false
        }

        ZipOutputStream(zip.outputStream()).use { output ->
            for (item in files) {
                if (item == null || !item.exists()) return false
                try {
                    compressFile(item, item.name, output)
                } catch (e: IOException) {
                    e.printStackTrace()
                    return false
                }
            }
        }
        return true
    }

    @Throws(IOException::class)
    private fun compressFile(file: File, filename: String, outputStream: ZipOutputStream) {
        if (file.isDirectory) {
            outputStream.putNextEntry(ZipEntry(filename + File.separator))
            val files = file.listFiles()
            files?.forEach { item ->
                compressFile(item, filename + File.separator + item.name, outputStream)
            }
        } else {
            outputStream.putNextEntry(ZipEntry(filename))
            file.inputStream().use { inputStream ->
                inputStream.copyTo(outputStream)
            }
        }
    }
}