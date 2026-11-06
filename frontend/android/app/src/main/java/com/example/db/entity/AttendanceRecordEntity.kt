package com.example.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.model.AttendanceStatus

@Entity(
    tableName = "attendance_records",
    indices = [
        Index(value = ["sessionId", "studentId"])
    ]
)
data class AttendanceRecordEntity(
    @PrimaryKey
    val id: String,
    val sessionId: String,
    val studentId: String,
    val status: AttendanceStatus,
    val checkInTime: String,
    val markedAt: Long
)

