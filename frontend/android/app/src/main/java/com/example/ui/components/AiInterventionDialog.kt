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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.AiInterventionPlan
import com.example.model.RiskLevel
import com.example.ui.theme.AbsentCoralSolid
import com.example.ui.theme.OceanTeal
import com.example.ui.theme.OutlineBorder
import com.example.ui.theme.PresentGreenSolid
import com.example.ui.theme.SlateSecondary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

@Composable
fun AiInterventionDialog(
    intervention: AiInterventionPlan,
    onDismiss: () -> Unit,
    onConfirmDispatch: () -> Unit
) {
    val isCritical = intervention.riskLevel == RiskLevel.CRITICAL

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .testTag("ai_intervention_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Academic Intervention Plan",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Statutory Remediation & Counseling Contract",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = SlateSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Recovery Target Strip
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isCritical) AbsentCoralSolid.copy(alpha = 0.08f) else Color(0xFFF0FDF4),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isCritical) AbsentCoralSolid.copy(alpha = 0.4f) else PresentGreenSolid.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Shortfall Target", fontSize = 11.sp, color = TextMuted)
                            Text(
                                "${intervention.currentAttendancePercent}% → ${intervention.targetAttendancePercent}%",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isCritical) AbsentCoralSolid else PresentGreenSolid
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Makeup Sessions Required", fontSize = 11.sp, color = TextMuted)
                            Text(
                                "${intervention.classesNeededForEligibility} consecutive lectures",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Remediation Action Steps
                Text("Remediation Action Items", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = SlateSecondary)
                Spacer(modifier = Modifier.height(6.dp))
                intervention.actionItems.forEachIndexed { idx, item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = OceanTeal,
                            modifier = Modifier.size(18.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("${idx + 1}", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(item, fontSize = 12.sp, color = TextPrimary, lineHeight = 16.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Counseling Memo Preview Box
                Text("Official Notice Letter Preview", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = SlateSecondary)
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFF8FAFC),
                    border = androidx.compose.foundation.BorderStroke(1.dp, OutlineBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = intervention.counselingLetter,
                        modifier = Modifier.padding(12.dp),
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF334155)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = Color(0xFFF1F5F9))
                Spacer(modifier = Modifier.height(16.dp))

                // Bottom Buttons
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
                            onConfirmDispatch()
                            onDismiss()
                        },
                        modifier = Modifier.weight(1.5f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = OceanTeal)
                    ) {
                        Text("Issue Official Notice", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

