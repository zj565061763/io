package com.sd.lib.io.dir

import java.io.File

interface IDir {
    /**
     * 在当前目录下创建一个文件
     */
    fun <T> newFile(ext: String?, block: (File?) -> T): T

    /**
     * 同步操作
     */
    fun <T> modify(block: (File?) -> T): T

    /**
     * 删除当前目录以及下面的所有文件
     */
    fun delete()
}