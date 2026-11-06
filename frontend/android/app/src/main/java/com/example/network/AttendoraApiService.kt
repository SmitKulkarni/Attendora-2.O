package com.example.network

import com.example.network.dto.AttendanceSubmissionRequestDto
import com.example.network.dto.AttendanceSubmissionResponseDto
import com.example.network.dto.AttendanceSummaryResponseDto
import com.example.network.dto.ClassesListResponseDto
import com.example.network.dto.StudentDashboardResponseDto
import com.example.network.dto.StudentDto
import com.example.network.dto.StudentsListResponseDto
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Streaming

interface AttendoraApiService {

    @GET("health")
    suspend fun checkHealth(): Response<Map<String, Any>>

    @POST("api/v1/attendance")
    suspend fun submitAttendance(
        @Body request: AttendanceSubmissionRequestDto
    ): Response<AttendanceSubmissionResponseDto>

    @GET("api/v1/attendance/summary")
    suspend fun getAttendanceSummary(): Response<AttendanceSummaryResponseDto>

    @GET("api/v1/student/dashboard")
    suspend fun getStudentDashboard(
        @Query("student_id") studentId: String? = null
    ): Response<StudentDashboardResponseDto>

    @GET("api/v1/students")
    suspend fun getStudents(
        @Query("search") search: String? = null,
        @Query("class_name") className: String? = null
    ): Response<StudentsListResponseDto>

    @POST("api/v1/students")
    suspend fun createStudent(
        @Body student: Map<String, String>
    ): Response<Map<String, Any>>

    @GET("api/v1/classes")
    suspend fun getClasses(): Response<ClassesListResponseDto>

    @GET("api/v1/student/{id}/transcript.pdf")
    @Streaming
    suspend fun downloadTranscript(
        @Path("id") studentId: String
    ): Response<ResponseBody>

    @POST("api/v1/reports/defaulters/notify")
    suspend fun notifyDefaulters(
        @Body request: Map<String, Double>
    ): Response<Map<String, Any>>

    @POST("api/v1/attendance/qr/generate")
    suspend fun generateQrToken(
        @Body request: com.example.network.dto.QrGenerateRequestDto
    ): Response<com.example.network.dto.QrGenerateResponseDto>

    @POST("api/v1/attendance/qr/checkin")
    suspend fun checkInWithQr(
        @Body request: com.example.network.dto.QrCheckInRequestDto
    ): Response<com.example.network.dto.QrCheckInResponseDto>

    @POST("api/v1/ai/predict-risk")
    suspend fun predictRisk(
        @Body request: com.example.network.dto.AiPredictRiskRequestDto
    ): Response<com.example.network.dto.AiPredictRiskResponseDto>

    @POST("api/v1/ai/intervention-plan")
    suspend fun generateInterventionPlan(
        @Body request: com.example.network.dto.AiInterventionPlanRequestDto
    ): Response<com.example.network.dto.AiInterventionResponseDto>

    @GET("api/v1/ai/insights")
    suspend fun getAiInsights(): Response<com.example.network.dto.AiInsightsResponseDto>

    @GET("api/v1/campuses")
    suspend fun getCampuses(): Response<com.example.network.dto.EnterpriseCampusesResponseDto>
}

