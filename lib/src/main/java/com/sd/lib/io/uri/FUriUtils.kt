package com.sd.lib.io.uri

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import java.io.File

object FUriUtils {
    @JvmStatic
    fun fileToUri(file: File, context: Context): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(context, FFileProvider.getAuthority(context), file)
        } else {
            Uri.fromFile(file)
        }
    }
}