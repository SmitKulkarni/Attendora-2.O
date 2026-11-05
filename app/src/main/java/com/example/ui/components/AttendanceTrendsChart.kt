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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TrendPoint
import com.example.ui.theme.ChartGradientEnd
import com.example.ui.theme.ChartGradientStart
import com.example.ui.theme.ChartLineTeal
import com.example.ui.theme.OutlineBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

@Composable
fun AttendanceTrendsCard(
    trendPoints: List<TrendPoint>,
    modifier: Modifier = Modifier
) {
    val cardShape = RoundedCornerShape(16.dp)

    Column(
        modifier = modifier
            .testTag("attendance_trends_card")
            .clip(cardShape)
            .background(Color.White)
            .border(1.dp, OutlineBorder, cardShape)
            .padding(20.dp)
    ) {
        Text(
            text = "Attendance Trends",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Chart Box with Y-axis labels and Canvas
        Row(modifier = Modifier.fillMaxWidth().height(160.dp)) {
            // Y-Axis labels (120, 80, 40, 0)
            Column(
                modifier = Modifier
                    .height(130.dp)
                    .padding(end = 12.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.End
            ) {
                listOf("120", "80", "40", "0").forEach { label ->
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        color = TextMuted,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Canvas with smooth curve
            Column(modifier = Modifier.weight(1f)) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                ) {
                    val width = size.width
                    val height = size.height

                    // Draw subtle grid lines
                    val gridSteps = 3
                    for (i in 0..gridSteps) {
                        val y = height * (i.toFloat() / gridSteps)
                        drawLine(
                            color = Color(0xFFF1F5F9),
                            start = Offset(0f, y),
                            end = Offset(width, y),
                            strokeWidth = 1f
                        )
                    }

                    // We draw the exact gentle smooth wave shown in the screenshot:
                    // Starts at ~10 (Sep), rises to ~70, dips to ~50 (Oct), then rises to ~98 (Nov)
                    val p0 = Offset(0f, height * 0.95f)
                    val p1 = Offset(width * 0.28f, height * 0.35f)
                    val p2 = Offset(width * 0.52f, height * 0.65f)
                    val p3 = Offset(width * 0.95f, height * 0.22f)
                    val pEnd = Offset(width, height * 0.22f)

                    val linePath = Path().apply {
                        moveTo(p0.x, p0.y)
                        // Cubic curve 1
                        cubicTo(
                            width * 0.12f, height * 0.80f,
                            width * 0.18f, height * 0.35f,
                            p1.x, p1.y
                        )
                        // Cubic curve 2: dip down
                        cubicTo(
                            width * 0.38f, height * 0.35f,
                            width * 0.42f, height * 0.65f,
                            p2.x, p2.y
                        )
                        // Cubic curve 3: rise up to high attendance
                        cubicTo(
                            width * 0.65f, height * 0.65f,
                            width * 0.78f, height * 0.22f,
                            p3.x, p3.y
                        )
                        lineTo(pEnd.x, pEnd.y)
                    }

                    // Fill Gradient Path
                    val fillPath = Path().apply {
                        addPath(linePath)
                        lineTo(width, height)
                        lineTo(0f, height)
                        close()
                    }

                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0x552E8B9A),
                                Color(0x182E8B9A),
                                Color(0x022E8B9A)
                            ),
                            startY = 0f,
                            endY = height
                        )
                    )

                    // Draw the stroke
                    drawPath(
                        path = linePath,
                        color = ChartLineTeal,
                        style = Stroke(
                            width = 3.5.dp.toPx(),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }

                // X-Axis labels: Sep, Oct, Nov
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Sep", fontSize = 12.sp, color = TextMuted, fontWeight = FontWeight.Medium)
                    Text(text = "Oct", fontSize = 12.sp, color = TextMuted, fontWeight = FontWeight.Medium)
                    Text(text = "Nov", fontSize = 12.sp, color = TextMuted, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}
