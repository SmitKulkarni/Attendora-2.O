package com.example.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SubjectDto(
    val name: String,
    val code: String,
    val attended: Int,
    val total: Int,
    val percentage: Float
)

@JsonClass(generateAdapter = true)
data class StudentDto(
    val id: String,
    val name: String,
    @Json(name = "roll_no") val rollNo: String,
    @Json(name = "class_name") val className: String,
    val department: String,
    val email: String,
    val status: String,
    @Json(name = "check_in_time") val checkInTime: String,
    @Json(name = "overall_percentage") val overallPercentage: Float,
    val subjects: List<SubjectDto> = emptyList()
)

@JsonClass(generateAdapter = true)
data class StudentsListResponseDto(
    val total: Int,
    val students: List<StudentDto>
)

@JsonClass(generateAdapter = true)
data class ClassGroupDto(
    val id: String,
    val name: String,
    val code: String,
    @Json(name = "course_name") val courseName: String = "",
    @Json(name = "academic_term") val academicTerm: String = "Fall 2026",
    val department: String,
    @Json(name = "teacher_name") val teacherName: String,
    @Json(name = "enrolled_count") val enrolledCount: Int = 45,
    @Json(name = "schedule_time") val scheduleTime: String = "10:00 AM - 11:30 AM",
    @Json(name = "room_location") val roomLocation: String = "Lecture Hall 101"
)

@JsonClass(generateAdapter = true)
data class ClassesListResponseDto(
    val total: Int,
    val classes: List<ClassGroupDto>
)

@JsonClass(generateAdapter = true)
data class AttendanceRecordItemDto(
    val id: String,
    val status: String,
    @Json(name = "check_in_time") val checkInTime: String = "-"
)

@JsonClass(generateAdapter = true)
data class AttendanceSubmissionRequestDto(
    @Json(name = "class_id") val classId: String,
    @Json(name = "session_date") val sessionDate: String,
    val records: List<AttendanceRecordItemDto>
)

@JsonClass(generateAdapter = true)
data class AttendanceSubmissionResponseDto(
    val success: Boolean,
    @Json(name = "session_id") val sessionId: String,
    @Json(name = "class_id") val classId: String,
    @Json(name = "records_processed") val recordsProcessed: Int,
    val status: String,
    val message: String
)

@JsonClass(generateAdapter = true)
data class TrendPointDto(
    val month: String,
    val value: Float
)

@JsonClass(generateAdapter = true)
data class WeeklyDataDto(
    val day: String,
    val present: Int,
    val absent: Int
)

@JsonClass(generateAdapter = true)
data class AttendanceSummaryResponseDto(
    @Json(name = "total_students") val totalStudents: Int,
    @Json(name = "today_attendance_percent") val todayAttendancePercent: Int,
    @Json(name = "on_time_percent") val onTimePercent: Int,
    @Json(name = "absent_count") val absentCount: Int,
    val trends: List<TrendPointDto> = emptyList(),
    @Json(name = "weekly_data") val weeklyData: List<WeeklyDataDto> = emptyList()
)

@JsonClass(generateAdapter = true)
data class StudentDashboardResponseDto(
    @Json(name = "student_id") val studentId: String,
    val name: String,
    @Json(name = "roll_no") val rollNo: String,
    val department: String,
    @Json(name = "overall_percentage") val overallPercentage: Float,
    val status: String,
    val subjects: List<SubjectDto> = emptyList()
)

@JsonClass(generateAdapter = true)
data class QrLocationDto(
    val name: String = "Hall A-101",
    val latitude: Double = 18.52043,
    val longitude: Double = 73.85674,
    @Json(name = "allowed_radius_meters") val allowedRadiusMeters: Double = 50.0
)

@JsonClass(generateAdapter = true)
data class QrGenerateRequestDto(
    @Json(name = "class_id") val classId: String,
    @Json(name = "session_id") val sessionId: String? = null
)

@JsonClass(generateAdapter = true)
data class QrGenerateResponseDto(
    val success: Boolean,
    @Json(name = "class_id") val classId: String,
    @Json(name = "session_id") val sessionId: String,
    @Json(name = "qr_token") val qrToken: String,
    @Json(name = "expires_in_seconds") val expiresInSeconds: Int,
    val step: Long,
    @Json(name = "classroom_location") val classroomLocation: QrLocationDto? = null
)

@JsonClass(generateAdapter = true)
data class QrCheckInRequestDto(
    @Json(name = "qr_token") val qrToken: String,
    @Json(name = "student_id") val studentId: String,
    val latitude: Double,
    val longitude: Double,
    @Json(name = "device_id") val deviceId: String
)

@JsonClass(generateAdapter = true)
data class QrCheckInResponseDto(
    val success: Boolean,
    val message: String,
    @Json(name = "student_id") val studentId: String,
    @Json(name = "class_id") val classId: String?,
    @Json(name = "session_id") val sessionId: String?,
    @Json(name = "distance_meters") val distanceMeters: Double,
    val status: String,
    @Json(name = "check_in_time") val checkInTime: String
)

// ============================================================================
// Phase 6: Gemini AI Predictive Intelligence & Enterprise Multi-Campus DTOs
// ============================================================================

@JsonClass(generateAdapter = true)
data class AiPredictRiskRequestDto(
    @Json(name = "student_id") val studentId: String
)

@JsonClass(generateAdapter = true)
data class AiRiskAssessmentDto(
    @Json(name = "risk_level") val riskLevel: String,
    @Json(name = "risk_score") val riskScore: Float,
    @Json(name = "predicted_final_percentage") val predictedFinalPercentage: Float,
    @Json(name = "confidence_score") val confidenceScore: Float = 0.94f,
    @Json(name = "risk_factors") val riskFactors: List<String> = emptyList(),
    val recommendations: List<String> = emptyList(),
    @Json(name = "ai_summary") val aiSummary: String,
    @Json(name = "model_provider") val modelProvider: String? = "attendora-gemini-v1"
)

@JsonClass(generateAdapter = true)
data class AiPredictRiskResponseDto(
    val success: Boolean,
    @Json(name = "student_id") val studentId: String,
    @Json(name = "risk_assessment") val riskAssessment: AiRiskAssessmentDto
)

@JsonClass(generateAdapter = true)
data class AiInterventionPlanRequestDto(
    @Json(name = "student_id") val studentId: String
)

@JsonClass(generateAdapter = true)
data class AiInterventionPlanDto(
    @Json(name = "student_id") val studentId: String,
    @Json(name = "student_name") val studentName: String,
    @Json(name = "roll_no") val rollNo: String,
    @Json(name = "risk_level") val riskLevel: String,
    @Json(name = "risk_score") val riskScore: Float,
    @Json(name = "current_attendance_percent") val currentAttendancePercent: Float,
    @Json(name = "target_attendance_percent") val targetAttendancePercent: Float,
    @Json(name = "classes_needed_for_eligibility") val classesNeededForEligibility: Int,
    val urgency: String,
    @Json(name = "action_items") val actionItems: List<String> = emptyList(),
    @Json(name = "counseling_letter") val counselingLetter: String,
    @Json(name = "generated_at") val generatedAt: String
)

@JsonClass(generateAdapter = true)
data class AiInterventionResponseDto(
    val success: Boolean,
    @Json(name = "student_id") val studentId: String,
    @Json(name = "intervention_plan") val interventionPlan: AiInterventionPlanDto
)

@JsonClass(generateAdapter = true)
data class AiInstitutionalInsightsDto(
    @Json(name = "total_students") val totalStudents: Int,
    @Json(name = "at_risk_count") val atRiskCount: Int,
    @Json(name = "average_attendance_percent") val averageAttendancePercent: Float,
    @Json(name = "system_health_index") val systemHealthIndex: Float,
    @Json(name = "risk_distribution") val riskDistribution: Map<String, Int> = emptyMap(),
    @Json(name = "executive_summary") val executiveSummary: String,
    val timestamp: String = ""
)

@JsonClass(generateAdapter = true)
data class AiInsightsResponseDto(
    val success: Boolean,
    @Json(name = "institution_id") val institutionId: String?,
    val insights: AiInstitutionalInsightsDto
)

@JsonClass(generateAdapter = true)
data class CampusDepartmentDto(
    val name: String,
    val students: Int,
    val attendance: Float
)

@JsonClass(generateAdapter = true)
data class CampusDetailDto(
    val id: String,
    val name: String,
    val code: String,
    val city: String,
    val state: String,
    val dean: String,
    @Json(name = "enrolled_students") val enrolledStudents: Int,
    @Json(name = "attendance_rate") val attendanceRate: Float,
    @Json(name = "defaulter_count") val defaulterCount: Int,
    @Json(name = "defaulter_rate") val defaulterRate: Float,
    @Json(name = "health_score") val healthScore: Float,
    val departments: List<CampusDepartmentDto> = emptyList()
)

@JsonClass(generateAdapter = true)
data class EnterpriseCampusesResponseDto(
    val success: Boolean,
    @Json(name = "system_name") val systemName: String,
    @Json(name = "system_tenant_id") val systemTenantId: String?,
    @Json(name = "total_campuses") val totalCampuses: Int,
    @Json(name = "total_enrolled_students") val totalEnrolledStudents: Int,
    @Json(name = "system_average_attendance") val systemAverageAttendance: Float,
    @Json(name = "total_defaulters") val totalDefaulters: Int,
    @Json(name = "system_defaulter_rate") val systemDefaulterRate: Float,
    @Json(name = "top_performing_campus") val topPerformingCampus: String,
    val campuses: List<CampusDetailDto> = emptyList(),
    @Json(name = "ai_strategic_brief") val aiStrategicBrief: String = ""
)


