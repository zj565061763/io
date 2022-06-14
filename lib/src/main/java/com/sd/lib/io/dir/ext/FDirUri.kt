package com.sd.lib.io.dir.ext

import android.content.Context
import android.net.Uri
import com.sd.lib.io.FExtUtils
import com.sd.lib.io.LibUtils
import com.sd.lib.io.dir.FFilesDir
import com.sd.lib.io.uri.FUriUtils
import java.io.File

/**
 * [Uri]文件保存目录
 */
object FDirUri {
    private val dir = FFilesDir("f_dir_uri")

    /**
     * 删除目录
     */
    @JvmStatic
    fun delete(context: Context) {
        dir.delete(context)
    }

    /**
     * 保存[uri]到目录下
     */
    @JvmStatic
    fun saveUri(uri: Uri?, context: Context): File? {
        if (uri == null) return null
        return dir.lock {
            val dir = dir.get(context)
            val file = newUriFile(uri, dir, context)
            saveUriToFile(uri, file, context)
        }
    }

    private fun newUriFile(uri: Uri, dir: File, context: Context): File {
        val md5 = LibUtils.md5(uri.toString())
        val ext = FExtUtils.getExt(FUriUtils.getName(uri, context))
        val fullExt = FExtUtils.fullExt(ext)
        return File(dir, md5 + fullExt)
    }

    private fun saveUriToFile(uri: Uri, file: File, context: Context): File? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                    file
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}