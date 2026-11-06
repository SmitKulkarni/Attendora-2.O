package com.example.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.db.entity.AttendanceRecordEntity
import com.example.db.entity.AttendanceSessionEntity
import com.example.model.AttendanceStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {

    @Query("SELECT * FROM attendance_sessions ORDER BY timestamp DESC")
    fun getSessionsFlow(): Flow<List<AttendanceSessionEntity>>

    @Query("SELECT * FROM attendance_sessions WHERE id = :sessionId")
    suspend fun getSessionById(sessionId: String): AttendanceSessionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: AttendanceSessionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecords(records: List<AttendanceRecordEntity>)

    @Query("SELECT * FROM attendance_records WHERE sessionId = :sessionId")
    fun getRecordsForSessionFlow(sessionId: String): Flow<List<AttendanceRecordEntity>>

    @Query("SELECT * FROM attendance_records WHERE sessionId = :sessionId")
    suspend fun getRecordsForSession(sessionId: String): List<AttendanceRecordEntity>

    @Query("UPDATE attendance_records SET status = :status, checkInTime = :checkInTime WHERE id = :recordId")
    suspend fun updateRecordStatus(recordId: String, status: AttendanceStatus, checkInTime: String)

    @Query("UPDATE attendance_sessions SET isLocked = 1 WHERE id = :sessionId")
    suspend fun lockSession(sessionId: String)

    @Transaction
    suspend fun submitFullSession(session: AttendanceSessionEntity, records: List<AttendanceRecordEntity>) {
        insertSession(session)
        insertRecords(records)
    }
}

