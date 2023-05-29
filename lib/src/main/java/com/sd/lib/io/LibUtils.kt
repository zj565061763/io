package com.sd.lib.io

import java.io.IOException
import java.security.MessageDigest

internal fun md5(value: String): String {
    val bytes = MessageDigest.getInstance("MD5").digest(value.toByteArray())
    return bytes.joinToString("") { "%02X".format(it) }
}

internal fun libWhiteExceptionList(): List<Class<out Exception>> {
    return listOf(
        IOException::class.java,
        SecurityException::class.java,
    )
}

internal fun <T> Exception.libThrowOrReturn(
    whiteList: List<Class<out Exception>> = libWhiteExceptionList(),
    block: () -> T,
): T {
    if (this.javaClass in whiteList) {
        this.printStackTrace()
        return block()
    } else {
        throw this
    }
}