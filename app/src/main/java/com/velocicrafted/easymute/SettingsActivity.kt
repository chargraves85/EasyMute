package com.velocicrafted.easymute

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)
        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener { goHome() }
    }

    fun goHome() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }


}
