package com.sd.lib.io

import java.io.Closeable
import java.io.InputStream
import java.io.OutputStream

/**
 * 把当前[InputStream]的内容拷贝到[out]并返回拷贝的数量(byte)
 * @param bufferSize 缓冲区大小
 * @param callback 每次拷贝之后回调，返回true停止拷贝
 */
@JvmOverloads
fun InputStream.fCopyTo(
    out: OutputStream,
    bufferSize: Int = DEFAULT_BUFFER_SIZE,
    callback: ((count: Long) -> Boolean)? = null,
): Long {
    var bytesCopied: Long = 0
    val buffer = ByteArray(bufferSize)
    var bytes = read(buffer)
    while (bytes >= 0) {
        out.write(buffer, 0, bytes)

        bytesCopied += bytes
        if (callback?.invoke(bytesCopied) == true) break

        bytes = read(buffer)
    }
    return bytesCopied
}

fun Closeable?.fClose() {
    try {
        this?.close()
    } catch (ignored: Throwable) {
    }
}