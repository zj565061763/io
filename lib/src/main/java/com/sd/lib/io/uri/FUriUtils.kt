package com.sd.lib.io.uri

import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import androidx.documentfile.provider.DocumentFile
import com.sd.lib.ctx.fContext
import com.sd.lib.io.fCreateNewFile
import com.sd.lib.io.fDirUri
import com.sd.lib.io.fExt
import com.sd.lib.io.fExtAddDot
import com.sd.lib.io.libMD5
import com.sd.lib.io.libThrowOrReturn
import java.io.File

/**
 * 文件转Uri
 */
fun File?.fToUri(): Uri? {
    if (this == null) return null
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        val context = fContext
        val authority = fFileProviderAuthority()
        FileProvider.getUriForFile(context, authority, this)
    } else {
        Uri.fromFile(this)
    }
}

/**
 * 读取Uri的内容并保存为文件到[fDirUri]目录下
 */
fun Uri?.fToFile(): File? {
    if (this == null) return null
    return fDirUri().modify { dir ->
        if (dir != null) {
            val name = libMD5(this.toString())
            val ext = this.fFileName().fExt().fExtAddDot()
            val file = dir.resolve(name + ext)
            if (this.saveToFile(file)) file else null
        } else {
            null
        }
    }
}

/**
 * Uri对应的文件名
 */
fun Uri?.fFileName(): String {
    if (this == null) return ""
    val context = fContext
    val documentFile = DocumentFile.fromSingleUri(context, this)
    return documentFile?.name ?: ""
}

private fun Uri?.saveToFile(file: File): Boolean {
    try {
        if (this == null) return false
        if (!file.fCreateNewFile()) return false
        val context = fContext
        context.contentResolver.openInputStream(this)?.use { input ->
            file.outputStream().buffered().use { output ->
                input.copyTo(output)
            }
        }
        return true
    } catch (e: Exception) {
        return e.libThrowOrReturn { false }
    }
}