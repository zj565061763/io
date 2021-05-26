package com.sd.lib.io

import java.io.*

object FIOUtils {
    /**
     * [inputStream]拷贝到[outputStream]
     */
    @JvmStatic
    @JvmOverloads
    @Throws(IOException::class)
    fun copy(inputStream: InputStream, outputStream: OutputStream, callback: CopyCallback? = null): Long {
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        var bytesCopied: Long = 0
        var bytes = inputStream.read(buffer)
        while (bytes >= 0) {
            outputStream.write(buffer, 0, bytes)

            bytesCopied += bytes
            if (callback != null && callback.onBytesCopied(bytesCopied)) break

            bytes = inputStream.read(buffer)
        }
        return bytesCopied
    }

    @JvmStatic
    fun close(closeable: Closeable?) {
        try {
            closeable?.close()
        } catch (ignored: Throwable) {
        }
    }

    //---------- ext ----------

    /**
     * 写入字符串
     */
    fun writeText(content: String?, file: File?): Boolean {
        if (file == null) return false
        return try {
            file.writeText(content ?: "")
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 读取字符串
     */
    fun readText(file: File?): String? {
        if (file == null) return null
        return try {
            file.readText()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun interface CopyCallback {
        fun onBytesCopied(count: Long): Boolean
    }
}