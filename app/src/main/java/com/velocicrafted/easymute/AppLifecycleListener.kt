package com.velocicrafted.easymute

import android.content.Context
import android.os.Build
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

class AppLifecycleListener(private val toggle: MuteToggle, private val window: Window, private val baseContext: Context) :
    DefaultLifecycleObserver {

    // Umute microphone if the app is moved from the foreground
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onStop(owner: LifecycleOwner) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        toggle.unmute()
        Toast.makeText(baseContext, "App moving to background. Unmuting microphone.", Toast.LENGTH_SHORT).show()
    }

    // Do not keep screen on if app isn't in foreground
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onStart(owner: LifecycleOwner) {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

}