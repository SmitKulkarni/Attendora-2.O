package com.example

import com.example.model.AttendanceStatus
import com.example.network.dto.QrCheckInRequestDto
import com.example.network.dto.QrGenerateResponseDto
import com.example.viewmodel.AttendoraViewModel
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class Phase5SmartAttendanceUnitTest {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @Test
    fun testQrGenerateResponseDtoSerialization() {
        val adapter = moshi.adapter(QrGenerateResponseDto::class.java)

        val json = """
            {
                "success": true,
                "class_id": "Class 1",
                "session_id": "sess_12345",
                "qr_token": "eyJjaWQiOiJDbGFzczEifQ.abcdef1234567890",
                "expires_in_seconds": 10,
                "step": 178239,
                "classroom_location": {
                    "name": "Hall A-101",
                    "latitude": 18.52043,
                    "longitude": 73.85674,
                    "allowed_radius_meters": 50.0
                }
            }
        """.trimIndent()

        val dto = adapter.fromJson(json)
        assertNotNull(dto)
        assertTrue(dto?.success == true)
        assertEquals("Class 1", dto?.classId)
        assertEquals(10, dto?.expiresInSeconds)
        assertEquals(50.0, dto?.classroomLocation?.allowedRadiusMeters ?: 0.0, 0.01)
    }

    @Test
    fun testQrCheckInRequestDtoSerialization() {
        val adapter = moshi.adapter(QrCheckInRequestDto::class.java)

        val dto = QrCheckInRequestDto(
            qrToken = "test_token_abc.123",
            studentId = "s_1",
            latitude = 18.52044,
            longitude = 73.85675,
            deviceId = "device_smit_99"
        )

        val json = adapter.toJson(dto)
        assertNotNull(json)
        assertTrue(json.contains("test_token_abc.123"))
        assertTrue(json.contains("device_smit_99"))
        assertTrue(json.contains("s_1"))
    }

    @Test
    fun testViewModelQrSessionLifecycle() {
        val viewModel = AttendoraViewModel()

        assertFalse(viewModel.isQrSessionActive.value)

        // Start QR Session
        viewModel.startQrSession("Class 1")
        assertTrue(viewModel.isQrSessionActive.value)
        assertEquals(0, viewModel.qrVerifiedCount.value)
        assertNotNull(viewModel.qrToken.value)

        // Student Check-In via QR
        val targetStudentId = "s_2" // Aarav Sharma (initially ABSENT)
        viewModel.checkInViaQr(targetStudentId)

        assertEquals(1, viewModel.qrVerifiedCount.value)
        val student = viewModel.students.value.first { it.id == targetStudentId }
        assertEquals(AttendanceStatus.PRESENT, student.status)

        // Stop QR Session
        viewModel.stopQrSession()
        assertFalse(viewModel.isQrSessionActive.value)
    }
}

