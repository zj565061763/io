package com.sd.lib.io.dir

import android.content.Context

/**
 * 在[Context.getExternalCacheDir]下创建指定名称的目录，如果失败则尝试在[Context.getCacheDir]下创建
 */
class FCacheDir(dirName: String) : FDir(dirName = dirName, isCacheDir = true)