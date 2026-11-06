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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.ui.theme.OceanTeal
import com.example.ui.theme.OutlineBorder
import com.example.ui.theme.PresentGreenFill
import com.example.ui.theme.PresentGreenStroke
import com.example.ui.theme.PresentGreenText
import com.example.ui.theme.SlateSecondary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier
) {
    var autoAlerts by remember { mutableStateOf(true) }
    var strictLock48h by remember { mutableStateOf(true) }
    var cloudRealtimeSync by remember { mutableStateOf(true) }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFEBF1F5))
            .verticalScroll(scrollState)
            .padding(16.dp)
            .testTag("settings_screen")
    ) {
        // Institution Profile Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, OutlineBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFE0F2FE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Business,
                            contentDescription = null,
                            tint = OceanTeal,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "Apex Institute of Technology",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Tenant ID: inst_edtech_9482 • Multi-Tenant Enterprise Tier",
                            fontSize = 12.sp,
                            color = SlateSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color(0xFFF1F5F9))
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "Current Academic Term", fontSize = 12.sp, color = TextMuted)
                        Text(text = "Fall Semester 2026", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    }
                    Column {
                        Text(text = "Registered Cohorts", fontSize = 12.sp, color = TextMuted)
                        Text(text = "12 Classes • 120 Students", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    }
                    Column {
                        Text(text = "System Status", fontSize = 12.sp, color = TextMuted)
                        Text(text = "99.9% Uptime", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = PresentGreenText)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // SaaS Architecture & Cloud Infrastructure Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, OutlineBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Cloud Architecture & Tenant Isolation",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "PostgreSQL Row-Level Security (RLS) + Firebase Auth JWT claims active.",
                    fontSize = 13.sp,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF8FAFC))
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Storage, contentDescription = null, tint = OceanTeal)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = "Database Isolation Boundary", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Text(text = "Shared DB with institution_id RLS check", fontSize = 12.sp, color = TextMuted)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(9999.dp))
                            .background(PresentGreenFill)
                            .border(1.dp, PresentGreenStroke, RoundedCornerShape(9999.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(text = "Enforced", color = PresentGreenText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Policies & Automation Controls
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, OutlineBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "System Policies & Alerts",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Automated Defaulter Warning Emails", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        Text(text = "Dispatch SendGrid notifications when student drops < 75%", fontSize = 12.sp, color = TextMuted)
                    }
                    Switch(
                        checked = autoAlerts,
                        onCheckedChange = { autoAlerts = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = OceanTeal)
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF1F5F9))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "48-Hour Faculty Edit Lock (FR-TCH-03)", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        Text(text = "Lock past attendance from edits after 48h without Admin override", fontSize = 12.sp, color = TextMuted)
                    }
                    Switch(
                        checked = strictLock48h,
                        onCheckedChange = { strictLock48h = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = OceanTeal)
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF1F5F9))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Realtime Cloud Sync", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        Text(text = "Stream submission events via Celery and Redis", fontSize = 12.sp, color = TextMuted)
                    }
                    Switch(
                        checked = cloudRealtimeSync,
                        onCheckedChange = { cloudRealtimeSync = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = OceanTeal)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
