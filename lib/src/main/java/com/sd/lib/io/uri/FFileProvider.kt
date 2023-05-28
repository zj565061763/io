package com.sd.lib.io.uri

import androidx.core.content.FileProvider
import com.sd.lib.ctx.fContext

internal class FFileProvider : FileProvider()

fun fFileProviderAuthority(): String {
    return "${fContext.packageName}.f-fp-lib-io"
}