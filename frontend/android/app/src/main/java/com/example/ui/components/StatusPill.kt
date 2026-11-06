package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.model.AttendanceStatus
import com.example.ui.theme.AbsentCoralFill
import com.example.ui.theme.AbsentCoralStroke
import com.example.ui.theme.AbsentCoralText
import com.example.ui.theme.LateAmberFill
import com.example.ui.theme.LateAmberStroke
import com.example.ui.theme.LateAmberText
import com.example.ui.theme.PresentGreenFill
import com.example.ui.theme.PresentGreenStroke
import com.example.ui.theme.PresentGreenText

@Composable
fun StatusPill(
    status: AttendanceStatus,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val (bgColor, strokeColor, textColor) = when (status) {
        AttendanceStatus.PRESENT -> Triple(PresentGreenFill, PresentGreenStroke, PresentGreenText)
        AttendanceStatus.ABSENT -> Triple(AbsentCoralFill, AbsentCoralStroke, AbsentCoralText)
        AttendanceStatus.LATE -> Triple(LateAmberFill, LateAmberStroke, LateAmberText)
    }

    val shape = RoundedCornerShape(9999.dp)
    val clickModifier = if (onClick != null) {
        Modifier.clickable(onClick = onClick)
    } else Modifier

    Box(
        modifier = modifier
            .testTag("status_pill_${status.name.lowercase()}")
            .clip(shape)
            .background(bgColor)
            .border(width = 1.dp, color = strokeColor, shape = shape)
            .then(clickModifier)
            .padding(horizontal = 14.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = status.label,
            color = textColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
