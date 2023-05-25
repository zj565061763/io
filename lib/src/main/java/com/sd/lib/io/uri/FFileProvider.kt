package com.sd.lib.io.uri

import androidx.core.content.FileProvider
import com.sd.lib.ctx.fContext

class FFileProvider : FileProvider() {
    companion object {
        @JvmStatic
        fun getAuthority(): String {
            return "${fContext.packageName}.f-fp-lib-io"
        }
    }
}