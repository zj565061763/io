package com.sd.lib.io

import java.security.MessageDigest

internal object LibUtils {
    @JvmStatic
    fun md5(value: String): String {
        val bytes = MessageDigest.getInstance("MD5").apply {
            this.update(value.toByteArray())
        }.digest()

        val builder = StringBuilder()
        for (i in bytes.indices) {
            val hex = Integer.toHexString(0xFF and bytes[i].toInt())
            if (hex.length == 1) {
                builder.append('0')
            }
            builder.append(hex)
        }
        return builder.toString()
    }
}