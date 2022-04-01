package com.velocicrafted.easymute

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class SettingsActivity : AppCompatActivity()  {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val settings: SharedPreferences = applicationContext.getSharedPreferences("Settings", MODE_PRIVATE)
        setContentView(R.layout.settings)
        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener { goHome() }

        // get our switches
        val premiumToggle = findViewById<SwitchCompat>(R.id.premiumToggle)
        val autoLaunchOnCallToggle = findViewById<SwitchCompat>(R.id.callLaunchAuto)
        val askLaunchOnCallToggle = findViewById<SwitchCompat>(R.id.callLaunchAsk)
        val minimizeMuteToggle = findViewById<SwitchCompat>(R.id.minimizeMute)
        val timedUnmuteToggle = findViewById<SwitchCompat>(R.id.timedUnmute)
        val holdToMuteToggle = findViewById<SwitchCompat>(R.id.holdToMute)

        // check premium status and update toggles accordingly, placed here to check after
        if (settings.getBoolean("premiumEnabled", false)) {
            premiumToggle.isEnabled = false
            autoLaunchOnCallToggle.isEnabled = true
            askLaunchOnCallToggle.isEnabled = true
            minimizeMuteToggle.isEnabled = true
            timedUnmuteToggle.isEnabled = true
            holdToMuteToggle.isEnabled = true
        } else {
            premiumToggle.isChecked = false
        }

        premiumToggle.setOnClickListener {

            enablePremium()

            // checks if above was successful TODO: check if purchased on app open, consider no service
            if (settings.getBoolean("premiumEnabled", false)) {
                premiumToggle.isChecked = false
                premiumToggle.isEnabled = false
                autoLaunchOnCallToggle.isEnabled = true
                askLaunchOnCallToggle.isEnabled = true
                minimizeMuteToggle.isEnabled = true
                timedUnmuteToggle.isEnabled = true
                holdToMuteToggle.isEnabled = true
            } else {
                premiumToggle.isChecked = false
            }
        }

        autoLaunchOnCallToggle.setOnClickListener {
            settings.edit().putBoolean("autoLaunch", true).apply()
        }

        askLaunchOnCallToggle.setOnClickListener {
            settings.edit().putBoolean("askLaunch", true).apply()
        }

        minimizeMuteToggle.setOnClickListener {
            settings.edit().putBoolean("minimizeMute", true).apply()
        }

        timedUnmuteToggle.setOnClickListener {
            settings.edit().putBoolean("timedUnmute", true).apply()
        }

        holdToMuteToggle.setOnClickListener {
            settings.edit().putBoolean("holdToMute", true).apply()
        }

    }

    private fun goHome() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun enablePremium() {
        val thisActivity = this
        runBlocking {
            launch {
                IAPController(thisActivity).connectToBilling()
            }
        }
    }
}
