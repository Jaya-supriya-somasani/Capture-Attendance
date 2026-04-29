package com.example.userattendence.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "attendance_records")
data class AttendanceRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val imageUri: String,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val capturedAt: String
)

