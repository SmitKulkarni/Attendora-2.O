package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AttendanceStatus
import com.example.model.ClassGroup
import com.example.model.Student
import com.example.ui.components.StatusPill
import com.example.ui.theme.AbsentCoralFill
import com.example.ui.theme.AbsentCoralSolid
import com.example.ui.theme.AbsentCoralStroke
import com.example.ui.theme.AbsentCoralText
import com.example.ui.theme.LateAmberFill
import com.example.ui.theme.LateAmberSolid
import com.example.ui.theme.LateAmberStroke
import com.example.ui.theme.LateAmberText
import com.example.ui.theme.OceanTeal
import com.example.ui.theme.OutlineBorder
import com.example.ui.theme.PresentGreenFill
import com.example.ui.theme.PresentGreenSolid
import com.example.ui.theme.PresentGreenStroke
import com.example.ui.theme.PresentGreenText
import com.example.ui.theme.SlateSecondary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

@Composable
fun AttendanceMarkingScreen(
    classes: List<ClassGroup>,
    students: List<Student>,
    onToggleStatus: (String) -> Unit,
    onSetStatus: (String, AttendanceStatus) -> Unit,
    onMarkAll: (String, AttendanceStatus) -> Unit,
    onSubmitAttendance: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedClass by remember { mutableStateOf(classes.firstOrNull()?.name ?: "Class 1") }
    val classStudents = students.filter { it.className == selectedClass }
    val presentCount = classStudents.count { it.status == AttendanceStatus.PRESENT }
    val absentCount = classStudents.count { it.status == AttendanceStatus.ABSENT }
    val lateCount = classStudents.count { it.status == AttendanceStatus.LATE }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFEBF1F5))
            .verticalScroll(scrollState)
            .padding(16.dp)
            .testTag("attendance_marking_screen")
    ) {
        // Class & Session Header Card
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
                            text = "Daily Attendance Session",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Today • Live Roll-Call Mode",
                            fontSize = 13.sp,
                            color = SlateSecondary
                        )
                    }

                    // 48-hour Edit Lock Pill from PRD
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(9999.dp))
                            .background(Color(0xFFFEF7E6))
                            .border(1.dp, Color(0xFFFDE68A), RoundedCornerShape(9999.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LockClock,
                            contentDescription = null,
                            tint = Color(0xFF92400E),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Editable 48h",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF92400E)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Select Class Chips
                Text(
                    text = "Select Scheduled Class / Subject",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SlateSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    classes.forEach { classGroup ->
                        val isSelected = selectedClass == classGroup.name
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedClass = classGroup.name },
                            label = { Text(classGroup.name) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = OceanTeal,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Summary Numbers and Bulk Actions
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF8FAFC))
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(text = "Total: ${classStudents.size}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        Text(text = "Present: $presentCount", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = PresentGreenText)
                        Text(text = "Absent: $absentCount", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = AbsentCoralText)
                        Text(text = "Late: $lateCount", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = LateAmberText)
                    }

                    // Bulk "Mark All Present" button
                    Button(
                        onClick = { onMarkAll(selectedClass, AttendanceStatus.PRESENT) },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = OceanTeal),
                        modifier = Modifier.testTag("mark_all_present_button")
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Mark All Present", fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Student Roster with 1-Tap Toggles
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, OutlineBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Enrolled Students Roster",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tap on any student or pill to toggle status instantly.",
                    fontSize = 13.sp,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(14.dp))

                classStudents.forEachIndexed { index, student ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onToggleStatus(student.id) }
                            .padding(vertical = 10.dp, horizontal = 6.dp),
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
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Roll No: ${student.rollNo} • Check-in: ${student.checkInTime}",
                                    fontSize = 12.sp,
                                    color = TextMuted
                                )
                            }
                        }

                        // Status Selector Buttons (Present / Absent / Late)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            AttendanceStatus.entries.forEach { status ->
                                val isActive = student.status == status
                                val (bg, stroke, text) = when (status) {
                                    AttendanceStatus.PRESENT -> Triple(PresentGreenFill, PresentGreenStroke, PresentGreenText)
                                    AttendanceStatus.ABSENT -> Triple(AbsentCoralFill, AbsentCoralStroke, AbsentCoralText)
                                    AttendanceStatus.LATE -> Triple(LateAmberFill, LateAmberStroke, LateAmberText)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isActive) bg else Color(0xFFF1F5F9))
                                        .border(1.dp, if (isActive) stroke else Color.Transparent, RoundedCornerShape(8.dp))
                                        .clickable { onSetStatus(student.id, status) }
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = status.label,
                                        fontSize = 12.sp,
                                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isActive) text else SlateSecondary
                                    )
                                }
                            }
                        }
                    }

                    if (index < classStudents.lastIndex) {
                        HorizontalDivider(color = Color(0xFFF1F5F9))
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Submit Button
                Button(
                    onClick = { onSubmitAttendance(selectedClass) },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OceanTeal),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("submit_attendance_cloud_button")
                ) {
                    Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Submit Attendance to Cloud Database",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
