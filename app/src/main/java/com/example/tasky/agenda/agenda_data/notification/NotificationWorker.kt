package com.example.tasky.agenda.agenda_data.notification

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.tasky.R
import com.example.tasky.core.domain.Result
import kotlin.random.Random

class NotificationWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {
    override fun doWork(): Result {
        val title = inputData.getString("title") ?: return Result.failure()
        val message = inputData.getString("message") ?: return Result.failure()

        showNotification(title, message)

        return Result.success()
    }

    private fun showNotification(title: String, message: String) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val builder = NotificationCompat.Builder(applicationContext, "tasky_notification")
            .setSmallIcon(R.drawable.tasky_logo)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        notificationManager.notify(Random.nextInt(), builder.build())
    }
}