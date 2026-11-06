package com.example.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sync_queue")
data class SyncQueueEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val entityType: String, // "STUDENT", "CLASS", "ATTENDANCE_SESSION"
    val entityId: String,
    val action: String, // "INSERT", "UPDATE", "DELETE"
    val payloadJson: String,
    val createdAt: Long = System.currentTimeMillis(),
    val retryCount: Int = 0,
    val status: String = "PENDING" // "PENDING", "SYNCED", "FAILED"
)

