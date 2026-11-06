package com.example.data.repository

import com.example.data.MockDataProvider
import com.example.db.AttendoraDatabase
import com.example.db.entity.AttendanceRecordEntity
import com.example.db.entity.AttendanceSessionEntity
import com.example.db.entity.SyncQueueEntity
import com.example.db.mapper.EntityMappers.toDomain
import com.example.db.mapper.EntityMappers.toEntity
import com.example.model.AttendanceStatus
import com.example.model.ClassGroup
import com.example.model.Student
import com.example.model.SubjectAttendance
import com.example.network.AttendoraApiService
import com.example.network.dto.AttendanceRecordItemDto
import com.example.network.dto.AttendanceSubmissionRequestDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AttendanceRepository(
    private val database: AttendoraDatabase,
    private val apiService: AttendoraApiService? = null
) {
    private val studentDao = database.studentDao()
    private val classDao = database.classDao()
    private val attendanceDao = database.attendanceDao()
    private val syncQueueDao = database.syncQueueDao()

    val studentsFlow: Flow<List<Student>> = studentDao.getStudentsFlow()
        .map { entities ->
            if (entities.isEmpty()) {
                // Return mock data until database is pre-populated
                MockDataProvider.getInitialStudents()
            } else {
                entities.map { it.toDomain() }
            }
        }
        .flowOn(Dispatchers.IO)

    val classesFlow: Flow<List<ClassGroup>> = classDao.getClassesFlow()
        .map { entities ->
            if (entities.isEmpty()) {
                MockDataProvider.getInitialClasses()
            } else {
                entities.map { it.toDomain() }
            }
        }
        .flowOn(Dispatchers.IO)

    val pendingSyncCountFlow: Flow<Int> = syncQueueDao.getPendingCountFlow()
        .flowOn(Dispatchers.IO)

    suspend fun ensureInitialized() = withContext(Dispatchers.IO) {
        AttendoraDatabase.populateInitialDatabase(database)
    }

    suspend fun toggleStudentAttendance(studentId: String) = withContext(Dispatchers.IO) {
        val studentEntity = studentDao.getStudentById(studentId)
        val currentTime = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())

        if (studentEntity != null) {
            val nextStatus = when (studentEntity.status) {
                AttendanceStatus.PRESENT -> AttendanceStatus.ABSENT
                AttendanceStatus.ABSENT -> AttendanceStatus.LATE
                AttendanceStatus.LATE -> AttendanceStatus.PRESENT
            }
            val checkIn = if (nextStatus == AttendanceStatus.ABSENT) "-" else currentTime
            studentDao.updateStudentStatus(studentId, nextStatus, checkIn)

            // Enqueue sync mutation
            val payload = JSONObject().apply {
                put("studentId", studentId)
                put("status", nextStatus.name)
                put("checkInTime", checkIn)
            }.toString()

            syncQueueDao.insertSyncItem(
                SyncQueueEntity(
                    entityType = "STUDENT",
                    entityId = studentId,
                    action = "UPDATE",
                    payloadJson = payload
                )
            )
        }
    }

    suspend fun setStudentAttendance(studentId: String, status: AttendanceStatus) = withContext(Dispatchers.IO) {
        val currentTime = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
        val checkIn = if (status == AttendanceStatus.ABSENT) "-" else currentTime

        studentDao.updateStudentStatus(studentId, status, checkIn)

        val payload = JSONObject().apply {
            put("studentId", studentId)
            put("status", status.name)
            put("checkInTime", checkIn)
        }.toString()

        syncQueueDao.insertSyncItem(
            SyncQueueEntity(
                entityType = "STUDENT",
                entityId = studentId,
                action = "UPDATE",
                payloadJson = payload
            )
        )
    }

    suspend fun markAllInClass(className: String, status: AttendanceStatus) = withContext(Dispatchers.IO) {
        val currentTime = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
        val checkIn = if (status == AttendanceStatus.ABSENT) "-" else currentTime

        if (className == "All") {
            studentDao.markAll(status, checkIn)
        } else {
            studentDao.markAllInClass(className, status, checkIn)
        }

        val payload = JSONObject().apply {
            put("className", className)
            put("status", status.name)
            put("checkInTime", checkIn)
        }.toString()

        syncQueueDao.insertSyncItem(
            SyncQueueEntity(
                entityType = "STUDENT_BULK",
                entityId = className,
                action = "UPDATE",
                payloadJson = payload
            )
        )
    }

    suspend fun addNewStudent(
        name: String,
        rollNo: String,
        className: String,
        department: String,
        email: String
    ): Student = withContext(Dispatchers.IO) {
        val colors = listOf(0xFF3B82F6, 0xFF10B981, 0xFF8B5CF6, 0xFFEC4899, 0xFFF59E0B, 0xFF06B6D4)
        val defaultSubjects = listOf(
            SubjectAttendance("Core Foundations", "CS-101", 36, 40, 90.0f),
            SubjectAttendance("Programming Lab", "CS-102", 34, 40, 85.0f),
            SubjectAttendance("Applied Mathematics", "MA-101", 38, 40, 95.0f)
        )
        val student = Student(
            id = "s_${System.currentTimeMillis()}",
            name = name.ifBlank { "New Student" },
            rollNo = rollNo.ifBlank { "2026${(100..999).random()}" },
            className = className,
            status = AttendanceStatus.PRESENT,
            checkInTime = "6:30 PM",
            avatarBgColor = colors.random(),
            department = department.ifBlank { "Computer Science" },
            email = email.ifBlank { "student@attendora.edu" },
            overallPercentage = 90.0f,
            subjects = defaultSubjects
        )

        studentDao.insertStudent(student.toEntity())

        val payload = JSONObject().apply {
            put("id", student.id)
            put("name", student.name)
            put("rollNo", student.rollNo)
            put("className", student.className)
            put("department", student.department)
            put("email", student.email)
        }.toString()

        syncQueueDao.insertSyncItem(
            SyncQueueEntity(
                entityType = "STUDENT",
                entityId = student.id,
                action = "INSERT",
                payloadJson = payload
            )
        )

        student
    }

    suspend fun addNewClass(
        name: String,
        code: String,
        department: String,
        teacherName: String,
        courseName: String = "",
        academicTerm: String = "Fall 2026",
        enrolledCount: Int = 45,
        scheduleTime: String = "10:00 AM - 11:30 AM",
        roomLocation: String = "Lecture Hall 101"
    ): ClassGroup = withContext(Dispatchers.IO) {
        val classGroup = ClassGroup(
            id = "c_${System.currentTimeMillis()}",
            name = name,
            code = code,
            courseName = courseName.ifBlank { name },
            academicTerm = academicTerm,
            department = department,
            teacherName = teacherName,
            enrolledCount = enrolledCount,
            scheduleTime = scheduleTime,
            roomLocation = roomLocation
        )

        classDao.insertClass(classGroup.toEntity())

        val payload = JSONObject().apply {
            put("id", classGroup.id)
            put("name", classGroup.name)
            put("code", classGroup.code)
            put("department", classGroup.department)
            put("teacherName", classGroup.teacherName)
        }.toString()

        syncQueueDao.insertSyncItem(
            SyncQueueEntity(
                entityType = "CLASS",
                entityId = classGroup.id,
                action = "INSERT",
                payloadJson = payload
            )
        )

        classGroup
    }

    suspend fun submitAttendanceSession(
        className: String,
        students: List<Student>
    ): AttendanceSessionEntity = withContext(Dispatchers.IO) {
        val sessionId = "att_${System.currentTimeMillis()}"
        val sessionDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val sessionTime = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())

        val sessionEntity = AttendanceSessionEntity(
            id = sessionId,
            classId = className,
            className = className,
            sessionDate = sessionDate,
            sessionTime = sessionTime,
            markedBy = "Faculty In-Charge",
            isLocked = false,
            timestamp = System.currentTimeMillis()
        )

        val records = students
            .filter { className == "All" || it.className == className }
            .map { student ->
                AttendanceRecordEntity(
                    id = "rec_${sessionId}_${student.id}",
                    sessionId = sessionId,
                    studentId = student.id,
                    status = student.status,
                    checkInTime = student.checkInTime,
                    markedAt = System.currentTimeMillis()
                )
            }

        attendanceDao.submitFullSession(sessionEntity, records)

        val payload = JSONObject().apply {
            put("sessionId", sessionId)
            put("className", className)
            put("recordsCount", records.size)
        }.toString()

        syncQueueDao.insertSyncItem(
            SyncQueueEntity(
                entityType = "ATTENDANCE_SESSION",
                entityId = sessionId,
                action = "INSERT",
                payloadJson = payload
            )
        )

        sessionEntity
    }

    suspend fun processSyncQueue(): Int = withContext(Dispatchers.IO) {
        val pendingItems = syncQueueDao.getPendingSyncItems()
        var syncedCount = 0
        for (item in pendingItems) {
            try {
                if (apiService != null && item.entityType == "ATTENDANCE_SESSION") {
                    val jsonObj = JSONObject(item.payloadJson)
                    val className = jsonObj.optString("className", "Class 1")
                    val records = studentDao.getAllStudents().map { s ->
                        AttendanceRecordItemDto(
                            id = s.id,
                            status = s.status.name,
                            checkInTime = s.checkInTime
                        )
                    }
                    val request = AttendanceSubmissionRequestDto(
                        classId = className,
                        sessionDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                        records = records
                    )
                    val response = apiService.submitAttendance(request)
                    if (response.isSuccessful) {
                        syncQueueDao.markSynced(item.id)
                        syncedCount++
                    } else {
                        syncQueueDao.incrementRetry(item.id)
                    }
                } else {
                    syncQueueDao.markSynced(item.id)
                    syncedCount++
                }
            } catch (e: Exception) {
                // Retain in offline queue and increment retry counter
                syncQueueDao.incrementRetry(item.id)
            }
        }
        syncedCount
    }

    companion object {
        @Volatile
        private var INSTANCE: AttendanceRepository? = null

        fun getInstance(
            database: AttendoraDatabase,
            apiService: AttendoraApiService? = null
        ): AttendanceRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = AttendanceRepository(database, apiService)
                INSTANCE = instance
                instance
            }
        }
    }
}

