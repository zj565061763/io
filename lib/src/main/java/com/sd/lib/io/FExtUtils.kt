package com.sd.lib.io

import android.webkit.MimeTypeMap

/**
 * 获取扩展名，不包括"."，
 * 例如：png
 */
@JvmOverloads
fun String?.fGetExt(defaultExt: String? = null): String {
    if (this.isNullOrEmpty()) {
        return formatDefaultExt(defaultExt)
    }

    var ext = MimeTypeMap.getFileExtensionFromUrl(this)
    if (ext.isNullOrEmpty()) {
        val lastIndex = this.lastIndexOf(".")
        if (lastIndex >= 0) ext = this.substring(lastIndex + 1)
    }

    return if (ext.isNullOrEmpty()) {
        formatDefaultExt(defaultExt)
    } else {
        removePrefixDot(ext)
    }
}

/**
 * 包含"."的完整扩展名，
 * 例如：png -> .png
 */
fun String?.fFullExt(): String {
    return if (this.isNullOrEmpty()) {
        ""
    } else {
        if (this.startsWith(".")) this else ".$this"
    }
}

private fun formatDefaultExt(defaultExt: String?): String {
    return if (defaultExt.isNullOrEmpty()) {
        ""
    } else {
        removePrefixDot(defaultExt)
    }
}

private fun removePrefixDot(input: String): String {
    var ret = input
    while (ret.startsWith(".")) {
        ret = ret.removePrefix(".")
    }
    return ret
}