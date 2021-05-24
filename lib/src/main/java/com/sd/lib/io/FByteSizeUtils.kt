package com.sd.lib.io

import java.io.File
import java.text.DecimalFormat

object FByteSizeUtils {
    const val KB = 1024L
    const val MB = 1024 * KB
    const val GB = 1024 * MB

    /**
     * 返回[file]的大小
     */
    @JvmStatic
    fun getFileSize(file: File?): Long {
        if (file == null || !file.exists()) return 0
        if (!file.isDirectory) return file.length()

        val files = file.listFiles()
        if (files == null || files.isEmpty()) return 0

        var length: Long = 0
        for (item in files) {
            length += getFileSize(item)
        }
        return length
    }

    @JvmStatic
    fun formatSize(byteSize: Long): String {
        val df = DecimalFormat("#.00")
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