package com.example.userattendence.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.userattendence.data.local.entity.AttendanceRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDetailsDao {
    @Insert
    suspend fun insertRecord(record: AttendanceRecord)

    @Query("SELECT * FROM attendance_records ORDER BY id DESC")
    fun getAllRecords(): Flow<List<AttendanceRecord>>

    @Query("DELETE FROM attendance_records")
    suspend fun clearAllRecords()

}