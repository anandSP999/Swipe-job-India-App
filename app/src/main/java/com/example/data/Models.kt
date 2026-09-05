package com.example.data

import java.util.UUID

data class Job(
    val id: String = "",
    val title: String = "",
    val companyName: String = "",
    val companyEmail: String = "",
    val location: String = "Pan India",
    val salary: String = "Not Disclosed",
    val vacancy: String = "N/A",
    val role: String = "General",
    val gender: String = "Any", // male, female, both, guys
    val edu: String = "N/A",
    val working: String = "Standard",
    val hiringFor: String = "Directly for Company",
    val benefits: String = "Standard Perks",
    val teamsize: String = "N/A",
    val interviewer: String = "HR Department",
    val desc: String = "",
    val category: String = "Other", // Computer, Marketing, Telecaller, Field Marketing, Sales, Accounts, HR, Other
    val jobType: String = "full-time", // full-time, part-time, remote, hybrid
    val isCampaignActive: Boolean = false,
    val referralReward: Double = 0.0,
    val applyCount: Int = 18,
    val status: String = "Approved",
    val createdAt: Long = System.currentTimeMillis()
)

data class CandidateProfile(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val mobile: String = "",
    val category: String = "",
    val city: String = "",
    val address: String = "",
    val dob: String = "",
    val age: String = "",
    val gender: String = "Male",
    val education: String = "",
    val expectedSalary: String = "",
    val noticePeriod: String = "",
    val prefLocation: String = "",
    val workHistory: String = "",
    val skills: String = "",
    val languages: String = "",
    val linkedin: String = "",
    val portfolio: String = "",
    val certificates: String = "",
    val panCard: String = "",
    val resumeUrl: String = "",
    val photoUrl: String = "",
    val aadhaarFUrl: String = "",
    val aadhaarBUrl: String = "",
    val referralCode: String = "",
    val walletBalance: Double = 0.0,
    val totalReferrals: Int = 0,
    val profileComplete: Boolean = false
)

data class ApplicationRecord(
    val id: String = "",
    val candidateId: String = "",
    val companyId: String = "",
    val jobId: String = "",
    val companyName: String = "",
    val jobTitle: String = "",
    val status: String = "Applied", // Applied, Selected, Shortlisted, Rejected, Interview Scheduled
    val usedReferralCode: String = "",
    val appliedAt: Long = System.currentTimeMillis()
)

data class SavedAccount(
    val uid: String,
    val email: String,
    val name: String,
    val category: String = "",
    val photoUrl: String = "",
    val lastActive: Long = System.currentTimeMillis(),
    val savedPassword: String = ""
)

data class AppNotification(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val message: String,
    val type: String, // "JOB_ALERT", "STATUS_UPDATE", "WALLET", "SYSTEM"
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)
