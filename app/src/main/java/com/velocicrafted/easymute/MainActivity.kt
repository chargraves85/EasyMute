package com.velocicrafted.easymute

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainActivity : AppCompatActivity() {

    @SuppressLint("ClickableViewAccessibility")  // TODO: Consider fixing this for onTouchListener
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
        val unmuteTimeSlider = findViewById<SeekBar>(R.id.muteDelay)
        val unmuteTimeText = findViewById<TextView>(R.id.unmutedTimeText)
        val unmuteTimeValue = findViewById<TextView>(R.id.unmuteTime)

        // settings strings for checking
        val premiumEnabled= "premiumEnabled"
        val autoLaunch = "autoLaunch"
        val askLaunch = "askLaunch"
        val minimizeMute = "minimizeMute"
        val timedUnmute = "timedUnmute"
        val holdToMute = "holdToMute"

        ProcessLifecycleOwner.get().lifecycle.addObserver(AppLifecycleListener(toggler, window, baseContext))

        if (!settings.getBoolean(premiumEnabled, false)) {
            isMutedImage.setOnClickListener { toggler.toggleMute() }
        } else {
            if (settings.getBoolean(minimizeMute, true)) {
                window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
            if (!settings.getBoolean(holdToMute, false)) {
                toggler.unmute()
                if (settings.getBoolean(timedUnmute, false)) {
                    toggler.mute()
                    unmuteTimeSlider.visibility = View.VISIBLE
                    unmuteTimeText.visibility = View.VISIBLE
                    unmuteTimeValue.visibility = View.VISIBLE
                    unmuteTimeSlider.setOnSeekBarChangeListener(object :
                        SeekBar.OnSeekBarChangeListener {
                        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                            unmuteTimeValue.text = progress.toString()
                        }
                        override fun onStartTrackingTouch(seekBar: SeekBar?) {
                        }

                        override fun onStopTrackingTouch(seekBar: SeekBar?) {
                        }
                    })
                    isMutedImage.setOnClickListener {
                        toggler.unmute()
                        var duration = unmuteTimeValue.text.toString().toLong()
                        object : CountDownTimer(duration * 1000, 1000) {
                            override fun onTick(millisUntilFinished: Long) {
                                duration -= 1
                                unmuteTimeValue.text = duration.toString()
                            }

                            override fun onFinish() {
                                unmuteTimeValue.text = unmuteTimeSlider.progress.toString()
                                toggler.mute()
                            }
                        }.start()
                    }
                } else {
                    unmuteTimeSlider.visibility = View.GONE
                    unmuteTimeText.visibility = View.GONE
                    unmuteTimeValue.visibility = View.GONE
                    isMutedImage.setOnClickListener {
                        toggler.toggleMute()
                    }
                }
            } else {
                toggler.mute()
                isMutedImage.setOnTouchListener { _, event ->
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        toggler.unmute()
                    }
                    if (event.action == MotionEvent.ACTION_UP) {
                        toggler.mute()
                    }
                    true
                }
            }
        }
        settingsButton.setOnClickListener { goToSettings() }
    }

    private fun goToSettings() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
        }

}
