package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.OceanTeal
import com.example.ui.theme.OceanTealDark
import com.example.ui.theme.OutlineBorder
import com.example.ui.theme.PresentGreenFill
import com.example.ui.theme.PresentGreenSolid
import com.example.ui.theme.PresentGreenStroke
import com.example.ui.theme.PresentGreenText
import com.example.ui.theme.SlateSecondary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

@Composable
fun DynamicQrDialog(
    className: String,
    qrToken: String,
    secondsRemaining: Int,
    verifiedCount: Int,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progressAnimated by animateFloatAsState(
        targetValue = secondsRemaining / 10f,
        animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
        label = "qr_countdown"
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("dynamic_qr_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(OceanTeal.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCode2,
                                contentDescription = null,
                                tint = OceanTeal,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Dynamic TOTP QR Session",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = "$className • Lecture Hall A-101",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = SlateSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // The Live Rotating QR Barcode
                QrCodeCanvas(
                    payload = qrToken,
                    size = 210.dp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Countdown Timer Pill
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFF1F5F9))
                        .border(1.dp, OutlineBorder, RoundedCornerShape(20.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            progress = { progressAnimated },
                            modifier = Modifier.size(18.dp),
                            color = OceanTeal,
                            trackColor = Color(0xFFCBD5E1),
                            strokeWidth = 2.5.dp
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Refreshes in ${secondsRemaining}s (Anti-Screenshot)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SlateSecondary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Security Badges Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Geofence Badge
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        color = PresentGreenFill,
                        border = androidx.compose.foundation.BorderStroke(1.dp, PresentGreenStroke)
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = PresentGreenSolid,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text("GPS Geofence", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PresentGreenText)
                                Text("50m Radius", fontSize = 10.sp, color = PresentGreenText)
                            }
                        }
                    }

                    // Anti-Proxy Badge
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFEFF6FF),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF93C5FD))
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = Color(0xFF2563EB),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text("Anti-Proxy", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E40AF))
                                Text("1 Device/Student", fontSize = 10.sp, color = Color(0xFF1E40AF))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Real-time Verified Counter
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Verified Attendance:",
                        fontSize = 13.sp,
                        color = TextMuted
                    )
                    Text(
                        text = "$verifiedCount Students Checked In",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = PresentGreenText
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OceanTealDark)
                ) {
                    Text("End QR Attendance Session", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

