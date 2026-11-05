package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.AttendanceSummaryMetrics
import com.example.model.Student
import com.example.model.TrendPoint
import com.example.model.WeeklyBarData
import com.example.ui.components.AttendanceTrendsCard
import com.example.ui.components.MetricCardsRow
import com.example.ui.components.StudentTableCard
import com.example.ui.components.WeeklyAttendanceCard

@Composable
fun AdminDashboardScreen(
    metrics: AttendanceSummaryMetrics,
    trendPoints: List<TrendPoint>,
    weeklyBars: List<WeeklyBarData>,
    students: List<Student>,
    onToggleStatus: (String) -> Unit,
    onStudentClick: (Student) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFEBF1F5)) // Slate ice canvas background
            .verticalScroll(scrollState)
            .padding(16.dp)
            .testTag("admin_dashboard_screen")
    ) {
        // Top 4 Metric Cards
        MetricCardsRow(
            metrics = metrics,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Analytics Charts Row (Side-by-side on wide screens, stacked on compact screens)
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            if (maxWidth >= 720.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AttendanceTrendsCard(
                        trendPoints = trendPoints,
                        modifier = Modifier.weight(1.1f)
                    )
                    WeeklyAttendanceCard(
                        weeklyData = weeklyBars,
                        modifier = Modifier.weight(0.9f)
                    )
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AttendanceTrendsCard(
                        trendPoints = trendPoints,
                        modifier = Modifier.fillMaxWidth()
                    )
                    WeeklyAttendanceCard(
                        weeklyData = weeklyBars,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Student List Table
        StudentTableCard(
            students = students,
            onToggleStatus = onToggleStatus,
            onStudentClick = onStudentClick,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}
