package com.example.tasky

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.example.tasky.Constants.CHANNEL_ID
import com.example.tasky.Constants.CHANNEL_NAME
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TaskyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val channelName = ""
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)
    }
}