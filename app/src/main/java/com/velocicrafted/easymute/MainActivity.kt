package com.velocicrafted.easymute

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback {

    companion object {
        private const val MODIFY_AUDIO_SETTINGS = 101
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val muteToggle = findViewById<Button>(R.id.muteToggle)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        muteToggle.setOnClickListener { toggleMute(muteToggle) }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun toggleMute(button: Button) {
        val audioManager = baseContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        if (checkSelfPermissionCompat(Manifest.permission.MODIFY_AUDIO_SETTINGS) == PackageManager.PERMISSION_GRANTED) {
            if (audioManager.isMicrophoneMute) {
                audioManager.isMicrophoneMute = false
                button.setBackgroundColor(Color.GREEN)
                button.text = "Speaking"
            } else {
                audioManager.isMicrophoneMute = true
                button.setBackgroundColor(Color.RED)
                button.text = "Muted"
            }
        } else {
            // Permission is missing and must be requested.
            Toast.makeText(this@MainActivity, "This app requires permission to modify audio settings.", Toast.LENGTH_SHORT).show()
            requestPermissionsCompat(arrayOf(Manifest.permission.MODIFY_AUDIO_SETTINGS), MODIFY_AUDIO_SETTINGS)
        }
    }

    private fun AppCompatActivity.checkSelfPermissionCompat(permission: String) =
        ActivityCompat.checkSelfPermission(this, permission)

    private fun AppCompatActivity.requestPermissionsCompat(permissionsArray: Array<String>, requestCode: Int) {
        ActivityCompat.requestPermissions(this, permissionsArray, requestCode)
    }

}