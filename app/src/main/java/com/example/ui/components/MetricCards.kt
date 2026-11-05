package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AttendanceSummaryMetrics
import com.example.ui.theme.OutlineBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MetricCardsRow(
    metrics: AttendanceSummaryMetrics,
    modifier: Modifier = Modifier
) {
    // 4 cards matching the screenshot
    val cards = listOf(
        MetricCardData(
            label = "Total Students:",
            value = "${metrics.totalStudents}",
            bgColor = Color.White,
            borderColor = OutlineBorder,
            testTag = "metric_total_students"
        ),
        MetricCardData(
            label = "Today's Attendance:",
            value = "${metrics.todayAttendancePercent}%",
            bgColor = Color(0xFFE5F5F7), // Soft pale teal tint
            borderColor = Color(0xFFC7E7EB),
            testTag = "metric_today_attendance"
        ),
        MetricCardData(
            label = "On-Time:",
            value = "${metrics.onTimePercent}%",
            bgColor = Color(0xFFE8F8F2), // Soft emerald tint
            borderColor = Color(0xFFC6EFE0),
            testTag = "metric_on_time"
        ),
        MetricCardData(
            label = "Absent:",
            value = "${metrics.absentCount}",
            bgColor = Color(0xFFFDECEB), // Soft peach/coral tint
            borderColor = Color(0xFFF9D1CF),
            testTag = "metric_absent"
        )
    )

    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        maxItemsInEachRow = 4
    ) {
        cards.forEach { card ->
            MetricCard(
                data = card,
                modifier = Modifier
                    .weight(1f)
            )
        }
    }
}

@Composable
fun MetricCard(
    data: MetricCardData,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(16.dp)

    Column(
        modifier = modifier
            .testTag(data.testTag)
            .clip(shape)
            .background(data.bgColor)
            .border(1.dp, data.borderColor, shape)
            .padding(horizontal = 20.dp, vertical = 18.dp)
    ) {
        Text(
            text = data.label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF334155)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = data.value,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            letterSpacing = (-0.5).sp
        )
    }
}

data class MetricCardData(
    val label: String,
    val value: String,
    val bgColor: Color,
    val borderColor: Color,
    val testTag: String
)
