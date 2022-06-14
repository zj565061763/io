package com.sd.lib.io.dir

import android.content.Context

/**
 * 在[Context.getExternalFilesDir]下创建指定名称的目录，如果失败则尝试在[Context.getFilesDir]下创建
 */
class FFilesDir(dirName: String) : FDir(dirName = dirName, isCacheDir = false)