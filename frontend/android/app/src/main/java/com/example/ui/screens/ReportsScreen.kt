package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.EnterpriseCampusesSummary
import com.example.model.Student
import com.example.ui.theme.AbsentCoralFill
import com.example.ui.theme.AbsentCoralSolid
import com.example.ui.theme.AbsentCoralStroke
import com.example.ui.theme.AbsentCoralText
import com.example.ui.theme.OceanTeal
import com.example.ui.theme.OutlineBorder
import com.example.ui.theme.PresentGreenSolid
import com.example.ui.theme.SlateSecondary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

@Composable
fun ReportsScreen(
    students: List<Student>,
    threshold: Int,
    onThresholdChange: (Int) -> Unit,
    onExportReport: (String) -> Unit,
    onSendAlerts: () -> Unit,
    modifier: Modifier = Modifier,
    onAiEvaluateStudent: ((Student) -> Unit)? = null,
    enterpriseCampuses: EnterpriseCampusesSummary? = null
) {
    val defaulters = students.filter { it.overallPercentage < threshold }
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFEBF1F5))
            .verticalScroll(scrollState)
            .padding(16.dp)
            .testTag("reports_screen")
    ) {
        // Defaulter Threshold Config Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, OutlineBorder),
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
                            text = "Institutional Defaulter Analytics",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Identify at-risk students and export compliance records",
                            fontSize = 13.sp,
                            color = SlateSecondary
                        )
                    }

                    // Threshold Badge
                    Box(
                        modifier = Modifier
                            .background(AbsentCoralFill, RoundedCornerShape(8.dp))
                            .border(1.dp, AbsentCoralStroke, RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Threshold: $threshold%",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = AbsentCoralText
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Threshold Slider
                Text(
                    text = "Minimum Attendance Requirement: $threshold%",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Slider(
                    value = threshold.toFloat(),
                    onValueChange = { onThresholdChange(it.toInt()) },
                    valueRange = 50f..90f,
                    steps = 7,
                    colors = SliderDefaults.colors(
                        thumbColor = OceanTeal,
                        activeTrackColor = OceanTeal,
                        inactiveTrackColor = Color(0xFFE2E8F0)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color(0xFFF1F5F9))
                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons: PDF, CSV, and Notification
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onExportReport("PDF") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = OceanTeal)
                    ) {
                        Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Export PDF", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }

                    OutlinedButton(
                        onClick = { onExportReport("CSV") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Export CSV", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    }

                    Button(
                        onClick = onSendAlerts,
                        modifier = Modifier.weight(1.3f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AbsentCoralSolid)
                    ) {
                        Icon(imageVector = Icons.Default.Email, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Notify Defaulters", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Defaulter Roster Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, OutlineBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Defaulters Roster (${defaulters.size} Students)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Text(
                        text = "Below $threshold% threshold",
                        fontSize = 12.sp,
                        color = SlateSecondary
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (defaulters.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No students below $threshold% attendance. Good compliance!",
                            fontSize = 14.sp,
                            color = PresentGreenSolid,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    defaulters.forEachIndexed { index, student ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(Color(student.avatarBgColor)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = student.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = student.name,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "Roll: ${student.rollNo} • ${student.className}",
                                        fontSize = 12.sp,
                                        color = SlateSecondary
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "${student.overallPercentage}%",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AbsentCoralSolid
                                    )
                                    Text(
                                        text = "Shortage: ${(threshold - student.overallPercentage).toInt()}%",
                                        fontSize = 11.sp,
                                        color = TextMuted
                                    )
                                }

                                if (onAiEvaluateStudent != null) {
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Button(
                                        onClick = { onAiEvaluateStudent(student) },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = OceanTeal),
                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                        modifier = Modifier.height(32.dp)
                                    ) {
                                        Text("AI ✦", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }
                        }

                        if (index < defaulters.lastIndex) {
                            HorizontalDivider(color = Color(0xFFF1F5F9))
                        }
                    }
                }
            }
        }

        // Phase 6: Enterprise Multi-Campus Federation Analytics
        if (enterpriseCampuses != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, OutlineBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("enterprise_campuses_card")
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Enterprise Multi-Campus Federation",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = enterpriseCampuses.systemName,
                                fontSize = 12.sp,
                                color = SlateSecondary
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = OceanTeal.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = "${enterpriseCampuses.totalCampuses} Campuses Active",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = OceanTeal
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Regional Aggregate Strip
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF8FAFC), RoundedCornerShape(10.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Total Enrolled", fontSize = 11.sp, color = TextMuted)
                            Text("${enterpriseCampuses.totalEnrolledStudents}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        Column {
                            Text("System Average", fontSize = 11.sp, color = TextMuted)
                            Text("${enterpriseCampuses.systemAverageAttendance}%", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PresentGreenSolid)
                        }
                        Column {
                            Text("Total Defaulters", fontSize = 11.sp, color = TextMuted)
                            Text("${enterpriseCampuses.totalDefaulters} (${enterpriseCampuses.systemDefaulterRate}%)", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AbsentCoralSolid)
                        }
                        Column {
                            Text("Top Campus", fontSize = 11.sp, color = TextMuted)
                            Text(enterpriseCampuses.topPerformingCampus.take(12), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = OceanTeal)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Individual Campus Roster
                    enterpriseCampuses.campuses.forEachIndexed { i, camp ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(camp.name, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                Text("${camp.city}, ${camp.state} • Dean: ${camp.dean}", fontSize = 11.sp, color = TextMuted)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("${camp.attendanceRate}%", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (camp.attendanceRate >= 80f) PresentGreenSolid else AbsentCoralSolid)
                                Text("${camp.defaulterCount} defaulters (${camp.defaulterRate}%)", fontSize = 11.sp, color = TextMuted)
                            }
                        }
                        if (i < enterpriseCampuses.campuses.lastIndex) {
                            HorizontalDivider(color = Color(0xFFF1F5F9))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "AI Strategic Brief: ${enterpriseCampuses.aiStrategicBrief}",
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        color = SlateSecondary,
                        modifier = Modifier
                            .background(Color(0xFFF1F5F9), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
