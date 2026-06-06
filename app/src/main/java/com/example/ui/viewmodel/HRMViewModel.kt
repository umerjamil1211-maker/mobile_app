package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HRMViewModel(private val repository: HRMRepository) : ViewModel() {

    // Dynamic acting role: "Super Admin", "Principal", "HR Manager", "Accountant", "Teacher (Ayesha)", "Teacher (Bilal)"
    private val _currentRole = MutableStateFlow("HR Manager")
    val currentRole: StateFlow<String> = _currentRole.asStateFlow()

    // Map roles to active employee profiles
    val actingEmployeeId: StateFlow<Int> = _currentRole.map { role ->
        when (role) {
            "Principal" -> 1
            "HR Manager" -> 2
            "Accountant" -> 3
            "Teacher (Ayesha)" -> 4
            "Teacher (Bilal)" -> 5
            else -> 2 // Default to HR Manager
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 2)

    val currentDayDate: String = "2026-06-06"

    // Exposed DB States
    val employees: StateFlow<List<Employee>> = repository.allEmployees
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val attendance: StateFlow<List<AttendanceRecord>> = repository.allAttendance
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val leaves: StateFlow<List<LeaveRequest>> = repository.allLeaves
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val workloads: StateFlow<List<WorkloadAllocation>> = repository.allWorkloads
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val payrolls: StateFlow<List<PayrollRecord>> = repository.allPayroll
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val announcements: StateFlow<List<Announcement>> = repository.allAnnouncements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val performanceReviews: StateFlow<List<PerformanceReview>> = repository.allPerformanceReviews
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI Toast Messages or State
    private val _statusMessage = MutableSharedFlow<String>()
    val statusMessage = _statusMessage.asSharedFlow()

    fun setRole(role: String) {
        _currentRole.value = role
        viewModelScope.launch {
            _statusMessage.emit("Switching dashboard portal to $role")
        }
    }

    fun showStatusMessage(message: String) {
        viewModelScope.launch {
            _statusMessage.emit(message)
        }
    }

    // --- Employee Operations ---
    fun addEmployee(employee: Employee) {
        viewModelScope.launch {
            repository.insertEmployee(employee)
            _statusMessage.emit("Successfully registered new profile: ${employee.name}")
        }
    }

    fun updateEmployee(employee: Employee) {
        viewModelScope.launch {
            repository.updateEmployee(employee)
            _statusMessage.emit("Profile updated: ${employee.name}")
        }
    }

    fun deleteEmployee(employee: Employee) {
        viewModelScope.launch {
            repository.deleteEmployee(employee)
            _statusMessage.emit("Profile deleted for ${employee.name}")
        }
    }

    // --- Leave Procedures ---
    fun applyLeave(leaveType: String, startDate: String, endDate: String, reason: String) {
        val empId = actingEmployeeId.value
        viewModelScope.launch {
            val emps = employees.value
            val currentEmpName = emps.find { it.id == empId }?.name ?: "Acting Staff"
            val request = LeaveRequest(
                employeeId = empId,
                employeeName = currentEmpName,
                leaveType = leaveType,
                startDate = startDate,
                endDate = endDate,
                reason = reason,
                status = "Pending",
                appliedDate = currentDayDate
            )
            repository.insertLeave(request)
            _statusMessage.emit("Your Leave Application (${leaveType}) has been submitted successfully.")
        }
    }

    fun updateLeaveStatus(leave: LeaveRequest, newStatus: String) {
        viewModelScope.launch {
            val updated = leave.copy(status = newStatus)
            repository.updateLeave(updated)
            _statusMessage.emit("Leave request for ${leave.employeeName} has been ${newStatus.lowercase()}ed.")

            // Auto-reflect in attendance if approved
            if (newStatus == "Approved") {
                // If they are approved, pre-fill attendance status
                val record = AttendanceRecord(
                    employeeId = leave.employeeId,
                    employeeName = leave.employeeName,
                    date = leave.startDate,
                    checkIn = "--:--",
                    status = "Leave / Absent"
                )
                repository.insertAttendance(record)
            }
        }
    }

    // --- Attendance Procedures ---
    fun simulateDailyCheckInCheckOut() {
        val empId = actingEmployeeId.value
        viewModelScope.launch {
            val emps = employees.value
            val currentEmpName = emps.find { it.id == empId }?.name ?: "Staff Member"

            // Check if there's already an attendance record for today
            val existing = repository.getAttendanceForEmployeeOnDate(empId, currentDayDate)
            if (existing == null) {
                // Determine if late (assuming 8:30 AM is starting limit)
                val sdf = SimpleDateFormat("hh:mm a", Locale.US)
                val timeStr = SimpleDateFormat("hh:mm a", Locale.US).format(Date())
                val isLate = Math.random() < 0.3 // simulated
                val status = if (isLate) "Late" else "Present"
                val lateMins = if (isLate) (15..45).random() else 0

                val record = AttendanceRecord(
                    employeeId = empId,
                    employeeName = currentEmpName,
                    date = currentDayDate,
                    checkIn = timeStr,
                    status = status,
                    lateMinutes = lateMins
                )
                repository.insertAttendance(record)
                _statusMessage.emit("Check-In Registered! Marked as $status today at $timeStr.")
            } else if (existing.checkOut == null) {
                val timeStr = SimpleDateFormat("hh:mm a", Locale.US).format(Date())
                val updated = existing.copy(checkOut = timeStr)
                repository.updateAttendance(updated)
                _statusMessage.emit("Check-Out Registered today at $timeStr.")
            } else {
                _statusMessage.emit("Your check-in and check-out logs are already recorded for today.")
            }
        }
    }

    // --- Payroll Operations ---
    fun processPayroll(payroll: PayrollRecord) {
        viewModelScope.launch {
            repository.insertPayroll(payroll)
            _statusMessage.emit("Payroll processed for ${payroll.employeeName} for ${payroll.monthYear}.")
        }
    }

    fun markPayrollPaid(payroll: PayrollRecord) {
        viewModelScope.launch {
            val updated = payroll.copy(
                status = "Paid",
                processedDate = currentDayDate
            )
            repository.updatePayroll(updated)
            _statusMessage.emit("Disbursed salary to ${payroll.employeeName}'s registered bank transfer.")
        }
    }

    // --- Teacher Workloads ---
    fun addWorkload(workload: WorkloadAllocation) {
        viewModelScope.launch {
            repository.insertWorkload(workload)
            _statusMessage.emit("Assigned workload: ${workload.subject} to ${workload.teacherName}")
        }
    }

    fun handleSubstitution(workload: WorkloadAllocation, substitutionDetails: String) {
        viewModelScope.launch {
            val updated = workload.copy(substitutionDetails = substitutionDetails)
            repository.updateWorkload(updated)
            _statusMessage.emit("Substitution assigned: ${workload.teacherName} substitution with: $substitutionDetails")
        }
    }

    // --- Performance Assessments ---
    fun addPerformanceReview(review: PerformanceReview) {
        viewModelScope.launch {
            repository.insertPerformanceReview(review)
            _statusMessage.emit("Evaluation added for ${review.employeeName}")
        }
    }

    // --- Communications ---
    fun postAnnouncement(title: String, content: String, target: String) {
        viewModelScope.launch {
            val sender = when (currentRole.value) {
                "Principal" -> "Dr. Arshad Ali (Principal)"
                "HR Manager" -> "Sarah Malik (HR Manager)"
                else -> "Institution Administration"
            }
            val announcement = Announcement(
                title = title,
                content = content,
                date = currentDayDate,
                senderName = sender,
                targetAudience = target
            )
            repository.insertAnnouncement(announcement)
            _statusMessage.emit("Announcement published and circular distributed to $target.")
        }
    }
}

class HRMViewModelFactory(private val repository: HRMRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HRMViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HRMViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
