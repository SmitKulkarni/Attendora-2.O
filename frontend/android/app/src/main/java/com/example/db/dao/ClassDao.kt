package com.example.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.db.entity.ClassGroupEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClassDao {

    @Query("SELECT * FROM classes ORDER BY name ASC")
    fun getClassesFlow(): Flow<List<ClassGroupEntity>>

    @Query("SELECT * FROM classes ORDER BY name ASC")
    suspend fun getAllClasses(): List<ClassGroupEntity>

    @Query("SELECT * FROM classes WHERE id = :id")
    suspend fun getClassById(id: String): ClassGroupEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClasses(classes: List<ClassGroupEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClass(classGroup: ClassGroupEntity)

    @Update
    suspend fun updateClass(classGroup: ClassGroupEntity)

    @Query("SELECT COUNT(*) FROM classes")
    suspend fun getCount(): Int

    @Query("DELETE FROM classes WHERE id = :id")
    suspend fun deleteClassById(id: String)
}

