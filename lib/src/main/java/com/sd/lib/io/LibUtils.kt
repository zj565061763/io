package com.sd.lib.io

import java.security.MessageDigest

internal object LibUtils {
    @JvmStatic
    fun md5(value: String): String {
        val bytes = MessageDigest.getInstance("MD5").digest(value.toByteArray())
        return bytes.joinToString("") { "%02X".format(it) }
    }
}