package com.example.userattendence.di

import android.content.Context
import androidx.room.Room
import com.example.userattendence.attendance.AttendanceDatabase
import com.example.userattendence.attendance.AttendanceDetailsDao
import com.example.userattendence.repository.AttendanceRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val context: Context) {

    @Provides
    @Singleton
    fun provideContext(): Context = context

    @Provides
    @Singleton
    fun provideDatabase(context: Context): AttendanceDatabase {
        return Room.databaseBuilder(
            context,
            AttendanceDatabase::class.java,
            "attendance_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideDao(db: AttendanceDatabase): AttendanceDetailsDao =
        db.attendanceDao()

    @Provides
    @Singleton
    fun provideRepository(dao: AttendanceDetailsDao): AttendanceRepository =
        AttendanceRepository(dao)
}