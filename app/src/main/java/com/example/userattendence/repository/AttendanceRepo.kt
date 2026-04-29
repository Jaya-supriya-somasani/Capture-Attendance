package com.example.userattendence.repository

import com.example.userattendence.attendance.AttendanceDetailsDao
import com.example.userattendence.attendance.AttendanceRecord
import com.example.userattendence.utils.LocationData
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AttendanceRepository @Inject constructor(
    private val dao: AttendanceDetailsDao
) {
    fun getAllRecords(): Flow<List<AttendanceRecord>> = dao.getAllRecords()

    suspend fun saveAttendance(imageUri: String, locationData: LocationData) {
        dao.insertRecord(
            AttendanceRecord(
                imageUri = imageUri,
                latitude = locationData.latitude,
                longitude = locationData.longitude,
                address = locationData.address,
                capturedAt = SimpleDateFormat(
                    "dd MMM yyyy, hh:mm a",
                    Locale.getDefault()
                ).format(Date())
            )
        )
    }
}