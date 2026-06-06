package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "employees")
data class Employee(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val role: String, // Principal, HR Manager, Teacher, Accountant, Support
    val department: String, // Science, Arts, Admin, IT, Finance, Support
    val email: String,
    val phone: String,
    val qualification: String,
    val basicSalary: Double,
    val cnic: String,
    val joiningDate: String,
    val status: String = "Active" // Active, Resigned, Suspended
) : Serializable

@Entity(tableName = "attendance")
data class AttendanceRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val employeeId: Int,
    val employeeName: String,
    val date: String, // YYYY-MM-DD
    val checkIn: String, // HH:MM AM/PM
    val checkOut: String? = null,
    val status: String, // Present, Late, Early Departure, Absent
    val lateMinutes: Int = 0
) : Serializable

@Entity(tableName = "leaves")
data class LeaveRequest(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val employeeId: Int,
    val employeeName: String,
    val leaveType: String, // Sick, Casual, Annual
    val startDate: String,
    val endDate: String,
    val reason: String,
    val status: String = "Pending", // Pending, Approved, Rejected
    val appliedDate: String
) : Serializable

@Entity(tableName = "workloads")
data class WorkloadAllocation(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val teacherId: Int,
    val teacherName: String,
    val subject: String,
    val classGrade: String, // Grade 10-A, BSCS-1st Sem
    val hoursPerWeek: Int,
    val examDuty: String? = null,
    val substitutionDetails: String? = null
) : Serializable

@Entity(tableName = "payroll")
data class PayrollRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val employeeId: Int,
    val employeeName: String,
    val monthYear: String, // June 2026
    val basicSalary: Double,
    val allowances: Double,
    val deductions: Double,
    val tax: Double,
    val overtimeHours: Int = 0,
    val netSalary: Double,
    val status: String = "Unpaid", // Paid, Unpaid
    val processedDate: String? = null
) : Serializable

@Entity(tableName = "announcements")
data class Announcement(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val date: String,
    val senderName: String,
    val targetAudience: String = "All" // All, Academic, Admin, Support
) : Serializable

@Entity(tableName = "performance")
data class PerformanceReview(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val employeeId: Int,
    val employeeName: String,
    val kpiScore: Double, // Rating 1.0 to 10.0
    val studentFeedback: Double, // Rating 1.0 to 5.0
    val headEvaluation: String,
    val reviewPeriod: String, // Annual 2025-2026, Semester Fall 2025
    val recommendations: String // Keep, Promoting, Training Required
) : Serializable
