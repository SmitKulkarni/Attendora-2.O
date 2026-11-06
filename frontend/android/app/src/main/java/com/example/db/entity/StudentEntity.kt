package com.example.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.AttendanceStatus

@Entity(tableName = "students")
data class StudentEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val rollNo: String,
    val className: String,
    val status: AttendanceStatus,
    val checkInTime: String,
    val avatarBgColor: Long,
    val department: String,
    val email: String,
    val overallPercentage: Float,
    val subjectsJson: String
)

