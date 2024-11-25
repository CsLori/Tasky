package com.example.tasky

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.example.tasky.Constants.AGENDA_ID
import com.example.tasky.Constants.AGENDA_OPTION
import com.example.tasky.Constants.CHANNEL_ID
import com.example.tasky.Constants.DESCRIPTION
import com.example.tasky.Constants.TIME
import com.example.tasky.Constants.TITLE
import com.example.tasky.core.data.local.ProtoUserPrefsRepository
import com.example.tasky.core.presentation.DateUtils.toLocalDateTime
import com.example.tasky.core.presentation.DateUtils.toMMMdHHmmFormat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


class AlarmBroadcastReceiver : BroadcastReceiver() {

    val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    @Inject
    lateinit var userPrefsRepository: ProtoUserPrefsRepository
    var isUserAuthenticated = false
    override fun onReceive(context: Context?, intent: Intent?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            context?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    "android.permission.POST_NOTIFICATIONS"
                )
            } != PackageManager.PERMISSION_GRANTED
        ) {
            Timber.e("Notification permission not granted")
            return
        }

        scope.launch {
            isUserAuthenticated = userPrefsRepository.getAccessToken().isNotEmpty()
        }


        val channelId = CHANNEL_ID
        val title = intent?.getStringExtra(TITLE) ?: return
        val description = intent.getStringExtra(DESCRIPTION) ?: return
        val agendaItemId = intent?.getStringExtra(AGENDA_ID) ?: return
        val time = intent?.getLongExtra(TIME, 0)?.toLocalDateTime()
        val agendaOption = intent?.getStringExtra(AGENDA_OPTION)

        context?.let { safeContext ->
            val activityIntent = Intent(Intent.ACTION_VIEW).apply {
                data =
                    if (isUserAuthenticated) "tasky://agenda_detail/${agendaOption}?agendaItemId=${agendaItemId}&isAgendaItemReadOnly=true&photoId=null".toUri() else "tasky://login".toUri()
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }

            val pendingIntent = TaskStackBuilder.create(context).run {
                addNextIntentWithParentStack(activityIntent)
                getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
            }

            val notificationManager =
                safeContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val notification = NotificationCompat.Builder(safeContext, channelId)
                .setSmallIcon(R.drawable.tasky_logo)
                .setContentTitle("Alarm $title at ${time?.toMMMdHHmmFormat()}")
                .setContentText(description)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build()

            notificationManager.notify(1, notification)
        }
    }
}