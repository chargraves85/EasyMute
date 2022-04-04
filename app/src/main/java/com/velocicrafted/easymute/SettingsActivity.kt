package com.velocicrafted.easymute

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import android.widget.Toast

import android.content.ActivityNotFoundException
import android.net.Uri


class SettingsActivity : AppCompatActivity()  {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val settings: SharedPreferences =
            applicationContext.getSharedPreferences("Settings", MODE_PRIVATE)
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
        
        // settings strings as vars to reduce errors
        val premiumEnabled= "premiumEnabled"
        val autoLaunch = "autoLaunch"
        val askLaunch = "askLaunch"
        val minimizeMute = "minimizeMute"
        val timedUnmute = "timedUnmute"
        val holdToMute = "holdToMute"

        if (settings.getBoolean(autoLaunch, false)) {
            autoLaunchOnCallToggle.isChecked = true
        }
        if (settings.getBoolean(askLaunch, false)) {
            askLaunchOnCallToggle.isChecked = true
        }
        if (settings.getBoolean(minimizeMute, true)) {
            minimizeMuteToggle.isChecked = true
        }
        if (settings.getBoolean(timedUnmute, false)) {
            timedUnmuteToggle.isChecked = true
        }
        if (settings.getBoolean(holdToMute, false)) {
            holdToMuteToggle.isChecked = true
        }

        // check premium status and update toggles accordingly, placed here to check after
        if (settings.getBoolean(premiumEnabled, false)) {
            premiumToggle.isChecked = true
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
            if (settings.getBoolean(premiumEnabled, false)) {
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
            if (!settings.getBoolean(autoLaunch, false)) {
                settings.edit().putBoolean(autoLaunch, true).apply()
                settings.edit().putBoolean(askLaunch, false).apply()
                askLaunchOnCallToggle.isChecked = false
            } else {
                settings.edit().putBoolean(autoLaunch, false).apply()
            }
        }

        askLaunchOnCallToggle.setOnClickListener {
            if (!settings.getBoolean(askLaunch, false)) {
                settings.edit().putBoolean(askLaunch, true).apply()
                settings.edit().putBoolean(autoLaunch, false).apply()
                autoLaunchOnCallToggle.isChecked = false
            } else {
                settings.edit().putBoolean(askLaunch, false).apply()
            }
        }

        minimizeMuteToggle.setOnClickListener {
            if (!settings.getBoolean(minimizeMute, false)) {
                settings.edit().putBoolean(minimizeMute, true).apply()
            } else {
                settings.edit().putBoolean(minimizeMute, false).apply()
            }
            println(settings.all)
        }

        timedUnmuteToggle.setOnClickListener {
            if (!settings.getBoolean(timedUnmute, false)) {
                settings.edit().putBoolean(timedUnmute, true).apply()
                settings.edit().putBoolean(holdToMute, false).apply()
                holdToMuteToggle.isChecked = false
            } else {
                settings.edit().putBoolean(timedUnmute, false).apply()
            }
            println(settings.all)
        }

        holdToMuteToggle.setOnClickListener {
            if (!settings.getBoolean(holdToMute, false)) {
                settings.edit().putBoolean(holdToMute, true).apply()
                settings.edit().putBoolean(timedUnmute, false).apply()
                timedUnmuteToggle.isChecked = false
            } else {
                settings.edit().putBoolean(holdToMute, false).apply()
            }
        }

        val shareButton = findViewById<Button>(R.id.share)
        val rateButton = findViewById<Button>(R.id.rate)
        val uri: Uri = Uri.parse("https://play.google.com/store/apps/details?id=$packageName")


        shareButton.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.putExtra(Intent.EXTRA_TEXT, "Hey! Check out this great app: $uri")
            intent.type = "text/plain"
            startActivity(Intent.createChooser(intent, "Share To:"))
        }

        rateButton.setOnClickListener {
            val myAppLinkToMarket = Intent(Intent.ACTION_VIEW, uri)
            try {
                startActivity(myAppLinkToMarket)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this, "Unable to find app on market.", Toast.LENGTH_LONG).show()
            }
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
                IAPController(thisActivity, applicationContext).connectToBilling()
            }
        }
    }
}
