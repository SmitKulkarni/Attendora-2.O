package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AbsentCoralSolid
import com.example.ui.theme.OceanTeal
import com.example.ui.theme.OceanTealDark
import com.example.ui.theme.OutlineBorder
import com.example.ui.theme.PresentGreenFill
import com.example.ui.theme.PresentGreenStroke
import com.example.ui.theme.PresentGreenText
import com.example.ui.theme.SlateSecondary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateClassScreen(
    onNavigateBack: () -> Unit,
    onCreateClass: (
        name: String,
        code: String,
        department: String,
        teacherName: String,
        courseName: String,
        academicTerm: String,
        enrolledCount: Int,
        scheduleTime: String,
        roomLocation: String
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    // Form States
    var courseName by remember { mutableStateOf("") }
    var academicTerm by remember { mutableStateOf("Fall 2026") }
    var className by remember { mutableStateOf("") }
    var courseCode by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("Computer Science") }
    var teacherName by remember { mutableStateOf("") }
    var scheduleTime by remember { mutableStateOf("Mon & Wed • 09:00 AM - 10:30 AM") }
    var roomLocation by remember { mutableStateOf("Lecture Hall 101") }
    var studentCapacity by remember { mutableIntStateOf(60) }
    var minAttendanceThreshold by remember { mutableFloatStateOf(75f) }

    // Validation State
    var showErrors by remember { mutableStateOf(false) }

    val courseNameError = showErrors && courseName.isBlank()
    val academicTermError = showErrors && academicTerm.isBlank()
    val classNameError = showErrors && className.isBlank()
    val courseCodeError = showErrors && courseCode.isBlank()
    val teacherNameError = showErrors && teacherName.isBlank()

    val scrollState = rememberScrollState()

    val termSuggestions = listOf("Fall 2026", "Spring 2027", "Summer 2027", "Winter 2026")
    val deptSuggestions = listOf("Computer Science", "Information Technology", "Data Science", "Electronics & Comm.")
    val scheduleSuggestions = listOf(
        "Mon & Wed • 09:00 AM - 10:30 AM",
        "Tue & Thu • 11:00 AM - 12:30 PM",
        "Mon & Wed • 02:00 PM - 03:30 PM",
        "Friday • 09:00 AM - 12:00 PM"
    )

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("create_class_screen"),
        topBar = {
            Surface(
                color = Color(0xFFF4FAFE),
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier.testTag("create_class_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = TextPrimary
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Create New Class",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Attendora Academic Architecture",
                                fontSize = 12.sp,
                                color = SlateSecondary
                            )
                        }
                    }

                    TextButton(
                        onClick = {
                            courseName = ""
                            academicTerm = "Fall 2026"
                            className = ""
                            courseCode = ""
                            teacherName = ""
                            showErrors = false
                        }
                    ) {
                        Text("Reset", color = SlateSecondary, fontSize = 13.sp)
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                color = Color.White,
                tonalElevation = 8.dp,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = SlateSecondary)
                    }

                    Button(
                        onClick = {
                            if (courseName.isBlank() || academicTerm.isBlank() || className.isBlank() || courseCode.isBlank() || teacherName.isBlank()) {
                                showErrors = true
                            } else {
                                onCreateClass(
                                    className.trim(),
                                    courseCode.trim().uppercase(),
                                    department.trim(),
                                    teacherName.trim(),
                                    courseName.trim(),
                                    academicTerm.trim(),
                                    studentCapacity,
                                    scheduleTime.trim(),
                                    roomLocation.trim()
                                )
                                onNavigateBack()
                            }
                        },
                        modifier = Modifier
                            .weight(1.5f)
                            .height(50.dp)
                            .testTag("create_class_submit_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = OceanTeal)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Create Class", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFEBF1F5))
                .verticalScroll(scrollState)
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Card 1: Course & Academic Term (Primary Requirement)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, OutlineBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFE0F7FA)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = OceanTeal,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Course & Term Details",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Mandatory curriculum mapping for attendance rosters",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Field 1: Course Name (REQUIRED)
                    Text(
                        text = "Course Name *",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = courseName,
                        onValueChange = {
                            courseName = it
                            if (it.isNotBlank()) showErrors = false
                        },
                        placeholder = { Text("e.g. Artificial Intelligence & Machine Learning") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.School, contentDescription = null, tint = OceanTeal)
                        },
                        isError = courseNameError,
                        supportingText = {
                            if (courseNameError) {
                                Text("Course name is required", color = AbsentCoralSolid, fontSize = 11.sp)
                            } else {
                                Text("Full academic course title as listed in curriculum", color = TextMuted, fontSize = 11.sp)
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OceanTeal,
                            unfocusedBorderColor = OutlineBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("course_name_input")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Field 2: Academic Term (REQUIRED)
                    Text(
                        text = "Academic Term *",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = academicTerm,
                        onValueChange = {
                            academicTerm = it
                            if (it.isNotBlank()) showErrors = false
                        },
                        placeholder = { Text("e.g. Fall 2026, Spring 2027") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.DateRange, contentDescription = null, tint = OceanTeal)
                        },
                        isError = academicTermError,
                        supportingText = {
                            if (academicTermError) {
                                Text("Academic term is required", color = AbsentCoralSolid, fontSize = 11.sp)
                            } else {
                                Text("Active semester or session for this class cohort", color = TextMuted, fontSize = 11.sp)
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OceanTeal,
                            unfocusedBorderColor = OutlineBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("academic_term_input")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Term Quick-Suggestion Chips
                    Text(
                        text = "Quick Select Term:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = SlateSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        termSuggestions.forEach { term ->
                            val isSelected = academicTerm == term
                            FilterChip(
                                selected = isSelected,
                                onClick = { academicTerm = term },
                                label = { Text(term, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = OceanTeal,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // Card 2: Class Identifier & Course Code
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, OutlineBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFE0F7FA)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Class,
                                contentDescription = null,
                                tint = OceanTeal,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Class & Cohort Identification",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Section identifier and institutional catalog code",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Class Name (e.g. Class 1 / CS 2nd Year)
                    Text(
                        text = "Class / Section Name *",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = className,
                        onValueChange = {
                            className = it
                            if (it.isNotBlank()) showErrors = false
                        },
                        placeholder = { Text("e.g. Class 1 or CS 2nd Year - Sec A") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Class, contentDescription = null, tint = OceanTeal)
                        },
                        isError = classNameError,
                        supportingText = {
                            if (classNameError) {
                                Text("Class name is required", color = AbsentCoralSolid, fontSize = 11.sp)
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OceanTeal,
                            unfocusedBorderColor = OutlineBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("class_name_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Course Code
                    Text(
                        text = "Course / Subject Code *",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = courseCode,
                        onValueChange = {
                            courseCode = it
                            if (it.isNotBlank()) showErrors = false
                        },
                        placeholder = { Text("e.g. CS-501 or CS-2A") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Tag, contentDescription = null, tint = OceanTeal)
                        },
                        isError = courseCodeError,
                        supportingText = {
                            if (courseCodeError) {
                                Text("Course code is required", color = AbsentCoralSolid, fontSize = 11.sp)
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OceanTeal,
                            unfocusedBorderColor = OutlineBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("course_code_input")
                    )
                }
            }

            // Card 3: Faculty & Department Assignment
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, OutlineBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFE0F7FA)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = OceanTeal,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Faculty & Department Mapping",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Assign primary faculty instructor and department",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "Assigned Faculty / Instructor Name *",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = teacherName,
                        onValueChange = {
                            teacherName = it
                            if (it.isNotBlank()) showErrors = false
                        },
                        placeholder = { Text("e.g. Prof. Priya Saxena or Dr. Arvind Sharma") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = OceanTeal)
                        },
                        isError = teacherNameError,
                        supportingText = {
                            if (teacherNameError) {
                                Text("Assigned teacher name is required", color = AbsentCoralSolid, fontSize = 11.sp)
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OceanTeal,
                            unfocusedBorderColor = OutlineBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("teacher_name_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Department / School",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        deptSuggestions.forEach { dept ->
                            val isSelected = department == dept
                            FilterChip(
                                selected = isSelected,
                                onClick = { department = dept },
                                label = { Text(dept, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = OceanTeal,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // Card 4: Schedule, Hall & Capacity
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, OutlineBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFE0F7FA)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = OceanTeal,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Timetable & Roster Settings",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Meeting times, lecture room, and student limits",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "Class Schedule",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = scheduleTime,
                        onValueChange = { scheduleTime = it },
                        placeholder = { Text("e.g. Mon & Wed • 09:00 AM - 10:30 AM") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Schedule, contentDescription = null, tint = OceanTeal)
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OceanTeal,
                            unfocusedBorderColor = OutlineBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        scheduleSuggestions.forEach { sched ->
                            val isSelected = scheduleTime == sched
                            FilterChip(
                                selected = isSelected,
                                onClick = { scheduleTime = sched },
                                label = { Text(sched, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = OceanTeal,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Room Location
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Lecture Hall / Room",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = roomLocation,
                                onValueChange = { roomLocation = it },
                                placeholder = { Text("e.g. Hall 101") },
                                leadingIcon = {
                                    Icon(imageVector = Icons.Default.MeetingRoom, contentDescription = null, tint = OceanTeal)
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = OceanTeal,
                                    unfocusedBorderColor = OutlineBorder
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        // Student Capacity Stepper
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Max Enrolled Capacity",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .border(1.dp, OutlineBorder, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                IconButton(
                                    onClick = { if (studentCapacity > 5) studentCapacity -= 5 },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease", tint = SlateSecondary)
                                }
                                Text(
                                    text = "$studentCapacity",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                IconButton(
                                    onClick = { if (studentCapacity < 200) studentCapacity += 5 },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = "Increase", tint = OceanTeal)
                                }
                            }
                        }
                    }
                }
            }

            // Card 5: Live Real-time Class Card Preview
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, OutlineBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Class Card Preview",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlateSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Preview Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFF8FAFC))
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color(0xFFE0F2FE)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Class,
                                            contentDescription = null,
                                            tint = OceanTeal,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = if (className.isNotBlank()) className else "Class 1",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = if (courseName.isNotBlank()) courseName else "Course Name Not Set",
                                            fontSize = 13.sp,
                                            color = OceanTealDark,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }

                                // Academic Term Badge
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(9999.dp))
                                        .background(PresentGreenFill)
                                        .border(1.dp, PresentGreenStroke, RoundedCornerShape(9999.dp))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = academicTerm,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PresentGreenText
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = Color(0xFFEDF2F7))
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Code: ${if (courseCode.isNotBlank()) courseCode.uppercase() else "CODE"} • $department",
                                    fontSize = 12.sp,
                                    color = SlateSecondary
                                )
                                Text(
                                    text = "Capacity: $studentCapacity",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Faculty: ${if (teacherName.isNotBlank()) teacherName else "Unassigned"}",
                                    fontSize = 12.sp,
                                    color = TextMuted
                                )
                                Text(
                                    text = "$scheduleTime • $roomLocation",
                                    fontSize = 12.sp,
                                    color = TextMuted
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
