package com.example.tasky

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.tasky.Constants.CHANNEL_ID

class AlarmBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val message = intent?.getStringExtra("EXTRA_MESSAGE") ?: return

        val channelId = CHANNEL_ID
        context?.let { safeContext ->
            val notificationManager =
                safeContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val builder = NotificationCompat.Builder(safeContext, channelId)
                .setSmallIcon(R.drawable.tasky_logo)
                .setContentTitle("Alarm")
                .setContentText("Notification message $message")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
            notificationManager.notify(1, builder.build())
        }
    }
}