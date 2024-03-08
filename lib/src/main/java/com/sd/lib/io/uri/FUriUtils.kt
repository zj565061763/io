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
 * 读取当前[Uri]的内容保存到[fDirUri]目录下并返回保存的文件
 */
fun Uri?.fToFile(): File? {
    if (this == null) return null
    return fDirUri().modify { dir ->
        if (dir != null) {
            val name = libMD5(this.toString())
            val dotExt = this.fFileName().fExt().fExtAddDot()
            val file = dir.resolve(name + dotExt)
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