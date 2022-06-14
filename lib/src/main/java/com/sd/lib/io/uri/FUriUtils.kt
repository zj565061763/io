package com.sd.lib.io.uri

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import androidx.documentfile.provider.DocumentFile
import java.io.File

object FUriUtils {
    /**
     * [File]转[Uri]
     */
    @JvmStatic
    fun fileToUri(file: File?, context: Context): Uri? {
        if (file == null) return null
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val authority = FFileProvider.getAuthority(context)
            FileProvider.getUriForFile(context, authority, file)
        } else {
            Uri.fromFile(file)
        }
    }

    /**
     * [uri]文件名
     */
    @JvmStatic
    fun getName(uri: Uri?, context: Context): String {
        if (uri == null) return ""
        val documentFile = DocumentFile.fromSingleUri(context, uri) ?: return ""
        return documentFile.name ?: ""
    }
}