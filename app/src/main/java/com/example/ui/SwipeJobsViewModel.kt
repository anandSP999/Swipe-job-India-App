package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppNotification
import com.example.data.ApplicationRecord
import com.example.data.CandidateProfile
import com.example.data.FirebaseRepository
import com.example.data.Job
import com.example.data.NetworkMonitor
import com.example.data.NotificationHelper
import com.example.data.SavedAccount
import com.example.data.SavedAccountStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

enum class ScreenTab {
    SWIPE,
    APPLIED,
    WALLET,
    PROFILE
}

enum class LocationFilter {
    NEARBY,
    PAN_INDIA
}

data class SwipeJobsUiState(
    val currentUserEmail: String? = null,
    val currentUserId: String? = null,
    val candidateProfile: CandidateProfile? = null,
    val allJobs: List<Job> = emptyList(),
    val filteredJobs: List<Job> = emptyList(),
    val applications: List<ApplicationRecord> = emptyList(),
    val referralApplications: List<ApplicationRecord> = emptyList(),
    val savedAccounts: List<SavedAccount> = emptyList(),
    val isOnline: Boolean = true,
    val isDarkTheme: Boolean = false,
    val selectedTab: ScreenTab = ScreenTab.SWIPE,
    val isListView: Boolean = false,
    val lastSwipedLeftJob: Job? = null,
    val selectedJobForModal: Job? = null,
    val selectedJobForApply: Job? = null,
    val selectedJobForShare: Job? = null,
    val selectedJobForReport: Job? = null,
    val showCvDialog: Boolean = false,
    val showAccountSwitchDialog: Boolean = false,
    val showNotificationsDialog: Boolean = false,
    val showCompleteProfilePrompt: Boolean = false,
    val showProfilePhotoViewer: Boolean = false,
    val viewingDocument: com.example.data.UploadedDocument? = null,
    val notifications: List<AppNotification> = emptyList(),
    val unreadNotificationsCount: Int = 0,
    val searchQuery: String = "",
    val selectedCategory: String = "All",
    val selectedJobTypes: Set<String> = setOf("full-time", "part-time", "remote", "hybrid"),
    val locationFilter: LocationFilter = LocationFilter.NEARBY,
    val genderFilter: String = "All",
    val displayedJobsLimit: Int = 10,
    val isLoadingMore: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class SwipeJobsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FirebaseRepository(application)
    private val accountStore = SavedAccountStore(application)
    private val networkMonitor = NetworkMonitor(application)
    private val notificationHelper = NotificationHelper(application)

    private val _uiState = MutableStateFlow(
        SwipeJobsUiState(
            isDarkTheme = accountStore.isDarkTheme(),
            savedAccounts = accountStore.getSavedAccounts()
        )
    )
    val uiState: StateFlow<SwipeJobsUiState> = _uiState.asStateFlow()

    private val dismissedJobIds = mutableSetOf<String>()
    private val knownJobIds = mutableSetOf<String>()
    private val knownAppStatuses = mutableMapOf<String, String>()

    init {
        // Monitor online/offline status
        viewModelScope.launch {
            networkMonitor.isOnline.collectLatest { online ->
                _uiState.update { it.copy(isOnline = online) }
            }
        }

        // Monitor Auth state
        viewModelScope.launch {
            repository.authStateFlow.collectLatest { user ->
                _uiState.update {
                    it.copy(
                        currentUserId = user?.uid,
                        currentUserEmail = user?.email
                    )
                }

                if (user != null) {
                    observeUserData(user.uid)
                } else {
                    _uiState.update {
                        it.copy(
                            candidateProfile = null,
                            applications = emptyList(),
                            referralApplications = emptyList()
                        )
                    }
                }
            }
        }

        // Monitor Jobs
        viewModelScope.launch {
            repository.observeJobs().collectLatest { jobs ->
                // Check for new jobs to fire push notification
                if (knownJobIds.isNotEmpty()) {
                    val newJobs = jobs.filter { !knownJobIds.contains(it.id) }
                    for (job in newJobs) {
                        notificationHelper.showJobAlertNotification(job.title, job.companyName, job.salary)
                        addInAppNotification(
                            AppNotification(
                                title = "🔥 New Job Alert",
                                message = "${job.companyName} is hiring for ${job.title} (${job.salary})",
                                type = "JOB_ALERT"
                            )
                        )
                    }
                }
                jobs.forEach { knownJobIds.add(it.id) }

                _uiState.update { state ->
                    val updatedAll = jobs
                    val filtered = computeFilteredJobs(updatedAll, state)
                    state.copy(
                        allJobs = updatedAll,
                        filteredJobs = filtered
                    )
                }
            }
        }

        // Auto-login from saved accounts if available
        checkSavedAccountsAutoLogin()
    }

    private fun checkSavedAccountsAutoLogin() {
        val activeUid = accountStore.getActiveUid()
        val accounts = accountStore.getSavedAccounts()
        val match = accounts.firstOrNull { it.uid == activeUid } ?: accounts.firstOrNull()
        if (match != null && repository.getCurrentUser() == null && match.savedPassword.isNotEmpty()) {
            login(match.email, match.savedPassword, saveCredentials = true)
        }
    }

    private fun observeUserData(uid: String) {
        viewModelScope.launch {
            repository.observeCandidateProfile(uid).collectLatest { profile ->
                _uiState.update { state ->
                    val updatedState = state.copy(candidateProfile = profile)
                    val filtered = computeFilteredJobs(state.allJobs, updatedState)
                    updatedState.copy(filteredJobs = filtered)
                }

                if (profile != null) {
                    // Update saved account details
                    accountStore.saveAccount(
                        SavedAccount(
                            uid = uid,
                            email = profile.email,
                            name = profile.name,
                            category = profile.category,
                            photoUrl = profile.photoUrl
                        )
                    )
                    _uiState.update { it.copy(savedAccounts = accountStore.getSavedAccounts()) }

                    // Also observe referral usages if profile has referral code
                    if (profile.referralCode.isNotEmpty()) {
                        observeReferrals(profile.referralCode)
                    }
                }
            }
        }

        viewModelScope.launch {
            repository.observeApplications(uid).collectLatest { apps ->
                // Check if any status changed (e.g. Selected, Shortlisted)
                for (app in apps) {
                    val prevStatus = knownAppStatuses[app.id]
                    if (prevStatus != null && prevStatus != app.status) {
                        notificationHelper.showApplicationStatusNotification(
                            app.jobTitle,
                            app.companyName,
                            app.status
                        )
                        addInAppNotification(
                            AppNotification(
                                title = "🎉 Status Updated: ${app.status}",
                                message = "Your application for ${app.jobTitle} at ${app.companyName} is now ${app.status}!",
                                type = "STATUS_UPDATE"
                            )
                        )
                    }
                    knownAppStatuses[app.id] = app.status
                }

                _uiState.update { it.copy(applications = apps) }
            }
        }
    }

    private fun observeReferrals(code: String) {
        viewModelScope.launch {
            repository.observeReferralUsages(code).collectLatest { list ->
                _uiState.update { it.copy(referralApplications = list) }
            }
        }
    }

    fun login(email: String, pass: String, saveCredentials: Boolean = true) {
        if (email.isBlank() || pass.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter both email and password") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val user = repository.login(email, pass)
                if (saveCredentials) {
                    accountStore.saveAccount(
                        SavedAccount(
                            uid = user.uid,
                            email = email.trim(),
                            name = email.substringBefore("@").replaceFirstChar { it.uppercase() },
                            savedPassword = pass
                        )
                    )
                }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        savedAccounts = accountStore.getSavedAccounts(),
                        successMessage = "Welcome back!"
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.localizedMessage ?: "Invalid credentials or network error"
                    )
                }
            }
        }
    }

    fun registerBasic(
        name: String,
        email: String,
        pass: String,
        mobile: String,
        category: String
    ) {
        if (name.isBlank() || email.isBlank() || pass.isBlank() || mobile.isBlank() || category.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please fill in all mandatory fields!") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val (user, profile) = repository.registerBasic(name, email, pass, mobile, category)
                accountStore.saveAccount(
                    SavedAccount(
                        uid = user.uid,
                        email = email,
                        name = name,
                        category = category,
                        savedPassword = pass
                    )
                )
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        candidateProfile = profile,
                        savedAccounts = accountStore.getSavedAccounts(),
                        successMessage = "Profile created successfully! Start swiping jobs now."
                    )
                }
                addInAppNotification(
                    AppNotification(
                        title = "👋 Welcome to SwipeJobs India",
                        message = "Your career dashboard is ready. Swipe right to apply, left to pass!",
                        type = "SYSTEM"
                    )
                )
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.localizedMessage ?: "Registration failed"
                    )
                }
            }
        }
    }

    fun switchAccount(account: SavedAccount) {
        viewModelScope.launch {
            if (account.savedPassword.isNotEmpty()) {
                login(account.email, account.savedPassword, saveCredentials = true)
            } else {
                accountStore.setActiveUid(account.uid)
                _uiState.update {
                    it.copy(
                        showAccountSwitchDialog = false,
                        successMessage = "Switched to ${account.name}"
                    )
                }
            }
        }
    }

    fun logout() {
        repository.logout()
        accountStore.clearActiveUid()
        _uiState.update {
            it.copy(
                currentUserEmail = null,
                currentUserId = null,
                candidateProfile = null,
                applications = emptyList(),
                referralApplications = emptyList(),
                selectedTab = ScreenTab.SWIPE,
                successMessage = "Logged out safely. Your saved accounts remain available for 1-tap switch."
            )
        }
    }

    fun removeSavedAccount(uid: String) {
        accountStore.removeAccount(uid)
        _uiState.update { it.copy(savedAccounts = accountStore.getSavedAccounts()) }
    }

    fun toggleDarkMode() {
        val newMode = !_uiState.value.isDarkTheme
        accountStore.setDarkTheme(newMode)
        _uiState.update { it.copy(isDarkTheme = newMode) }
    }

    fun selectTab(tab: ScreenTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun toggleListView() {
        _uiState.update { it.copy(isListView = !it.isListView) }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { state ->
            val updated = state.copy(searchQuery = query)
            updated.copy(filteredJobs = computeFilteredJobs(state.allJobs, updated))
        }
    }

    fun onCategoryFilterChanged(category: String) {
        _uiState.update { state ->
            val updated = state.copy(selectedCategory = category)
            updated.copy(filteredJobs = computeFilteredJobs(state.allJobs, updated))
        }
    }

    fun setLocationFilter(filter: LocationFilter) {
        _uiState.update { state ->
            val updated = state.copy(locationFilter = filter)
            updated.copy(filteredJobs = computeFilteredJobs(state.allJobs, updated))
        }
    }

    fun toggleJobTypeFilter(jobType: String) {
        _uiState.update { state ->
            val set = state.selectedJobTypes.toMutableSet()
            if (set.contains(jobType)) {
                if (set.size > 1) set.remove(jobType)
            } else {
                set.add(jobType)
            }
            val updated = state.copy(selectedJobTypes = set)
            updated.copy(filteredJobs = computeFilteredJobs(state.allJobs, updated))
        }
    }

    fun setGenderFilter(gender: String) {
        _uiState.update { state ->
            val updated = state.copy(genderFilter = gender)
            updated.copy(filteredJobs = computeFilteredJobs(state.allJobs, updated))
        }
    }

    fun swipePass(job: Job) {
        dismissedJobIds.add(job.id)
        _uiState.update { state ->
            val updated = state.copy(lastSwipedLeftJob = job)
            updated.copy(filteredJobs = computeFilteredJobs(state.allJobs, updated))
        }
    }

    fun undoSwipe() {
        val lastJob = _uiState.value.lastSwipedLeftJob ?: return
        dismissedJobIds.remove(lastJob.id)
        _uiState.update { state ->
            val updated = state.copy(lastSwipedLeftJob = null)
            updated.copy(filteredJobs = computeFilteredJobs(state.allJobs, updated))
        }
    }

    fun requestApply(job: Job) {
        val profile = _uiState.value.candidateProfile
        if (profile == null || !profile.profileComplete) {
            _uiState.update {
                it.copy(
                    selectedJobForApply = job,
                    showCompleteProfilePrompt = true
                )
            }
        } else {
            _uiState.update { it.copy(selectedJobForApply = job) }
        }
    }

    fun confirmApply(job: Job, friendReferCode: String) {
        val candidateId = _uiState.value.currentUserId ?: return
        val profile = _uiState.value.candidateProfile ?: return

        if (friendReferCode.isNotBlank() && friendReferCode.equals(profile.referralCode, ignoreCase = true)) {
            _uiState.update { it.copy(errorMessage = "You cannot use your own referral code!") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                repository.submitApplication(candidateId, job, profile, friendReferCode)
                dismissedJobIds.add(job.id)
                _uiState.update { state ->
                    val updated = state.copy(
                        isLoading = false,
                        selectedJobForApply = null,
                        successMessage = "Application submitted for ${job.title} at ${job.companyName}!"
                    )
                    updated.copy(filteredJobs = computeFilteredJobs(state.allJobs, updated))
                }

                notificationHelper.showApplicationStatusNotification(
                    job.title,
                    job.companyName,
                    "Application Sent"
                )
                addInAppNotification(
                    AppNotification(
                        title = "🚀 Application Submitted",
                        message = "You applied for ${job.title} at ${job.companyName}",
                        type = "STATUS_UPDATE"
                    )
                )
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.localizedMessage ?: "Failed to apply. Please try again."
                    )
                }
            }
        }
    }

    fun updateProfile(updated: CandidateProfile) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                repository.saveProfile(updated)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        candidateProfile = updated,
                        showCompleteProfilePrompt = false,
                        successMessage = "Profile updated & synced successfully!"
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.localizedMessage ?: "Failed to update profile"
                    )
                }
            }
        }
    }

    fun requestRedeem(phone: String) {
        val uid = _uiState.value.currentUserId ?: return
        val balance = _uiState.value.candidateProfile?.walletBalance ?: 0.0
        if (balance < 600.0) {
            _uiState.update { it.copy(errorMessage = "Minimum ₹600 required to redeem. Current balance: ₹$balance") }
            return
        }

        viewModelScope.launch {
            try {
                repository.requestRedemption(uid, balance, phone)
                _uiState.update {
                    it.copy(successMessage = "Redeem request of ₹$balance sent! Admin will verify and transfer.")
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.localizedMessage) }
            }
        }
    }

    fun reportJob(job: Job, reason: String) {
        val uid = _uiState.value.currentUserId ?: return
        viewModelScope.launch {
            try {
                repository.reportJob(job.id, uid, reason)
                _uiState.update {
                    it.copy(
                        selectedJobForReport = null,
                        successMessage = "Job reported. Our moderation team will investigate."
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.localizedMessage) }
            }
        }
    }

    fun openJobDetails(job: Job) {
        _uiState.update { it.copy(selectedJobForModal = job) }
    }

    fun openJobById(jobId: String) {
        if (jobId.isBlank()) return
        val existing = _uiState.value.allJobs.find { it.id == jobId }
            ?: _uiState.value.filteredJobs.find { it.id == jobId }
            ?: repository.getStarterJobs().find { it.id == jobId }

        if (existing != null) {
            _uiState.update { it.copy(selectedJobForModal = existing) }
        } else {
            viewModelScope.launch {
                val fetched = repository.getJobById(jobId)
                if (fetched != null) {
                    _uiState.update { it.copy(selectedJobForModal = fetched) }
                }
            }
        }
    }

    fun loadMoreJobs() {
        val currentLimit = _uiState.value.displayedJobsLimit
        val totalFiltered = _uiState.value.filteredJobs.size
        if (currentLimit < totalFiltered && !_uiState.value.isLoadingMore) {
            _uiState.update { it.copy(isLoadingMore = true) }
            viewModelScope.launch {
                kotlinx.coroutines.delay(250) // Micro-chunk smooth loading effect
                _uiState.update {
                    it.copy(
                        displayedJobsLimit = (currentLimit + 10).coerceAtMost(totalFiltered),
                        isLoadingMore = false
                    )
                }
            }
        }
    }

    fun closeJobDetails() {
        _uiState.update { it.copy(selectedJobForModal = null) }
    }

    fun openShareJob(job: Job) {
        _uiState.update { it.copy(selectedJobForShare = job) }
    }

    fun closeShareJob() {
        _uiState.update { it.copy(selectedJobForShare = null) }
    }

    fun openReportDialog(job: Job) {
        _uiState.update { it.copy(selectedJobForReport = job) }
    }

    fun closeReportDialog() {
        _uiState.update { it.copy(selectedJobForReport = null) }
    }

    fun openCvDialog() {
        _uiState.update { it.copy(showCvDialog = true) }
    }

    fun closeCvDialog() {
        _uiState.update { it.copy(showCvDialog = false) }
    }

    fun openAccountSwitchDialog() {
        _uiState.update { it.copy(showAccountSwitchDialog = true) }
    }

    fun closeAccountSwitchDialog() {
        _uiState.update { it.copy(showAccountSwitchDialog = false) }
    }

    fun openNotificationsDialog() {
        _uiState.update { it.copy(showNotificationsDialog = true, unreadNotificationsCount = 0) }
    }

    fun closeNotificationsDialog() {
        _uiState.update { it.copy(showNotificationsDialog = false) }
    }

    fun openProfilePhotoViewer() {
        _uiState.update { it.copy(showProfilePhotoViewer = true) }
    }

    fun closeProfilePhotoViewer() {
        _uiState.update { it.copy(showProfilePhotoViewer = false) }
    }

    fun openDocumentViewer(doc: com.example.data.UploadedDocument) {
        _uiState.update { it.copy(viewingDocument = doc) }
    }

    fun closeDocumentViewer() {
        _uiState.update { it.copy(viewingDocument = null) }
    }

    fun updateProfilePhoto(uri: String) {
        val current = _uiState.value.candidateProfile ?: return
        val updated = current.copy(photoUrl = uri)
        updateProfile(updated)
        // Also update saved accounts
        val uid = _uiState.value.currentUserId ?: return
        val accounts = accountStore.getSavedAccounts().map {
            if (it.uid == uid) it.copy(photoUrl = uri) else it
        }
        _uiState.update {
            it.copy(
                candidateProfile = updated,
                savedAccounts = accounts,
                successMessage = "Profile photo updated successfully!"
            )
        }
    }

    fun removeProfilePhoto() {
        val current = _uiState.value.candidateProfile ?: return
        val updated = current.copy(photoUrl = "")
        updateProfile(updated)
        val uid = _uiState.value.currentUserId ?: return
        val accounts = accountStore.getSavedAccounts().map {
            if (it.uid == uid) it.copy(photoUrl = "") else it
        }
        _uiState.update {
            it.copy(
                candidateProfile = updated,
                savedAccounts = accounts,
                successMessage = "Profile photo removed."
            )
        }
    }

    fun uploadDocument(name: String, type: String, uri: String, size: String = "1.2 MB") {
        val current = _uiState.value.candidateProfile ?: return
        val newDoc = com.example.data.UploadedDocument(
            id = java.util.UUID.randomUUID().toString(),
            name = name,
            type = type,
            uriOrUrl = uri,
            uploadedAt = System.currentTimeMillis(),
            fileSize = size,
            isVerified = true
        )
        val updatedList = listOf(newDoc) + current.uploadedDocuments
        val updatedProfile = if (type == "RESUME") {
            current.copy(
                uploadedDocuments = updatedList,
                resumeUrl = uri,
                resumeFileName = name
            )
        } else if (type == "AADHAAR_FRONT") {
            current.copy(
                uploadedDocuments = updatedList,
                aadhaarFUrl = uri
            )
        } else if (type == "AADHAAR_BACK") {
            current.copy(
                uploadedDocuments = updatedList,
                aadhaarBUrl = uri
            )
        } else {
            current.copy(uploadedDocuments = updatedList)
        }

        updateProfile(updatedProfile)
        _uiState.update {
            it.copy(
                candidateProfile = updatedProfile,
                successMessage = "$name uploaded successfully!"
            )
        }
    }

    fun deleteDocument(docId: String) {
        val current = _uiState.value.candidateProfile ?: return
        val updatedList = current.uploadedDocuments.filter { it.id != docId }
        val updatedProfile = current.copy(uploadedDocuments = updatedList)
        updateProfile(updatedProfile)
        _uiState.update {
            it.copy(
                candidateProfile = updatedProfile,
                successMessage = "Document removed."
            )
        }
    }

    fun exportAiResume(context: android.content.Context) {
        val profile = _uiState.value.candidateProfile ?: return
        val resumeText = buildString {
            appendLine("=========================================")
            appendLine("      SWIPEJOBS INDIA - AI RESUME        ")
            appendLine("=========================================")
            appendLine("NAME: ${profile.name.uppercase()}")
            appendLine("EMAIL: ${profile.email}")
            appendLine("MOBILE: ${profile.mobile}")
            appendLine("LOCATION: ${profile.address}, ${profile.city}")
            appendLine("JOB CATEGORY: ${profile.category}")
            appendLine("\n--- PROFESSIONAL SUMMARY ---")
            appendLine("A dedicated and adaptable professional specializing in ${profile.category}. Committed to excellence, continuous learning, and delivering high organizational value.")
            appendLine("\n--- HIGHEST QUALIFICATION ---")
            appendLine(profile.education.ifEmpty { "Bachelor's Degree / Diploma" })
            if (profile.certificates.isNotBlank()) {
                appendLine("Certifications: ${profile.certificates}")
            }
            appendLine("\n--- CORE SKILLS ---")
            appendLine(profile.skills.ifEmpty { "Communication, Problem Solving, Computer Literacy, Team Collaboration" })
            appendLine("\n--- WORK EXPERIENCE ---")
            appendLine(profile.workHistory.ifEmpty { "Fresher - Ready for immediate employment and career advancement." })
            appendLine("\n--- CANDIDATE DETAILS ---")
            appendLine("Languages: ${profile.languages.ifEmpty { "English, Hindi" }}")
            appendLine("Expected CTC: ${profile.expectedSalary.ifEmpty { "Best in Industry" }}")
            appendLine("Notice Period: ${profile.noticePeriod.ifEmpty { "Immediate" }}")
            appendLine("PAN / ID: ${profile.panCard.ifEmpty { "Verified" }}")
            appendLine("Verification ID: ${profile.referralCode}")
            appendLine("=========================================")
            appendLine("Generated by SwipeJobs India AI Resume Engine")
        }

        try {
            val sendIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(android.content.Intent.EXTRA_SUBJECT, "${profile.name} - AI Resume (SwipeJobs)")
                putExtra(android.content.Intent.EXTRA_TEXT, resumeText)
                flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(android.content.Intent.createChooser(sendIntent, "Download / Share AI Resume"))
            _uiState.update { it.copy(successMessage = "AI Resume ready! Choose an app to download or share.") }
        } catch (e: Exception) {
            _uiState.update { it.copy(errorMessage = "Could not share resume: ${e.localizedMessage}") }
        }
    }

    fun closeCompleteProfilePrompt() {
        _uiState.update { it.copy(showCompleteProfilePrompt = false) }
    }

    fun dismissApplyDialog() {
        _uiState.update { it.copy(selectedJobForApply = null) }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }

    private fun addInAppNotification(notification: AppNotification) {
        _uiState.update { state ->
            val list = listOf(notification) + state.notifications
            state.copy(
                notifications = list,
                unreadNotificationsCount = state.unreadNotificationsCount + 1
            )
        }
    }

    private fun computeFilteredJobs(all: List<Job>, state: SwipeJobsUiState): List<Job> {
        val userCity = state.candidateProfile?.city?.lowercase(Locale.ROOT) ?: ""
        val userCategory = state.candidateProfile?.category

        return all.filter { job ->
            // Skip already swiped/dismissed
            if (dismissedJobIds.contains(job.id)) return@filter false

            // Search query
            if (state.searchQuery.isNotBlank()) {
                val q = state.searchQuery.lowercase(Locale.ROOT)
                val matchesTitle = job.title.lowercase(Locale.ROOT).contains(q)
                val matchesCompany = job.companyName.lowercase(Locale.ROOT).contains(q)
                val matchesRole = job.role.lowercase(Locale.ROOT).contains(q)
                val matchesCategory = job.category.lowercase(Locale.ROOT).contains(q)
                if (!matchesTitle && !matchesCompany && !matchesRole && !matchesCategory) return@filter false
            }

            // Category filter
            if (state.selectedCategory != "All") {
                if (!job.category.equals(state.selectedCategory, ignoreCase = true)) return@filter false
            }

            // Job Type filter
            val jobType = job.jobType.lowercase(Locale.ROOT)
            if (!state.selectedJobTypes.contains(jobType)) return@filter false

            // Gender filter
            if (state.genderFilter != "All") {
                val g = job.gender.lowercase(Locale.ROOT)
                if (g != "both" && g != "any" && !g.contains(state.genderFilter.lowercase(Locale.ROOT))) {
                    return@filter false
                }
            }

            true
        }.sortedWith { a, b ->
            var scoreA = 0
            var scoreB = 0
            if (userCategory != null && a.category.equals(userCategory, ignoreCase = true)) scoreA += 10
            if (userCategory != null && b.category.equals(userCategory, ignoreCase = true)) scoreB += 10

            if (state.locationFilter == LocationFilter.NEARBY && userCity.isNotBlank()) {
                if (a.location.lowercase(Locale.ROOT).contains(userCity)) scoreA += 5
                if (b.location.lowercase(Locale.ROOT).contains(userCity)) scoreB += 5
            }
            scoreB.compareTo(scoreA)
        }
    }
}
