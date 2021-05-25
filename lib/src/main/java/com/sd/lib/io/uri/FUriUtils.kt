package com.sd.lib.io.uri

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import androidx.documentfile.provider.DocumentFile
import com.sd.lib.io.FFileUtils
import java.io.File
import java.security.MessageDigest

object FUriUtils {
    /**
     * [File]转[Uri]
     */
    @JvmStatic
    fun fileToUri(file: File?, context: Context): Uri? {
        if (file == null) return null
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(context, FFileProvider.getAuthority(context), file)
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

    /**
     * 把[uri]对应的文件，拷贝到指定的目录[dir]
     */
    @JvmStatic
    fun saveToDir(uri: Uri?, dir: File?, context: Context): File? {
        if (uri == null) return null
        if (!FFileUtils.checkDir(dir)) return null

        val name = getName(uri, context)
        val suffix = if (name.isEmpty()) "" else "_${name}"
        val filename = md5(uri.toString()) + suffix

        val file = File(dir, filename)
        val resolver = context.contentResolver

        try {
            resolver.openInputStream(uri).use { input ->
                file.outputStream().use { output ->
                    input!!.copyTo(output)
                    return file
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    @JvmStatic
    private fun md5(value: String): String {
        val bytes = MessageDigest.getInstance("MD5").apply {
            this.update(value.toByteArray())
        }.digest()

        val builder = StringBuilder()
        for (i in bytes.indices) {
            val hex = Integer.toHexString(0xFF and bytes[i].toInt())
            if (hex.length == 1) {
                builder.append('0')
            }
            builder.append(hex)
        }
        return builder.toString()
    }
}