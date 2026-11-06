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

// ============================================================================
// Phase 6: Gemini AI Predictive Intelligence & Enterprise Multi-Campus
// ============================================================================

enum class RiskLevel(val label: String, val colorHex: Long) {
    LOW("Low Risk", 0xFF10B981),
    MODERATE("Moderate Risk", 0xFFF59E0B),
    HIGH("High Risk", 0xFFF97316),
    CRITICAL("Critical Risk", 0xFFEF4444)
}

data class AiRiskAssessment(
    val riskLevel: RiskLevel,
    val riskScore: Float,
    val predictedFinalPercentage: Float,
    val confidenceScore: Float = 0.94f,
    val riskFactors: List<String> = emptyList(),
    val recommendations: List<String> = emptyList(),
    val aiSummary: String
)

data class AiInterventionPlan(
    val studentId: String,
    val studentName: String,
    val rollNo: String,
    val riskLevel: RiskLevel,
    val riskScore: Float,
    val currentAttendancePercent: Float,
    val targetAttendancePercent: Float,
    val classesNeededForEligibility: Int,
    val urgency: String,
    val actionItems: List<String>,
    val counselingLetter: String,
    val generatedAt: String
)

data class CampusDepartment(
    val name: String,
    val students: Int,
    val attendance: Float
)

data class CampusDetail(
    val id: String,
    val name: String,
    val code: String,
    val city: String,
    val state: String,
    val dean: String,
    val enrolledStudents: Int,
    val attendanceRate: Float,
    val defaulterCount: Int,
    val defaulterRate: Float,
    val healthScore: Float,
    val departments: List<CampusDepartment> = emptyList()
)

data class EnterpriseCampusesSummary(
    val systemName: String,
    val totalCampuses: Int,
    val totalEnrolledStudents: Int,
    val systemAverageAttendance: Float,
    val totalDefaulters: Int,
    val systemDefaulterRate: Float,
    val topPerformingCampus: String,
    val campuses: List<CampusDetail> = emptyList(),
    val aiStrategicBrief: String = ""
)
