package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface HRMDao {

    // --- Employee Queries ---
    @Query("SELECT * FROM employees ORDER BY name ASC")
    fun getAllEmployees(): Flow<List<Employee>>

    @Query("SELECT * FROM employees WHERE id = :id LIMIT 1")
    suspend fun getEmployeeById(id: Int): Employee?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmployee(employee: Employee): Long

    @Update
    suspend fun updateEmployee(employee: Employee)

    @Delete
    suspend fun deleteEmployee(employee: Employee)

    // --- Attendance Queries ---
    @Query("SELECT * FROM attendance ORDER BY date DESC, checkIn DESC")
    fun getAllAttendance(): Flow<List<AttendanceRecord>>

    @Query("SELECT * FROM attendance WHERE employeeId = :empId ORDER BY date DESC")
    fun getAttendanceForEmployee(empId: Int): Flow<List<AttendanceRecord>>

    @Query("SELECT * FROM attendance WHERE date = :date")
    suspend fun getAttendanceByDate(date: String): List<AttendanceRecord>

    @Query("SELECT * FROM attendance WHERE employeeId = :empId AND date = :date LIMIT 1")
    suspend fun getAttendanceForEmployeeOnDate(empId: Int, date: String): AttendanceRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(record: AttendanceRecord): Long

    @Update
    suspend fun updateAttendance(record: AttendanceRecord)

    // --- Leave Queries ---
    @Query("SELECT * FROM leaves ORDER BY appliedDate DESC")
    fun getAllLeaves(): Flow<List<LeaveRequest>>

    @Query("SELECT * FROM leaves WHERE employeeId = :empId ORDER BY appliedDate DESC")
    fun getLeavesForEmployee(empId: Int): Flow<List<LeaveRequest>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeave(request: LeaveRequest): Long

    @Update
    suspend fun updateLeave(request: LeaveRequest)

    @Delete
    suspend fun deleteLeave(request: LeaveRequest)

    // --- Workload Queries ---
    @Query("SELECT * FROM workloads ORDER BY teacherName ASC")
    fun getAllWorkloads(): Flow<List<WorkloadAllocation>>

    @Query("SELECT * FROM workloads WHERE teacherId = :teacherId")
    fun getWorkloadsForTeacher(teacherId: Int): Flow<List<WorkloadAllocation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkload(workload: WorkloadAllocation): Long

    @Update
    suspend fun updateWorkload(workload: WorkloadAllocation)

    @Delete
    suspend fun deleteWorkload(workload: WorkloadAllocation)

    // --- Payroll Queries ---
    @Query("SELECT * FROM payroll ORDER BY monthYear DESC, employeeName ASC")
    fun getAllPayroll(): Flow<List<PayrollRecord>>

    @Query("SELECT * FROM payroll WHERE employeeId = :empId ORDER BY monthYear DESC")
    fun getPayrollForEmployee(empId: Int): Flow<List<PayrollRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayroll(payroll: PayrollRecord): Long

    @Update
    suspend fun updatePayroll(payroll: PayrollRecord)

    // --- Announcement Queries ---
    @Query("SELECT * FROM announcements ORDER BY date DESC")
    fun getAllAnnouncements(): Flow<List<Announcement>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnouncement(announcement: Announcement): Long

    @Delete
    suspend fun deleteAnnouncement(announcement: Announcement)

    // --- Performance Queries ---
    @Query("SELECT * FROM performance ORDER BY kpiScore DESC")
    fun getAllPerformanceReviews(): Flow<List<PerformanceReview>>

    @Query("SELECT * FROM performance WHERE employeeId = :empId")
    fun getPerformanceReviewsForEmployee(empId: Int): Flow<List<PerformanceReview>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPerformanceReview(review: PerformanceReview): Long

    @Update
    suspend fun updatePerformanceReview(review: PerformanceReview)
}
