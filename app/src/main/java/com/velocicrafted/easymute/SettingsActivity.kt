package com.velocicrafted.easymute

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class SettingsActivity: AppCompatActivity()  {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)

        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener { goHome() }

        val premiumToggle = findViewById<SwitchCompat>(R.id.premiumToggle)
        premiumToggle.setOnClickListener {
            val thisActivity = this
            runBlocking {
                launch { IAPController(baseContext, thisActivity).connectToBilling() }
            }
        }
    }

    private fun goHome() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }




}
