package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.window.Dialog
import com.example.model.AttendanceStatus
import com.example.model.Student
import com.example.ui.theme.AbsentCoralSolid
import com.example.ui.theme.OceanTeal
import com.example.ui.theme.OutlineBorder
import com.example.ui.theme.PresentGreenSolid
import com.example.ui.theme.SlateSecondary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

@Composable
fun StudentDetailDialog(
    student: Student,
    onDismiss: () -> Unit,
    onStatusChange: (AttendanceStatus) -> Unit,
    onAiEvaluate: (() -> Unit)? = null,
    onGenerateIntervention: (() -> Unit)? = null
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("student_detail_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header with close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Student Profile & Roster",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = SlateSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Profile Summary Card
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF8FAFC), RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(student.avatarBgColor),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            val initials = student.name.split(" ")
                                .mapNotNull { it.firstOrNull()?.toString() }
                                .take(2)
                                .joinToString("")
                            Text(
                                text = initials,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = student.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Roll: ${student.rollNo} • ${student.className}",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                        Text(
                            text = student.department,
                            fontSize = 11.sp,
                            color = OceanTeal,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${student.overallPercentage}%",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (student.overallPercentage >= 75f) PresentGreenSolid else AbsentCoralSolid
                        )
                        Text(
                            text = "Cumulative",
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Subject-wise Breakdown
                Text(
                    text = "Subject-Wise Attendance",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))

                student.subjects.forEach { subject ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = subject.name,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextPrimary
                            )
                            Text(
                                text = "${subject.attended}/${subject.total} (${subject.percentage}%)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (subject.percentage >= 75f) PresentGreenSolid else AbsentCoralSolid
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { (subject.percentage / 100f).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = if (subject.percentage >= 75f) OceanTeal else AbsentCoralSolid,
                            trackColor = Color(0xFFE2E8F0)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color(0xFFF1F5F9))
                Spacer(modifier = Modifier.height(12.dp))

                // Phase 6: Gemini AI Predictive Intelligence Actions
                if (onAiEvaluate != null || onGenerateIntervention != null) {
                    Text(
                        text = "Gemini AI Predictive Intelligence",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = OceanTeal
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (onAiEvaluate != null) {
                            Button(
                                onClick = onAiEvaluate,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = OceanTeal)
                            ) {
                                Text("AI Risk Forecast ✦", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                        if (onGenerateIntervention != null) {
                            OutlinedButton(
                                onClick = onGenerateIntervention,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Remediation Plan 📄", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Quick Status Override Buttons
                Text(
                    text = "Update Today's Status",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SlateSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AttendanceStatus.entries.forEach { status ->
                        val isCurrent = student.status == status
                        Button(
                            onClick = { onStatusChange(status) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isCurrent) OceanTeal else Color(0xFFEEF4F8),
                                contentColor = if (isCurrent) Color.White else TextPrimary
                            )
                        ) {
                            Text(
                                text = status.label,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}
