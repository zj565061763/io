package com.sd.lib.io.uri

import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import androidx.documentfile.provider.DocumentFile
import com.sd.lib.ctx.fContext
import java.io.File

object FUriUtils {
    /**
     * [File]转[Uri]
     */
    @JvmStatic
    fun fileToUri(file: File?): Uri? {
        if (file == null) return null
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val context = fContext
            val authority = FFileProvider.getAuthority()
            FileProvider.getUriForFile(context, authority, file)
        } else {
            Uri.fromFile(file)
        }
    }

    /**
     * [uri]文件名
     */
    @JvmStatic
    fun getName(uri: Uri?): String {
        if (uri == null) return ""
        val context = fContext
        val documentFile = DocumentFile.fromSingleUri(context, uri) ?: return ""
        return documentFile.name ?: ""
    }
}