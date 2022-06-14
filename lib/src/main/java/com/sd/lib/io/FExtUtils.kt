package com.sd.lib.io

import android.webkit.MimeTypeMap

object FExtUtils {
    /**
     * 获取扩展名，不包括"."，
     * 例如：png
     */
    @JvmStatic
    @JvmOverloads
    fun getExt(url: String?, defaultExt: String? = null): String {
        val defaultExtFormat = if (defaultExt.isNullOrEmpty()) {
            ""
        } else {
            var ext: String = defaultExt
            while (ext.startsWith(".")) {
                ext = ext.removePrefix(".")
            }
            ext
        }

        if (url.isNullOrEmpty()) {
            return defaultExtFormat
        }

        var ext = MimeTypeMap.getFileExtensionFromUrl(url)
        if (ext.isNullOrEmpty()) {
            val lastIndex = url.lastIndexOf(".")
            if (lastIndex >= 0) ext = url.substring(lastIndex + 1)
        }

        return if (ext.isNullOrEmpty()) {
            defaultExtFormat
        } else {
            ext
        }
    }

    /**
     * 完整扩展名
     */
    @JvmStatic
    fun fullExt(ext: String?): String {
        return if (ext.isNullOrEmpty()) {
            ""
        } else {
            if (ext.startsWith(".")) ext else ".$ext"
        }
    }
}