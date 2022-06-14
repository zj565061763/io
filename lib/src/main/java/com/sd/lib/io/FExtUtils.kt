package com.sd.lib.io

import android.webkit.MimeTypeMap

object FExtUtils {
    /**
     * 获取扩展名
     */
    @JvmStatic
    @JvmOverloads
    fun getExt(url: String?, defaultExt: String? = null): String {
        val defaultExtFormat = if (defaultExt == null || defaultExt.isEmpty()) {
            ""
        } else {
            if (defaultExt.startsWith(".")) defaultExt.substring(1) else defaultExt
        }

        if (url == null || url.isEmpty()) {
            return defaultExtFormat
        }

        var ext = MimeTypeMap.getFileExtensionFromUrl(url)
        if (ext == null || ext.isEmpty()) {
            val lastIndex = url.lastIndexOf(".")
            if (lastIndex >= 0) ext = url.substring(lastIndex + 1)
        }

        return if (ext == null || ext.isEmpty()) {
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