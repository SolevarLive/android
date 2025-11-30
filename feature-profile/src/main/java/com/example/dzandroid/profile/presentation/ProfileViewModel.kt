package com.example.dzandroid.profile.presentation

import android.app.AlarmManager
import android.app.DownloadManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dzandroid.core.AlarmConstants
import com.example.dzandroid.data.domain.GetProfileUseCase
import com.example.dzandroid.data.domain.SaveProfileUseCase
import com.example.dzandroid.data.local.datastore.ProfileDataStore
import com.example.dzandroid.data.models.Profile
import com.example.dzandroid.profile.presentation.receivers.AlarmScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ProfileViewModel(
    private val getProfileUseCase: GetProfileUseCase,
    private val saveProfileUseCase: SaveProfileUseCase,
    private val profileDataStore: ProfileDataStore,
    private val alarmScheduler: AlarmScheduler? = null
) : ViewModel() {

    private val _profileState = MutableStateFlow(Profile())
    val profileState: StateFlow<Profile> = _profileState.asStateFlow()

    private val _avatarUri = MutableStateFlow<Uri?>(null)
    val avatarUri: StateFlow<Uri?> = _avatarUri.asStateFlow()

    private val _downloadStatus = MutableStateFlow<String?>(null)
    val downloadStatus: StateFlow<String?> = _downloadStatus.asStateFlow()

    private val _favoriteClassTime = MutableStateFlow("")
    val favoriteClassTime: StateFlow<String> = _favoriteClassTime.asStateFlow()

    private var currentAlarmPendingIntent: PendingIntent? = null

    init {
        loadProfile()
        loadFavoriteClassTime()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            getProfileUseCase().collect { profile ->
                _profileState.value = profile
                if (profile.avatarUri.isNotEmpty()) {
                    try {
                        _avatarUri.value = Uri.parse(profile.avatarUri)
                    } catch (e: Exception) {
                    }
                }
            }
        }
    }

    private fun loadFavoriteClassTime() {
        viewModelScope.launch {
            getProfileUseCase().collect { profile ->
                _favoriteClassTime.value = profile.favoriteClassTime
            }
        }
    }

    fun updateAvatar(uri: Uri) {
        _avatarUri.value = uri
    }

    fun saveProfile(
        fullName: String,
        resumeUrl: String,
        position: String = "",
        email: String = "",
        favoriteClassTime: String = ""
    ) {
        viewModelScope.launch {
            val avatarUriString = _avatarUri.value?.toString() ?: ""
            val profile = Profile(
                fullName = fullName,
                avatarUri = avatarUriString,
                resumeUrl = resumeUrl,
                position = position,
                email = email,
                favoriteClassTime = favoriteClassTime
            )
            saveProfileUseCase(profile)
            _profileState.value = profile
        }
    }

    fun setClassReminder(time: String) {
        viewModelScope.launch {
            if (!validateTimeFormat(time)) {
                Toast.makeText(
                    profileDataStore.context,
                    "Неверный формат времени: $time",
                    Toast.LENGTH_SHORT
                ).show()
                return@launch
            }

            alarmScheduler?.scheduleReminder(
                context = profileDataStore.context,
                fullName = _profileState.value.fullName,
                time = time
            ) ?: run {
                scheduleReminderFallback(time)
            }
        }
    }

    private fun scheduleReminderFallback(time: String) {
        val context = profileDataStore.context
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, com.example.dzandroid.profile.presentation.receivers.ClassReminderReceiver::class.java).apply {
            action = AlarmConstants.ACTION_ALARM
            putExtra(AlarmConstants.EXTRA_FULL_NAME, _profileState.value.fullName)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            AlarmConstants.REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        cancelClassReminderFallback()

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
            currentAlarmPendingIntent = pendingIntent

            val timeString = String.format("%02d:%02d", hours, minutes)
            val dayMessage = if (calendar.get(Calendar.DAY_OF_MONTH) == Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) {
                "сегодня"
            } else {
                "завтра"
            }

            Toast.makeText(
                context,
                "Напоминание установлено на $timeString ($dayMessage)",
                Toast.LENGTH_LONG
            ).show()

        } catch (e: SecurityException) {
            e.printStackTrace()
            Toast.makeText(
                context,
                "Ошибка безопасности: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun cancelClassReminder() {
        viewModelScope.launch {
            alarmScheduler?.cancelReminder(profileDataStore.context) ?: cancelClassReminderFallback()
        }
    }

    private fun cancelClassReminderFallback() {
        val context = profileDataStore.context
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, com.example.dzandroid.profile.presentation.receivers.ClassReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            AlarmConstants.REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
        currentAlarmPendingIntent = null

        Toast.makeText(
            context,
            "Напоминание отменено",
            Toast.LENGTH_SHORT
        ).show()
    }

    fun downloadResume(context: Context, url: String) {
        viewModelScope.launch {
            try {
                if (url.isEmpty()) {
                    _downloadStatus.value = "Ссылка на резюме не указана"
                    return@launch
                }

                val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val fileName = "resume_${_profileState.value.fullName}_$timestamp.pdf"

                val request = DownloadManager.Request(Uri.parse(url))
                    .setTitle("Резюме - ${_profileState.value.fullName}")
                    .setDescription("Скачивание резюме")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)

                downloadManager.enqueue(request)
                _downloadStatus.value = "Резюме скачивается в папку Загрузки"
            } catch (e: Exception) {
                e.printStackTrace()
                _downloadStatus.value = "Ошибка скачивания: ${e.message}"
            }
        }
    }

    fun clearDownloadStatus() {
        _downloadStatus.value = null
    }

    fun validateTimeFormat(time: String): Boolean {
        if (time.isEmpty()) return true
        val parts = time.split(":")
        if (parts.size != 2) return false
        val hours = parts[0].toIntOrNull() ?: return false
        val minutes = parts[1].toIntOrNull() ?: return false
        return hours in 0..23 && minutes in 0..59
    }
}