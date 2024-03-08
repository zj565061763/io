package com.sd.lib.io

import android.webkit.MimeTypeMap

private const val Dot = "."

/**
 * 把当前字符串重命名为[name]，
 * 如果[name]包含扩展名则返回[name]，
 * 如果[name]不包含扩展名则返回[name].原扩展名
 */
fun String.fExtRename(name: String?): String {
    val current = this
    if (name.isNullOrEmpty()) return current
    if (name.fExt().isNotEmpty()) return name
    return name + current.fExt().fExtAddDot()
}

/**
 * 获取扩展名，不包括"."（例如mp3），
 * 如果未获取到到扩展名，则返回[defaultExt]，如果[defaultExt]包括"."则会移除“.”后返回
 */
@JvmOverloads
fun String?.fExt(defaultExt: String? = null): String {
    if (this.isNullOrEmpty()) return defaultExt.fExtRemoveDot()
    val ext = MimeTypeMap.getFileExtensionFromUrl(this).takeUnless { it.isNullOrEmpty() }
        ?: this.substringAfterLast(delimiter = Dot, missingDelimiterValue = "").takeUnless { it.isEmpty() }
        ?: defaultExt
    return ext.fExtRemoveDot()
}

/**
 * mp3 -> .mp3
 */
fun String?.fExtAddDot(): String {
    if (this.isNullOrEmpty()) return ""
    return if (this.startsWith(Dot)) this else "${Dot}${this}"
}

/**
 * .mp3 -> mp3
 */
fun String?.fExtRemoveDot(): String {
    if (this.isNullOrEmpty()) return ""
    var ret: String = this
    while (ret.startsWith(Dot)) {
        ret = ret.removePrefix(Dot)
    }
    return ret
}