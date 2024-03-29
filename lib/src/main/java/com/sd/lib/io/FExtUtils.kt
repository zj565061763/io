package com.sd.lib.io

private const val Dot = "."

/**
 * 把当前[String]重命名为[name]，
 * 如果[name]包含扩展名则返回[name]，
 * 如果[name]不包含扩展名则返回[name].原扩展名
 */
fun String.fExtRename(name: String?): String {
    if (name.isNullOrEmpty()) return this
    if (name.fExt().isNotEmpty()) return name
    return name + this.fExt().fExtAddDot()
}

/**
 * 获取当前[String]的扩展名，不包括"."(例如mp3)，
 * 如果未获取到到扩展名，则返回[defaultExt]，如果[defaultExt]包括"."则会移除“.”后返回
 */
@JvmOverloads
fun String?.fExt(defaultExt: String? = null): String {
    val ext = this?.substringAfterLast(Dot, "")
        ?.takeUnless { it.isEmpty() }
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