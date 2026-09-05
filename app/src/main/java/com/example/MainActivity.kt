package com.example

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.SwitchAccount
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.ScreenTab
import com.example.ui.SwipeJobsUiState
import com.example.ui.SwipeJobsViewModel
import com.example.ui.components.AccountSwitchDialog
import com.example.ui.components.ApplyReferralDialog
import com.example.ui.components.DocumentViewerDialog
import com.example.ui.components.JobDetailDialog
import com.example.ui.components.NotificationDialog
import com.example.ui.components.OfflineBanner
import com.example.ui.components.ProfilePhotoViewerDialog
import com.example.ui.components.ResumeCvDialog
import com.example.ui.components.ShareJobDialog
import com.example.ui.screens.ApplicationsScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.JobsSwipeScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.WalletScreen
import com.example.ui.theme.SwipeAppliedBlue
import com.example.ui.theme.SwipeJobsTheme
import com.example.ui.theme.SwipePrimary
import com.example.ui.theme.SwipeRejectRed
import com.example.ui.theme.SwipeSelectedGreen
import com.example.ui.theme.SwipeWarningAmber

class MainActivity : ComponentActivity() {

    private var activeViewModel: SwipeJobsViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: SwipeJobsViewModel = viewModel()
            activeViewModel = viewModel
            val uiState by viewModel.uiState.collectAsState()

            LaunchedEffect(intent) {
                handleIncomingDeepLink(intent, viewModel)
            }

            SwipeJobsTheme(darkTheme = uiState.isDarkTheme) {
                SwipeJobsApp(viewModel = viewModel, uiState = uiState)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        activeViewModel?.let { handleIncomingDeepLink(intent, it) }
    }

    private fun handleIncomingDeepLink(intent: Intent?, vm: SwipeJobsViewModel) {
        val uri: Uri = intent?.data ?: return
        // Formats:
        // https://ais-pre-jbmiw3g2ezswn7gcdlmgyn-637005264324.asia-southeast1.run.app/job/{jobId}
        // https://swipejobs.in/job/{jobId}
        // swipejobs://job/{jobId}
        // or ?jobId={jobId}
        val queryJobId = uri.getQueryParameter("jobId")
        val pathJobId = if (uri.pathSegments.isNotEmpty()) {
            val segments = uri.pathSegments
            val jobIndex = segments.indexOf("job")
            if (jobIndex != -1 && jobIndex + 1 < segments.size) {
                segments[jobIndex + 1]
            } else {
                uri.lastPathSegment
            }
        } else if (uri.scheme == "swipejobs") {
            if (uri.host != null && uri.host != "job") uri.host else uri.lastPathSegment
        } else null

        val targetJobId = queryJobId ?: pathJobId
        if (!targetJobId.isNullOrBlank()) {
            vm.openJobById(targetJobId)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeJobsApp(
    viewModel: SwipeJobsViewModel,
    uiState: SwipeJobsUiState
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Request notification permission for Android 13+ (TIRAMISU)
    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasNotificationPermission = isGranted
        }
    )

    val photoViewerPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { viewModel.updateProfilePhoto(it.toString()) }
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotificationPermission) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    // Show Snackbar on messages
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (uiState.currentUserId != null) {
                Column {
                    TopAppBar(
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(SwipePrimary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Work,
                                        contentDescription = "SwipeJobs",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "SwipeJobs India",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = SwipePrimary
                                )
                            }
                        },
                        actions = {
                            // Notifications bell with unread badge
                            IconButton(onClick = { viewModel.openNotificationsDialog() }) {
                                BadgedBox(
                                    badge = {
                                        if (uiState.unreadNotificationsCount > 0) {
                                            Badge(containerColor = SwipeRejectRed) {
                                                Text(text = "${uiState.unreadNotificationsCount}")
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Notifications,
                                        contentDescription = "Notifications",
                                        tint = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            // Fast Account Switch button
                            IconButton(onClick = { viewModel.openAccountSwitchDialog() }) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(SwipePrimary.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = uiState.candidateProfile?.name?.firstOrNull()?.uppercase()
                                            ?: uiState.currentUserEmail?.firstOrNull()?.uppercase()
                                            ?: "U",
                                        fontWeight = FontWeight.Bold,
                                        color = SwipePrimary,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    )

                    // Offline Banner
                    OfflineBanner(isOnline = uiState.isOnline)
                }
            }
        },
        bottomBar = {
            if (uiState.currentUserId != null) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = uiState.selectedTab == ScreenTab.SWIPE,
                        onClick = { viewModel.selectTab(ScreenTab.SWIPE) },
                        icon = { Icon(Icons.Default.Work, contentDescription = "Swipe") },
                        label = { Text("Swipe Jobs") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SwipePrimary,
                            selectedTextColor = SwipePrimary,
                            indicatorColor = SwipePrimary.copy(alpha = 0.12f)
                        )
                    )

                    NavigationBarItem(
                        selected = uiState.selectedTab == ScreenTab.APPLIED,
                        onClick = { viewModel.selectTab(ScreenTab.APPLIED) },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (uiState.applications.isNotEmpty()) {
                                        Badge(containerColor = SwipeAppliedBlue) {
                                            Text("${uiState.applications.size}")
                                        }
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Assignment, contentDescription = "Applied")
                            }
                        },
                        label = { Text("Applied") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SwipePrimary,
                            selectedTextColor = SwipePrimary,
                            indicatorColor = SwipePrimary.copy(alpha = 0.12f)
                        )
                    )

                    NavigationBarItem(
                        selected = uiState.selectedTab == ScreenTab.WALLET,
                        onClick = { viewModel.selectTab(ScreenTab.WALLET) },
                        icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = "Wallet") },
                        label = { Text("Wallet") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SwipePrimary,
                            selectedTextColor = SwipePrimary,
                            indicatorColor = SwipePrimary.copy(alpha = 0.12f)
                        )
                    )

                    NavigationBarItem(
                        selected = uiState.selectedTab == ScreenTab.PROFILE,
                        onClick = { viewModel.selectTab(ScreenTab.PROFILE) },
                        icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                        label = { Text("Profile") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SwipePrimary,
                            selectedTextColor = SwipePrimary,
                            indicatorColor = SwipePrimary.copy(alpha = 0.12f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (uiState.currentUserId == null) {
                // Auth Screen
                AuthScreen(
                    savedAccounts = uiState.savedAccounts,
                    isLoading = uiState.isLoading,
                    errorMessage = uiState.errorMessage,
                    onLogin = { email, pass -> viewModel.login(email, pass, saveCredentials = true) },
                    onRegister = { name, email, pass, mobile, cat ->
                        viewModel.registerBasic(name, email, pass, mobile, cat)
                    },
                    onSelectSavedAccount = { acc -> viewModel.switchAccount(acc) }
                )
            } else {
                // Main Authenticated Screens with smooth iOS-style spring transition
                AnimatedContent(
                    targetState = uiState.selectedTab,
                    transitionSpec = {
                        val direction = if (targetState.ordinal > initialState.ordinal) 1 else -1
                        (slideInHorizontally(
                            animationSpec = spring(
                                dampingRatio = 0.85f,
                                stiffness = 380f
                            ),
                            initialOffsetX = { fullWidth -> fullWidth * direction / 3 }
                        ) + fadeIn(animationSpec = tween(220))) togetherWith (slideOutHorizontally(
                            animationSpec = spring(
                                dampingRatio = 0.85f,
                                stiffness = 380f
                            ),
                            targetOffsetX = { fullWidth -> -fullWidth * direction / 3 }
                        ) + fadeOut(animationSpec = tween(180)))
                    },
                    label = "tab_animation"
                ) { tab ->
                    when (tab) {
                        ScreenTab.SWIPE -> JobsSwipeScreen(
                            jobs = uiState.filteredJobs,
                            searchQuery = uiState.searchQuery,
                            selectedCategory = uiState.selectedCategory,
                            selectedJobTypes = uiState.selectedJobTypes,
                            locationFilter = uiState.locationFilter,
                            isListView = uiState.isListView,
                            lastSwipedJob = uiState.lastSwipedLeftJob,
                            displayedLimit = uiState.displayedJobsLimit,
                            isLoadingMore = uiState.isLoadingMore,
                            onLoadMore = { viewModel.loadMoreJobs() },
                            onSearchQueryChange = { viewModel.onSearchQueryChanged(it) },
                            onCategoryChange = { viewModel.onCategoryFilterChanged(it) },
                            onLocationFilterChange = { viewModel.setLocationFilter(it) },
                            onJobTypeToggle = { viewModel.toggleJobTypeFilter(it) },
                            onToggleListView = { viewModel.toggleListView() },
                            onSwipePass = { viewModel.swipePass(it) },
                            onSwipeApply = { viewModel.requestApply(it) },
                            onUndoSwipe = { viewModel.undoSwipe() },
                            onViewDetails = { viewModel.openJobDetails(it) },
                            onShareJob = { viewModel.openShareJob(it) },
                            onReportJob = { viewModel.openReportDialog(it) }
                        )

                        ScreenTab.APPLIED -> ApplicationsScreen(
                            applications = uiState.applications,
                            onOpenCv = { viewModel.openCvDialog() },
                            onViewJob = { jobId ->
                                val job = uiState.allJobs.firstOrNull { it.id == jobId }
                                if (job != null) viewModel.openJobDetails(job)
                            }
                        )

                        ScreenTab.WALLET -> WalletScreen(
                            profile = uiState.candidateProfile,
                            referralApps = uiState.referralApplications,
                            campaignJobs = uiState.allJobs,
                            onRedeem = { phone -> viewModel.requestRedeem(phone) }
                        )

                        ScreenTab.PROFILE -> ProfileScreen(
                            profile = uiState.candidateProfile,
                            savedAccounts = uiState.savedAccounts,
                            isDarkTheme = uiState.isDarkTheme,
                            isLoading = uiState.isLoading,
                            onSaveProfile = { updated -> viewModel.updateProfile(updated) },
                            onOpenSwitchAccount = { viewModel.openAccountSwitchDialog() },
                            onOpenCv = { viewModel.openCvDialog() },
                            onDownloadAiResume = { viewModel.exportAiResume(context) },
                            onOpenPhotoViewer = { viewModel.openProfilePhotoViewer() },
                            onUpdateProfilePhoto = { uri -> viewModel.updateProfilePhoto(uri) },
                            onUploadDocument = { name, type, uri, size -> viewModel.uploadDocument(name, type, uri, size) },
                            onViewDocument = { doc -> viewModel.openDocumentViewer(doc) },
                            onDeleteDocument = { docId -> viewModel.deleteDocument(docId) },
                            onToggleDarkMode = { viewModel.toggleDarkMode() },
                            onLogout = { viewModel.logout() }
                        )
                    }
                }
            }
        }
    }

    // Modal Dialogs
    uiState.selectedJobForModal?.let { job ->
        JobDetailDialog(
            job = job,
            onDismiss = { viewModel.closeJobDetails() },
            onApply = {
                viewModel.closeJobDetails()
                viewModel.requestApply(job)
            },
            onShare = {
                viewModel.closeJobDetails()
                viewModel.openShareJob(job)
            },
            onReport = {
                viewModel.closeJobDetails()
                viewModel.openReportDialog(job)
            }
        )
    }

    uiState.selectedJobForApply?.let { job ->
        ApplyReferralDialog(
            job = job,
            onDismiss = { viewModel.dismissApplyDialog() },
            onSubmit = { referCode -> viewModel.confirmApply(job, referCode) }
        )
    }

    uiState.selectedJobForShare?.let { job ->
        ShareJobDialog(
            job = job,
            referralCode = uiState.candidateProfile?.referralCode ?: "SJINDIA",
            onDismiss = { viewModel.closeShareJob() }
        )
    }

    if (uiState.showCvDialog) {
        ResumeCvDialog(
            profile = uiState.candidateProfile,
            onDismiss = { viewModel.closeCvDialog() },
            onDownloadAiResume = { viewModel.exportAiResume(context) }
        )
    }

    if (uiState.showAccountSwitchDialog) {
        AccountSwitchDialog(
            savedAccounts = uiState.savedAccounts,
            currentUid = uiState.currentUserId,
            onSelectAccount = { acc -> viewModel.switchAccount(acc) },
            onAddNewAccount = {
                viewModel.closeAccountSwitchDialog()
                viewModel.logout()
            },
            onRemoveAccount = { uid -> viewModel.removeSavedAccount(uid) },
            onDismiss = { viewModel.closeAccountSwitchDialog() }
        )
    }

    if (uiState.showNotificationsDialog) {
        NotificationDialog(
            notifications = uiState.notifications,
            onDismiss = { viewModel.closeNotificationsDialog() }
        )
    }

    if (uiState.showProfilePhotoViewer) {
        ProfilePhotoViewerDialog(
            profile = uiState.candidateProfile,
            onDismiss = { viewModel.closeProfilePhotoViewer() },
            onChangePhoto = {
                photoViewerPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            onRemovePhoto = { viewModel.removeProfilePhoto() }
        )
    }

    uiState.viewingDocument?.let { doc ->
        DocumentViewerDialog(
            document = doc,
            onDismiss = { viewModel.closeDocumentViewer() },
            onDelete = { docId ->
                viewModel.deleteDocument(docId)
                viewModel.closeDocumentViewer()
            }
        )
    }

    // Incomplete profile prompt dialog
    if (uiState.showCompleteProfilePrompt) {
        AlertDialog(
            onDismissRequest = { viewModel.closeCompleteProfilePrompt() },
            icon = {
                Icon(Icons.Default.Warning, contentDescription = null, tint = SwipeWarningAmber)
            },
            title = { Text("Complete Your Profile") },
            text = {
                Text(
                    text = "Employers require your education, skills, and work details to process your application. Please complete your profile before applying!",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.closeCompleteProfilePrompt()
                        viewModel.selectTab(ScreenTab.PROFILE)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SwipePrimary)
                ) {
                    Text("Go to Profile")
                }
            },
            dismissButton = {
                Button(
                    onClick = { viewModel.closeCompleteProfilePrompt() },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text("Later", color = MaterialTheme.colorScheme.onSurface)
                }
            }
        )
    }

    // Report Job Dialog
    uiState.selectedJobForReport?.let { job ->
        var reportReason by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { viewModel.closeReportDialog() },
            title = { Text("Report Suspicious Job") },
            text = {
                Column {
                    Text(
                        text = "Why are you reporting ${job.title} at ${job.companyName}?",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = reportReason,
                        onValueChange = { reportReason = it },
                        placeholder = { Text("e.g. Asked for money, fake address...") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.reportJob(job, reportReason) },
                    colors = ButtonDefaults.buttonColors(containerColor = SwipeRejectRed)
                ) {
                    Text("Submit Report")
                }
            },
            dismissButton = {
                Button(
                    onClick = { viewModel.closeReportDialog() },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onSurface)
                }
            }
        )
    }
}
