package com.example.renai.instagram.screens.common

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import com.example.renai.instagram.data.UsersRepository
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class CameraHelper(private val activity: Activity) {
    var imageUri: Uri? = null

    companion object {
        const val REQUEST_CODE: Int = 13
    }

    private val simpleDateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)


    fun takeCameraPicture() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(activity.packageManager) != null) {
            val imageFile = createImageFile()
            imageUri = FileProvider.getUriForFile(
                activity,
                "com.example.renai.instagram.fileprovider",
                imageFile
            )
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            activity.startActivityForResult(intent, REQUEST_CODE)
        }
    }


    private fun createImageFile(): File {
        val storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${simpleDateFormat.format(Date())}_", ".jpg", storageDir)
    }
}