package com.sd.lib.io.uri

import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import androidx.documentfile.provider.DocumentFile
import com.sd.lib.ctx.fContext
import com.sd.lib.io.fCreateNewFile
import com.sd.lib.io.fExt
import com.sd.lib.io.fFilesDir
import com.sd.lib.io.fNewFile
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
 * 读取当前[Uri]的内容并保存为文件返回
 */
fun Uri?.fToFile(): File? {
    if (this == null) return null
    val ext = this.fFileName().fExt()
    val file = fFilesDir("f_dir_uri").fNewFile(ext) ?: return null
    return if (this.saveToFile(file)) file else null
}

/**
 * 获取当前[Uri]的文件名
 */
fun Uri?.fFileName(): String {
    if (this == null) return ""
    return DocumentFile.fromSingleUri(fContext, this)?.name ?: ""
}

private fun Uri?.saveToFile(file: File): Boolean {
    try {
        if (this == null) return false
        if (!file.fCreateNewFile()) return false

        fContext.contentResolver.openInputStream(this)?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }.let { size ->
            if (size == null || size <= 0) {
                file.deleteRecursively()
                return false
            }
        }

        return true
    } catch (e: IOException) {
        e.printStackTrace()
        return false
    }
}