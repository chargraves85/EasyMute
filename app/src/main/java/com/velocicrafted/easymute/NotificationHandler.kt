package com.velocicrafted.easymute

import android.R
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat


class NotificationHandler() : Service() {

    private val receiver = createBroadCastReceiver()
    private val channelId = "default"

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        registerReceiver()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver()
    }

    private lateinit var notificationManager: NotificationManager

    // Create an Intent for the activity you want to start

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            val name = "getString(R.string.channel_name)"
            val descriptionText = "getString(R.string.channel_description)"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(channelId, name, importance)
            mChannel.description = descriptionText
            notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
    }

    private fun createNotification(): Notification {

        val resultIntent = Intent(this, MainActivity::class.java)
        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
            // Add the intent, which inflates the back stack
            addNextIntentWithParentStack(resultIntent)
            // Get the PendingIntent containing the entire back stack
            getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        val fullScreenIntent = Intent(this, MainActivity::class.java)
        val fullScreenPendingIntent = PendingIntent.getActivity(
            this, 0,
            fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.arrow_up_float)
            .setContentTitle("EasyMute")
            .setContentText("Click here to launch EasyMute")
            .setContentIntent(resultPendingIntent)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

    }

    private fun createBroadCastReceiver(): BroadcastReceiver {
        return object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
                if (state == null) {
                    with(NotificationManagerCompat.from(context)) {
                        notify(1, createNotification())
                    }
                } else if (state == TelephonyManager.EXTRA_STATE_OFFHOOK) {
                    with(NotificationManagerCompat.from(context)) {
                        notify(1, createNotification())
                    }
                }
            }
        }
    }

    private fun registerReceiver() {
        try {
            registerReceiver(receiver, IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED))
            Log.i("broadcastreceiver", "Broadcast Receiver Registered")
        } catch (e: IllegalArgumentException) {
            Log.i("broadcastreceiver", "$e")
        }
    }

    private fun unregisterReceiver() {
        try {
            unregisterReceiver(receiver)
            Log.i("broadcastreceiver", "Broadcast Receiver Unregistered")
        } catch (e: IllegalArgumentException) {
            Log.i("broadcastreceiver", "$e")
        }
    }

}