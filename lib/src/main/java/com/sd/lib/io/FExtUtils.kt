package com.sd.lib.io

import android.webkit.MimeTypeMap

object FExtUtils {
    /**
     * 获取扩展名
     */
    @JvmStatic
    fun getExt(url: String?): String {
        if (url == null) return ""
        var ext = MimeTypeMap.getFileExtensionFromUrl(url)
        if (ext == null || ext.isEmpty()) {
            val lastIndex = url.lastIndexOf(".")
            if (lastIndex > 0) {
                ext = url.substring(lastIndex + 1)
            }
        }
        return ext ?: ""
    }

    /**
     * 完整扩展名
     */
    @JvmStatic
    fun completeExt(ext: String?): String {
        return if (ext == null || ext.isEmpty()) {
            ""
        } else {
            if (ext.startsWith(".")) ext else ".$ext"
        }
    }
}