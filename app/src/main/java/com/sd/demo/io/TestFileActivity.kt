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
import com.sd.lib.io.FFileUtils
import com.sd.lib.io.FZipUtils
import com.sd.lib.io.fCacheDir
import java.io.File

class TestFileActivity : ComponentActivity() {

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
        val cacheDir = fCacheDir("my_cache")
        val filesDir = FFileUtils.getFilesDir("my_files")

        val cacheZip = File(filesDir, "cacheZip.zip")
        FZipUtils.zip(cacheDir, cacheZip)
        FZipUtils.unzip(cacheZip, filesDir)
    }

    private fun deleteFile() {
        val cacheDir = fCacheDir("my_cache")
        val filesDir = FFileUtils.getFilesDir("my_files")

        FFileUtils.delete(cacheDir)
        FFileUtils.delete(filesDir)
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
