package com.example.tasky

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.tasky.agenda.agenda_domain.AlarmScheduler
import com.example.tasky.agenda.agenda_domain.model.AgendaItemDetails
import com.example.tasky.agenda.agenda_domain.model.AgendaOption
import com.example.tasky.agenda.agenda_domain.repository.LocalDatabaseRepository
import com.example.tasky.core.di.TaskyModule
import com.example.tasky.core.presentation.DateUtils.toLong
import com.example.tasky.util.Logger
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SystemBootCompletedReceiver : BroadcastReceiver() {

    @Inject
    lateinit var localDataSource: LocalDatabaseRepository
    @Inject
    lateinit var alarmScheduler: AlarmScheduler


    val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {

            context?.let {
                val appContext = it.applicationContext as TaskyApp
                val entryPoint = EntryPointAccessors.fromApplication(
                    appContext,
                    TaskyModule.BootReceiverEntryPoint::class.java
                )
                localDataSource = entryPoint.getRepository()

                scope.launch {
                    try {
                        val agendaItems = localDataSource.getAllAgendaItems().first()
                        agendaItems.forEach { agendaItem ->
                            val option = when (agendaItem.details) {
                                is AgendaItemDetails.Task -> AgendaOption.TASK
                                is AgendaItemDetails.Event -> AgendaOption.EVENT
                                is AgendaItemDetails.Reminder -> AgendaOption.REMINDER
                            }
                            if (agendaItem.time.toLong() > System.currentTimeMillis()) {
                                alarmScheduler.schedule(agendaItem, option)
                            }
                        }
                    } catch (e: Exception) {
                        if (e is CancellationException) throw e

                        Logger.d("Error scheduling alarms: ${e.message}", e)
                    } finally {
                        Logger.d("Coroutine scope completed, cancelling scope")
                        scope.cancel()
                    }
                }
            }
        }
    }
}