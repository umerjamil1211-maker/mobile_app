package com.example.data

import kotlinx.coroutines.flow.Flow

class HRMRepository(private val hrmDao: HRMDao) {

    // --- Employee ---
    val allEmployees: Flow<List<Employee>> = hrmDao.getAllEmployees()

    suspend fun getEmployeeById(id: Int): Employee? = hrmDao.getEmployeeById(id)

    suspend fun insertEmployee(employee: Employee): Long = hrmDao.insertEmployee(employee)

    suspend fun updateEmployee(employee: Employee) = hrmDao.updateEmployee(employee)

    suspend fun deleteEmployee(employee: Employee) = hrmDao.deleteEmployee(employee)

    // --- Attendance ---
    val allAttendance: Flow<List<AttendanceRecord>> = hrmDao.getAllAttendance()

    fun getAttendanceForEmployee(empId: Int): Flow<List<AttendanceRecord>> =
        hrmDao.getAttendanceForEmployee(empId)

    suspend fun getAttendanceByDate(date: String): List<AttendanceRecord> =
        hrmDao.getAttendanceByDate(date)

    suspend fun getAttendanceForEmployeeOnDate(empId: Int, date: String): AttendanceRecord? =
        hrmDao.getAttendanceForEmployeeOnDate(empId, date)

    suspend fun insertAttendance(record: AttendanceRecord): Long = hrmDao.insertAttendance(record)

    suspend fun updateAttendance(record: AttendanceRecord) = hrmDao.updateAttendance(record)

    // --- Leaves ---
    val allLeaves: Flow<List<LeaveRequest>> = hrmDao.getAllLeaves()

    fun getLeavesForEmployee(empId: Int): Flow<List<LeaveRequest>> =
        hrmDao.getLeavesForEmployee(empId)

    suspend fun insertLeave(request: LeaveRequest): Long = hrmDao.insertLeave(request)

    suspend fun updateLeave(request: LeaveRequest) = hrmDao.updateLeave(request)

    suspend fun deleteLeave(request: LeaveRequest) = hrmDao.deleteLeave(request)

    // --- Workloads ---
    val allWorkloads: Flow<List<WorkloadAllocation>> = hrmDao.getAllWorkloads()

    fun getWorkloadsForTeacher(teacherId: Int): Flow<List<WorkloadAllocation>> =
        hrmDao.getWorkloadsForTeacher(teacherId)

    suspend fun insertWorkload(workload: WorkloadAllocation): Long = hrmDao.insertWorkload(workload)

    suspend fun updateWorkload(workload: WorkloadAllocation) = hrmDao.updateWorkload(workload)

    suspend fun deleteWorkload(workload: WorkloadAllocation) = hrmDao.deleteWorkload(workload)

    // --- Payroll ---
    val allPayroll: Flow<List<PayrollRecord>> = hrmDao.getAllPayroll()

    fun getPayrollForEmployee(empId: Int): Flow<List<PayrollRecord>> =
        hrmDao.getPayrollForEmployee(empId)

    suspend fun insertPayroll(payroll: PayrollRecord): Long = hrmDao.insertPayroll(payroll)

    suspend fun updatePayroll(payroll: PayrollRecord) = hrmDao.updatePayroll(payroll)

    // --- Announcements ---
    val allAnnouncements: Flow<List<Announcement>> = hrmDao.getAllAnnouncements()

    suspend fun insertAnnouncement(announcement: Announcement): Long = hrmDao.insertAnnouncement(announcement)

    suspend fun deleteAnnouncement(announcement: Announcement) = hrmDao.deleteAnnouncement(announcement)

    // --- Performance ---
    val allPerformanceReviews: Flow<List<PerformanceReview>> = hrmDao.getAllPerformanceReviews()

    fun getPerformanceReviewsForEmployee(empId: Int): Flow<List<PerformanceReview>> =
        hrmDao.getPerformanceReviewsForEmployee(empId)

    suspend fun insertPerformanceReview(review: PerformanceReview): Long = hrmDao.insertPerformanceReview(review)

    suspend fun updatePerformanceReview(review: PerformanceReview) = hrmDao.updatePerformanceReview(review)
}
