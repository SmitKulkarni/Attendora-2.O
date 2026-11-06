package com.example.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.db.entity.StudentEntity
import com.example.model.AttendanceStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {

    @Query("SELECT * FROM students ORDER BY rollNo ASC")
    fun getStudentsFlow(): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students ORDER BY rollNo ASC")
    suspend fun getAllStudents(): List<StudentEntity>

    @Query("SELECT * FROM students WHERE id = :id")
    suspend fun getStudentById(id: String): StudentEntity?

    @Query("SELECT * FROM students WHERE className = :className ORDER BY rollNo ASC")
    fun getStudentsByClassFlow(className: String): Flow<List<StudentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudents(students: List<StudentEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: StudentEntity)

    @Update
    suspend fun updateStudent(student: StudentEntity)

    @Query("UPDATE students SET status = :status, checkInTime = :checkInTime WHERE id = :studentId")
    suspend fun updateStudentStatus(studentId: String, status: AttendanceStatus, checkInTime: String)

    @Query("UPDATE students SET status = :status, checkInTime = :checkInTime WHERE className = :className")
    suspend fun markAllInClass(className: String, status: AttendanceStatus, checkInTime: String)

    @Query("UPDATE students SET status = :status, checkInTime = :checkInTime")
    suspend fun markAll(status: AttendanceStatus, checkInTime: String)

    @Query("SELECT COUNT(*) FROM students")
    suspend fun getCount(): Int

    @Query("DELETE FROM students WHERE id = :id")
    suspend fun deleteStudentById(id: String)
}

