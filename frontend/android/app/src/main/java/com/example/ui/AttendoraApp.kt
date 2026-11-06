package com.example.ui

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.NavTab
import com.example.model.UserRole
import com.example.ui.components.AiInterventionDialog
import com.example.ui.components.AiRiskDialog
import com.example.ui.components.AttendoraSidebar
import com.example.ui.components.AttendoraTopBar
import com.example.ui.components.DynamicQrDialog
import com.example.ui.components.StudentDetailDialog
import com.example.ui.components.StudentQrScannerDialog
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.AttendanceMarkingScreen
import com.example.ui.screens.ClassesManagementScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.StudentMobileScreen
import com.example.ui.screens.StudentsManagementScreen
import com.example.ui.theme.OceanTeal
import com.example.ui.theme.SlateSecondary
import com.example.viewmodel.AttendoraViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun AttendoraApp(
    viewModel: AttendoraViewModel = viewModel()
) {
    val currentRole by viewModel.currentRole.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedClassFilter by viewModel.selectedClassFilter.collectAsState()
    val students by viewModel.students.collectAsState()
    val classes by viewModel.classes.collectAsState()
    val notices by viewModel.notices.collectAsState()
    val trendPoints by viewModel.trendPoints.collectAsState()
    val weeklyBars by viewModel.weeklyBars.collectAsState()
    val studentDayLogs by viewModel.studentDayLogs.collectAsState()
    val activeStudentDetail by viewModel.activeStudentDetail.collectAsState()
    val alertThreshold by viewModel.alertThreshold.collectAsState()
    val isQrSessionActive by viewModel.isQrSessionActive.collectAsState()
    val qrToken by viewModel.qrToken.collectAsState()
    val qrSecondsRemaining by viewModel.qrSecondsRemaining.collectAsState()
    val qrVerifiedCount by viewModel.qrVerifiedCount.collectAsState()
    val activeAiRisk by viewModel.activeAiRisk.collectAsState()
    val activeInterventionPlan by viewModel.activeInterventionPlan.collectAsState()
    val enterpriseCampuses by viewModel.enterpriseCampuses.collectAsState()
    var isStudentScannerOpen by remember { androidx.compose.runtime.mutableStateOf(false) }

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Handle back button when drawer is open or secondary screens are active
    if (drawerState.isOpen) {
        BackHandler {
            scope.launch { drawerState.close() }
        }
    } else if (selectedTab != NavTab.DASHBOARD && currentRole != UserRole.STUDENT) {
        BackHandler {
            viewModel.switchTab(NavTab.DASHBOARD)
        }
    } else if (currentRole == UserRole.STUDENT) {
        BackHandler {
            viewModel.switchRole(UserRole.ADMIN)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.toastEvent.collectLatest { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            snackbarHostState.showSnackbar(msg)
        }
    }

    // Detail dialog with AI integration
    activeStudentDetail?.let { student ->
        StudentDetailDialog(
            student = student,
            onDismiss = { viewModel.selectStudentForDetail(null) },
            onStatusChange = { newStatus ->
                viewModel.setStudentAttendance(student.id, newStatus)
                viewModel.selectStudentForDetail(null)
            },
            onAiEvaluate = {
                viewModel.evaluateAiRisk(student)
            },
            onGenerateIntervention = {
                viewModel.generateInterventionPlan(student)
            }
        )
    }

    // Phase 6: Gemini AI Predictive Risk Dialog
    activeAiRisk?.let { risk ->
        activeStudentDetail?.let { student ->
            AiRiskDialog(
                student = student,
                riskAssessment = risk,
                onDismiss = { viewModel.dismissAiRiskModal() },
                onDraftIntervention = {
                    viewModel.dismissAiRiskModal()
                    viewModel.generateInterventionPlan(student)
                }
            )
        }
    }

    // Phase 6: Automated Academic Intervention Dialog
    activeInterventionPlan?.let { plan ->
        AiInterventionDialog(
            intervention = plan,
            onDismiss = { viewModel.dismissInterventionModal() },
            onConfirmDispatch = {
                viewModel.sendDefaulterAlerts()
            }
        )
    }

    // Phase 5: Dynamic QR Session Dialog for Faculty
    if (isQrSessionActive) {
        DynamicQrDialog(
            className = selectedClassFilter,
            qrToken = qrToken,
            secondsRemaining = qrSecondsRemaining,
            verifiedCount = qrVerifiedCount,
            onDismiss = { viewModel.stopQrSession() }
        )
    }

    // Phase 5: Student QR Scanner Dialog
    if (isStudentScannerOpen) {
        StudentQrScannerDialog(
            studentName = viewModel.getSelectedStudent().name,
            onCheckInSuccess = {
                viewModel.checkInViaQr(viewModel.getSelectedStudent().id)
            },
            onDismiss = { isStudentScannerOpen = false }
        )
    }

    if (currentRole == UserRole.STUDENT) {
        // STUDENT MOBILE VIEW
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                AttendoraTopBar(
                    searchQuery = searchQuery,
                    onSearchChange = { viewModel.setSearchQuery(it) },
                    currentRole = currentRole,
                    onRoleChange = { viewModel.switchRole(it) },
                    notices = notices,
                    onNoticeClick = {
                        scope.launch {
                            snackbarHostState.showSnackbar("Logged in as student Smit Kulkarni (Roll No 2016001)")
                        }
                    },
                    onMenuClick = null
                )
            }
        ) { paddingValues ->
            StudentMobileScreen(
                student = viewModel.getSelectedStudent(),
                dayLogs = studentDayLogs,
                onDownloadTranscript = {
                    viewModel.exportDefaulterReport("Transcript")
                },
                onApplyLeave = {
                    scope.launch {
                        snackbarHostState.showSnackbar("Leave Application submitted to Faculty Office")
                    }
                },
                onScanQr = { isStudentScannerOpen = true },
                modifier = Modifier.padding(paddingValues)
            )
        }
    } else {
        // ADMIN / FACULTY WEB & SAAS PORTAL VIEW
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isWideScreen = maxWidth >= 800.dp

            if (isWideScreen) {
                // Wide layout: Left pinned sidebar + Right main content
                Row(modifier = Modifier.fillMaxSize()) {
                    AttendoraSidebar(
                        selectedTab = selectedTab,
                        onTabSelected = { viewModel.switchTab(it) }
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        AttendoraTopBar(
                            searchQuery = searchQuery,
                            onSearchChange = { viewModel.setSearchQuery(it) },
                            currentRole = currentRole,
                            onRoleChange = { viewModel.switchRole(it) },
                            notices = notices,
                            onNoticeClick = { viewModel.switchTab(NavTab.REPORTS) },
                            onMenuClick = null
                        )

                        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                            when (selectedTab) {
                                NavTab.DASHBOARD -> AdminDashboardScreen(
                                    metrics = viewModel.getSummaryMetrics(),
                                    trendPoints = trendPoints,
                                    weeklyBars = weeklyBars,
                                    students = viewModel.getFilteredStudents(),
                                    onToggleStatus = { viewModel.toggleStudentAttendance(it) },
                                    onStudentClick = { viewModel.selectStudentForDetail(it) }
                                )
                                NavTab.STUDENTS -> StudentsManagementScreen(
                                    students = viewModel.getFilteredStudents(),
                                    selectedClassFilter = selectedClassFilter,
                                    onClassFilterChange = { viewModel.setClassFilter(it) },
                                    onStudentClick = { viewModel.selectStudentForDetail(it) },
                                    onToggleStatus = { viewModel.toggleStudentAttendance(it) },
                                    onAddNewStudent = { name, roll, cls, dept, email ->
                                        viewModel.addNewStudent(name, roll, cls, dept, email)
                                    }
                                )
                                NavTab.ATTENDANCE -> AttendanceMarkingScreen(
                                    classes = classes,
                                    students = students,
                                    onToggleStatus = { viewModel.toggleStudentAttendance(it) },
                                    onSetStatus = { id, status -> viewModel.setStudentAttendance(id, status) },
                                    onMarkAll = { cls, status -> viewModel.markAllInClass(cls, status) },
                                    onSubmitAttendance = { viewModel.submitAttendanceSession(it) },
                                    onStartQrSession = { viewModel.startQrSession(it) }
                                )
                                NavTab.CLASSES -> ClassesManagementScreen(
                                    classes = classes,
                                    onAddNewClass = { name, code, dept, teacher, course, term, count, schedule, room ->
                                        viewModel.addNewClass(name, code, dept, teacher, course, term, count, schedule, room)
                                    }
                                )
                                NavTab.REPORTS -> ReportsScreen(
                                    students = students,
                                    threshold = alertThreshold,
                                    onThresholdChange = { viewModel.setThreshold(it) },
                                    onExportReport = { viewModel.exportDefaulterReport(it) },
                                    onSendAlerts = { viewModel.sendDefaulterAlerts() },
                                    onAiEvaluateStudent = { student ->
                                        viewModel.selectStudentForDetail(student)
                                        viewModel.evaluateAiRisk(student)
                                    },
                                    enterpriseCampuses = enterpriseCampuses
                                )
                                NavTab.SETTINGS -> SettingsScreen()
                            }
                        }
                    }
                }
            } else {
                // Mobile Portrait layout with drawer and bottom bar
                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet {
                            AttendoraSidebar(
                                selectedTab = selectedTab,
                                onTabSelected = {
                                    viewModel.switchTab(it)
                                    scope.launch { drawerState.close() }
                                }
                            )
                        }
                    }
                ) {
                    Scaffold(
                        snackbarHost = { SnackbarHost(snackbarHostState) },
                        topBar = {
                            AttendoraTopBar(
                                searchQuery = searchQuery,
                                onSearchChange = { viewModel.setSearchQuery(it) },
                                currentRole = currentRole,
                                onRoleChange = { viewModel.switchRole(it) },
                                notices = notices,
                                onNoticeClick = { viewModel.switchTab(NavTab.REPORTS) },
                                onMenuClick = {
                                    scope.launch { drawerState.open() }
                                }
                            )
                        },
                        bottomBar = {
                            NavigationBar(
                                containerColor = Color.White,
                                tonalElevation = 8.dp
                            ) {
                                val navItems = listOf(
                                    Triple(NavTab.DASHBOARD, Icons.Default.Dashboard, "Dashboard"),
                                    Triple(NavTab.STUDENTS, Icons.Default.Group, "Students"),
                                    Triple(NavTab.ATTENDANCE, Icons.Default.Schedule, "Attendance"),
                                    Triple(NavTab.CLASSES, Icons.Default.CalendarMonth, "Classes"),
                                    Triple(NavTab.REPORTS, Icons.Default.Assessment, "Reports")
                                )

                                navItems.forEach { (tab, icon, label) ->
                                    val isSelected = selectedTab == tab
                                    NavigationBarItem(
                                        selected = isSelected,
                                        onClick = { viewModel.switchTab(tab) },
                                        icon = { Icon(icon, contentDescription = label) },
                                        label = { Text(label, maxLines = 1) },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = OceanTeal,
                                            selectedTextColor = OceanTeal,
                                            indicatorColor = Color(0xFFE0F7FA),
                                            unselectedIconColor = SlateSecondary,
                                            unselectedTextColor = SlateSecondary
                                        )
                                    )
                                }
                            }
                        }
                    ) { paddingValues ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues)
                        ) {
                            when (selectedTab) {
                                NavTab.DASHBOARD -> AdminDashboardScreen(
                                    metrics = viewModel.getSummaryMetrics(),
                                    trendPoints = trendPoints,
                                    weeklyBars = weeklyBars,
                                    students = viewModel.getFilteredStudents(),
                                    onToggleStatus = { viewModel.toggleStudentAttendance(it) },
                                    onStudentClick = { viewModel.selectStudentForDetail(it) }
                                )
                                NavTab.STUDENTS -> StudentsManagementScreen(
                                    students = viewModel.getFilteredStudents(),
                                    selectedClassFilter = selectedClassFilter,
                                    onClassFilterChange = { viewModel.setClassFilter(it) },
                                    onStudentClick = { viewModel.selectStudentForDetail(it) },
                                    onToggleStatus = { viewModel.toggleStudentAttendance(it) },
                                    onAddNewStudent = { name, roll, cls, dept, email ->
                                        viewModel.addNewStudent(name, roll, cls, dept, email)
                                    }
                                )
                                NavTab.ATTENDANCE -> AttendanceMarkingScreen(
                                    classes = classes,
                                    students = students,
                                    onToggleStatus = { viewModel.toggleStudentAttendance(it) },
                                    onSetStatus = { id, status -> viewModel.setStudentAttendance(id, status) },
                                    onMarkAll = { cls, status -> viewModel.markAllInClass(cls, status) },
                                    onSubmitAttendance = { viewModel.submitAttendanceSession(it) },
                                    onStartQrSession = { viewModel.startQrSession(it) }
                                )
                                NavTab.CLASSES -> ClassesManagementScreen(
                                    classes = classes,
                                    onAddNewClass = { name, code, dept, teacher, course, term, count, schedule, room ->
                                        viewModel.addNewClass(name, code, dept, teacher, course, term, count, schedule, room)
                                    }
                                )
                                NavTab.REPORTS -> ReportsScreen(
                                    students = students,
                                    threshold = alertThreshold,
                                    onThresholdChange = { viewModel.setThreshold(it) },
                                    onExportReport = { viewModel.exportDefaulterReport(it) },
                                    onSendAlerts = { viewModel.sendDefaulterAlerts() },
                                    onAiEvaluateStudent = { student ->
                                        viewModel.selectStudentForDetail(student)
                                        viewModel.evaluateAiRisk(student)
                                    },
                                    enterpriseCampuses = enterpriseCampuses
                                )
                                NavTab.SETTINGS -> SettingsScreen()
                            }
                        }
                    }
                }
            }
        }
    }
}
