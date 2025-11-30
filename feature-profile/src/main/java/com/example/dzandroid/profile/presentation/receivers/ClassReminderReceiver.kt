package com.example.dzandroid.profile.presentation.receivers

import android.Manifest
import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.dzandroid.core.AlarmConstants

class ClassReminderReceiver : BroadcastReceiver() {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onReceive(context: Context, intent: Intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        val fullName = intent.getStringExtra(AlarmConstants.EXTRA_FULL_NAME) ?: "друг"
        val message = "Пора на пару, $fullName!"

        createNotificationChannel(context)
        showNotification(context, message)
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                AlarmConstants.CHANNEL_ID,
                "Class Reminder",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Напоминания о начале пар"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500)
            }
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun showNotification(context: Context, message: String) {
        val builder = NotificationCompat.Builder(context, AlarmConstants.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_dialog_info)
            .setContentTitle("🔔 Любимая пара!")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 500, 200, 500))

        try {
            NotificationManagerCompat.from(context).notify(AlarmConstants.NOTIFICATION_ID, builder.build())
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    companion object {
        const val CHANNEL_ID = AlarmConstants.CHANNEL_ID
        const val NOTIFICATION_ID = AlarmConstants.NOTIFICATION_ID
        const val EXTRA_FULL_NAME = AlarmConstants.EXTRA_FULL_NAME
        const val ACTION_ALARM = AlarmConstants.ACTION_ALARM
    }
}