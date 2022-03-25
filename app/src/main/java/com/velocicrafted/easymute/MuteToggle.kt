package com.velocicrafted.easymute

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioManager
import android.os.Build
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat

class MuteToggle(private val image: ImageView, private val text: TextView, private val audioManager: AudioManager, private val baseContext: Context) {

    companion object {
        private const val MODIFY_AUDIO_SETTINGS = 100
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun toggleMute() {

        if (checkSelfPermissionCompat(Manifest.permission.MODIFY_AUDIO_SETTINGS) == PackageManager.PERMISSION_GRANTED) {
            if (audioManager.isMicrophoneMute) {
                audioManager.isMicrophoneMute = false
                image.setImageResource(R.drawable.micopen)
                text.setText(R.string.speaking)
            } else {
                audioManager.isMicrophoneMute = true
                image.setImageResource(R.drawable.micclosed)
                text.setText(R.string.muted)
            }
        } else {
            // Permission is missing and must be requested.
            Toast.makeText(baseContext, "This app requires permission to modify audio settings.", Toast.LENGTH_SHORT).show()
            requestPermissionsCompat(arrayOf(Manifest.permission.MODIFY_AUDIO_SETTINGS),
                MODIFY_AUDIO_SETTINGS
            )
        }
    }

    fun unmute() {
        if (checkSelfPermissionCompat(Manifest.permission.MODIFY_AUDIO_SETTINGS) == PackageManager.PERMISSION_GRANTED) {
            audioManager.isMicrophoneMute = false
            image.setImageResource(R.drawable.micopen)
            text.setText(R.string.speaking)
        } else {
            // Permission is missing and must be requested.
            Toast.makeText(baseContext, "This app requires permission to modify audio settings.", Toast.LENGTH_SHORT).show()
            requestPermissionsCompat(arrayOf(Manifest.permission.MODIFY_AUDIO_SETTINGS),
                MODIFY_AUDIO_SETTINGS
            )
        }

    }

    fun mute() {
        if (checkSelfPermissionCompat(Manifest.permission.MODIFY_AUDIO_SETTINGS) == PackageManager.PERMISSION_GRANTED) {
            audioManager.isMicrophoneMute = true
            image.setImageResource(R.drawable.micclosed)
            text.setText(R.string.muted)
        } else {
            // Permission is missing and must be requested.
            Toast.makeText(baseContext, "This app requires permission to modify audio settings.", Toast.LENGTH_SHORT).show()
            requestPermissionsCompat(arrayOf(Manifest.permission.MODIFY_AUDIO_SETTINGS),
                MODIFY_AUDIO_SETTINGS
            )
        }
    }

    private fun checkSelfPermissionCompat(permission: String) =
        ActivityCompat.checkSelfPermission(baseContext, permission)

    private fun requestPermissionsCompat(permissionsArray: Array<String>, requestCode: Int) {
        ActivityCompat.requestPermissions(Activity().parent, permissionsArray, requestCode)
    }

}