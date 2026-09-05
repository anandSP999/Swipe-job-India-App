package com.example.data

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Locale

class FirebaseRepository(private val context: Context) {

    private val auth: FirebaseAuth by lazy {
        ensureFirebaseInitialized()
        FirebaseAuth.getInstance()
    }

    private val db: FirebaseFirestore by lazy {
        ensureFirebaseInitialized()
        FirebaseFirestore.getInstance()
    }

    private fun ensureFirebaseInitialized() {
        if (FirebaseApp.getApps(context).isEmpty()) {
            val options = FirebaseOptions.Builder()
                .setApiKey("AIzaSyDWYjuwSKnaK8S_S7Af1kQPE11ZrPH8OhU")
                .setApplicationId("1:916910145870:web:7afb0e09cba45405d0de1f")
                .setProjectId("swipejobs-a5317")
                .setGcmSenderId("916910145870")
                .build()
            FirebaseApp.initializeApp(context, options)
        }
    }

    fun getCurrentUser(): FirebaseUser? {
        return try {
            auth.currentUser
        } catch (e: Exception) {
            null
        }
    }

    val authStateFlow: Flow<FirebaseUser?> = callbackFlow {
        ensureFirebaseInitialized()
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser)
        }
        auth.addAuthStateListener(listener)
        awaitClose {
            auth.removeAuthStateListener(listener)
        }
    }

    suspend fun login(email: String, pass: String): FirebaseUser {
        ensureFirebaseInitialized()
        val result = auth.signInWithEmailAndPassword(email.trim(), pass).await()
        return result.user ?: throw Exception("Login failed: User is null")
    }

    suspend fun registerBasic(
        name: String,
        email: String,
        pass: String,
        mobile: String,
        category: String
    ): Pair<FirebaseUser, CandidateProfile> {
        ensureFirebaseInitialized()
        val result = auth.createUserWithEmailAndPassword(email.trim(), pass).await()
        val user = result.user ?: throw Exception("Account creation failed")
        val uid = user.uid

        val refCode = generateReferralCode(name, "2000")
        val newProfile = CandidateProfile(
            uid = uid,
            name = name,
            email = email,
            mobile = mobile,
            category = category,
            referralCode = refCode,
            walletBalance = 0.0,
            totalReferrals = 0,
            profileComplete = false
        )

        try {
            db.collection("candidates").document(uid).set(newProfile).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return Pair(user, newProfile)
    }

    fun logout() {
        try {
            auth.signOut()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun observeJobs(): Flow<List<Job>> = callbackFlow {
        ensureFirebaseInitialized()
        var registration: ListenerRegistration? = null
        try {
            registration = db.collection("jobs")
                .whereEqualTo("status", "Approved")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(getStarterJobs())
                        return@addSnapshotListener
                    }

                    if (snapshot == null || snapshot.isEmpty) {
                        trySend(getStarterJobs())
                    } else {
                        val jobsList = snapshot.documents.mapNotNull { doc ->
                            try {
                                val data = doc.data ?: return@mapNotNull null
                                Job(
                                    id = doc.id,
                                    title = data["title"] as? String ?: "Job Vacancy",
                                    companyName = data["companyName"] as? String ?: (data["companyEmail"] as? String)?.substringBefore("@")?.uppercase(Locale.ROOT) ?: "Top Enterprise",
                                    companyEmail = data["companyEmail"] as? String ?: "",
                                    location = data["location"] as? String ?: "Pan India",
                                    salary = data["salary"] as? String ?: "₹25,000 - ₹45,000 / mo",
                                    vacancy = (data["vacancy"] ?: "3").toString(),
                                    role = data["role"] as? String ?: "Associate",
                                    gender = data["gender"] as? String ?: "Any",
                                    edu = data["edu"] as? String ?: "Graduate / Diploma",
                                    working = data["working"] as? String ?: "9:30 AM - 6:30 PM",
                                    hiringFor = data["hiringFor"] as? String ?: "Direct Payroll",
                                    benefits = data["benefits"] as? String ?: "PF + Medical + Performance Incentives",
                                    teamsize = (data["teamsize"] ?: "50+").toString(),
                                    interviewer = data["interviewer"] as? String ?: "Talent Acquisition Lead",
                                    desc = data["desc"] as? String ?: "Exciting career opportunity with rapid promotions, modern workplace, and team benefits.",
                                    category = data["category"] as? String ?: "Computer",
                                    jobType = data["jobType"] as? String ?: "full-time",
                                    isCampaignActive = data["isCampaignActive"] as? Boolean ?: true,
                                    referralReward = (data["referralReward"] as? Number)?.toDouble() ?: 500.0,
                                    applyCount = (data["applyCount"] as? Number)?.toInt() ?: 24,
                                    status = data["status"] as? String ?: "Approved"
                                )
                            } catch (e: Exception) {
                                null
                            }
                        }
                        if (jobsList.isEmpty()) {
                            trySend(getStarterJobs())
                        } else {
                            trySend(jobsList)
                        }
                    }
                }
        } catch (e: Exception) {
            trySend(getStarterJobs())
        }

        awaitClose {
            registration?.remove()
        }
    }

    fun observeCandidateProfile(uid: String): Flow<CandidateProfile?> = callbackFlow {
        ensureFirebaseInitialized()
        if (uid.isEmpty()) {
            trySend(null)
            close()
            return@callbackFlow
        }

        var registration: ListenerRegistration? = null
        try {
            registration = db.collection("candidates").document(uid)
                .addSnapshotListener { snapshot, _ ->
                    if (snapshot != null && snapshot.exists()) {
                        val d = snapshot.data ?: return@addSnapshotListener
                        val profile = CandidateProfile(
                            uid = uid,
                            name = d["name"] as? String ?: "",
                            email = d["email"] as? String ?: "",
                            mobile = d["mobile"] as? String ?: "",
                            category = d["category"] as? String ?: "",
                            city = d["city"] as? String ?: "",
                            address = d["address"] as? String ?: "",
                            dob = d["dob"] as? String ?: "",
                            age = d["age"] as? String ?: "",
                            education = d["education"] as? String ?: "",
                            expectedSalary = d["expectedSalary"] as? String ?: "",
                            noticePeriod = d["noticePeriod"] as? String ?: "",
                            prefLocation = d["prefLocation"] as? String ?: "",
                            workHistory = d["workHistory"] as? String ?: "",
                            skills = d["skills"] as? String ?: "",
                            languages = d["languages"] as? String ?: "",
                            linkedin = d["linkedin"] as? String ?: "",
                            portfolio = d["portfolio"] as? String ?: "",
                            certificates = d["certificates"] as? String ?: "",
                            panCard = d["panCard"] as? String ?: "",
                            resumeUrl = d["resumeUrl"] as? String ?: "",
                            photoUrl = d["photoUrl"] as? String ?: "",
                            aadhaarFUrl = d["aadhaarFUrl"] as? String ?: "",
                            aadhaarBUrl = d["aadhaarBUrl"] as? String ?: "",
                            referralCode = d["referralCode"] as? String ?: generateReferralCode(d["name"] as? String ?: "User", "2000"),
                            walletBalance = (d["walletBalance"] as? Number)?.toDouble() ?: 0.0,
                            totalReferrals = (d["totalReferrals"] as? Number)?.toInt() ?: 0,
                            profileComplete = d["profileComplete"] as? Boolean ?: false
                        )
                        trySend(profile)
                    } else {
                        trySend(null)
                    }
                }
        } catch (e: Exception) {
            trySend(null)
        }

        awaitClose {
            registration?.remove()
        }
    }

    suspend fun saveProfile(profile: CandidateProfile) {
        ensureFirebaseInitialized()
        db.collection("candidates").document(profile.uid).set(profile).await()
    }

    fun observeApplications(uid: String): Flow<List<ApplicationRecord>> = callbackFlow {
        ensureFirebaseInitialized()
        if (uid.isEmpty()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        var registration: ListenerRegistration? = null
        try {
            registration = db.collection("applications")
                .whereEqualTo("candidateId", uid)
                .addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null) {
                        trySend(emptyList())
                        return@addSnapshotListener
                    }

                    val list = snapshot.documents.mapNotNull { doc ->
                        val data = doc.data ?: return@mapNotNull null
                        ApplicationRecord(
                            id = doc.id,
                            candidateId = data["candidateId"] as? String ?: "",
                            companyId = data["companyId"] as? String ?: "",
                            jobId = data["jobId"] as? String ?: "",
                            companyName = data["companyName"] as? String ?: "Company",
                            jobTitle = data["jobTitle"] as? String ?: "Role",
                            status = data["status"] as? String ?: "Applied",
                            usedReferralCode = data["usedReferralCode"] as? String ?: "",
                            appliedAt = (data["appliedAt"] as? com.google.firebase.Timestamp)?.toDate()?.time
                                ?: System.currentTimeMillis()
                        )
                    }.sortedByDescending { it.appliedAt }

                    trySend(list)
                }
        } catch (e: Exception) {
            trySend(emptyList())
        }

        awaitClose {
            registration?.remove()
        }
    }

    suspend fun submitApplication(
        candidateId: String,
        job: Job,
        candidateProfile: CandidateProfile,
        usedReferralCode: String
    ): ApplicationRecord {
        ensureFirebaseInitialized()
        val docRef = db.collection("applications").document()
        val appRecord = ApplicationRecord(
            id = docRef.id,
            candidateId = candidateId,
            companyId = job.companyEmail,
            jobId = job.id,
            companyName = job.companyName,
            jobTitle = job.title,
            status = "Applied",
            usedReferralCode = usedReferralCode.trim().uppercase(Locale.ROOT),
            appliedAt = System.currentTimeMillis()
        )

        val payload = hashMapOf(
            "candidateId" to candidateId,
            "companyId" to job.companyEmail,
            "jobId" to job.id,
            "companyName" to job.companyName,
            "jobTitle" to job.title,
            "status" to "Applied",
            "candidateData" to hashMapOf(
                "name" to candidateProfile.name,
                "email" to candidateProfile.email,
                "mobile" to candidateProfile.mobile,
                "category" to candidateProfile.category,
                "education" to candidateProfile.education,
                "skills" to candidateProfile.skills,
                "city" to candidateProfile.city
            ),
            "usedReferralCode" to usedReferralCode.trim().uppercase(Locale.ROOT),
            "appliedAt" to com.google.firebase.Timestamp.now()
        )

        docRef.set(payload).await()
        return appRecord
    }

    fun observeReferralUsages(referralCode: String): Flow<List<ApplicationRecord>> = callbackFlow {
        ensureFirebaseInitialized()
        if (referralCode.isEmpty()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        var registration: ListenerRegistration? = null
        try {
            registration = db.collection("applications")
                .whereEqualTo("usedReferralCode", referralCode.uppercase(Locale.ROOT))
                .addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null) {
                        trySend(emptyList())
                        return@addSnapshotListener
                    }
                    val list = snapshot.documents.mapNotNull { doc ->
                        val data = doc.data ?: return@mapNotNull null
                        ApplicationRecord(
                            id = doc.id,
                            candidateId = data["candidateId"] as? String ?: "",
                            companyId = data["companyId"] as? String ?: "",
                            jobId = data["jobId"] as? String ?: "",
                            companyName = data["companyName"] as? String ?: "Company",
                            jobTitle = data["jobTitle"] as? String ?: "Role",
                            status = data["status"] as? String ?: "Applied",
                            usedReferralCode = referralCode,
                            appliedAt = (data["appliedAt"] as? com.google.firebase.Timestamp)?.toDate()?.time
                                ?: System.currentTimeMillis()
                        )
                    }
                    trySend(list)
                }
        } catch (e: Exception) {
            trySend(emptyList())
        }

        awaitClose {
            registration?.remove()
        }
    }

    suspend fun reportJob(jobId: String, candidateId: String, reason: String) {
        ensureFirebaseInitialized()
        val payload = hashMapOf(
            "jobId" to jobId,
            "candidateId" to candidateId,
            "reason" to reason,
            "timestamp" to com.google.firebase.Timestamp.now()
        )
        db.collection("reports").add(payload).await()
    }

    suspend fun requestRedemption(uid: String, amount: Double, phone: String) {
        ensureFirebaseInitialized()
        val payload = hashMapOf(
            "candidateId" to uid,
            "amount" to amount,
            "mobile" to phone,
            "status" to "Pending Verification",
            "requestedAt" to com.google.firebase.Timestamp.now()
        )
        db.collection("redeem_requests").add(payload).await()
    }

    private fun generateReferralCode(name: String, dob: String): String {
        val cleanName = name.replace("\\s+".toRegex(), "").take(4).uppercase(Locale.ROOT)
        val cleanYear = dob.split("-").firstOrNull()?.takeLast(4) ?: "2024"
        return if (cleanName.length >= 2) "$cleanName$cleanYear" else "SJINDIA${(100..999).random()}"
    }

    fun getStarterJobs(): List<Job> {
        return listOf(
            Job(
                id = "sj_job_1",
                title = "Android App Developer (Jetpack Compose)",
                companyName = "Infosys Mobility Labs",
                companyEmail = "careers@infosys.com",
                location = "Bangalore, Karnataka",
                salary = "₹6.5 - ₹10.5 LPA",
                vacancy = "4",
                role = "Software Engineer",
                gender = "Both",
                edu = "B.Tech / BCA / MCA",
                working = "Hybrid (3 days office)",
                hiringFor = "Direct Company Payroll",
                benefits = "Health Insurance + Free Canteen + Performance Bonus",
                teamsize = "250+",
                interviewer = "Lead Mobile Architect",
                desc = "We are seeking a talented Android Developer experienced in Kotlin, Jetpack Compose, Coroutines, and MVVM architecture. You will craft next-generation consumer apps with seamless animations and real-time syncing.",
                category = "Computer",
                jobType = "hybrid",
                isCampaignActive = true,
                referralReward = 1200.0,
                applyCount = 48,
                status = "Approved"
            ),
            Job(
                id = "sj_job_2",
                title = "Senior Digital Marketing Strategist",
                companyName = "Zomato Media Pvt Ltd",
                companyEmail = "talent@zomato.com",
                location = "Gurugram, Delhi NCR",
                salary = "₹5.0 - ₹8.0 LPA",
                vacancy = "2",
                role = "Marketing Manager",
                gender = "Both",
                edu = "Any Graduate / MBA",
                working = "Monday - Friday (Full Time)",
                hiringFor = "Direct Payroll",
                benefits = "Meal Credits + Gym Subsidy + ESOPs",
                teamsize = "500+",
                interviewer = "Head of Growth & Brand",
                desc = "Scale our social viral marketing campaigns, influencer partnerships, and user acquisition pipelines. Strong copywriting, data analytics, and ROAS optimization skills required.",
                category = "Marketing",
                jobType = "full-time",
                isCampaignActive = true,
                referralReward = 800.0,
                applyCount = 62,
                status = "Approved"
            ),
            Job(
                id = "sj_job_3",
                title = "Customer Support Specialist (Voice/Chat)",
                companyName = "Swiggy India Operations",
                companyEmail = "jobs@swiggy.in",
                location = "Pune, Maharashtra",
                salary = "₹22,000 - ₹32,000 / mo",
                vacancy = "15",
                role = "Support Associate",
                gender = "Both",
                edu = "12th Pass / Any Graduate",
                working = "Rotational Shifts (5 Days/wk)",
                hiringFor = "Direct Operations",
                benefits = "Night Shift Allowance + Cab Facility + Insurance",
                teamsize = "1000+",
                interviewer = "Operations Manager",
                desc = "Assist valued restaurant partners and customers with high empathy, quick issue resolution, and friendly communication in English and Hindi.",
                category = "Telecaller",
                jobType = "full-time",
                isCampaignActive = true,
                referralReward = 600.0,
                applyCount = 95,
                status = "Approved"
            ),
            Job(
                id = "sj_job_4",
                title = "Financial Accountant & GST Specialist",
                companyName = "Tata Consultancy Services",
                companyEmail = "finance-hiring@tcs.com",
                location = "Mumbai, Maharashtra",
                salary = "₹4.5 - ₹7.2 LPA",
                vacancy = "3",
                role = "Finance Analyst",
                gender = "Both",
                edu = "B.Com / M.Com / Inter CA",
                working = "Standard Corporate (9:30 AM - 6 PM)",
                hiringFor = "Direct Tata Enterprise",
                benefits = "Gratuity + Yearly Bonus + Medical Cover",
                teamsize = "10,000+",
                interviewer = "Senior Finance Director",
                desc = "Responsible for corporate ledger reconciliation, GST return filings, TDS compliance, and preparation of monthly P&L accounts.",
                category = "Accounts",
                jobType = "full-time",
                isCampaignActive = true,
                referralReward = 1000.0,
                applyCount = 37,
                status = "Approved"
            ),
            Job(
                id = "sj_job_5",
                title = "Territory Field Sales Executive",
                companyName = "Reliance Retail JioMart",
                companyEmail = "recruitment@reliance.com",
                location = "Ahmedabad, Gujarat",
                salary = "₹28,000 - ₹40,000 + Daily TA/DA",
                vacancy = "8",
                role = "Sales Executive",
                gender = "Male",
                edu = "10th / 12th Pass / Graduate",
                working = "Field Visits (10 AM - 7 PM)",
                hiringFor = "Direct Reliance Payroll",
                benefits = "Mobile Allowance + Travel Fuel + High Commission",
                teamsize = "2000+",
                interviewer = "Area Sales Manager",
                desc = "Onboard local retail kirana stores onto JioMart B2B platform. Promote FMCG catalogue discounts and achieve weekly order targets.",
                category = "Field Marketing",
                jobType = "full-time",
                isCampaignActive = true,
                referralReward = 750.0,
                applyCount = 81,
                status = "Approved"
            ),
            Job(
                id = "sj_job_6",
                title = "HR Talent Acquisition Executive",
                companyName = "Paytm Financial Services",
                companyEmail = "hr@paytm.com",
                location = "Noida, Uttar Pradesh",
                salary = "₹3.8 - ₹6.0 LPA",
                vacancy = "2",
                role = "HR Specialist",
                gender = "Female",
                edu = "MBA in HR / Any Graduate",
                working = "Monday - Friday (Hybrid)",
                hiringFor = "Direct Payroll",
                benefits = "Laptop Provided + Wellness Benefits + Paid Leaves",
                teamsize = "1200+",
                interviewer = "HR Business Partner",
                desc = "Handle full-cycle recruitment for tech and sales roles. Screen candidate resumes, coordinate interview rounds, and prepare offer rollouts.",
                category = "HR",
                jobType = "hybrid",
                isCampaignActive = true,
                referralReward = 900.0,
                applyCount = 44,
                status = "Approved"
            )
        )
    }
}
