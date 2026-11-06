package com.example.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "classes")
data class ClassGroupEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val code: String,
    val department: String,
    val teacherName: String,
    val enrolledCount: Int,
    val scheduleTime: String,
    val courseName: String,
    val academicTerm: String,
    val roomLocation: String
)

