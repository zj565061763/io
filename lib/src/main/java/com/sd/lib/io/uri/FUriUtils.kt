package com.sd.lib.io.uri

import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import androidx.documentfile.provider.DocumentFile
import com.sd.lib.ctx.fContext
import com.sd.lib.io.fDirUri
import com.sd.lib.io.fFullExt
import com.sd.lib.io.fGetExt
import java.io.File
import java.security.MessageDigest

/**
 * 文件转Uri
 */
fun File?.fToUri(): Uri? {
    if (this == null) return null
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val context = fContext
            val authority = FFileProvider.getAuthority()
            FileProvider.getUriForFile(context, authority, this)
        } else {
            Uri.fromFile(this)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}


/**
 * Uri转文件
 */
fun Uri?.fToFile(): File? {
    if (this == null) return null
    return fDirUri().modify { dir ->
        if (dir != null) {
            try {
                val name = md5(this.toString())
                val ext = this.fFileName().fGetExt().fFullExt()
                val file = File(dir, name + ext)
                this.saveToFile(file)
                if (file.exists()) file else null
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
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

private fun Uri.saveToFile(file: File) {
    val context = fContext
    context.contentResolver.openInputStream(this)?.use { input ->
        file.outputStream().buffered().use { output ->
            input.copyTo(output)
        }
    }
}

private fun md5(value: String): String {
    val bytes = MessageDigest.getInstance("MD5").digest(value.toByteArray())
    return bytes.joinToString("") { "%02X".format(it) }
}