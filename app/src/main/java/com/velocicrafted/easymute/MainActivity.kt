package com.velocicrafted.easymute

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val settings = applicationContext.getSharedPreferences("Settings", MODE_PRIVATE)

        val isMutedImage = findViewById<ImageView>(R.id.isMutedImage)
        val audioManager = baseContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val text = findViewById<TextView>(R.id.isMutedText)
        val toggler = MuteToggle(isMutedImage, text, audioManager, baseContext)
        val settingsButton = findViewById<FloatingActionButton>(R.id.settingsButton)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        ProcessLifecycleOwner.get().lifecycle.addObserver(AppLifecycleListener(toggler, window, baseContext))
        isMutedImage.setOnClickListener { toggler.toggleMute() }
        settingsButton.setOnClickListener { goToSettings() }

    }

    private fun goToSettings() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
        }

}
