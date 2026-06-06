package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Employee::class,
        AttendanceRecord::class,
        LeaveRequest::class,
        WorkloadAllocation::class,
        PayrollRecord::class,
        Announcement::class,
        PerformanceReview::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SchoolHRMDatabase : RoomDatabase() {
    abstract fun hrmDao(): HRMDao

    companion object {
        @Volatile
        private var INSTANCE: SchoolHRMDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): SchoolHRMDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SchoolHRMDatabase::class.java,
                    "school_hrm_database"
                )
                .addCallback(DatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.hrmDao())
                    }
                }
            }
        }

        suspend fun populateDatabase(dao: HRMDao) {
            // Populating Predefined School Staff with localized/international identities
            val emp1 = Employee(
                id = 1,
                name = "Dr. Arshad Ali",
                role = "Principal",
                department = "Admin",
                email = "principal@edu.com",
                phone = "0300-1234567",
                qualification = "Ph.D. in Educational Leadership",
                basicSalary = 180000.0,
                cnic = "35201-1234567-1",
                joiningDate = "2020-03-12"
            )
            val emp2 = Employee(
                id = 2,
                name = "Sarah Malik",
                role = "HR Manager",
                department = "Admin",
                email = "hr@edu.com",
                phone = "0311-9876543",
                qualification = "MBA in Human Resources",
                basicSalary = 95000.0,
                cnic = "34101-9876543-2",
                joiningDate = "2021-08-01"
            )
            val emp3 = Employee(
                id = 3,
                name = "Muhammad Junaid",
                role = "Accountant",
                department = "Finance",
                email = "accounts@edu.com",
                phone = "0322-1112233",
                qualification = "ACCA Certified",
                basicSalary = 85000.0,
                cnic = "33102-1112233-5",
                joiningDate = "2022-01-15"
            )
            val emp4 = Employee(
                id = 4,
                name = "Prof. Ayesha Khan",
                role = "Teacher",
                department = "Science",
                email = "ayesha.khan@edu.com",
                phone = "0333-4455667",
                qualification = "M.Phil. in Physics",
                basicSalary = 110000.0,
                cnic = "35202-4455667-6",
                joiningDate = "2019-10-10"
            )
            val emp5 = Employee(
                id = 5,
                name = "Prof. Bilal Ahmed",
                role = "Teacher",
                department = "IT",
                email = "bilal.ahmed@edu.com",
                phone = "0345-5678123",
                qualification = "MS in Computer Science",
                basicSalary = 120000.0,
                cnic = "35203-5678123-1",
                joiningDate = "2021-02-28"
            )
            val emp6 = Employee(
                id = 6,
                name = "Prof. Maria Zainab",
                role = "Teacher",
                department = "Science",
                email = "maria.zainab@edu.com",
                phone = "0304-4443332",
                qualification = "M.Sc. in Mathematics",
                basicSalary = 100000.0,
                cnic = "35102-4443332-8",
                joiningDate = "2022-09-01"
            )
            val emp7 = Employee(
                id = 7,
                name = "Sajid Mahmood",
                role = "Support",
                department = "Support",
                email = "sajid.support@edu.com",
                phone = "0315-7778889",
                qualification = "Intermediate",
                basicSalary = 45000.0,
                cnic = "35202-7778889-3",
                joiningDate = "2023-05-14"
            )

            dao.insertEmployee(emp1)
            dao.insertEmployee(emp2)
            dao.insertEmployee(emp3)
            dao.insertEmployee(emp4)
            dao.insertEmployee(emp5)
            dao.insertEmployee(emp6)
            dao.insertEmployee(emp7)

            // Populate Teacher Workloads
            dao.insertWorkload(
                WorkloadAllocation(
                    teacherId = 4,
                    teacherName = "Prof. Ayesha Khan",
                    subject = "Advanced Physics (XII)",
                    classGrade = "Grade 12-A",
                    hoursPerWeek = 18,
                    examDuty = "Hall A - Morning Shift"
                )
            )
            dao.insertWorkload(
                WorkloadAllocation(
                    teacherId = 4,
                    teacherName = "Prof. Ayesha Khan",
                    subject = "Electrodynamics (BS-Physics)",
                    classGrade = "BS-Physics 4th Sem",
                    hoursPerWeek = 6
                )
            )
            dao.insertWorkload(
                WorkloadAllocation(
                    teacherId = 5,
                    teacherName = "Prof. Bilal Ahmed",
                    subject = "Introduction to Programming (Kotlin)",
                    classGrade = "BSCS-1st Sem",
                    hoursPerWeek = 12,
                    examDuty = "Lab 2 - Evening Shift"
                )
            )
            dao.insertWorkload(
                WorkloadAllocation(
                    teacherId = 5,
                    teacherName = "Prof. Bilal Ahmed",
                    subject = "Mobile App Development",
                    classGrade = "BSCS-6th Sem",
                    hoursPerWeek = 12,
                )
            )
            dao.insertWorkload(
                WorkloadAllocation(
                    teacherId = 6,
                    teacherName = "Prof. Maria Zainab",
                    subject = "Calculus-II",
                    classGrade = "Grade 11-C",
                    hoursPerWeek = 15,
                    examDuty = "Hall B - Morning Shift"
                )
            )

            // Populate Attendance for last couple of days
            val dates = listOf("2026-06-05", "2026-06-06")
            for (date in dates) {
                dao.insertAttendance(AttendanceRecord(employeeId = 1, employeeName = "Dr. Arshad Ali", date = date, checkIn = "08:05 AM", checkOut = "04:30 PM", status = "Present"))
                dao.insertAttendance(AttendanceRecord(employeeId = 2, employeeName = "Sarah Malik", date = date, checkIn = "08:15 AM", checkOut = "04:15 PM", status = "Present"))
                dao.insertAttendance(AttendanceRecord(employeeId = 3, employeeName = "Muhammad Junaid", date = date, checkIn = "08:45 AM", checkOut = "04:45 PM", status = "Late", lateMinutes = 15))
                dao.insertAttendance(AttendanceRecord(employeeId = 4, employeeName = "Prof. Ayesha Khan", date = date, checkIn = "08:00 AM", checkOut = "03:10 PM", status = "Present"))
                dao.insertAttendance(AttendanceRecord(employeeId = 5, employeeName = "Prof. Bilal Ahmed", date = date, checkIn = "08:10 AM", checkOut = "03:40 PM", status = "Present"))
                if (date == "2026-06-05") {
                    dao.insertAttendance(AttendanceRecord(employeeId = 6, employeeName = "Prof. Maria Zainab", date = date, checkIn = "08:25 AM", checkOut = "03:00 PM", status = "Present"))
                } else {
                    dao.insertAttendance(AttendanceRecord(employeeId = 6, employeeName = "Prof. Maria Zainab", date = date, checkIn = "--:--", checkOut = null, status = "Absent"))
                }
                dao.insertAttendance(AttendanceRecord(employeeId = 7, employeeName = "Sajid Mahmood", date = date, checkIn = "07:50 AM", checkOut = "05:00 PM", status = "Present"))
            }

            // Populate Leave Requests
            dao.insertLeave(
                LeaveRequest(
                    employeeId = 6,
                    employeeName = "Prof. Maria Zainab",
                    leaveType = "Sick",
                    startDate = "2026-06-06",
                    endDate = "2026-06-07",
                    reason = "Suffering from severe flu and high blood pressure",
                    status = "Approved",
                    appliedDate = "2026-06-05"
                )
            )
            dao.insertLeave(
                LeaveRequest(
                    employeeId = 5,
                    employeeName = "Prof. Bilal Ahmed",
                    leaveType = "Personal/Casual",
                    startDate = "2026-06-15",
                    endDate = "2026-06-16",
                    reason = "Family litigation matters in Lahore high court",
                    status = "Pending",
                    appliedDate = "2026-06-06"
                )
            )

            // Populate Payroll Records (Previous month May 2026)
            dao.insertPayroll(
                PayrollRecord(
                    employeeId = 4,
                    employeeName = "Prof. Ayesha Khan",
                    monthYear = "May 2026",
                    basicSalary = 110000.0,
                    allowances = 12000.0,
                    deductions = 0.0,
                    tax = 8500.0,
                    netSalary = 113500.0,
                    status = "Paid",
                    processedDate = "2026-05-30"
                )
            )
            dao.insertPayroll(
                PayrollRecord(
                    employeeId = 5,
                    employeeName = "Prof. Bilal Ahmed",
                    monthYear = "May 2026",
                    basicSalary = 120000.0,
                    allowances = 15000.0,
                    deductions = 3000.0,
                    tax = 9200.0,
                    netSalary = 122800.0,
                    status = "Paid",
                    processedDate = "2026-05-30"
                )
            )
            dao.insertPayroll(
                PayrollRecord(
                    employeeId = 6,
                    employeeName = "Prof. Maria Zainab",
                    monthYear = "May 2026",
                    basicSalary = 100000.0,
                    allowances = 10000.0,
                    deductions = 1500.0,
                    tax = 7000.0,
                    netSalary = 101500.0,
                    status = "Pending"
                )
            )

            // Populate Performance Reviews
            dao.insertPerformanceReview(
                PerformanceReview(
                    employeeId = 4,
                    employeeName = "Prof. Ayesha Khan",
                    kpiScore = 9.4,
                    studentFeedback = 4.8,
                    headEvaluation = "Excellent classroom instruction. High research alignment.",
                    reviewPeriod = "Annual 2025-2026",
                    recommendations = "Promoting to Assistant Prof"
                )
            )
            dao.insertPerformanceReview(
                PerformanceReview(
                    employeeId = 5,
                    employeeName = "Prof. Bilal Ahmed",
                    kpiScore = 8.8,
                    studentFeedback = 4.5,
                    headEvaluation = "Strong technical knowledge, highly active in computer lab setup.",
                    reviewPeriod = "Annual 2025-2026",
                    recommendations = "Key Retention Bonus"
                )
            )

            // Populate Announcements
            dao.insertAnnouncement(
                Announcement(
                    title = "Board of Directors Meeting Schedule",
                    content = "The 12th annual BoD meeting will take place in the executive committee room on Tuesday at 10:00 AM. All HODs are requested to bring departmental KPIs budgets.",
                    date = "2026-06-05",
                    senderName = "Dr. Arshad Ali (Principal)",
                    targetAudience = "Admin"
                )
            )
            dao.insertAnnouncement(
                Announcement(
                    title = "Teacher Training Workshop: Modern Pedagogy",
                    content = "A three-day compulsory training workshop for teaching staff on integration of digital LMS and active learning pedagogy starts from next Wednesday in the main auditorium.",
                    date = "2026-06-03",
                    senderName = "Sarah Malik (HR Manager)",
                    targetAudience = "Academic"
                )
            )
            dao.insertAnnouncement(
                Announcement(
                    title = "Implementation of RFID Bio-Attendance",
                    content = "All staff are directed to register their biometrics and collect new RFID active cards from the HR desk. Attendance scaling starts strictly from 15th June.",
                    date = "2026-06-01",
                    senderName = "Dr. Arshad Ali (Principal)",
                    targetAudience = "All"
                )
            )
        }
    }
}
