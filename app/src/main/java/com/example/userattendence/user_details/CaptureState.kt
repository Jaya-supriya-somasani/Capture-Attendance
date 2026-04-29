package com.example.userattendence.user_details

import android.net.Uri

data class CaptureState(
    val imageUri: Uri? = null,
    val address: String = "",
    val isLoading: Boolean = false,
    val filePath: String = "",
)