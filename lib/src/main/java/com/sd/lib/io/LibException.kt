package com.sd.lib.io

import java.io.IOException

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
    return if (this.javaClass in whiteList) {
        this.printStackTrace()
        block()
    } else {
        throw this
    }
}