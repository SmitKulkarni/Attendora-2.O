package com.example.data

import com.example.model.AttendanceDayLog
import com.example.model.AttendanceNotice
import com.example.model.AttendanceStatus
import com.example.model.AttendanceSummaryMetrics
import com.example.model.ClassGroup
import com.example.model.Student
import com.example.model.SubjectAttendance
import com.example.model.TrendPoint
import com.example.model.WeeklyBarData

object MockDataProvider {

    fun getInitialStudents(): List<Student> {
        val defaultSubjects1 = listOf(
            SubjectAttendance("Artificial Intelligence", "CS-501", 34, 40, 85.0f),
            SubjectAttendance("Cloud Computing", "CS-502", 36, 40, 90.0f),
            SubjectAttendance("Theory of Computation", "CS-503", 26, 40, 65.0f), // Defaulter candidate
            SubjectAttendance("Database Management", "CS-504", 38, 40, 95.0f)
        )

        val defaultSubjects2 = listOf(
            SubjectAttendance("Artificial Intelligence", "CS-501", 24, 40, 60.0f), // Defaulter
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
}
