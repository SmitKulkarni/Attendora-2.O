package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import com.example.model.Student
import com.example.ui.theme.OutlineBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun StudentTableCard(
    students: List<Student>,
    onToggleStatus: (String) -> Unit,
    onStudentClick: (Student) -> Unit,
    modifier: Modifier = Modifier
) {
    val cardShape = RoundedCornerShape(16.dp)

    Column(
        modifier = modifier
            .testTag("student_list_table_card")
            .clip(cardShape)
            .background(Color.White)
            .border(1.dp, OutlineBorder, cardShape)
    ) {
        // Table Title
        Text(
            text = "Student List",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)
        )

        // Horizontal scroll to prevent clipping on mobile portrait
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
        ) {
            val minTableWidth = 680.dp

            // Header Row (soft track matching screenshot)
            Row(
                modifier = Modifier
                    .width(minTableWidth)
                    .background(Color(0xFFF8FAFC))
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Student Name",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary,
                    modifier = Modifier.width(220.dp)
                )
                Text(
                    text = "Roll No.",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary,
                    modifier = Modifier.width(130.dp)
                )
                Text(
                    text = "Class",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary,
                    modifier = Modifier.width(110.dp)
                )
                Text(
                    text = "Status",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary,
                    modifier = Modifier.width(110.dp)
                )
                Text(
                    text = "Check-In Time",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary,
                    modifier = Modifier.width(110.dp)
                )
            }

            // Student Rows
            if (students.isEmpty()) {
                Box(
                    modifier = Modifier
                        .width(minTableWidth)
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No students found matching your criteria",
                        color = TextMuted,
                        fontSize = 14.sp
                    )
                }
            } else {
                students.forEachIndexed { index, student ->
                    StudentTableRow(
                        student = student,
                        onToggleStatus = { onToggleStatus(student.id) },
                        onRowClick = { onStudentClick(student) },
                        modifier = Modifier.width(minTableWidth)
                    )
                    if (index < students.lastIndex) {
                        HorizontalDivider(
                            color = Color(0xFFF1F5F9),
                            thickness = 1.dp,
                            modifier = Modifier.width(minTableWidth)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StudentTableRow(
    student: Student,
    onToggleStatus: () -> Unit,
    onRowClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .testTag("student_row_${student.rollNo}")
            .clickable(onClick = onRowClick)
            .padding(horizontal = 24.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Name Column with Avatar
        Row(
            modifier = Modifier.width(220.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Circular Avatar matching screenshot
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(student.avatarBgColor)),
                contentAlignment = Alignment.Center
            ) {
                // Initial character or Person Icon
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
            }
        }

        // Roll No.
        Text(
            text = student.rollNo,
            fontSize = 14.sp,
            color = Color(0xFF334155),
            fontWeight = FontWeight.Normal,
            modifier = Modifier.width(130.dp)
        )

        // Class
        Text(
            text = student.className,
            fontSize = 14.sp,
            color = Color(0xFF334155),
            fontWeight = FontWeight.Normal,
            modifier = Modifier.width(110.dp)
        )

        // Status Pill
        Box(modifier = Modifier.width(110.dp)) {
            StatusPill(
                status = student.status,
                onClick = onToggleStatus
            )
        }

        // Check-In Time
        Text(
            text = student.checkInTime,
            fontSize = 14.sp,
            color = Color(0xFF334155),
            fontWeight = FontWeight.Normal,
            modifier = Modifier.width(110.dp)
        )
    }
}
