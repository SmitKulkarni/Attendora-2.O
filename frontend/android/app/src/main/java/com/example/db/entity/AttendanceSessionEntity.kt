package com.example.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "attendance_sessions")
data class AttendanceSessionEntity(
    @PrimaryKey
    val id: String,
    val classId: String,
    val className: String,
    val sessionDate: String,
    val sessionTime: String,
    val markedBy: String,
    val isLocked: Boolean,
    val timestamp: Long
)

