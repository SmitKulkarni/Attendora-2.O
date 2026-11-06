package com.example.data

import com.example.model.AiInterventionPlan
import com.example.model.AiRiskAssessment
import com.example.model.AttendanceDayLog
import com.example.model.AttendanceNotice
import com.example.model.AttendanceStatus
import com.example.model.CampusDepartment
import com.example.model.CampusDetail
import com.example.model.ClassGroup
import com.example.model.EnterpriseCampusesSummary
import com.example.model.RiskLevel
import com.example.model.Student
import com.example.model.SubjectAttendance
import com.example.model.TrendPoint
import com.example.model.WeeklyBarData

object MockDataProvider {

    fun getInitialStudents(): List<Student> {
        val defaultSubjects1 = listOf(
            SubjectAttendance("Artificial Intelligence", "CS-501", 34, 40, 85.0f),
            SubjectAttendance("Cloud Computing", "CS-502", 36, 40, 90.0f),
            SubjectAttendance("Theory of Computation", "CS-503", 26, 40, 65.0f),
            SubjectAttendance("Database Management", "CS-504", 38, 40, 95.0f)
        )

        val defaultSubjects2 = listOf(
            SubjectAttendance("Artificial Intelligence", "CS-501", 24, 40, 60.0f),
            SubjectAttendance("Cloud Computing", "CS-502", 28, 40, 70.0f),
            SubjectAttendance("Theory of Computation", "CS-503", 22, 40, 55.0f),
            SubjectAttendance("Database Management", "CS-504", 25, 40, 62.5f)
        )

        val defaultSubjects3 = listOf(
            SubjectAttendance("Operating Systems", "CS-401", 32, 40, 80.0f),
            SubjectAttendance("Computer Networks", "CS-402", 35, 40, 87.5f),
            SubjectAttendance("Software Engineering", "CS-403", 30, 40, 75.0f),
            SubjectAttendance("Web Technologies", "CS-404", 34, 40, 85.0f)
        )

        return listOf(
            Student(
                id = "s_1",
                name = "Smit Kulkarni",
                rollNo = "2016001",
                className = "Class 1",
                status = AttendanceStatus.PRESENT,
                checkInTime = "6:30 PM",
                avatarBgColor = 0xFF3B82F6,
                department = "Computer Science",
                email = "smitkulkarni96@gmail.com",
                overallPercentage = 84.5f,
                subjects = defaultSubjects1
            ),
            Student(
                id = "s_2",
                name = "Aarav Sharma",
                rollNo = "2010002",
                className = "Class 1",
                status = AttendanceStatus.ABSENT,
                checkInTime = "6:30 PM",
                avatarBgColor = 0xFF10B981,
                department = "Computer Science",
                email = "aarav.sharma@attendora.edu",
                overallPercentage = 61.8f,
                subjects = defaultSubjects2
            ),
            Student(
                id = "s_3",
                name = "Priya Patel",
                rollNo = "2018003",
                className = "Class 1",
                status = AttendanceStatus.LATE,
                checkInTime = "6:30 PM",
                avatarBgColor = 0xFF8B5CF6,
                department = "Computer Science",
                email = "priya.patel@attendora.edu",
                overallPercentage = 77.2f,
                subjects = defaultSubjects1
            ),
            Student(
                id = "s_4",
                name = "Ananya Rao",
                rollNo = "2018004",
                className = "Class 2",
                status = AttendanceStatus.PRESENT,
                checkInTime = "6:30 PM",
                avatarBgColor = 0xFFEC4899,
                department = "Information Technology",
                email = "ananya.rao@attendora.edu",
                overallPercentage = 92.0f,
                subjects = defaultSubjects3
            ),
            Student(
                id = "s_5",
                name = "Rohan Verma",
                rollNo = "2018005",
                className = "Class 2",
                status = AttendanceStatus.PRESENT,
                checkInTime = "6:35 PM",
                avatarBgColor = 0xFFF59E0B,
                department = "Information Technology",
                email = "rohan.verma@attendora.edu",
                overallPercentage = 88.5f,
                subjects = defaultSubjects3
            ),
            Student(
                id = "s_6",
                name = "Tanvi Deshmukh",
                rollNo = "2018006",
                className = "Class 1",
                status = AttendanceStatus.PRESENT,
                checkInTime = "6:32 PM",
                avatarBgColor = 0xFF06B6D4,
                department = "Computer Science",
                email = "tanvi.d@attendora.edu",
                overallPercentage = 94.0f,
                subjects = defaultSubjects1
            ),
            Student(
                id = "s_7",
                name = "Vikram Mehta",
                rollNo = "2018007",
                className = "Class 2",
                status = AttendanceStatus.ABSENT,
                checkInTime = "-",
                avatarBgColor = 0xFFEF4444,
                department = "Information Technology",
                email = "vikram.m@attendora.edu",
                overallPercentage = 68.0f,
                subjects = defaultSubjects2
            ),
            Student(
                id = "s_8",
                name = "Neha Kapoor",
                rollNo = "2018008",
                className = "Class 1",
                status = AttendanceStatus.PRESENT,
                checkInTime = "6:28 PM",
                avatarBgColor = 0xFF6366F1,
                department = "Computer Science",
                email = "neha.k@attendora.edu",
                overallPercentage = 91.5f,
                subjects = defaultSubjects1
            )
        )
    }

    fun getInitialClasses(): List<ClassGroup> {
        return listOf(
            ClassGroup(
                id = "c_1",
                name = "Class 1",
                code = "CS-2A",
                courseName = "Artificial Intelligence & Foundations",
                academicTerm = "Fall 2026",
                department = "Computer Science",
                teacherName = "Prof. Priya Saxena",
                enrolledCount = 60,
                scheduleTime = "09:00 AM - 10:30 AM",
                roomLocation = "Lecture Hall 101"
            ),
            ClassGroup(
                id = "c_2",
                name = "Class 2",
                code = "IT-2B",
                courseName = "Cloud Computing & Distributed Systems",
                academicTerm = "Fall 2026",
                department = "Information Technology",
                teacherName = "Dr. Arvind Sharma",
                enrolledCount = 60,
                scheduleTime = "11:00 AM - 12:30 PM",
                roomLocation = "Lecture Hall 204"
            ),
            ClassGroup(
                id = "c_3",
                name = "CS 3rd Year",
                code = "CS-3A",
                courseName = "Theory of Computation & Compiler Design",
                academicTerm = "Fall 2026",
                department = "Computer Science",
                teacherName = "Prof. R. Sen",
                enrolledCount = 55,
                scheduleTime = "02:00 PM - 03:30 PM",
                roomLocation = "Auditorium C"
            )
        )
    }

    fun getTrendPoints(): List<TrendPoint> {
        return listOf(
            TrendPoint("Sep", 12f),
            TrendPoint("Oct", 58f),
            TrendPoint("Nov", 98f)
        )
    }

    fun getWeeklyBars(): List<WeeklyBarData> {
        return listOf(
            WeeklyBarData("Mon", 78, 52),
            WeeklyBarData("Tue", 68, 38),
            WeeklyBarData("Wed", 88, 60),
            WeeklyBarData("Thu", 82, 22),
            WeeklyBarData("Fri", 80, 38)
        )
    }

    fun getInitialNotices(): List<AttendanceNotice> {
        return listOf(
            AttendanceNotice(
                id = "n_1",
                title = "Low Attendance Warning",
                message = "2 students in Class 1 are below the institutional 75% threshold.",
                timeAgo = "10m ago",
                isWarning = true
            ),
            AttendanceNotice(
                id = "n_2",
                title = "Class CS101 Logged",
                message = "Prof. Priya marked today's 6:30 PM attendance session successfully.",
                timeAgo = "35m ago",
                isWarning = false
            ),
            AttendanceNotice(
                id = "n_3",
                title = "Cloud Synchronization Completed",
                message = "All 120 tenant attendance logs synced to Firestore database.",
                timeAgo = "1h ago",
                isWarning = false
            )
        )
    }

    fun getStudentDayLogs(): List<AttendanceDayLog> {
        return listOf(
            AttendanceDayLog("Nov 05", "Wednesday", AttendanceStatus.PRESENT, "6:30 PM", "Artificial Intelligence", "Prof. Priya Saxena"),
            AttendanceDayLog("Nov 04", "Tuesday", AttendanceStatus.PRESENT, "09:05 AM", "Cloud Computing", "Dr. Arvind Sharma"),
            AttendanceDayLog("Nov 03", "Monday", AttendanceStatus.PRESENT, "11:15 AM", "Theory of Computation", "Prof. R. Sen"),
            AttendanceDayLog("Oct 31", "Friday", AttendanceStatus.LATE, "09:22 AM", "Database Management", "Prof. K. Iyer"),
            AttendanceDayLog("Oct 30", "Thursday", AttendanceStatus.PRESENT, "02:00 PM", "Artificial Intelligence", "Prof. Priya Saxena"),
            AttendanceDayLog("Oct 29", "Wednesday", AttendanceStatus.ABSENT, "-", "Cloud Computing", "Dr. Arvind Sharma")
        )
    }

    // ========================================================================
    // Phase 6: Gemini AI Fallback & Multi-Campus Mock Generators
    // ========================================================================

    fun getMockAiRiskAssessment(student: Student): AiRiskAssessment {
        val pct = student.overallPercentage
        val (level, score, projected) = when {
            pct < 65.0f -> Triple(RiskLevel.CRITICAL, 78.5f, pct - 6.2f)
            pct < 75.0f -> Triple(RiskLevel.HIGH, 54.0f, pct - 3.5f)
            pct < 85.0f -> Triple(RiskLevel.MODERATE, 32.0f, pct - 1.2f)
            else -> Triple(RiskLevel.LOW, 12.0f, pct + 0.5f)
        }

        val factors = mutableListOf<String>()
        if (pct < 75.0f) {
            factors.add("Cumulative attendance (${pct}%) is below statutory minimum threshold (75.0%).")
        }
        student.subjects.filter { it.percentage < 65.0f }.forEach { sub ->
            factors.add("Critical subject deficit: ${sub.name} (${sub.code}) at ${sub.percentage}%.")
        }
        if (factors.isEmpty()) {
            factors.add("Consistent attendance across all registered courses.")
        }

        val recommendations = when (level) {
            RiskLevel.CRITICAL -> listOf(
                "Immediate Academic Board hearing required within 48 hours.",
                "Mandatory attendance in weekend remedial makeup labs.",
                "Formal debarment notification issued to parent/guardian."
            )
            RiskLevel.HIGH -> listOf(
                "Weekly attendance counseling with assigned faculty mentor.",
                "Submit formal justification documentation for accumulated absences."
            )
            RiskLevel.MODERATE -> listOf(
                "Avoid unexcused absences to prevent slipping into defaulter status."
            )
            RiskLevel.LOW -> listOf(
                "Maintain consistent attendance; eligible for semester honors recognition."
            )
        }

        val summary = "Student ${student.name} (${student.rollNo}) exhibits ${level.label} " +
                "(Risk Index: ${score}/100). Projected final completion rate: ${projected.coerceIn(0f, 100f)}%."

        return AiRiskAssessment(
            riskLevel = level,
            riskScore = score,
            predictedFinalPercentage = projected.coerceIn(0f, 100f),
            confidenceScore = 0.94f,
            riskFactors = factors,
            recommendations = recommendations,
            aiSummary = summary
        )
    }

    fun getMockInterventionPlan(student: Student): AiInterventionPlan {
        val risk = getMockAiRiskAssessment(student)
        val neededClasses = if (student.overallPercentage < 75.0f) 14 else 0
        val urgency = if (risk.riskLevel == RiskLevel.CRITICAL) "EMERGENCY_DEBARMENT_WARNING" else "ACADEMIC_REMEDIATION"

        val actionItems = listOf(
            "Achieve 100% attendance in the next $neededClasses consecutive lectures.",
            "Attend mandatory counseling session with Faculty Advisor.",
            "Enroll in remedial lab tutorials for courses below 75%."
        )

        val letter = """
            # CONFIDENTIAL: ACADEMIC ATTENDANCE INTERVENTION NOTICE
            **Attendora University System — Office of the Dean of Academic Affairs**
            
            TO: ${student.name} (Roll No: ${student.rollNo})
            Department: ${student.department} (${student.className})
            
            Current Cumulative Attendance: ${student.overallPercentage}%
            Statutory University Requirement: 75.0%
            Assessed Risk Tier: ${risk.riskLevel.label}
            
            Remediation Contract Terms:
            1. You must complete $neededClasses consecutive class sessions without absence.
            2. Return signed contract to the Dean's office within 3 working days.
            3. Continued non-compliance will trigger examination debarment under §14-B.
            
            Authorized Signatures:
            Faculty Advisor: Prof. Priya Saxena
            Head of Department: Dr. Arvind Sharma
            Dean of Academic Affairs, Attendora University System
        """.trimIndent()

        return AiInterventionPlan(
            studentId = student.id,
            studentName = student.name,
            rollNo = student.rollNo,
            riskLevel = risk.riskLevel,
            riskScore = risk.riskScore,
            currentAttendancePercent = student.overallPercentage,
            targetAttendancePercent = 75.0f,
            classesNeededForEligibility = neededClasses,
            urgency = urgency,
            actionItems = actionItems,
            counselingLetter = letter,
            generatedAt = "2026-11-05T12:00:00Z"
        )
    }

    fun getMockEnterpriseCampuses(): EnterpriseCampusesSummary {
        val campuses = listOf(
            CampusDetail(
                id = "camp_pune_01",
                name = "Pune Central Campus",
                code = "PUNE-01",
                city = "Pune",
                state = "Maharashtra",
                dean = "Dr. Rameshwar Joshi",
                enrolledStudents = 1420,
                attendanceRate = 87.4f,
                defaulterCount = 60,
                defaulterRate = 4.2f,
                healthScore = 92.5f,
                departments = listOf(
                    CampusDepartment("Computer Science", 480, 88.2f),
                    CampusDepartment("Information Technology", 420, 86.9f),
                    CampusDepartment("Mechanical Engineering", 520, 87.1f)
                )
            ),
            CampusDetail(
                id = "camp_mum_02",
                name = "Mumbai Metro Campus",
                code = "MUM-02",
                city = "Mumbai",
                state = "Maharashtra",
                dean = "Dr. Sunita Kulkarni",
                enrolledStudents = 1180,
                attendanceRate = 82.1f,
                defaulterCount = 92,
                defaulterRate = 7.8f,
                healthScore = 84.0f,
                departments = listOf(
                    CampusDepartment("Computer Science", 400, 83.5f),
                    CampusDepartment("Data Science", 380, 81.2f),
                    CampusDepartment("Civil Engineering", 400, 81.6f)
                )
            ),
            CampusDetail(
                id = "camp_nsh_03",
                name = "Nashik Tech Campus",
                code = "NSH-03",
                city = "Nashik",
                state = "Maharashtra",
                dean = "Dr. Anand Deshmukh",
                enrolledStudents = 850,
                attendanceRate = 79.5f,
                defaulterCount = 96,
                defaulterRate = 11.3f,
                healthScore = 78.2f,
                departments = listOf(
                    CampusDepartment("Computer Science", 320, 80.4f),
                    CampusDepartment("Electrical Engineering", 280, 78.5f),
                    CampusDepartment("Electronics & Telecom", 250, 79.6f)
                )
            )
        )

        return EnterpriseCampusesSummary(
            systemName = "Attendora Multi-Campus University System",
            totalCampuses = 3,
            totalEnrolledStudents = 3450,
            systemAverageAttendance = 83.7f,
            totalDefaulters = 248,
            systemDefaulterRate = 7.2f,
            topPerformingCampus = "Pune Central Campus",
            campuses = campuses,
            aiStrategicBrief = "Multi-campus predictive telemetry indicates steady attendance across Western Region. Nashik Tech Campus exhibits elevated defaulter concentration (11.3%) requiring regional task force."
        )
    }
}
