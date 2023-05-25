package com.sd.lib.io

import java.io.Closeable
import java.io.InputStream
import java.io.OutputStream

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