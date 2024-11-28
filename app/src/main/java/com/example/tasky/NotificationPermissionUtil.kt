package com.example.tasky

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.tasky.Constants.REQUEST_CODE_POST_NOTIFICATIONS

class NotificationPermissionUtil(private val context: Context) {

    fun showNotificationPermissionDialogIfNotEnabled(activity: MainActivity) {
        if (arePushNotificationsEnabledOnTheDevice(context)) {
            showNotificationPermissionDialog(activity)
        }
    }

    private fun showNotificationPermissionDialog(activity: MainActivity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf("android.permission.POST_NOTIFICATIONS"),
            REQUEST_CODE_POST_NOTIFICATIONS
        )
    }

    fun isAndroid13OrHigher() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            "android.permission.POST_NOTIFICATIONS"
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun arePushNotificationsEnabledOnTheDevice(context: Context): Boolean {
        return NotificationManagerCompat.from(context).areNotificationsEnabled()
    }
}