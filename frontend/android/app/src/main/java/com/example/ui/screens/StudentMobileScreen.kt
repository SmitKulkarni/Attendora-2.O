package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AttendanceDayLog
import com.example.model.AttendanceStatus
import com.example.model.Student
import com.example.ui.components.StatusPill
import com.example.ui.theme.AbsentCoralFill
import com.example.ui.theme.AbsentCoralSolid
import com.example.ui.theme.AbsentCoralStroke
import com.example.ui.theme.AbsentCoralText
import com.example.ui.theme.LateAmberSolid
import com.example.ui.theme.OceanTeal
import com.example.ui.theme.OceanTealDark
import com.example.ui.theme.OutlineBorder
import com.example.ui.theme.PresentGreenFill
import com.example.ui.theme.PresentGreenSolid
import com.example.ui.theme.PresentGreenStroke
import com.example.ui.theme.PresentGreenText
import com.example.ui.theme.SlateSecondary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

@Composable
fun StudentMobileScreen(
    student: Student,
    dayLogs: List<AttendanceDayLog>,
    onDownloadTranscript: () -> Unit,
    onApplyLeave: () -> Unit,
    onScanQr: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF4FAFE))
            .verticalScroll(scrollState)
            .padding(16.dp)
            .testTag("student_mobile_screen")
    ) {
        // Welcome Header Card with Student Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = OceanTealDark),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Student Portal",
                            color = Color(0xFF9EEEFF),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = student.name,
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1D808F)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = student.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Roll No: ${student.rollNo}",
                        color = Color(0xFFE0F7FA),
                        fontSize = 13.sp
                    )
                    Text(
                        text = "•",
                        color = Color(0xFF7ED3E3),
                        fontSize = 13.sp
                    )
                    Text(
                        text = student.className,
                        color = Color(0xFFE0F7FA),
                        fontSize = 13.sp
                    )
                    Text(
                        text = "•",
                        color = Color(0xFF7ED3E3),
                        fontSize = 13.sp
                    )
                    Text(
                        text = student.department,
                        color = Color(0xFFE0F7FA),
                        fontSize = 13.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Big Attendance Gauge Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, OutlineBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Overall Attendance Percentage",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SlateSecondary
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Circular Progress Gauge
                Box(
                    modifier = Modifier.size(160.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val isSafe = student.overallPercentage >= 75f
                    val gaugeColor = if (isSafe) PresentGreenSolid else AbsentCoralSolid

                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeWidth = 14.dp.toPx()
                        val diameter = size.minDimension - strokeWidth
                        val topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f)
                        val arcSize = Size(diameter, diameter)

                        // Background track
                        drawArc(
                            color = Color(0xFFE2E8F0),
                            startAngle = -90f,
                            sweepAngle = 360f,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )

                        // Foreground progress
                        val sweep = (student.overallPercentage / 100f) * 360f
                        drawArc(
                            color = gaugeColor,
                            startAngle = -90f,
                            sweepAngle = sweep,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${student.overallPercentage}%",
                            fontSize = 34.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = if (student.overallPercentage >= 75f) "SAFE" else "AT RISK",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (student.overallPercentage >= 75f) PresentGreenText else AbsentCoralText
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Status pill
                val isSafe = student.overallPercentage >= 75f
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(9999.dp))
                        .background(if (isSafe) PresentGreenFill else AbsentCoralFill)
                        .border(1.dp, if (isSafe) PresentGreenStroke else AbsentCoralStroke, RoundedCornerShape(9999.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isSafe) Icons.Default.CheckCircle else Icons.Default.Warning,
                        contentDescription = null,
                        tint = if (isSafe) PresentGreenText else AbsentCoralText,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isSafe) "Eligible to Sit for End-Semester Exams" else "Attendance Defaulter Warning (<75%)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isSafe) PresentGreenText else AbsentCoralText
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color(0xFFF1F5F9))
                Spacer(modifier = Modifier.height(12.dp))

                // Stats breakdown
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "134 hrs", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(text = "Attended", fontSize = 12.sp, color = TextMuted)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "22 hrs", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AbsentCoralSolid)
                        Text(text = "Missed", fontSize = 12.sp, color = TextMuted)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "14 Days", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = OceanTeal)
                        Text(text = "Streak", fontSize = 12.sp, color = TextMuted)
                    }
                }
            }
        }

        // Low Attendance Alert Card if any subject is < 75%
        val atRiskSubjects = student.subjects.filter { it.percentage < 75f }
        if (atRiskSubjects.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AbsentCoralFill),
                border = androidx.compose.foundation.BorderStroke(1.dp, AbsentCoralStroke),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Warning",
                        tint = AbsentCoralSolid,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Low Attendance Notice",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = AbsentCoralText
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${atRiskSubjects.first().name} is at ${atRiskSubjects.first().percentage}%. You must attend the next 3 scheduled classes to safely reach 75%.",
                            fontSize = 13.sp,
                            color = AbsentCoralText
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Subject Breakdown Section
        Text(
            text = "Subject-Wise Breakdown",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(10.dp))

        student.subjects.forEach { subject ->
            val isSubjectSafe = subject.percentage >= 75f
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, OutlineBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = subject.name,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Code: ${subject.code} • ${subject.attended}/${subject.total} Classes",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }

                        Text(
                            text = "${subject.percentage}%",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSubjectSafe) PresentGreenSolid else AbsentCoralSolid
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = { (subject.percentage / 100f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = if (isSubjectSafe) OceanTeal else AbsentCoralSolid,
                        trackColor = Color(0xFFEEF4F8)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Recent Attendance Timeline Log
        Text(
            text = "Recent Attendance History",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(10.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, OutlineBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                dayLogs.take(5).forEachIndexed { index, log ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "${log.date} (${log.dayName})",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Text(
                                text = "${log.subjectTitle} • ${log.markedBy}",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = log.checkInTime,
                                fontSize = 13.sp,
                                color = SlateSecondary,
                                modifier = Modifier.padding(end = 10.dp)
                            )
                            StatusPill(status = log.status)
                        }
                    }

                    if (index < 4) {
                        HorizontalDivider(color = Color(0xFFF1F5F9))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Quick Student Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onApplyLeave,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.EventNote,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Apply Leave", fontSize = 13.sp)
            }

            Button(
                onClick = onDownloadTranscript,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OceanTeal)
            ) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Transcript", fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Phase 5: Smart QR Attendance Check-In Button
        Button(
            onClick = onScanQr,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("student_scan_qr_btn"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = OceanTealDark)
        ) {
            Icon(
                imageVector = Icons.Default.QrCodeScanner,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Scan Classroom QR Code", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(28.dp))
    }
}
