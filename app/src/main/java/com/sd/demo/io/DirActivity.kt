package com.sd.demo.io

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sd.demo.io.ui.theme.AppTheme
import com.sd.lib.io.fCacheDir
import com.sd.lib.io.fDelete
import com.sd.lib.io.fFilesDir
import com.sd.lib.io.fFormatByteSize
import com.sd.lib.io.fNewFile
import com.sd.lib.io.fSize

class DirActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Content(
                    onClickCache = {
                        clickCache()
                    },
                    onClickFiles = {
                        clickFiles()
                    },
                    onClickDelete = {
                        deleteFile()
                    },
                )
            }
        }
    }

    private val _cacheDir get() = fCacheDir("my_cache")
    private val _filesDir get() = fFilesDir("my_files")

    private fun clickCache() {
        _cacheDir.fNewFile("txt")?.let { file ->
            file.writeText("hello cache !!!")
            logMsg { "cache:${file.readText()} size:${file.fSize().fFormatByteSize()}" }
        }
    }

    private fun clickFiles() {
        _filesDir.fNewFile("txt")?.let { file ->
            file.writeText("hello files !!!!!!")
            logMsg { "file:${file.readText()} size:${file.fSize().fFormatByteSize()}" }
        }
    }

    private fun deleteFile() {
        _cacheDir.fDelete()
        _filesDir.fDelete()
    }

    override fun onDestroy() {
        super.onDestroy()
        deleteFile()
    }
}

@Composable
private fun Content(
    onClickCache: () -> Unit,
    onClickFiles: () -> Unit,
    onClickDelete: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Button(
            onClick = onClickCache
        ) {
            Text(text = "cache")
        }

        Button(
            onClick = onClickFiles
        ) {
            Text(text = "files")
        }

        Button(
            onClick = onClickDelete
        ) {
            Text(text = "delete")
        }
    }
}