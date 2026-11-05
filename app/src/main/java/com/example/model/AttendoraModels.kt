package com.example.model

enum class AttendanceStatus(val label: String) {
    PRESENT("Present"),
    ABSENT("Absent"),
    LATE("Late")
}

enum class UserRole(val displayName: String) {
    ADMIN("Administrator / Principal"),
    TEACHER("Faculty / Professor"),
    STUDENT("Student Portal")
}

enum class NavTab(val label: String) {
    DASHBOARD("Dashboard"),
    STUDENTS("Students"),
    ATTENDANCE("Attendance"),
    CLASSES("Classes"),
    REPORTS("Reports"),
    SETTINGS("Settings")
}

data class SubjectAttendance(
    val name: String,
    val code: String,
    val attended: Int,
    val total: Int,
    val percentage: Float
)

data class Student(
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
    val subjects: List<SubjectAttendance>
)

data class ClassGroup(
    val id: String,
    val name: String,
    val code: String,
    val department: String,
    val teacherName: String,
    val enrolledCount: Int,
    val scheduleTime: String,
    val courseName: String = "",
    val academicTerm: String = "Fall 2026",
    val roomLocation: String = "Hall A-101"
)

data class AttendanceSummaryMetrics(
    val totalStudents: Int = 120,
    val todayAttendancePercent: Int = 94,
    val onTimePercent: Int = 88,
    val absentCount: Int = 7
)

data class TrendPoint(
    val month: String,
    val value: Float // 0 to 120
)

data class WeeklyBarData(
    val day: String,
    val present: Int,
    val absent: Int
)

data class AttendanceNotice(
    val id: String,
    val title: String,
    val message: String,
    val timeAgo: String,
    val isRead: Boolean = false,
    val isWarning: Boolean = false
)

data class AttendanceDayLog(
    val date: String,
    val dayName: String,
    val status: AttendanceStatus,
    val checkInTime: String,
    val subjectTitle: String,
    val markedBy: String
)
