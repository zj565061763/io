package com.sd.lib.io.uri

import android.content.Context
import androidx.core.content.FileProvider
import com.sd.lib.context.FContext

class FFileProvider : FileProvider() {
    companion object {
        internal val savedContext: Context
            get() {
                return checkNotNull(FContext.get()) { "Context is null" }
            }

        @JvmStatic
        fun getAuthority(): String {
            return "${savedContext.packageName}.f-f-provider-lib-io"
        }
    }
}