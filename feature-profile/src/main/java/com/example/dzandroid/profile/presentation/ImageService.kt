package com.example.dzandroid.profile.presentation

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ImageService(private val context: Context) {

    private var currentPhotoPath: String? = null

    fun getCameraIntent(): Intent {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile = createImageFile()

        val photoURI: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            photoFile
        )
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        currentPhotoPath = photoFile.absolutePath
        return takePictureIntent
    }

    fun getCameraUri(): Uri? {
        val photoFile = createImageFile()
        return try {
            val photoURI: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                photoFile
            )
            currentPhotoPath = photoFile.absolutePath
            photoURI
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )
    }

    fun getPendingCameraImageUri(): Uri? {
        return currentPhotoPath?.let { path ->
            Uri.fromFile(File(path))
        }?.also {
            currentPhotoPath = null
        }
    }

    fun hasPermissions(permissions: Array<String>): Boolean {
        return permissions.all { permission ->
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }
    }
}

@Composable
fun rememberImageService(): ImageService {
    val context = LocalContext.current
    return remember { ImageService(context) }
}