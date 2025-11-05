package com.example.dzandroid.presentation

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dzandroid.domain.GetProfileUseCase
import com.example.dzandroid.domain.SaveProfileUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ProfileViewModel(
    private val getProfileUseCase: GetProfileUseCase,
    private val saveProfileUseCase: SaveProfileUseCase
) : ViewModel() {

    private val _profileState = MutableStateFlow(com.example.dzandroid.data.models.Profile())
    val profileState: StateFlow<com.example.dzandroid.data.models.Profile> = _profileState.asStateFlow()

    private val _avatarUri = MutableStateFlow<Uri?>(null)
    val avatarUri: StateFlow<Uri?> = _avatarUri.asStateFlow()

    private val _downloadStatus = MutableStateFlow<String?>(null)
    val downloadStatus: StateFlow<String?> = _downloadStatus.asStateFlow()

    init {
        loadProfile()
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

    fun updateAvatar(uri: Uri) {
        _avatarUri.value = uri
    }

    fun saveProfile(
        fullName: String,
        resumeUrl: String,
        position: String = "",
        email: String = ""
    ) {
        viewModelScope.launch {
            val avatarUriString = _avatarUri.value?.toString() ?: ""
            val profile = com.example.dzandroid.data.models.Profile(
                fullName = fullName,
                avatarUri = avatarUriString,
                resumeUrl = resumeUrl,
                position = position,
                email = email
            )
            saveProfileUseCase(profile)
            _profileState.value = profile
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

                val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val fileName = "resume_$timestamp.pdf"

                val request = DownloadManager.Request(Uri.parse(url))
                    .setTitle("Резюме - ${_profileState.value.fullName}")
                    .setDescription("Скачивание резюме")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)

                val downloadId = downloadManager.enqueue(request)
                _downloadStatus.value = "Резюме скачивается..."

                launch {
                    kotlinx.coroutines.delay(3000)
                    _downloadStatus.value = null
                }

            } catch (e: Exception) {
                _downloadStatus.value = "Ошибка скачивания: ${e.message}"
                e.printStackTrace()
            }
        }
    }

    fun clearDownloadStatus() {
        _downloadStatus.value = null
    }
}