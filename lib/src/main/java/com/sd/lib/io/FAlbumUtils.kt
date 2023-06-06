package com.sd.lib.io

import android.content.ContentResolver
import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import com.sd.lib.ctx.fContext
import java.io.File
import java.util.UUID

private const val DefaultImageExt = "jpg"
private const val DefaultImageMimeType = "image/jpeg"

/**
 * 保存图片到相册
 */
fun fAlbumSaveImage(file: File?): Uri? {
    if (file == null || !file.exists()) return null

    val contentValues = ContentValues().apply {
        val uuid = UUID.randomUUID().toString()
        val ext = file.absolutePath.fExt(DefaultImageExt)
        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext) ?: DefaultImageMimeType

        this.put(MediaStore.Images.ImageColumns.TITLE, uuid)
        this.put(MediaStore.Images.ImageColumns.DISPLAY_NAME, uuid + ext.fExtAddDot())
        this.put(MediaStore.Images.ImageColumns.DESCRIPTION, uuid)
        this.put(MediaStore.Images.ImageColumns.MIME_TYPE, mimeType)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }
    }

    val resolver = fContext.contentResolver
    val uri = resolver.fInsert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues) ?: return null

    return if (file.saveToUri(uri, resolver)) {
        uri
    } else {
        resolver.fDelete(uri)
        null
    }
}

private fun ContentResolver.fInsert(uri: Uri, contentValues: ContentValues): Uri? {
    return try {
        this.insert(uri, contentValues)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

private fun ContentResolver.fDelete(uri: Uri): Int {
    return try {
        this.delete(uri, null, null)
    } catch (e: Exception) {
        e.printStackTrace()
        0
    }
}

private fun File.saveToUri(uri: Uri, resolver: ContentResolver): Boolean {
    try {
        resolver.openOutputStream(uri)?.use { output ->
            this.inputStream().use { input ->
                return input.copyTo(output) > 0
            }
        }
    } catch (e: Exception) {
        return e.libThrowOrReturn { false }
    }
    return false
}