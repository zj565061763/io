package com.sd.lib.io.dir

import android.content.Context
import android.net.Uri
import com.sd.lib.io.LibUtils
import com.sd.lib.io.uri.FUriUtils
import java.io.File

/**
 * [Uri]文件保存目录
 */
object FDirUri {
    private val DIR by lazy { FDir("f_dir_uri") }

    /**
     * 删除目录
     */
    @JvmStatic
    fun delete(context: Context) {
        DIR.delete(context)
    }

    /**
     * 保存[uri]到目录下
     */
    @JvmStatic
    fun saveUri(uri: Uri?, context: Context): File? {
        if (uri == null) return null
        return DIR.lock {
            val dir = DIR.get(context) ?: return@lock null
            val file = getUriFile(uri, dir, context)
            saveUriToFile(uri, file, context)
        }
    }

    @JvmStatic
    private fun getUriFile(uri: Uri, dir: File, context: Context): File {
        val md5 = LibUtils.md5(uri.toString())
        val name = FUriUtils.getName(uri, context)
        return File(dir, "${md5}_${name}")
    }

    @JvmStatic
    private fun saveUriToFile(uri: Uri, file: File, context: Context): File? {
        return try {
            context.contentResolver.openInputStream(uri)!!.use { input ->
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