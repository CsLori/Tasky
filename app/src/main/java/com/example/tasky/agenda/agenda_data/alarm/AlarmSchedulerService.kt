package com.example.tasky.agenda.agenda_data.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.tasky.AlarmBroadcastReceiver
import com.example.tasky.Constants.AGENDA_ID
import com.example.tasky.Constants.AGENDA_OPTION
import com.example.tasky.Constants.DESCRIPTION
import com.example.tasky.Constants.TIME
import com.example.tasky.Constants.TITLE
import com.example.tasky.agenda.agenda_domain.AlarmScheduler
import com.example.tasky.agenda.agenda_domain.model.AgendaItem
import com.example.tasky.agenda.agenda_domain.model.AgendaOption
import com.example.tasky.core.presentation.DateUtils.toLong

class AlarmSchedulerService(private val context: Context) : AlarmScheduler {

    private val alarmManager by lazy {
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    override suspend fun schedule(agendaItem: AgendaItem, option: AgendaOption) {

        val intent = Intent(context, AlarmBroadcastReceiver::class.java).apply {
            putExtra(TITLE, agendaItem.title)
            putExtra(DESCRIPTION, agendaItem.description)
            putExtra(TIME, agendaItem.time.toLong())
            putExtra(AGENDA_ID, agendaItem.id)
            putExtra(AGENDA_OPTION, option.displayName.lowercase())
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