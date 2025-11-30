package com.example.dzandroid.profile.presentation.receivers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import java.util.Calendar
import com.example.dzandroid.profile.presentation.Navigator

class ClassReminderScheduler(
    private val navigator: Navigator
) : AlarmScheduler {

    companion object {
        private const val REQUEST_CODE = 1001
    }

    override fun scheduleReminder(context: Context, fullName: String, time: String) {
        val intent = Intent(context, ClassReminderReceiver::class.java).apply {
            action = ClassReminderReceiver.ACTION_ALARM
            putExtra(ClassReminderReceiver.EXTRA_FULL_NAME, fullName)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val parts = time.split(":")
        val hours = parts[0].toIntOrNull() ?: return
        val minutes = parts[1].toIntOrNull() ?: return

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hours)
            set(Calendar.MINUTE, minutes)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }

            val timeString = String.format("%02d:%02d", hours, minutes)
            val dayMessage = if (calendar.get(Calendar.DAY_OF_MONTH) == Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) {
                "сегодня"
            } else {
                "завтра"
            }

            val debugMessage = "Напоминание установлено на $timeString ($dayMessage)"
            Toast.makeText(context, debugMessage, Toast.LENGTH_LONG).show()

        } catch (e: SecurityException) {
            e.printStackTrace()
            Toast.makeText(context, "Ошибка безопасности: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun cancelReminder(context: Context) {
        val intent = Intent(context, ClassReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)

        Toast.makeText(context, "Напоминание отменено", Toast.LENGTH_SHORT).show()
    }
}