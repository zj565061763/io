package com.sd.lib.io

import java.io.Closeable
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

object FIOUtils {
    /**
     * 从[inputStream]中读取byte数组
     */
    @JvmStatic
    @Throws(IOException::class)
    fun readBytes(inputStream: InputStream): ByteArray {
        return inputStream.use {
            it.readBytes()
        }
    }

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
    fun closeQuietly(closeable: Closeable?) {
        try {
            closeable?.close()
        } catch (ignored: Throwable) {
        }
    }

    fun interface CopyCallback {
        fun onBytesCopied(count: Long): Boolean
    }
}