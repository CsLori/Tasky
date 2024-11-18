package com.example.tasky.agenda.agenda_data.notification

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.workDataOf
import androidx.work.WorkManager
import com.example.tasky.agenda.agenda_domain.NotificationScheduler
import com.example.tasky.agenda.agenda_domain.model.AgendaItem
import java.util.concurrent.TimeUnit

class NotificationService(private val context: Context): NotificationScheduler {
    override fun schedule(agendaItem: AgendaItem) {
        val delay = agendaItem.remindAt - System.currentTimeMillis()
        val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInputData(workDataOf("title" to agendaItem.title, "message" to agendaItem.description))
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }

    override fun cancel(agendaItem: AgendaItem) {
        WorkManager.getInstance(context).cancelAllWorkByTag(agendaItem.id)
    }
}