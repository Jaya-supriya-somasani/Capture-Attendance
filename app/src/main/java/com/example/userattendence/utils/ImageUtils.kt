package com.example.userattendence.utils

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun createImageFile(context: Context): Pair<Uri, String> {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFile = File(
        context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
        "IMG_$timeStamp.jpg"
    )
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        imageFile
    )
    return Pair(uri, imageFile.absolutePath)
}