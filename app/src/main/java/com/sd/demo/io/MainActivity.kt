package com.sd.demo.io

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.sd.lib.io.fCacheDir
import com.sd.lib.io.fMakeDirs
import com.sd.lib.io.fMoveToFile

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val file = fCacheDir().resolve("1").resolve("2").resolve("hello.txt")

        logMsg {
            file.parentFile?.mkdirs().toString()
        }
    }
}

inline fun logMsg(block: () -> String) {
    Log.i("io-demo", block())
}