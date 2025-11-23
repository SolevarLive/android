package com.example.dzandroid.presentation.screens

import android.Manifest
import android.app.Activity
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import com.example.dzandroid.R
import com.example.dzandroid.presentation.ProfileViewModel
import com.example.dzandroid.presentation.receivers.ClassReminderReceiver
import com.example.dzandroid.presentation.rememberImageService
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    viewModel: ProfileViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val imageService = rememberImageService()

    val profile by viewModel.profileState.collectAsState()
    val avatarUri by viewModel.avatarUri.collectAsState()
    val savedTime by viewModel.favoriteClassTime.collectAsState()

    var fullName by remember { mutableStateOf(profile.fullName) }
    var resumeUrl by remember { mutableStateOf(profile.resumeUrl) }
    var position by remember { mutableStateOf(profile.position) }
    var email by remember { mutableStateOf(profile.email) }
    var favoriteClassTimeInput by remember { mutableStateOf(savedTime) }
    var isTimeError by remember { mutableStateOf(false) }

    LaunchedEffect(favoriteClassTimeInput) {
        isTimeError = favoriteClassTimeInput.isNotEmpty() && !viewModel.validateTimeFormat(favoriteClassTimeInput)
    }

    var showImageSourceDialog by remember { mutableStateOf(false) }
    var showPermissionDeniedDialog by remember { mutableStateOf(false) }
    var cameraImageUri by remember { mutableStateOf<Uri?>(null) }

    val pickMediaLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let { viewModel.updateAvatar(it) }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            cameraImageUri?.let { uri -> viewModel.updateAvatar(uri) }
        }
        cameraImageUri = null
    }

    val android13PermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) showImageSourceDialog = true
        else showPermissionDeniedDialog = true
    }

    val legacyPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.all { it }) showImageSourceDialog = true
        else showPermissionDeniedDialog = true
    }

    fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasMediaPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
            if (hasMediaPermission) showImageSourceDialog = true
            else android13PermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val perms = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
            if (perms.all { ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED }) {
                showImageSourceDialog = true
            } else {
                legacyPermissionLauncher.launch(perms)
            }
        } else {
            showImageSourceDialog = true
        }
    }

    fun openCamera() {
        try {
            val uri = imageService.getCameraUri()
            if (uri != null) {
                cameraImageUri = uri
                cameraLauncher.launch(uri)
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Ошибка открытия камеры", Toast.LENGTH_SHORT).show()
        }
    }

    fun openGallery() {
        pickMediaLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    fun openTimePicker() {
        val activity = context as? Activity ?: return
        val now = Calendar.getInstance()
        TimePickerDialog(
            activity,
            { _, hour, minute ->
                favoriteClassTimeInput = "%02d:%02d".format(hour, minute)
            },
            now[Calendar.HOUR_OF_DAY],
            now[Calendar.MINUTE],
            true
        ).show()
    }

    fun saveAndExit() {
        viewModel.saveProfile(
            fullName = fullName,
            resumeUrl = resumeUrl,
            position = position,
            email = email,
            favoriteClassTime = favoriteClassTimeInput
        )

        if (viewModel.validateTimeFormat(favoriteClassTimeInput)) {
            viewModel.setClassReminder(favoriteClassTimeInput)
        } else {
            viewModel.cancelClassReminder()
        }

        onBackClick()
    }

    val canSave = fullName.isNotBlank() && !isTimeError

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Редактирование профиля") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(
                        enabled = canSave,
                        onClick = { saveAndExit() }
                    ) {
                        Icon(Icons.Default.Done, contentDescription = "Готово")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clickable { checkPermissions() }
            ) {
                val painter = avatarUri?.let { rememberAsyncImagePainter(it) }
                    ?: painterResource(R.drawable.ic_profile_placeholder)

                Image(
                    painter = painter,
                    contentDescription = "Аватар",
                    modifier = Modifier.size(150.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier.matchParentSize().clip(CircleShape)
                ) {
                    Text(
                        text = "Изменить фото",
                        modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 8.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(Modifier.padding(24.dp)) {
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("ФИО") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = resumeUrl,
                        onValueChange = { resumeUrl = it },
                        label = { Text("Ссылка на резюме") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = position,
                        onValueChange = { position = it },
                        label = { Text("Должность") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = favoriteClassTimeInput,
                        onValueChange = { input ->
                            favoriteClassTimeInput = input
                        },
                        label = { Text("Время любимой пары (HH:mm)") },
                        placeholder = { Text("Например: 13:40") },
                        trailingIcon = {
                            IconButton(onClick = { openTimePicker() }) {
                                Icon(Icons.Default.AccessTime, contentDescription = "Выбрать время")
                            }
                        },
                        isError = isTimeError,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (isTimeError) {
                        Text(
                            text = "Неверный формат времени. Должно быть HH:mm (например: 14:30)",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { saveAndExit() },
                enabled = canSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text("Сохранить изменения")
            }

            Button(
                onClick = {
                    val intent = Intent(context, ClassReminderReceiver::class.java)
                    context.sendBroadcast(intent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text("Тест уведомления")
            }
        }
    }

    if (showImageSourceDialog) {
        Dialog(onDismissRequest = { showImageSourceDialog = false }) {
            Surface(modifier = Modifier.fillMaxWidth(0.8f), shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text("Выберите источник", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 16.dp))
                    Text("Камера", modifier = Modifier.fillMaxWidth().clickable { openCamera(); showImageSourceDialog = false }.padding(vertical = 12.dp))
                    Divider()
                    Text("Галерея", modifier = Modifier.fillMaxWidth().clickable { openGallery(); showImageSourceDialog = false }.padding(vertical = 12.dp))
                }
            }
        }
    }

    if (showPermissionDeniedDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDeniedDialog = false },
            title = { Text("Доступ запрещён") },
            text = { Text("Без разрешений нельзя выбрать фото") },
            confirmButton = {
                TextButton(onClick = { showPermissionDeniedDialog = false }) { Text("OK") }
            }
        )
    }
}