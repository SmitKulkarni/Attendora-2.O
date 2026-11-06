package com.example.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.db.entity.SyncQueueEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SyncQueueDao {

    @Query("SELECT * FROM sync_queue WHERE status = 'PENDING' ORDER BY createdAt ASC")
    fun getPendingSyncItemsFlow(): Flow<List<SyncQueueEntity>>

    @Query("SELECT * FROM sync_queue WHERE status = 'PENDING' ORDER BY createdAt ASC")
    suspend fun getPendingSyncItems(): List<SyncQueueEntity>

    @Query("SELECT COUNT(*) FROM sync_queue WHERE status = 'PENDING'")
    fun getPendingCountFlow(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSyncItem(item: SyncQueueEntity): Long

    @Update
    suspend fun updateSyncItem(item: SyncQueueEntity)

    @Query("DELETE FROM sync_queue WHERE id = :id")
    suspend fun deleteSyncItem(id: Long)

    @Query("UPDATE sync_queue SET status = 'SYNCED' WHERE id = :id")
    suspend fun markSynced(id: Long)

    @Query("UPDATE sync_queue SET retryCount = retryCount + 1, status = CASE WHEN retryCount >= 3 THEN 'FAILED' ELSE 'PENDING' END WHERE id = :id")
    suspend fun incrementRetry(id: Long)

    @Query("DELETE FROM sync_queue WHERE status = 'SYNCED'")
    suspend fun clearSyncedItems()

    @Query("DELETE FROM sync_queue")
    suspend fun clearAll()
}

