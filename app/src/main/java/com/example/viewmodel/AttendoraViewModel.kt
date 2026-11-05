package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.MockDataProvider
import com.example.model.AttendanceDayLog
import com.example.model.AttendanceNotice
import com.example.model.AttendanceStatus
import com.example.model.AttendanceSummaryMetrics
import com.example.model.ClassGroup
import com.example.model.NavTab
import com.example.model.Student
import com.example.model.TrendPoint
import com.example.model.UserRole
import com.example.model.WeeklyBarData
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AttendoraViewModel : ViewModel() {

    private val _currentRole = MutableStateFlow(UserRole.ADMIN)
    val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

    private val _selectedTab = MutableStateFlow(NavTab.DASHBOARD)
    val selectedTab: StateFlow<NavTab> = _selectedTab.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedClassFilter = MutableStateFlow("All")
    val selectedClassFilter: StateFlow<String> = _selectedClassFilter.asStateFlow()

    private val _students = MutableStateFlow(MockDataProvider.getInitialStudents())
    val students: StateFlow<List<Student>> = _students.asStateFlow()

    private val _classes = MutableStateFlow(MockDataProvider.getInitialClasses())
    val classes: StateFlow<List<ClassGroup>> = _classes.asStateFlow()

    private val _notices = MutableStateFlow(MockDataProvider.getInitialNotices())
    val notices: StateFlow<List<AttendanceNotice>> = _notices.asStateFlow()

    private val _trendPoints = MutableStateFlow(MockDataProvider.getTrendPoints())
    val trendPoints: StateFlow<List<TrendPoint>> = _trendPoints.asStateFlow()

    private val _weeklyBars = MutableStateFlow(MockDataProvider.getWeeklyBars())
    val weeklyBars: StateFlow<List<WeeklyBarData>> = _weeklyBars.asStateFlow()

    private val _studentDayLogs = MutableStateFlow(MockDataProvider.getStudentDayLogs())
    val studentDayLogs: StateFlow<List<AttendanceDayLog>> = _studentDayLogs.asStateFlow()

    private val _selectedStudentId = MutableStateFlow("s_1") // Smit Kulkarni
    val selectedStudentId: StateFlow<String> = _selectedStudentId.asStateFlow()

    private val _activeStudentDetail = MutableStateFlow<Student?>(null)
    val activeStudentDetail: StateFlow<Student?> = _activeStudentDetail.asStateFlow()

    private val _alertThreshold = MutableStateFlow(75)
    val alertThreshold: StateFlow<Int> = _alertThreshold.asStateFlow()

    private val _isCloudSynced = MutableStateFlow(true)
    val isCloudSynced: StateFlow<Boolean> = _isCloudSynced.asStateFlow()

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent: SharedFlow<String> = _toastEvent.asSharedFlow()

    fun switchRole(role: UserRole) {
        _currentRole.value = role
    }

    fun switchTab(tab: NavTab) {
        _selectedTab.value = tab
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setClassFilter(className: String) {
        _selectedClassFilter.value = className
    }

    fun setThreshold(threshold: Int) {
        _alertThreshold.value = threshold
    }

    fun selectStudentForDetail(student: Student?) {
        _activeStudentDetail.value = student
    }

    fun selectActiveStudentUser(studentId: String) {
        _selectedStudentId.value = studentId
    }

    fun toggleStudentAttendance(studentId: String) {
        val currentTime = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
        _students.value = _students.value.map { student ->
            if (student.id == studentId) {
                val nextStatus = when (student.status) {
                    AttendanceStatus.PRESENT -> AttendanceStatus.ABSENT
                    AttendanceStatus.ABSENT -> AttendanceStatus.LATE
                    AttendanceStatus.LATE -> AttendanceStatus.PRESENT
                }
                val checkIn = if (nextStatus == AttendanceStatus.ABSENT) "-" else currentTime
                student.copy(status = nextStatus, checkInTime = checkIn)
            } else student
        }
    }

    fun setStudentAttendance(studentId: String, status: AttendanceStatus) {
        val currentTime = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
        _students.value = _students.value.map { student ->
            if (student.id == studentId) {
                val checkIn = if (status == AttendanceStatus.ABSENT) "-" else currentTime
                student.copy(status = status, checkInTime = checkIn)
            } else student
        }
    }

    fun markAllInClass(className: String, status: AttendanceStatus) {
        val currentTime = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
        _students.value = _students.value.map { student ->
            if (className == "All" || student.className == className) {
                val checkIn = if (status == AttendanceStatus.ABSENT) "-" else currentTime
                student.copy(status = status, checkInTime = checkIn)
            } else student
        }
        viewModelScope.launch {
            _toastEvent.emit("Marked all in $className as ${status.label}")
        }
    }

    fun submitAttendanceSession(className: String) {
        viewModelScope.launch {
            _isCloudSynced.value = true
            val notice = AttendanceNotice(
                id = "n_${System.currentTimeMillis()}",
                title = "Attendance Submitted - $className",
                message = "Session locked and synced. Editable for 48 hours by faculty.",
                timeAgo = "Just now",
                isWarning = false
            )
            _notices.value = listOf(notice) + _notices.value
            _toastEvent.emit("Attendance successfully submitted and synced to Cloud!")
        }
    }

    fun addNewStudent(name: String, rollNo: String, className: String, department: String, email: String) {
        val colors = listOf(0xFF3B82F6, 0xFF10B981, 0xFF8B5CF6, 0xFFEC4899, 0xFFF59E0B, 0xFF06B6D4)
        val randomColor = colors.random()
        val defaultSubjects = listOf(
            com.example.model.SubjectAttendance("Core Foundations", "CS-101", 36, 40, 90.0f),
            com.example.model.SubjectAttendance("Programming Lab", "CS-102", 34, 40, 85.0f),
            com.example.model.SubjectAttendance("Applied Mathematics", "MA-101", 38, 40, 95.0f)
        )
        val newStudent = Student(
            id = "s_${System.currentTimeMillis()}",
            name = name.ifBlank { "New Student" },
            rollNo = rollNo.ifBlank { "2026${(100..999).random()}" },
            className = className,
            status = AttendanceStatus.PRESENT,
            checkInTime = "6:30 PM",
            avatarBgColor = randomColor,
            department = department.ifBlank { "Computer Science" },
            email = email.ifBlank { "student@attendora.edu" },
            overallPercentage = 90.0f,
            subjects = defaultSubjects
        )
        _students.value = listOf(newStudent) + _students.value
        viewModelScope.launch {
            _toastEvent.emit("Added ${newStudent.name} ($rollNo) to $className")
        }
    }

    fun addNewClass(
        name: String,
        code: String,
        department: String,
        teacherName: String,
        courseName: String = "",
        academicTerm: String = "Fall 2026",
        enrolledCount: Int = 45,
        scheduleTime: String = "10:00 AM - 11:30 AM",
        roomLocation: String = "Lecture Hall 101"
    ) {
        val newClass = ClassGroup(
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
        _classes.value = _classes.value + newClass
        viewModelScope.launch {
            _toastEvent.emit("Created new class: $name ($code) for $academicTerm")
        }
    }

    fun exportDefaulterReport(format: String) {
        viewModelScope.launch {
            val count = _students.value.count { it.overallPercentage < _alertThreshold.value }
            _toastEvent.emit("Generated $format Report: $count at-risk students below ${_alertThreshold.value}%")
        }
    }

    fun sendDefaulterAlerts() {
        viewModelScope.launch {
            val count = _students.value.count { it.overallPercentage < _alertThreshold.value }
            val notice = AttendanceNotice(
                id = "n_${System.currentTimeMillis()}",
                title = "Automated Defaulter Warnings Dispatched",
                message = "Sent low-attendance email alerts to $count students (< ${_alertThreshold.value}%).",
                timeAgo = "Just now",
                isWarning = true
            )
            _notices.value = listOf(notice) + _notices.value
            _toastEvent.emit("Automated alerts dispatched to $count students!")
        }
    }

    fun dismissNotice(id: String) {
        _notices.value = _notices.value.filter { it.id != id }
    }

    fun getFilteredStudents(): List<Student> {
        val query = _searchQuery.value.trim().lowercase()
        val classFilter = _selectedClassFilter.value

        return _students.value.filter { student ->
            val matchesClass = classFilter == "All" || student.className == classFilter
            val matchesQuery = query.isEmpty() ||
                    student.name.lowercase().contains(query) ||
                    student.rollNo.lowercase().contains(query) ||
                    student.department.lowercase().contains(query)
            matchesClass && matchesQuery
        }
    }

    fun getSummaryMetrics(): AttendanceSummaryMetrics {
        val total = 120 // Fixed baseline benchmark as per dashboard mockup
        val currentStudents = _students.value
        val presentCount = currentStudents.count { it.status == AttendanceStatus.PRESENT }
        val lateCount = currentStudents.count { it.status == AttendanceStatus.LATE }
        val absentCount = currentStudents.count { it.status == AttendanceStatus.ABSENT }

        val todayPercent = if (currentStudents.isNotEmpty()) {
            (((presentCount + lateCount).toFloat() / currentStudents.size) * 100).toInt()
        } else 94

        val onTimePercent = if (currentStudents.isNotEmpty()) {
            ((presentCount.toFloat() / currentStudents.size) * 100).toInt()
        } else 88

        return AttendanceSummaryMetrics(
            totalStudents = total,
            todayAttendancePercent = todayPercent,
            onTimePercent = onTimePercent,
            absentCount = absentCount
        )
    }

    fun getSelectedStudent(): Student {
        return _students.value.find { it.id == _selectedStudentId.value }
            ?: _students.value.first()
    }
}
