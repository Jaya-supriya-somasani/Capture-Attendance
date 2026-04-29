package com.example.userattendence.attendance

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDetailsDao {
    @Insert
    suspend fun insertRecord(record: AttendanceRecord)

    @Query("SELECT * FROM attendance_records ORDER BY id DESC")
    fun getAllRecords(): Flow<List<AttendanceRecord>>

}