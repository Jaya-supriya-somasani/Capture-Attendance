package com.example.userattendence.attendance

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [AttendanceRecord::class], version = 1)
abstract class AttendanceDatabase : RoomDatabase() {

    abstract fun attendanceDao(): AttendanceDetailsDao

    companion object {
        @Volatile
        private var INSTANCE: AttendanceDatabase? = null

        fun getDatabase(context: Context): AttendanceDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AttendanceDatabase::class.java,
                    "attendance_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}