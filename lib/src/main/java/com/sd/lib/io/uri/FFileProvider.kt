package com.sd.lib.io.uri

import android.content.Context
import androidx.core.content.FileProvider

class FFileProvider : FileProvider() {
    companion object {
        @JvmStatic
        fun getAuthority(context: Context): String {
            return "${context.packageName}.${FFileProvider::class.java.simpleName.toLowerCase()}"
        }
    }
}