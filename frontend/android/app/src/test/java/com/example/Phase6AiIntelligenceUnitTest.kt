package com.example

import com.example.data.MockDataProvider
import com.example.model.RiskLevel
import com.example.network.dto.AiInterventionResponseDto
import com.example.network.dto.AiPredictRiskResponseDto
import com.example.network.dto.EnterpriseCampusesResponseDto
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class Phase6AiIntelligenceUnitTest {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @Test
    fun testAiPredictRiskResponseDtoSerialization() {
        val adapter = moshi.adapter(AiPredictRiskResponseDto::class.java)

        val json = """
            {
                "success": true,
                "student_id": "s_2",
                "risk_assessment": {
                    "risk_level": "CRITICAL",
                    "risk_score": 78.5,
                    "predicted_final_percentage": 55.6,
                    "confidence_score": 0.94,
                    "risk_factors": [
                        "Cumulative attendance (61.8%) falls below statutory university threshold (75.0%).",
                        "Severe academic deficit in Theory of Computation at 55.0%."
                    ],
                    "recommendations": [
                        "Immediate dean counseling session required within 48 hours."
                    ],
                    "ai_summary": "Student profile exhibits a CRITICAL dropout/debarment risk.",
                    "model_provider": "attendora-heuristic-v1"
                }
            }
        """.trimIndent()

        val dto = adapter.fromJson(json)
        assertNotNull(dto)
        assertTrue(dto?.success == true)
        assertEquals("s_2", dto?.studentId)
        assertEquals("CRITICAL", dto?.riskAssessment?.riskLevel)
        assertEquals(78.5f, dto?.riskAssessment?.riskScore ?: 0f, 0.01f)
        assertEquals(2, dto?.riskAssessment?.riskFactors?.size)
    }

    @Test
    fun testAiInterventionResponseDtoSerialization() {
        val adapter = moshi.adapter(AiInterventionResponseDto::class.java)

        val json = """
            {
                "success": true,
                "student_id": "s_2",
                "intervention_plan": {
                    "student_id": "s_2",
                    "student_name": "Aarav Sharma",
                    "roll_no": "2010002",
                    "risk_level": "CRITICAL",
                    "risk_score": 78.5,
                    "current_attendance_percent": 61.8,
                    "target_attendance_percent": 75.0,
                    "classes_needed_for_eligibility": 14,
                    "urgency": "EMERGENCY_DEBARMENT_WARNING",
                    "action_items": [
                        "Achieve 100% attendance in the next 14 consecutive lectures.",
                        "Attend scheduled academic counseling conference."
                    ],
                    "counseling_letter": "# CONFIDENTIAL: ACADEMIC ATTENDANCE INTERVENTION NOTICE",
                    "generated_at": "2026-11-05T12:00:00Z"
                }
            }
        """.trimIndent()

        val dto = adapter.fromJson(json)
        assertNotNull(dto)
        assertTrue(dto?.success == true)
        assertEquals("Aarav Sharma", dto?.interventionPlan?.studentName)
        assertEquals(14, dto?.interventionPlan?.classesNeededForEligibility)
        assertEquals("EMERGENCY_DEBARMENT_WARNING", dto?.interventionPlan?.urgency)
    }

    @Test
    fun testEnterpriseCampusesResponseDtoSerialization() {
        val adapter = moshi.adapter(EnterpriseCampusesResponseDto::class.java)

        val json = """
            {
                "success": true,
                "system_name": "Attendora Multi-Campus University System",
                "system_tenant_id": "00000000-0000-0000-0000-000000000001",
                "total_campuses": 3,
                "total_enrolled_students": 3450,
                "system_average_attendance": 83.7,
                "total_defaulters": 248,
                "system_defaulter_rate": 7.2,
                "top_performing_campus": "Pune Central Campus",
                "campuses": [
                    {
                        "id": "camp_pune_01",
                        "name": "Pune Central Campus",
                        "code": "PUNE-01",
                        "city": "Pune",
                        "state": "Maharashtra",
                        "dean": "Dr. Rameshwar Joshi",
                        "enrolled_students": 1420,
                        "attendance_rate": 87.4,
                        "defaulter_count": 60,
                        "defaulter_rate": 4.2,
                        "health_score": 92.5,
                        "departments": [
                            {"name": "Computer Science", "students": 480, "attendance": 88.2}
                        ]
                    }
                ],
                "ai_strategic_brief": "Multi-campus predictive telemetry indicates steady attendance."
            }
        """.trimIndent()

        val dto = adapter.fromJson(json)
        assertNotNull(dto)
        assertTrue(dto?.success == true)
        assertEquals(3, dto?.totalCampuses)
        assertEquals(1, dto?.campuses?.size)
        assertEquals("Pune Central Campus", dto?.campuses?.first()?.name)
    }

    @Test
    fun testMockDataProviderRiskCalculation() {
        val students = MockDataProvider.getInitialStudents()
        val defaulter = students.first { it.overallPercentage < 65.0f }
        val goodStudent = students.first { it.overallPercentage >= 85.0f }

        val defaulterRisk = MockDataProvider.getMockAiRiskAssessment(defaulter)
        assertEquals(RiskLevel.CRITICAL, defaulterRisk.riskLevel)
        assertTrue(defaulterRisk.riskScore >= 70.0f)
        assertTrue(defaulterRisk.riskFactors.isNotEmpty())

        val goodRisk = MockDataProvider.getMockAiRiskAssessment(goodStudent)
        assertEquals(RiskLevel.LOW, goodRisk.riskLevel)
        assertTrue(goodRisk.riskScore < 25.0f)
    }

    @Test
    fun testMockDataProviderInterventionPlan() {
        val students = MockDataProvider.getInitialStudents()
        val defaulter = students.first { it.overallPercentage < 65.0f }
        val plan = MockDataProvider.getMockInterventionPlan(defaulter)

        assertEquals(defaulter.id, plan.studentId)
        assertEquals(RiskLevel.CRITICAL, plan.riskLevel)
        assertTrue(plan.classesNeededForEligibility > 0)
        assertTrue(plan.counselingLetter.contains("ACADEMIC ATTENDANCE INTERVENTION NOTICE"))
        assertTrue(plan.counselingLetter.contains(defaulter.name))
    }

    @Test
    fun testMockDataProviderEnterpriseCampuses() {
        val campusesSummary = MockDataProvider.getMockEnterpriseCampuses()

        assertEquals(3, campusesSummary.totalCampuses)
        assertEquals(3450, campusesSummary.totalEnrolledStudents)
        assertTrue(campusesSummary.systemAverageAttendance > 80.0f)
        assertEquals(3, campusesSummary.campuses.size)
        assertTrue(campusesSummary.aiStrategicBrief.isNotBlank())
    }
}

