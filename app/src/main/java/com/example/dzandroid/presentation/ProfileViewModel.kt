package com.example.dzandroid.presentation

import android.app.AlarmManager
import android.app.DownloadManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dzandroid.data.local.datastore.ProfileDataStore
import com.example.dzandroid.domain.GetProfileUseCase
import com.example.dzandroid.domain.SaveProfileUseCase
import com.example.dzandroid.presentation.receivers.ClassReminderReceiver
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

class ProfileViewModel(
    private val getProfileUseCase: GetProfileUseCase,
    private val saveProfileUseCase: SaveProfileUseCase,
    private val profileDataStore: ProfileDataStore
) : ViewModel() {

    private val _profileState = MutableStateFlow(com.example.dzandroid.data.models.Profile())
    val profileState: StateFlow<com.example.dzandroid.data.models.Profile> = _profileState.asStateFlow()

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
            val profile = com.example.dzandroid.data.models.Profile(
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
                android.widget.Toast.makeText(
                    profileDataStore.context,
                    "Неверный формат времени: $time",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
                return@launch
            }

            val context = profileDataStore.context
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val intent = Intent(context, ClassReminderReceiver::class.java).apply {
                action = ClassReminderReceiver.ACTION_ALARM
                putExtra(ClassReminderReceiver.EXTRA_FULL_NAME, _profileState.value.fullName)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            cancelClassReminder()

            val parts = time.split(":")
            val hours = parts[0].toIntOrNull() ?: return@launch
            val minutes = parts[1].toIntOrNull() ?: return@launch

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

                val debugMessage = "Напоминание установлено на $timeString ($dayMessage)"
                android.widget.Toast.makeText(
                    context,
                    debugMessage,
                    android.widget.Toast.LENGTH_LONG
                ).show()

                android.util.Log.d("AlarmDebug", "Alarm set for: ${calendar.timeInMillis} ($timeString $dayMessage), current: ${System.currentTimeMillis()}")

            } catch (e: SecurityException) {
                e.printStackTrace()
                android.widget.Toast.makeText(
                    context,
                    "Ошибка безопасности: ${e.message}",
                    android.widget.Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    companion object {
        private const val REQUEST_CODE = 1001
    }

    fun cancelClassReminder() {
        viewModelScope.launch {
            val context = profileDataStore.context
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val intent = Intent(context, ClassReminderReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.cancel(pendingIntent)
            currentAlarmPendingIntent = null

            android.widget.Toast.makeText(
                context,
                "Напоминание отменено",
                android.widget.Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun downloadResume(context: Context, url: String) {
        viewModelScope.launch {
            try {
                if (url.isEmpty()) {
                    _downloadStatus.value = "Ссылка на резюме не указана"
                    return@launch
                }

                val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                val timestamp = java.text.SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.getDefault()).format(java.util.Date())
                val fileName = "resume_${_profileState.value.fullName}_$timestamp.pdf"

                val request = android.app.DownloadManager.Request(android.net.Uri.parse(url))
                    .setTitle("Резюме - ${_profileState.value.fullName}")
                    .setDescription("Скачивание резюме")
                    .setNotificationVisibility(android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(android.os.Environment.DIRECTORY_DOWNLOADS, fileName)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)

                downloadManager.enqueue(request)
                _downloadStatus.value = "Резюме скачивается в папку Загрузки"
            } catch (e: Exception) {
                _downloadStatus.value = "Ошибка скачивания: ${e.message}"
                e.printStackTrace()
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