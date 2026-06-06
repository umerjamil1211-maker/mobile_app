package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*
import com.example.ui.viewmodel.HRMViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HRMMainScreen(
    viewModel: HRMViewModel,
    modifier: Modifier = Modifier
) {
    val currentRole by viewModel.currentRole.collectAsStateWithLifecycle()
    val actingEmpId by viewModel.actingEmployeeId.collectAsStateWithLifecycle()
    val employees by viewModel.employees.collectAsStateWithLifecycle()
    val attendanceList by viewModel.attendance.collectAsStateWithLifecycle()
    val leavesList by viewModel.leaves.collectAsStateWithLifecycle()
    val workloadsList by viewModel.workloads.collectAsStateWithLifecycle()
    val payrollsList by viewModel.payrolls.collectAsStateWithLifecycle()
    val announcementsList by viewModel.announcements.collectAsStateWithLifecycle()
    val reviewsList by viewModel.performanceReviews.collectAsStateWithLifecycle()

    var activeAdminTab by remember { mutableIntStateOf(0) }
    var activeTeacherTab by remember { mutableIntStateOf(0) }

    var showAddEmployeeDialog by remember { mutableStateOf(false) }
    var showAddWorkloadDialog by remember { mutableStateOf(false) }
    var showAddReviewDialog by remember { mutableStateOf(false) }
    var showAddAnnouncementDialog by remember { mutableStateOf(false) }
    var showProcessPayrollDialog by remember { mutableStateOf(false) }
    var selectedPayrollEmployee by remember { mutableStateOf<Employee?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.statusMessage.collectLatest { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // Sleek Top App Bar Header from Design
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "EDUSTAFF HRM",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "St. Andrews College",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground,
                            letterSpacing = (-0.5).sp
                        )
                    }

                    // Dynamically set avatar initials based on the acting role (fits 'JD' from design)
                    val initials = when (currentRole) {
                        "HR Manager" -> "HM"
                        "Principal" -> "PR"
                        "Accountant" -> "AC"
                        "Teacher (Ayesha)" -> "TA"
                        "Teacher (Bilal)" -> "TB"
                        else -> "JD"
                    }
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), CircleShape)
                            .clickable { viewModel.showStatusMessage("Acting session logged in as: $currentRole") },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initials,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                // Sleek Quick Stats Chips from Design
                val totalStaff = employees.size
                val presentToday = attendanceList.filter { it.date == viewModel.currentDayDate && (it.status == "Present" || it.status == "Late") }.size
                val pendingLeaves = leavesList.filter { it.status == "Pending" }.size

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Active Present Chip
                    Row(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                        Text(
                            text = "$presentToday/$totalStaff Present",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }

                    // Leave Pending Alert Chip
                    Row(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.errorContainer)
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.error)
                        )
                        Text(
                            text = "$pendingLeaves Leaves Pending",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }

                // Acting role selector
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = "ACTING USER PORTAL:",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.sp
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val roles = listOf("HR Manager", "Principal", "Accountant", "Teacher (Ayesha)", "Teacher (Bilal)")
                            roles.forEach { role ->
                                val isSelected = currentRole == role
                                FilterChip(
                                    modifier = Modifier.testTag("role_chip_$role"),
                                    selected = isSelected,
                                    onClick = {
                                        viewModel.setRole(role)
                                        activeAdminTab = 0
                                        activeTeacherTab = 0
                                    },
                                    label = { Text(role, fontSize = 11.sp) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = when {
                                                role == "HR Manager" -> Icons.Default.Badge
                                                role == "Principal" -> Icons.Default.AccountBalance
                                                role == "Accountant" -> Icons.Default.Payments
                                                else -> Icons.Default.Person
                                            },
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                )
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            val isAdminView = currentRole in listOf("Super Admin", "Principal", "HR Manager", "Accountant")
            if (isAdminView) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
                ) {
                    NavigationBarItem(
                        selected = activeAdminTab == 0,
                        onClick = { activeAdminTab = 0 },
                        label = { Text("Dashboard", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                        icon = { Icon(Icons.Outlined.Dashboard, contentDescription = "Dashboard Tab") }
                    )
                    NavigationBarItem(
                        selected = activeAdminTab == 1,
                        onClick = { activeAdminTab = 1 },
                        label = { Text("Directory", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                        icon = { Icon(Icons.Outlined.People, contentDescription = "Directory Tab") }
                    )
                    NavigationBarItem(
                        selected = activeAdminTab == 2,
                        onClick = { activeAdminTab = 2 },
                        label = { Text("Workloads", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                        icon = { Icon(Icons.Outlined.MenuBook, contentDescription = "Workloads Tab") }
                    )
                    NavigationBarItem(
                        selected = activeAdminTab == 3,
                        onClick = { activeAdminTab = 3 },
                        label = { Text("Leaves", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                        icon = { Icon(Icons.Outlined.EventNote, contentDescription = "Leaves Tab") }
                    )
                    NavigationBarItem(
                        selected = activeAdminTab == 4,
                        onClick = { activeAdminTab = 4 },
                        label = { Text("Finance", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                        icon = { Icon(Icons.Outlined.MonetizationOn, contentDescription = "Finance Tab") }
                    )
                }
            } else {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
                ) {
                    NavigationBarItem(
                        selected = activeTeacherTab == 0,
                        onClick = { activeTeacherTab = 0 },
                        label = { Text("My Portal") },
                        icon = { Icon(Icons.Outlined.Home, contentDescription = "Portal Home") }
                    )
                    NavigationBarItem(
                        selected = activeTeacherTab == 1,
                        onClick = { activeTeacherTab = 1 },
                        label = { Text("Apply Leave") },
                        icon = { Icon(Icons.Outlined.NoteAdd, contentDescription = "Apply Leave Tab") }
                    )
                    NavigationBarItem(
                        selected = activeTeacherTab == 2,
                        onClick = { activeTeacherTab = 2 },
                        label = { Text("My Status") },
                        icon = { Icon(Icons.Outlined.Assessment, contentDescription = "My Status tab") }
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            val isAdminView = currentRole in listOf("Super Admin", "Principal", "HR Manager", "Accountant")

            if (isAdminView) {
                when (activeAdminTab) {
                    0 -> AdminDashboardScreen(
                        viewModel = viewModel,
                        employees = employees,
                        attendanceList = attendanceList,
                        leavesList = leavesList,
                        payrollsList = payrollsList,
                        reviewsList = reviewsList,
                        announcementsList = announcementsList,
                        onAddNoticeClick = { showAddAnnouncementDialog = true }
                    )
                    1 -> StaffDirectoryScreen(
                        employees = employees,
                        onAddEmployeeClick = { showAddEmployeeDialog = true },
                        onUpdateEmployee = { viewModel.updateEmployee(it) },
                        onDeleteEmployee = { viewModel.deleteEmployee(it) }
                    )
                    2 -> WorkloadsScreen(
                        workloads = workloadsList,
                        teachers = employees.filter { it.role == "Teacher" },
                        onAddWorkloadClick = { showAddWorkloadDialog = true },
                        onAssignSubstitution = { wl, rep -> viewModel.handleSubstitution(wl, rep) }
                    )
                    3 -> LeavesApprovalScreen(
                        leaves = leavesList,
                        onApproveLeave = { viewModel.updateLeaveStatus(it, "Approved") },
                        onRejectLeave = { viewModel.updateLeaveStatus(it, "Rejected") }
                    )
                    4 -> FinanceAndPerformanceScreen(
                        payrolls = payrollsList,
                        reviews = reviewsList,
                        employees = employees,
                        onProcessPayrollClick = { employee ->
                            selectedPayrollEmployee = employee
                            showProcessPayrollDialog = true
                        },
                        onMarkPaid = { viewModel.markPayrollPaid(it) },
                        onAddReviewClick = { showAddReviewDialog = true }
                    )
                }
            } else {
                val loggedInEmployeeId = actingEmpId
                val personalEmployee = employees.find { it.id == loggedInEmployeeId }

                if (personalEmployee == null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    val personalAttendance = attendanceList.filter { it.employeeId == loggedInEmployeeId }
                    val personalLeaves = leavesList.filter { it.employeeId == loggedInEmployeeId }
                    val personalWorkloads = workloadsList.filter { it.teacherId == loggedInEmployeeId }
                    val personalPayrolls = payrollsList.filter { it.employeeId == loggedInEmployeeId }
                    val personalReviews = reviewsList.filter { it.employeeId == loggedInEmployeeId }

                    when (activeTeacherTab) {
                        0 -> TeacherPortalDashboard(
                            employee = personalEmployee,
                            workloads = personalWorkloads,
                            attendanceList = personalAttendance,
                            announcements = announcementsList,
                            currentDate = viewModel.currentDayDate,
                            onCheckInCheckOut = { viewModel.simulateDailyCheckInCheckOut() }
                        )
                        1 -> TeacherLeaveApplicationScreen(
                            leaves = personalLeaves,
                            onApplyLeave = { type, from, to, reason ->
                                viewModel.applyLeave(type, from, to, reason)
                            }
                        )
                        2 -> TeacherStatusAndHistoryScreen(
                            payrolls = personalPayrolls,
                            reviews = personalReviews
                        )
                    }
                }
            }
        }
    }

    if (showAddEmployeeDialog) {
        AddEmployeeDialog(
            onDismiss = { showAddEmployeeDialog = false },
            onConfirm = { emp ->
                viewModel.addEmployee(emp)
                showAddEmployeeDialog = false
            }
        )
    }

    if (showAddWorkloadDialog) {
        val teacherList = employees.filter { it.role == "Teacher" }
        AddWorkloadDialog(
            teachers = teacherList,
            onDismiss = { showAddWorkloadDialog = false },
            onConfirm = { wl ->
                viewModel.addWorkload(wl)
                showAddWorkloadDialog = false
            }
        )
    }

    if (showAddReviewDialog) {
        AddPerformanceReviewDialog(
            employees = employees,
            onDismiss = { showAddReviewDialog = false },
            onConfirm = { rev ->
                viewModel.addPerformanceReview(rev)
                showAddReviewDialog = false
            }
        )
    }

    if (showAddAnnouncementDialog) {
        AddAnnouncementDialog(
            onDismiss = { showAddAnnouncementDialog = false },
            onConfirm = { title, content, target ->
                viewModel.postAnnouncement(title, content, target)
                showAddAnnouncementDialog = false
            }
        )
    }

    if (showProcessPayrollDialog && selectedPayrollEmployee != null) {
        ProcessPayrollDialog(
            employee = selectedPayrollEmployee!!,
            onDismiss = {
                showProcessPayrollDialog = false
                selectedPayrollEmployee = null
            },
            onConfirm = { payroll ->
                viewModel.processPayroll(payroll)
                showProcessPayrollDialog = false
                selectedPayrollEmployee = null
            }
        )
    }
}

// --- SCREEN: ADMIN DASHBOARD (ANALYTICS & CIRCULARS) ---
@Composable
fun AdminDashboardScreen(
    viewModel: HRMViewModel,
    employees: List<Employee>,
    attendanceList: List<AttendanceRecord>,
    leavesList: List<LeaveRequest>,
    payrollsList: List<PayrollRecord>,
    reviewsList: List<PerformanceReview>,
    announcementsList: List<Announcement>,
    onAddNoticeClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "School & College HRM Dashboard",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        // Sleek Interface Hero Action Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Payroll Review",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = "May 2026 cycles ready for approval",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Light
                    )
                }

                Button(
                    onClick = {
                        viewModel.showStatusMessage("Please switch to the Finance tab to process pending staff payrolls!")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    shape = CircleShape,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text("Process", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        val totalStaff = employees.size
        val presentToday = attendanceList.filter { it.date == viewModel.currentDayDate && (it.status == "Present" || it.status == "Late") }.size
        val activeTeacherCount = employees.filter { it.role == "Teacher" }.size
        val pendingLeaves = leavesList.filter { it.status == "Pending" }.size
        val totalPayrollBudget = payrollsList.filter { it.monthYear == "May 2026" }.sumOf { it.netSalary }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .height(210.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            userScrollEnabled = false
        ) {
            item {
                StatCard(
                    title = "Total Staff",
                    value = totalStaff.toString(),
                    subtext = "$activeTeacherCount Faculty Lecturers",
                    icon = Icons.Default.Groups,
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            }
            item {
                StatCard(
                    title = "Attendance Today",
                    value = "$presentToday/$totalStaff",
                    subtext = "Date: ${viewModel.currentDayDate}",
                    icon = Icons.Default.EventAvailable,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            }
            item {
                StatCard(
                    title = "Pending Leaves",
                    value = pendingLeaves.toString(),
                    subtext = "Awaiting approvals",
                    icon = Icons.Default.Outbox,
                    containerColor = if (pendingLeaves > 0) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.tertiaryContainer
                )
            }
            item {
                StatCard(
                    title = "May Payroll",
                    value = "Rs. ${totalPayrollBudget.toInt()}",
                    subtext = "Total Staff Wages",
                    icon = Icons.Default.Payments,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Info",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Column {
                    Text("Interactive Demo Environment", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Text(
                        "Switch acting roles at the top of the screen to simulate self-service requests (check-in, applying leaves), and immediately notice updating charts & approval states as Accountant or Principal.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Sleek Interface: Active Exam Duties section from Design Spec
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Active Exam Duties",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "VIEW SCHEDULE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.clickable {
                        viewModel.showStatusMessage("All active faculty exam and lecture schedules are fully synchronized.")
                    }
                )
            }

            // Beautiful Duty Card based on design template
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Accent calendar date block
                    Box(
                        modifier = Modifier
                            .size(45.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "MAY",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Text(
                                text = "14",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onPrimary,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    // Duty Details
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Dr. Sarah Ahmed",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Main Hall • Economics 101",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Time Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "09:00 AM",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Administrative Announcements",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Button(
                onClick = onAddNoticeClick,
                modifier = Modifier.testTag("publish_announcement_button"),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Publish Alert",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text("Publish Notice", fontSize = 12.sp)
            }
        }

        if (announcementsList.isEmpty()) {
            EmptyListPlaceholder(text = "No active notices or circulars exist.")
        } else {
            announcementsList.forEach { note ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = note.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Badge(
                                containerColor = when (note.targetAudience) {
                                    "All" -> MaterialTheme.colorScheme.tertiaryContainer
                                    "Academic" -> MaterialTheme.colorScheme.primaryContainer
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                }
                            ) {
                                Text(note.targetAudience, fontSize = 10.sp, modifier = Modifier.padding(2.dp))
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = note.content,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "By: ${note.senderName}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Date: ${note.date}",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- SCREEN: STAFF DIRECTORY SCREEN ---
@Composable
fun StaffDirectoryScreen(
    employees: List<Employee>,
    onAddEmployeeClick: () -> Unit,
    onUpdateEmployee: (Employee) -> Unit,
    onDeleteEmployee: (Employee) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedDepartmentFilter by remember { mutableStateOf("All") }

    val filteredStaff = employees.filter { emp ->
        val matchesSearch = emp.name.contains(searchQuery, ignoreCase = true) || emp.role.contains(searchQuery, ignoreCase = true)
        val matchesDept = selectedDepartmentFilter == "All" || emp.department == selectedDepartmentFilter
        matchesSearch && matchesDept
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Staff Directory (${filteredStaff.size})",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            ElevatedButton(
                onClick = onAddEmployeeClick,
                modifier = Modifier.testTag("add_employee_fab")
            ) {
                Icon(
                    imageVector = Icons.Default.PersonAdd,
                    contentDescription = "Add Staff",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text("Add Staff")
            }
        }

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search staff profiles or roles...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = if (searchQuery.isNotEmpty()) {
                { IconButton(onClick = { searchQuery = "" }) { Icon(Icons.Default.Clear, contentDescription = null) } }
            } else null,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val depts = listOf("All", "Science", "Arts", "IT", "Finance", "Admin", "Support")
            depts.forEach { dept ->
                FilterChip(
                    selected = selectedDepartmentFilter == dept,
                    onClick = { selectedDepartmentFilter = dept },
                    label = { Text(dept, fontSize = 12.sp) }
                )
            }
        }

        if (filteredStaff.isEmpty()) {
            EmptyListPlaceholder("No staff members match the chosen criteria.")
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredStaff, key = { it.id }) { emp ->
                    var isExpanded by remember { mutableStateOf(false) }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isExpanded = !isExpanded },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(45.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = emp.name.firstOrNull()?.toString()?.uppercase() ?: "E",
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        fontSize = 18.sp
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = emp.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(text = emp.role, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
                                        Text(text = "•", fontSize = 12.sp, color = Color.Gray)
                                        Text(text = emp.department, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    val statusColor = when (emp.status) {
                                        "Active" -> Color(0xFF4CAF50)
                                        else -> Color.Gray
                                    }
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(statusColor)
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text(emp.status, fontSize = 11.sp, color = statusColor, fontWeight = FontWeight.SemiBold)
                                    IconButton(onClick = { isExpanded = !isExpanded }) {
                                        Icon(
                                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                            contentDescription = "Expand Details"
                                        )
                                    }
                                }
                            }

                            AnimatedVisibility(visible = isExpanded) {
                                Column(
                                    modifier = Modifier
                                        .padding(top = 16.dp)
                                        .fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Divider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

                                    ProfileDetailRow(icon = Icons.Default.School, label = "Qualification", value = emp.qualification)
                                    ProfileDetailRow(icon = Icons.Default.AlternateEmail, label = "Email Address", value = emp.email)
                                    ProfileDetailRow(icon = Icons.Default.Phone, label = "Contact Phone", value = emp.phone)
                                    ProfileDetailRow(icon = Icons.Default.Fingerprint, label = "CNIC Number", value = emp.cnic)
                                    ProfileDetailRow(icon = Icons.Default.DateRange, label = "Joining Date", value = emp.joiningDate)
                                    ProfileDetailRow(icon = Icons.Default.LocalAtm, label = "Basic Salary", value = "Rs. ${emp.basicSalary.toInt()}")

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 8.dp),
                                        horizontalArrangement = Arrangement.End,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        TextButton(onClick = { onDeleteEmployee(emp) }) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete",
                                                modifier = Modifier.size(16.dp),
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                            Spacer(Modifier.width(4.dp))
                                            Text("Remove Staff", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- SCREEN: TEACHER WORKLOAD ---
@Composable
fun WorkloadsScreen(
    workloads: List<WorkloadAllocation>,
    teachers: List<Employee>,
    onAddWorkloadClick: () -> Unit,
    onAssignSubstitution: (WorkloadAllocation, String) -> Unit
) {
    var showSubstituteDialogForWorkload by remember { mutableStateOf<WorkloadAllocation?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Faculty Hour Workloads",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Subjects & Substitution Management",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            ElevatedButton(
                onClick = onAddWorkloadClick,
                modifier = Modifier.testTag("add_workload_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Assign Subject",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text("Assign Class")
            }
        }

        if (workloads.isEmpty()) {
            EmptyListPlaceholder("No academic subject workloads currently scheduled.")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(workloads) { wl ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = wl.subject, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.primary)
                                    Text(text = "Class: ${wl.classGrade}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                }
                                Badge(containerColor = MaterialTheme.colorScheme.secondaryContainer) {
                                    Text("${wl.hoursPerWeek} hrs/week", modifier = Modifier.padding(4.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(Modifier.height(8.dp))
                            ProfileDetailRow(icon = Icons.Default.Person, label = "Faculty Lecturer", value = wl.teacherName)

                            if (wl.examDuty != null) {
                                ProfileDetailRow(
                                    icon = Icons.Default.AssignmentLate,
                                    label = "Invigilation Duty",
                                    value = wl.examDuty
                                )
                            }

                            if (wl.substitutionDetails != null) {
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Sync,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                        Text(
                                            text = "Substitution Active: ${wl.substitutionDetails}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onErrorContainer
                                        )
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                horizontalArrangement = Arrangement.End
                            ) {
                                OutlinedButton(
                                    onClick = { showSubstituteDialogForWorkload = wl },
                                    modifier = Modifier.testTag("substitute_btn_${wl.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Sync,
                                        contentDescription = "Substitute",
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text("Substitution Trigger", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showSubstituteDialogForWorkload != null) {
        SubstitutionDialog(
            workload = showSubstituteDialogForWorkload!!,
            teachers = teachers.filter { it.name != showSubstituteDialogForWorkload!!.teacherName },
            onDismiss = { showSubstituteDialogForWorkload = null },
            onConfirm = { substituteDetails ->
                onAssignSubstitution(showSubstituteDialogForWorkload!!, substituteDetails)
                showSubstituteDialogForWorkload = null
            }
        )
    }
}

// --- SCREEN: LEAVE WORKFLOW RE-VALUATION ---
@Composable
fun LeavesApprovalScreen(
    leaves: List<LeaveRequest>,
    onApproveLeave: (LeaveRequest) -> Unit,
    onRejectLeave: (LeaveRequest) -> Unit
) {
    var filterStatus by remember { mutableStateOf("Pending") }

    val filteredLeaves = leaves.filter {
        filterStatus == "All" || it.status == filterStatus
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
            Text(
                text = "Academic Leave Workflows",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Approve and track absences of staff members",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val statusOptions = listOf("Pending", "Approved", "Rejected", "All")
            statusOptions.forEach { opt ->
                FilterChip(
                    selected = filterStatus == opt,
                    onClick = { filterStatus = opt },
                    label = { Text(opt) }
                )
            }
        }

        if (filteredLeaves.isEmpty()) {
            EmptyListPlaceholder("No leave requests found in category $filterStatus.")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(filteredLeaves) { leave ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = leave.employeeName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Text(text = "Leave Category: ${leave.leaveType}", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                }
                                Badge(
                                    containerColor = when (leave.status) {
                                        "Approved" -> Color(0xFFE8F5E9)
                                        "Rejected" -> Color(0xFFFFEBEE)
                                        else -> Color(0xFFFFF8E1)
                                    },
                                    contentColor = when (leave.status) {
                                        "Approved" -> Color(0xFF2E7D32)
                                        "Rejected" -> Color(0xFFC62828)
                                        else -> Color(0xFFF57F17)
                                    }
                                ) {
                                    Text(leave.status, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }

                            Spacer(Modifier.height(8.dp))
                            ProfileDetailRow(icon = Icons.Default.DateRange, label = "Time Period", value = "${leave.startDate} to ${leave.endDate}")
                            ProfileDetailRow(icon = Icons.Default.Feedback, label = "Reason Statement", value = leave.reason)
                            ProfileDetailRow(icon = Icons.Default.AccessTime, label = "Applied On", value = leave.appliedDate)

                            if (leave.status == "Pending") {
                                Spacer(Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Button(
                                        onClick = { onApproveLeave(leave) },
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("approve_leave_button_${leave.id}"),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Approve",
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(Modifier.width(4.dp))
                                        Text("Approve", fontSize = 12.sp)
                                    }

                                    OutlinedButton(
                                        onClick = { onRejectLeave(leave) },
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("reject_leave_button_${leave.id}"),
                                        border = BorderStroke(1.dp, Color(0xFFE53935)),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFE53935))
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Reject",
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(Modifier.width(4.dp))
                                        Text("Reject", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- SCREEN: INTERACTIVE TEACHER PORTAL SELF-SERVICE ---
@Composable
fun TeacherPortalDashboard(
    employee: Employee,
    workloads: List<WorkloadAllocation>,
    attendanceList: List<AttendanceRecord>,
    announcements: List<Announcement>,
    currentDate: String,
    onCheckInCheckOut: () -> Unit
) {
    val scrollState = rememberScrollState()
    val todayRecord = attendanceList.find { it.date == currentDate }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "AOA, ${employee.name}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "Welcome to your Personal Employee Self-Service Space.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Daily Attendance Registration", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(
                    "Register checking log for today: $currentDate",
                    fontSize = 11.sp,
                    color = Color.Gray
                )

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Check-In: ${todayRecord?.checkIn ?: "--:--"}",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Check-Out: ${todayRecord?.checkOut ?: "--:--"}",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    }

                    Button(
                        onClick = onCheckInCheckOut,
                        modifier = Modifier.testTag("rfid_check_in_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (todayRecord?.checkIn == null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fingerprint,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = when {
                                todayRecord?.checkIn == null -> "Simulate RFID Tap"
                                todayRecord.checkOut == null -> "Simulate Checkout"
                                else -> "Tapped Out"
                            },
                            fontSize = 12.sp
                        )
                    }
                }

                if (todayRecord != null) {
                    Spacer(Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val statusCol = when (todayRecord.status) {
                            "Present" -> Color(0xFF2E7D32)
                            "Late" -> Color(0xFFF57F17)
                            else -> Color(0xFFC62828)
                        }
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(statusCol)
                        )
                        Text(
                            text = "Status: Marked as ${todayRecord.status} ${if (todayRecord.lateMinutes > 0) "(${todayRecord.lateMinutes} mins late)" else ""}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = statusCol
                        )
                    }
                }
            }
        }

        Text(text = "My Personal Lecture Timetable", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
        if (workloads.isEmpty()) {
            EmptyListPlaceholder("You have no subjects allocated today.")
        } else {
            workloads.forEach { wl ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = wl.subject, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Badge { Text("${wl.hoursPerWeek} hrs", modifier = Modifier.padding(2.dp)) }
                        }
                        Text("Class: ${wl.classGrade}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        if (wl.examDuty != null) {
                            Spacer(Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(
                                    imageVector = Icons.Default.AssignmentLate,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = Color(0xFFC62828)
                                )
                                Text("Exam Duty: ${wl.examDuty}", fontSize = 11.sp, color = Color(0xFFC62828), fontWeight = FontWeight.SemiBold)
                            }
                        }
                        if (wl.substitutionDetails != null) {
                            Spacer(Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(
                                    imageVector = Icons.Default.Sync,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text("Substitution Allocated: ${wl.substitutionDetails}", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }

        Text(text = "Unread School Circulars", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
        val filteredNotices = announcements.filter { it.targetAudience in listOf("All", "Academic") }
        if (filteredNotices.isEmpty()) {
            EmptyListPlaceholder("No circular announcements posted for your rank.")
        } else {
            filteredNotices.forEach { note ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(note.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(Modifier.height(4.dp))
                        Text(note.content, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("By: ${note.senderName}", fontSize = 9.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                            Text("Date: ${note.date}", fontSize = 9.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

// --- OTHER SUB VIEWS ---
@Composable
fun StatCard(
    title: String,
    value: String,
    subtext: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    containerColor: Color
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = containerColor),
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column {
                Text(text = value, fontSize = 21.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.5).sp)
                Text(text = subtext, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f), maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
fun ProfileDetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(text = "$label:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.Gray)
        Text(text = value, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun EmptyListPlaceholder(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.HourglassEmpty,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = Color.LightGray
            )
            Text(text = text, fontSize = 12.sp, color = Color.Gray, textAlign = TextAlign.Center)
        }
    }
}

// --- SCREEN: FINANCE AND PERFORMANCE MANAGEMENT ---
@Composable
fun FinanceAndPerformanceScreen(
    payrolls: List<PayrollRecord>,
    reviews: List<PerformanceReview>,
    employees: List<Employee>,
    onProcessPayrollClick: (Employee) -> Unit,
    onMarkPaid: (PayrollRecord) -> Unit,
    onAddReviewClick: () -> Unit
) {
    var selectedSubTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
            Text(
                text = "Accounts & Faculty Appraisals",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Disburse salaries and track appraisals",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        TabRow(selectedTabIndex = selectedSubTab) {
            Tab(selected = selectedSubTab == 0, onClick = { selectedSubTab = 0 }, text = { Text("Payroll Portal") })
            Tab(selected = selectedSubTab == 1, onClick = { selectedSubTab = 1 }, text = { Text("Performance Reviews") })
        }

        if (selectedSubTab == 0) {
            // PAYROLL PORTAL
            Text("Select Staff Member to Process Wages:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
                items(employees) { emp ->
                    val empPayroll = payrolls.find { it.employeeId == emp.id && it.monthYear == "May 2026" }
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(emp.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("${emp.role} • Basic: Rs. ${emp.basicSalary.toInt()}", fontSize = 11.sp, color = Color.Gray)
                            }
                            if (empPayroll == null) {
                                Button(
                                    onClick = { onProcessPayrollClick(emp) },
                                    modifier = Modifier.testTag("process_payroll_btn_${emp.id}"),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                ) {
                                    Text("Disburse May", fontSize = 11.sp)
                                }
                            } else {
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("May Paid: Rs. ${empPayroll.netSalary.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                                    if (empPayroll.status == "Issued") {
                                        TextButton(onClick = { onMarkPaid(empPayroll) }) {
                                            Text("Mark as Cleared", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary)
                                        }
                                    } else {
                                        Badge(containerColor = Color(0xFFE8F5E9)) {
                                            Text("Cleared", color = Color(0xFF2E7D32), fontSize = 10.sp, modifier = Modifier.padding(2.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // PERFORMANCE REVIEWS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Historic Review Appraisals", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Button(onClick = onAddReviewClick, modifier = Modifier.testTag("add_review_fab")) {
                    Text("Add Score")
                }
            }

            if (reviews.isEmpty()) {
                EmptyListPlaceholder("No performance reviews logged yet.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.weight(1f)) {
                    items(reviews) { rev ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(rev.employeeName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Badge(containerColor = MaterialTheme.colorScheme.primaryContainer) {
                                        Text("Score: ${rev.kpiScore}/10.0", modifier = Modifier.padding(4.dp), fontWeight = FontWeight.Bold)
                                    }
                                }
                                Text("Remarks: ${rev.headEvaluation}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(Modifier.height(4.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Review Period: ${rev.reviewPeriod}", fontSize = 10.sp, color = Color.Gray)
                                    Text("Recommendations: ${rev.recommendations}", fontSize = 10.sp, color = Color.Gray)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- SCREEN: TEACHER LEAVE APPLICATIONS ---
@Composable
fun TeacherLeaveApplicationScreen(
    leaves: List<LeaveRequest>,
    onApplyLeave: (String, String, String, String) -> Unit
) {
    var leaveType by remember { mutableStateOf("Casual") }
    var reasonText by remember { mutableStateOf("") }
    var startDateText by remember { mutableStateOf("29 May 2026") }
    var endDateText by remember { mutableStateOf("30 May 2026") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Apply For Leave Absence", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("New Absence Application Form", fontWeight = FontWeight.Bold, fontSize = 14.sp)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val types = listOf("Casual", "Sick", "Maternity", "Duty")
                    types.forEach { t ->
                        FilterChip(
                            selected = leaveType == t,
                            onClick = { leaveType = t },
                            label = { Text(t, fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = startDateText,
                    onValueChange = { startDateText = it },
                    label = { Text("From Date") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = endDateText,
                    onValueChange = { endDateText = it },
                    label = { Text("To Date") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = reasonText,
                    onValueChange = { reasonText = it },
                    label = { Text("Reason Details") },
                    placeholder = { Text("Type complete reason details...") },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        if (reasonText.isNotBlank()) {
                            onApplyLeave(leaveType, startDateText, endDateText, reasonText)
                            reasonText = ""
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("submit_leave_btn")
                ) {
                    Text("Submit Application to Principal")
                }
            }
        }

        Text("My Absences Status History & Records", fontWeight = FontWeight.Bold, fontSize = 15.sp)

        if (leaves.isEmpty()) {
            EmptyListPlaceholder("You have no pending or processed leaves registered.")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
                items(leaves) { leave ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("${leave.leaveType} Absence", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Dates: ${leave.startDate} to ${leave.endDate}", fontSize = 11.sp, color = Color.Gray)
                                Text(leave.reason, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Badge(
                                containerColor = when (leave.status) {
                                    "Approved" -> Color(0xFFE8F5E9)
                                    "Rejected" -> Color(0xFFFFEBEE)
                                    else -> Color(0xFFFFF8E1)
                                }
                            ) {
                                Text(leave.status, color = when (leave.status) {
                                    "Approved" -> Color(0xFF2E7D32)
                                    "Rejected" -> Color(0xFFC62828)
                                    else -> Color(0xFFF57F17)
                                }, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.padding(4.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- SCREEN: TEACHER STATUS & ACCOUNTS ---
@Composable
fun TeacherStatusAndHistoryScreen(
    payrolls: List<PayrollRecord>,
    reviews: List<PerformanceReview>
) {
    var subTabSelection by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Account Slips & Appraisals", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)

        TabRow(selectedTabIndex = subTabSelection) {
            Tab(selected = subTabSelection == 0, onClick = { subTabSelection = 0 }, text = { Text("My Account Payslips") })
            Tab(selected = subTabSelection == 1, onClick = { subTabSelection = 1 }, text = { Text("My Appraisal Reports") })
        }

        if (subTabSelection == 0) {
            // PAYSLIPS
            if (payrolls.isEmpty()) {
                EmptyListPlaceholder("No payslips prepared yet by Accounting.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(payrolls) { pay ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Payslip Summary • ${pay.monthYear}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Badge(containerColor = Color(0xFFE8F5E9)) {
                                        Text(pay.status, color = Color(0xFF2E7D32), fontSize = 10.sp, modifier = Modifier.padding(2.dp), fontWeight = FontWeight.Bold)
                                    }
                                }
                                Divider(thickness = 0.5.dp)
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Basic Salary Sub-Total:", fontSize = 12.sp, color = Color.Gray)
                                    Text("Rs. ${pay.basicSalary.toInt()}", fontSize = 12.sp)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Tax & Leave Deductions:", fontSize = 12.sp, color = Color.Gray)
                                    Text("- Rs. ${pay.deductions.toInt() + pay.tax.toInt()}", fontSize = 12.sp, color = MaterialTheme.colorScheme.error)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Bonus Allowances:", fontSize = 12.sp, color = Color.Gray)
                                    Text("+ Rs. ${pay.allowances.toInt()}", fontSize = 12.sp, color = Color(0xFF2E7D32))
                                }
                                Divider(thickness = 0.5.dp)
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Net Released Salary:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    Text("Rs. ${pay.netSalary.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // APPRAISALS
            if (reviews.isEmpty()) {
                EmptyListPlaceholder("No performance logs recorded for you.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(reviews) { rev ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Annual Evaluation Score", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Badge(containerColor = MaterialTheme.colorScheme.primaryContainer) {
                                        Text("Score: ${rev.kpiScore}/10.0", modifier = Modifier.padding(4.dp), fontWeight = FontWeight.Bold)
                                    }
                                }
                                Text("Remarks: ${rev.headEvaluation}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Divider(thickness = 0.5.dp)
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Review Period: ${rev.reviewPeriod}", fontSize = 10.sp, color = Color.Gray)
                                    Text("Recommendations: ${rev.recommendations}", fontSize = 10.sp, color = Color.Gray)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- DIALOGS: CREATIVE COMPONENT FORMS ---

@Composable
fun AddEmployeeDialog(
    onDismiss: () -> Unit,
    onConfirm: (Employee) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var dept by remember { mutableStateOf("Science") }
    var role by remember { mutableStateOf("Teacher") }
    var cnic by remember { mutableStateOf("") }
    var salary by remember { mutableStateOf("55000") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Register Employee Profile", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Complete Name") })
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Institutional Email") })
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Contact Number") })
                OutlinedTextField(value = cnic, onValueChange = { cnic = it }, label = { Text("National CNIC #") })

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = role, onValueChange = { role = it }, label = { Text("Staff Role") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = dept, onValueChange = { dept = it }, label = { Text("Department") }, modifier = Modifier.weight(1f))
                }

                OutlinedTextField(
                    value = salary,
                    onValueChange = { salary = it },
                    label = { Text("Basic Salary Rate (Rs.)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val emp = Employee(
                            name = name,
                            email = email,
                            phone = phone,
                            department = dept,
                            role = role,
                            cnic = cnic,
                            joiningDate = "01 Jun 2026",
                            qualification = "Masters Professional",
                            basicSalary = salary.toDoubleOrNull() ?: 50000.0,
                            status = "Active"
                        )
                        onConfirm(emp)
                    }
                },
                modifier = Modifier.testTag("submit_employee_btn")
            ) {
                Text("Register")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Dismiss") }
        }
    )
}

@Composable
fun AddWorkloadDialog(
    teachers: List<Employee>,
    onDismiss: () -> Unit,
    onConfirm: (WorkloadAllocation) -> Unit
) {
    var selectedTeacher by remember { mutableStateOf(teachers.firstOrNull()) }
    var subject by remember { mutableStateOf("") }
    var classGrade by remember { mutableStateOf("Class-9 (B)") }
    var hrs by remember { mutableStateOf("6") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Schedule Subject Class Allocation", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = subject, onValueChange = { subject = it }, label = { Text("Core Subject Name") })
                OutlinedTextField(value = classGrade, onValueChange = { classGrade = it }, label = { Text("Assigned Grade/Section") })
                OutlinedTextField(value = hrs, onValueChange = { hrs = it }, label = { Text("Calculated Hrs/Week") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))

                Text("Assign Class Lecturer:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    teachers.forEach { t ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedTeacher = t }
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            RadioButton(selected = selectedTeacher?.id == t.id, onClick = { selectedTeacher = t })
                            Text(t.name, fontSize = 13.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (subject.isNotBlank() && selectedTeacher != null) {
                        val wl = WorkloadAllocation(
                            teacherId = selectedTeacher!!.id,
                            teacherName = selectedTeacher!!.name,
                            subject = subject,
                            classGrade = classGrade,
                            hoursPerWeek = hrs.toIntOrNull() ?: 6,
                            examDuty = null,
                            substitutionDetails = null
                        )
                        onConfirm(wl)
                    }
                },
                modifier = Modifier.testTag("submit_workload_btn")
            ) {
                Text("Schedule")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Dismiss") }
        }
    )
}

@Composable
fun AddPerformanceReviewDialog(
    employees: List<Employee>,
    onDismiss: () -> Unit,
    onConfirm: (PerformanceReview) -> Unit
) {
    var selectedEmployee by remember { mutableStateOf(employees.firstOrNull()) }
    var appraisalScore by remember { mutableStateOf("8.5") }
    var remarksText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Record Appraisal score", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = appraisalScore, onValueChange = { appraisalScore = it }, label = { Text("Performance Score (1.0 to 10.0)") })
                OutlinedTextField(value = remarksText, onValueChange = { remarksText = it }, label = { Text("Official Appraisal Remarks") })

                Text("Target Staff Member:", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                LazyColumn(modifier = Modifier.height(100.dp)) {
                    items(employees) { e ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedEmployee = e }
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            RadioButton(selected = selectedEmployee?.id == e.id, onClick = { selectedEmployee = e })
                            Text(e.name, fontSize = 12.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (selectedEmployee != null && remarksText.isNotBlank()) {
                        val rev = PerformanceReview(
                            employeeId = selectedEmployee!!.id,
                            employeeName = selectedEmployee!!.name,
                            kpiScore = appraisalScore.toDoubleOrNull() ?: 8.0,
                            studentFeedback = 4.5,
                            headEvaluation = remarksText,
                            reviewPeriod = "Annual 2025-2026",
                            recommendations = "Promoting / High Merit"
                        )
                        onConfirm(rev)
                    }
                },
                modifier = Modifier.testTag("submit_review_btn")
            ) {
                Text("Release Log")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Dismiss") }
        }
    )
}

@Composable
fun AddAnnouncementDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var audience by remember { mutableStateOf("All") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Issue Official Circular", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Brief Notice Title") })
                OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("Circular Body Content") }, minLines = 3)

                Text("Target Faculty Audience Rank:", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    listOf("All", "Academic", "Administrative").forEach { target ->
                        FilterChip(
                            selected = audience == target,
                            onClick = { audience = target },
                            label = { Text(target, fontSize = 11.sp) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && content.isNotBlank()) {
                        onConfirm(title, content, audience)
                    }
                },
                modifier = Modifier.testTag("submit_announcement_btn")
            ) {
                Text("Publish Notice")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Dismiss") }
        }
    )
}

@Composable
fun ProcessPayrollDialog(
    employee: Employee,
    onDismiss: () -> Unit,
    onConfirm: (PayrollRecord) -> Unit
) {
    var allowances by remember { mutableStateOf("5000") }
    var deductions by remember { mutableStateOf("1500") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Prepare May Payslip: ${employee.name}", fontWeight = FontWeight.Bold, fontSize = 15.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Base Salary Amount: Rs. ${employee.basicSalary.toInt()}", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)

                OutlinedTextField(value = allowances, onValueChange = { allowances = it }, label = { Text("Performance Bonuses / Allowances (Rs.)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                OutlinedTextField(value = deductions, onValueChange = { deductions = it }, label = { Text("Leave Cuts / Deductions (Rs.)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val basic = employee.basicSalary
                    val bonus = allowances.toDoubleOrNull() ?: 0.0
                    val tax = deductions.toDoubleOrNull() ?: 0.0
                    val payroll = PayrollRecord(
                        employeeId = employee.id,
                        employeeName = employee.name,
                        monthYear = "May 2026",
                        basicSalary = basic,
                        allowances = bonus,
                        deductions = tax,
                        tax = tax * 0.1,
                        netSalary = basic + bonus - tax,
                        status = "Issued"
                    )
                    onConfirm(payroll)
                },
                modifier = Modifier.testTag("submit_payroll_btn")
            ) {
                Text("Disburse Released Amount")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Dismiss") }
        }
    )
}

@Composable
fun SubstitutionDialog(
    workload: WorkloadAllocation,
    teachers: List<Employee>,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var selectedReplacement by remember { mutableStateOf(teachers.firstOrNull()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Substitutive Teacher", fontWeight = FontWeight.Bold, fontSize = 15.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Assign temporary substitute for Subject:", fontSize = 12.sp)
                Text("${workload.subject} (${workload.classGrade})", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)

                Text("Available Colleagues:", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                LazyColumn(modifier = Modifier.height(140.dp)) {
                    items(teachers) { t ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedReplacement = t }
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            RadioButton(selected = selectedReplacement?.id == t.id, onClick = { selectedReplacement = t })
                            Text(t.name, fontSize = 12.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (selectedReplacement != null) {
                        onConfirm("Replacement assigned to ${selectedReplacement!!.name} due to active leave period.")
                    }
                },
                modifier = Modifier.testTag("submit_substitution_btn")
            ) {
                Text("Enforce Substitute")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Dismiss") }
        }
    )
}

