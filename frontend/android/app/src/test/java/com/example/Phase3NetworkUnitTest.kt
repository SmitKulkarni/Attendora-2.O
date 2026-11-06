package com.example

import com.example.network.AuthInterceptor
import com.example.network.dto.AttendanceRecordItemDto
import com.example.network.dto.AttendanceSubmissionRequestDto
import com.example.network.dto.AttendanceSummaryResponseDto
import com.example.network.dto.StudentDto
import com.example.network.dto.SubjectDto
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class Phase3NetworkUnitTest {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @Test
    fun testAttendanceSubmissionRequestDtoSerialization() {
        val adapter = moshi.adapter(AttendanceSubmissionRequestDto::class.java)

        val dto = AttendanceSubmissionRequestDto(
            classId = "Class 1",
            sessionDate = "2026-11-05",
            records = listOf(
                AttendanceRecordItemDto("s_1", "PRESENT", "09:00 AM"),
                AttendanceRecordItemDto("s_2", "ABSENT", "-")
            )
        )

        val json = adapter.toJson(dto)
        assertNotNull(json)
        assertTrue(json.contains("Class 1"))
        assertTrue(json.contains("2026-11-05"))
        assertTrue(json.contains("PRESENT"))
    }

    @Test
    fun testAttendanceSummaryDtoDeserialization() {
        val json = """
            {
                "total_students": 120,
                "today_attendance_percent": 94,
                "on_time_percent": 88,
                "absent_count": 7,
                "trends": [
                    {"month": "Sep", "value": 12.0},
                    {"month": "Oct", "value": 58.0}
                ],
                "weekly_data": [
                    {"day": "Mon", "present": 78, "absent": 52}
                ]
            }
        """.trimIndent()

        val adapter = moshi.adapter(AttendanceSummaryResponseDto::class.java)
        val dto = adapter.fromJson(json)

        assertNotNull(dto)
        assertEquals(120, dto?.totalStudents)
        assertEquals(94, dto?.todayAttendancePercent)
        assertEquals(88, dto?.onTimePercent)
        assertEquals(7, dto?.absentCount)
        assertEquals(2, dto?.trends?.size)
        assertEquals("Sep", dto?.trends?.first()?.month)
    }

    @Test
    fun testStudentDtoMapping() {
        val json = """
            {
                "id": "s_1",
                "name": "Smit Kulkarni",
                "roll_no": "2016001",
                "class_name": "Class 1",
                "department": "Computer Science",
                "email": "smitkulkarni96@gmail.com",
                "status": "PRESENT",
                "check_in_time": "6:30 PM",
                "overall_percentage": 84.5,
                "subjects": [
                    {"name": "AI", "code": "CS-101", "attended": 34, "total": 40, "percentage": 85.0}
                ]
            }
        """.trimIndent()

        val adapter = moshi.adapter(StudentDto::class.java)
        val student = adapter.fromJson(json)

        assertNotNull(student)
        assertEquals("s_1", student?.id)
        assertEquals("Smit Kulkarni", student?.name)
        assertEquals("2016001", student?.rollNo)
        assertEquals("Class 1", student?.className)
        assertEquals(84.5f, student?.overallPercentage ?: 0f, 0.01f)
    }
}

