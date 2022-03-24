package com.velocicrafted.easymute

import android.content.Context
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ProcessLifecycleOwner


class MainActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback {

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val muteToggle = findViewById<Button>(R.id.muteToggle)
        val audioManager = baseContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val toggler = MuteToggle(baseContext, muteToggle, audioManager)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        ProcessLifecycleOwner.get().lifecycle.addObserver(AppLifecycleListener(toggler, window, baseContext))
        muteToggle.setOnClickListener { toggler.toggleMute() }
    }

}
