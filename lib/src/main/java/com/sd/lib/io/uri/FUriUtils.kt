package com.sd.lib.io.uri

import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import androidx.documentfile.provider.DocumentFile
import com.sd.lib.ctx.fContext
import com.sd.lib.io.fCreateNewFile
import java.io.File
import java.io.IOException

/**
 * 把当前[File]转为[Uri]
 */
fun File?.fToUri(): Uri? {
    if (this == null) return null
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        FileProvider.getUriForFile(fContext, fFileProviderAuthority(), this)
    } else {
        Uri.fromFile(this)
    }
}

/**
 * 获取当前[Uri]的文件名
 */
fun Uri?.fFileName(): String {
    if (this == null) return ""
    return DocumentFile.fromSingleUri(fContext, this)?.name ?: ""
}

/**
 * 把当前[Uri]保存为[file]
 */
fun Uri?.fSaveToFile(file: File?): Boolean {
    if (this == null) return false
    if (file == null) return false
    if (!file.fCreateNewFile()) return false
    return try {
        fContext.contentResolver.openInputStream(this)?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        } != null
    } catch (e: IOException) {
        e.printStackTrace()
        false
    }
}