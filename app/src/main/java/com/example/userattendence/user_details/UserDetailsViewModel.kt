package com.example.userattendence.user_details

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.userattendence.attendance.AttendanceDatabase
import com.example.userattendence.attendance.AttendanceRecord
import com.example.userattendence.utils.LocationData
import com.example.userattendence.utils.getCurrentLocation
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UserDetailsViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AttendanceDatabase.getDatabase(application).attendanceDao()

    val records: StateFlow<List<AttendanceRecord>> = dao
        .getAllRecords()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun saveAttendance(
        context: Context,
        imageUri: String,
        locationData: LocationData
    ) {
        viewModelScope.launch {
            val dao = AttendanceDatabase.getDatabase(context).attendanceDao()
            val record = AttendanceRecord(
                imageUri = imageUri,
                latitude = locationData.latitude,
                longitude = locationData.longitude,
                address = locationData.address,
                capturedAt = SimpleDateFormat(
                    "dd MMM yyyy, hh:mm a",
                    Locale.getDefault()
                ).format(Date())
            )
            dao.insertRecord(record)
        }
    }

    fun getAllRecords(context: Context) =
        AttendanceDatabase.getDatabase(context)
            .attendanceDao()
            .getAllRecords()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
}