package com.example.tasky.agenda.agenda_data.alarm

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.provider.AlarmClock.EXTRA_MESSAGE
import com.example.tasky.AlarmBroadcastReceiver
import com.example.tasky.agenda.agenda_domain.AlarmScheduler
import com.example.tasky.agenda.agenda_domain.model.AgendaItem
import com.example.tasky.core.presentation.DateUtils.toLong
import timber.log.Timber

class AlarmSchedulerService(private val context: Context) : AlarmScheduler {

    private val alarmManager by lazy {
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    @SuppressLint("ScheduleExactAlarm")
    override suspend fun schedule(agendaItem: AgendaItem) {

        Timber.d("DDD - Scheduling alarm for ${agendaItem.remindAt} || title: ${agendaItem.title}")
        val intent = Intent(context, AlarmBroadcastReceiver::class.java).apply {
            putExtra(EXTRA_MESSAGE, agendaItem.title)
        }
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            agendaItem.remindAt.toLong(),
            PendingIntent.getBroadcast(
                context,
                agendaItem.id.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

    override suspend fun cancel(agendaItem: AgendaItem) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                agendaItem.id.hashCode(),
                Intent(context, AlarmBroadcastReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

}