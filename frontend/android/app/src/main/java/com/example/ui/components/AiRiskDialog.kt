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
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.AiRiskAssessment
import com.example.model.RiskLevel
import com.example.model.Student
import com.example.ui.theme.AbsentCoralSolid
import com.example.ui.theme.OceanTeal
import com.example.ui.theme.OutlineBorder
import com.example.ui.theme.PresentGreenSolid
import com.example.ui.theme.SlateSecondary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

@Composable
fun AiRiskDialog(
    student: Student,
    riskAssessment: AiRiskAssessment,
    onDismiss: () -> Unit,
    onDraftIntervention: () -> Unit
) {
    val tierColor = when (riskAssessment.riskLevel) {
        RiskLevel.CRITICAL -> AbsentCoralSolid
        RiskLevel.HIGH -> Color(0xFFF97316)
        RiskLevel.MODERATE -> Color(0xFFF59E0B)
        RiskLevel.LOW -> PresentGreenSolid
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .testTag("ai_risk_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header with Gemini Branding
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = OceanTeal.copy(alpha = 0.12f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("✦", fontSize = 18.sp, color = OceanTeal, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Gemini Predictive AI",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Attendance Anomaly & Debarment Risk",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = SlateSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Student Identity Strip
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF8FAFC),
                    border = androidx.compose.foundation.BorderStroke(1.dp, OutlineBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(student.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text(
                                "Roll: ${student.rollNo} • ${student.department}",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                "${student.overallPercentage}%",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (student.overallPercentage >= 75f) PresentGreenSolid else AbsentCoralSolid
                            )
                            Text("Current Standing", fontSize = 11.sp, color = TextMuted)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Risk Tier Hero Card
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = tierColor.copy(alpha = 0.08f),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, tierColor.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = tierColor
                            ) {
                                Text(
                                    text = riskAssessment.riskLevel.label.uppercase(),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Text(
                                text = "Risk Index: ${riskAssessment.riskScore.toInt()}/100",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = tierColor
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Projected Semester End", fontSize = 11.sp, color = TextMuted)
                                Text(
                                    "${riskAssessment.predictedFinalPercentage}%",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Model Confidence", fontSize = 11.sp, color = TextMuted)
                                Text(
                                    "${(riskAssessment.confidenceScore * 100).toInt()}%",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OceanTeal
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // AI Summary Box
                Text("Predictive Diagnostic", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = SlateSecondary)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = riskAssessment.aiSummary,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Risk Factors
                if (riskAssessment.riskFactors.isNotEmpty()) {
                    Text("Observed Risk Factors", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = SlateSecondary)
                    Spacer(modifier = Modifier.height(6.dp))
                    riskAssessment.riskFactors.forEach { factor ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text("• ", fontSize = 14.sp, color = tierColor, fontWeight = FontWeight.Bold)
                            Text(factor, fontSize = 12.sp, color = TextPrimary)
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // AI Recommendations
                if (riskAssessment.recommendations.isNotEmpty()) {
                    Text("Advisory Interventions", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = SlateSecondary)
                    Spacer(modifier = Modifier.height(6.dp))
                    riskAssessment.recommendations.forEach { rec ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text("✓ ", fontSize = 12.sp, color = OceanTeal, fontWeight = FontWeight.Bold)
                            Text(rec, fontSize = 12.sp, color = TextPrimary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = Color(0xFFF1F5F9))
                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Close", fontSize = 13.sp, color = SlateSecondary)
                    }

                    Button(
                        onClick = {
                            onDismiss()
                            onDraftIntervention()
                        },
                        modifier = Modifier.weight(1.5f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = OceanTeal)
                    ) {
                        Text("Draft Plan 📄", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

