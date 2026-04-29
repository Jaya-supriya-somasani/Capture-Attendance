package com.example.userattendence.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.userattendence.data.local.dao.AttendanceDetailsDao
import com.example.userattendence.data.local.entity.AttendanceRecord

@Database(entities = [AttendanceRecord::class], version = 1)
abstract class AttendanceDatabase : RoomDatabase() {

    abstract fun attendanceDao(): AttendanceDetailsDao
}