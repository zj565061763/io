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
import com.sd.lib.io.FZipUtils
import com.sd.lib.io.fCacheDir
import com.sd.lib.io.fCheckDir
import com.sd.lib.io.fDelete
import com.sd.lib.io.fFilesDir
import com.sd.lib.io.fZipTo

class ZipActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Content(
                    onClickZip = {
                        testZip()
                    },
                    onClickDelete = {
                        deleteFile()
                    },
                )
            }
        }
    }

    private fun testZip() {
        val cacheDir = fCacheDir("my_cache").apply {
            this.resolve("testZip_hello.txt").apply {
                this.writeText("hello")
            }
            this.resolve("testZip_world.txt").apply {
                this.writeText("world")
            }
            this.resolve("testZip").apply {
                this.fCheckDir()
                this.resolve("hello world.txt").apply {
                    this.writeText("hello world")
                }
            }
        }

        val filesDir = fFilesDir("my_files")
        val zip = filesDir.resolve("my_cache.zip")

        cacheDir.fZipTo(zip)
        FZipUtils.unzip(zip, filesDir)
    }

    private fun deleteFile() {
        fCacheDir("my_cache").fDelete()
        fFilesDir("my_files").fDelete()
    }

    override fun onDestroy() {
        super.onDestroy()
        deleteFile()
    }
}

@Composable
private fun Content(
    onClickZip: () -> Unit,
    onClickDelete: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Button(
            onClick = onClickZip
        ) {
            Text(text = "zip")
        }

        Button(
            onClick = onClickDelete
        ) {
            Text(text = "delete")
        }
    }
}
