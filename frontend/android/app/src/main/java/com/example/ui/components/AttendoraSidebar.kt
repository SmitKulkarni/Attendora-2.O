package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.NavTab
import com.example.ui.theme.OceanTeal
import com.example.ui.theme.SlateSecondary
import com.example.ui.theme.TextPrimary

@Composable
fun AttendoraLogo(
    modifier: Modifier = Modifier,
    iconSize: Int = 32,
    textSize: Int = 22
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Distinctive Attendora Teal loop glyph
        Box(
            modifier = Modifier
                .size(iconSize.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(OceanTeal),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Attendora Logo Icon",
                tint = Color.White,
                modifier = Modifier.size((iconSize * 0.7).dp)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "Attendora",
            fontSize = textSize.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            letterSpacing = (-0.5).sp
        )
    }
}

@Composable
fun AttendoraSidebar(
    selectedTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(220.dp)
            .background(Color(0xFFF4FAFE))
            .padding(vertical = 24.dp, horizontal = 16.dp)
    ) {
        // Logo Header
        AttendoraLogo(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .testTag("app_brand_logo")
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Navigation Items
        val items = listOf(
            SidebarItemData(NavTab.DASHBOARD, Icons.Outlined.Dashboard, Icons.Filled.Dashboard),
            SidebarItemData(NavTab.STUDENTS, Icons.Outlined.Group, Icons.Filled.Group),
            SidebarItemData(NavTab.ATTENDANCE, Icons.Outlined.Schedule, Icons.Outlined.Schedule),
            SidebarItemData(NavTab.CLASSES, Icons.Outlined.CalendarMonth, Icons.Outlined.CalendarMonth),
            SidebarItemData(NavTab.REPORTS, Icons.Outlined.Assessment, Icons.Outlined.Assessment),
            SidebarItemData(NavTab.SETTINGS, Icons.Outlined.Settings, Icons.Outlined.Settings)
        )

        items.forEach { item ->
            val isSelected = selectedTab == item.tab
            val shape = RoundedCornerShape(12.dp)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(shape)
                    .background(if (isSelected) OceanTeal else Color.Transparent)
                    .clickable { onTabSelected(item.tab) }
                    .testTag("nav_${item.tab.name.lowercase()}")
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isSelected) item.activeIcon else item.icon,
                    contentDescription = item.tab.label,
                    tint = if (isSelected) Color.White else SlateSecondary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = item.tab.label,
                    color = if (isSelected) Color.White else SlateSecondary,
                    fontSize = 15.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                )
            }
        }
    }
}

private data class SidebarItemData(
    val tab: NavTab,
    val icon: ImageVector,
    val activeIcon: ImageVector
)
