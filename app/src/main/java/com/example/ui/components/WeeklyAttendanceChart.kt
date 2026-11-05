package com.example.ui.components

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.WeeklyBarData
import com.example.ui.theme.AbsentCoralStroke
import com.example.ui.theme.OceanTeal
import com.example.ui.theme.OutlineBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

@Composable
fun WeeklyAttendanceCard(
    weeklyData: List<WeeklyBarData>,
    modifier: Modifier = Modifier
) {
    val cardShape = RoundedCornerShape(16.dp)

    Column(
        modifier = modifier
            .testTag("weekly_attendance_card")
            .clip(cardShape)
            .background(Color.White)
            .border(1.dp, OutlineBorder, cardShape)
            .padding(20.dp)
    ) {
        // Title & Legend Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Weekly Attendance Overview",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            // Legend
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(9.dp)
                            .clip(CircleShape)
                            .background(OceanTeal)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Present",
                        fontSize = 12.sp,
                        color = TextMuted,
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(9.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFDC6868)) // Coral bar color
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Absent",
                        fontSize = 12.sp,
                        color = TextMuted,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Chart Area
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        ) {
            // Y-Axis (100, 80, 60, 40, 20, 0)
            Column(
                modifier = Modifier
                    .height(130.dp)
                    .padding(end = 12.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.End
            ) {
                listOf("100", "80", "60", "40", "20", "0").forEach { label ->
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        color = TextMuted,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Canvas for grouped bars
            Column(modifier = Modifier.weight(1f)) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                ) {
                    val width = size.width
                    val height = size.height

                    // Grid lines
                    val steps = 5
                    for (i in 0..steps) {
                        val y = height * (i.toFloat() / steps)
                        drawLine(
                            color = Color(0xFFF1F5F9),
                            start = Offset(0f, y),
                            end = Offset(width, y),
                            strokeWidth = 1f
                        )
                    }

                    if (weeklyData.isEmpty()) return@Canvas

                    val groupCount = weeklyData.size
                    val groupWidth = width / groupCount
                    val barWidth = (groupWidth * 0.28f).coerceAtMost(16.dp.toPx())
                    val barSpacing = 4.dp.toPx()
                    val cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())

                    weeklyData.forEachIndexed { index, item ->
                        val groupCenterX = groupWidth * index + (groupWidth / 2f)

                        // Present Bar (Teal)
                        val presentHeight = (item.present / 100f).coerceIn(0f, 1f) * height
                        val presentLeft = groupCenterX - barWidth - (barSpacing / 2f)
                        val presentTop = height - presentHeight

                        drawRoundRect(
                            color = OceanTeal,
                            topLeft = Offset(presentLeft, presentTop),
                            size = Size(barWidth, presentHeight),
                            cornerRadius = cornerRadius
                        )

                        // Absent Bar (Coral)
                        val absentHeight = (item.absent / 100f).coerceIn(0f, 1f) * height
                        val absentLeft = groupCenterX + (barSpacing / 2f)
                        val absentTop = height - absentHeight

                        drawRoundRect(
                            color = Color(0xFFDC6868),
                            topLeft = Offset(absentLeft, absentTop),
                            size = Size(barWidth, absentHeight),
                            cornerRadius = cornerRadius
                        )
                    }
                }

                // X-Axis labels: Mon, Tue, Wed, Thu, Fri
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    weeklyData.forEach { data ->
                        Text(
                            text = data.day,
                            fontSize = 12.sp,
                            color = TextMuted,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
