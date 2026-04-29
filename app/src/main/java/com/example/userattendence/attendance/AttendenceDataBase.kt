package com.example.userattendence.attendance

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [AttendanceRecord::class], version = 1)
abstract class AttendanceDatabase : RoomDatabase() {

    abstract fun attendanceDao(): AttendanceDetailsDao
}