package com.sd.lib.io.dir.ext

import android.net.Uri
import com.sd.lib.io.FExtUtils
import com.sd.lib.io.dir.FFilesDir
import com.sd.lib.io.uri.FFileProvider
import com.sd.lib.io.uri.FUriUtils
import java.io.File
import java.security.MessageDigest

/**
 * [Uri]文件保存目录
 */
object FDirUri {
    private val dir = FFilesDir("f_dir_uri")

    /**
     * 删除目录
     */
    @JvmStatic
    fun delete() {
        dir.delete()
    }

    /**
     * 保存[uri]到目录下
     */
    @JvmStatic
    fun saveUri(uri: Uri?): File? {
        if (uri == null) return null
        return dir.lock {
            try {
                val md5Name = md5(uri.toString())
                val ext = FExtUtils.getExt(FUriUtils.getName(uri))
                val fullExt = FExtUtils.fullExt(ext)
                val file = File(it, md5Name + fullExt)
                saveUriToFile(uri, file)
                if (file.exists()) file else null
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    private fun saveUriToFile(uri: Uri, file: File) {
        val context = FFileProvider.savedContext
        context.contentResolver.openInputStream(uri)?.use { input ->
            file.outputStream().buffered().use { output ->
                input.copyTo(output)
            }
        }
    }

    private fun md5(value: String): String {
        val bytes = MessageDigest.getInstance("MD5").digest(value.toByteArray())
        return bytes.joinToString("") { "%02X".format(it) }
    }
}