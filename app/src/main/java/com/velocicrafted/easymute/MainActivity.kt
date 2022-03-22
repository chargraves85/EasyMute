package com.velocicrafted.easymute

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner


class MainActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback {

    companion object {
        private const val MODIFY_AUDIO_SETTINGS = 100
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val muteToggle = findViewById<Button>(R.id.muteToggle)
        val audioManager = baseContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        ProcessLifecycleOwner.get().lifecycle.addObserver(AppLifecycleListener(audioManager, muteToggle, baseContext, window))
        muteToggle.setOnClickListener { toggleMute(muteToggle, audioManager) }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun toggleMute(button: Button, audioManager: AudioManager) {

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
            Toast.makeText(baseContext, "This app requires permission to modify audio settings.", Toast.LENGTH_SHORT).show()
            requestPermissionsCompat(arrayOf(Manifest.permission.MODIFY_AUDIO_SETTINGS), MODIFY_AUDIO_SETTINGS)
        }
    }

    private fun AppCompatActivity.checkSelfPermissionCompat(permission: String) =
        ActivityCompat.checkSelfPermission(this, permission)

    private fun AppCompatActivity.requestPermissionsCompat(permissionsArray: Array<String>, requestCode: Int) {
        ActivityCompat.requestPermissions(this, permissionsArray, requestCode)
    }

}


class AppLifecycleListener(private val audioManager: AudioManager, private val button: Button, private val context: Context, private val window: Window) : DefaultLifecycleObserver {

    // Umuted microphone if the app is moved from the foreground
    override fun onStop(owner: LifecycleOwner) { // app moved to background
        if (audioManager.isMicrophoneMute) {
            audioManager.isMicrophoneMute = false
            button.setBackgroundColor(Color.GREEN)
            button.text = "Speaking"
            Toast.makeText(context, "App moving to background.  Unmuting microphone.", Toast.LENGTH_SHORT).show()
        } else {
            audioManager.isMicrophoneMute = true
            button.setBackgroundColor(Color.RED)
            button.text = "Muted"
        }
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
    // Do not keep screen on if app isn't in foreground
    override fun onStart(owner: LifecycleOwner) {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
}