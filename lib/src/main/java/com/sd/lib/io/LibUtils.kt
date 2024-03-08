package com.sd.lib.io

import java.security.MessageDigest

internal fun libMD5(value: String): String {
    return MessageDigest.getInstance("MD5")
        .digest(value.toByteArray())
        .joinToString("") { "%02X".format(it) }
}