package com.sd.lib.io

import java.text.DecimalFormat

const val FByteKB = 1024L
const val FByteMB = FByteKB * FByteKB
const val FByteGB = FByteKB * FByteMB
const val FByteTB = FByteKB * FByteGB

/**
 * 返回格式化的字符串
 */
@JvmOverloads
fun Long.fFormatByteSize(df: DecimalFormat = DecimalFormat("#.0")): String {
    return when {
        this <= 0 -> df.format(0.0) + "B"
        this < FByteKB -> df.format(this.toDouble()) + "B"
        this < FByteMB -> df.format(this.toDouble() / FByteKB) + "KB"
        this < FByteGB -> df.format(this.toDouble() / FByteMB) + "MB"
        this < FByteTB -> df.format(this.toDouble() / FByteGB) + "GB"
        else -> df.format(this.toDouble() / FByteTB) + "TB"
    }
}