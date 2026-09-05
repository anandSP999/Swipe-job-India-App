package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.SwitchAccount
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CandidateProfile
import com.example.data.SavedAccount
import com.example.ui.theme.SwipePrimary
import com.example.ui.theme.SwipeRejectRed
import com.example.ui.theme.SwipeSelectedGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    profile: CandidateProfile?,
    savedAccounts: List<SavedAccount>,
    isDarkTheme: Boolean,
    isLoading: Boolean,
    onSaveProfile: (CandidateProfile) -> Unit,
    onOpenSwitchAccount: () -> Unit,
    onOpenCv: () -> Unit,
    onToggleDarkMode: () -> Unit,
    onLogout: () -> Unit
) {
    var name by remember(profile) { mutableStateOf(profile?.name ?: "") }
    var mobile by remember(profile) { mutableStateOf(profile?.mobile ?: "") }
    var email by remember(profile) { mutableStateOf(profile?.email ?: "") }
    var category by remember(profile) { mutableStateOf(profile?.category ?: "Computer") }
    var age by remember(profile) { mutableStateOf(profile?.age ?: "") }
    var gender by remember(profile) { mutableStateOf(profile?.gender ?: "Male") }
    var dob by remember(profile) { mutableStateOf(profile?.dob ?: "") }
    var panCard by remember(profile) { mutableStateOf(profile?.panCard ?: "") }
    var address by remember(profile) { mutableStateOf(profile?.address ?: "") }
    var city by remember(profile) { mutableStateOf(profile?.city ?: "") }
    var education by remember(profile) { mutableStateOf(profile?.education ?: "") }
    var expectedSalary by remember(profile) { mutableStateOf(profile?.expectedSalary ?: "") }
    var noticePeriod by remember(profile) { mutableStateOf(profile?.noticePeriod ?: "") }
    var workHistory by remember(profile) { mutableStateOf(profile?.workHistory ?: "") }
    var skills by remember(profile) { mutableStateOf(profile?.skills ?: "") }
    var languages by remember(profile) { mutableStateOf(profile?.languages ?: "") }
    var certificates by remember(profile) { mutableStateOf(profile?.certificates ?: "") }
    var linkedin by remember(profile) { mutableStateOf(profile?.linkedin ?: "") }
    var portfolio by remember(profile) { mutableStateOf(profile?.portfolio ?: "") }

    var categoryExpanded by remember { mutableStateOf(false) }
    val categories = listOf(
        "Computer",
        "Marketing",
        "Telecaller",
        "Field Marketing",
        "Sales",
        "Accounts",
        "HR",
        "Other"
    )

    // Calculate completeness
    val fields = listOf(name, mobile, email, category, age, gender, address, education, expectedSalary, skills)
    val filledCount = fields.count { it.isNotBlank() }
    val progress = (filledCount.toFloat() / fields.size.toFloat()).coerceIn(0f, 1f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header Profile Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(SwipePrimary.copy(alpha = 0.15f))
                                    .border(2.dp, SwipePrimary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = name.firstOrNull()?.uppercase() ?: "C",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SwipePrimary
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = name.ifEmpty { "Candidate" },
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = email,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = SwipePrimary.copy(alpha = 0.1f),
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    Text(
                                        text = category,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SwipePrimary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        IconButton(onClick = onOpenCv) {
                            Icon(imageVector = Icons.Default.Description, contentDescription = "Digital CV", tint = SwipePrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Profile Completion Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Profile Completion",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${(progress * 100).toInt()}%",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SwipePrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = SwipePrimary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Saved Accounts & Fast Switch Card (Persistent Login)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Groups, contentDescription = null, tint = SwipePrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Permanent Accounts (${savedAccounts.size})", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Text(
                            text = "1-Tap fast switch between saved accounts",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }

                    Button(
                        onClick = onOpenSwitchAccount,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SwipePrimary),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(imageVector = Icons.Default.SwitchAccount, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Switch", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Personal Information Section
            Text(
                text = "PERSONAL INFORMATION",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = SwipePrimary,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Full Name *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = mobile,
                        onValueChange = { mobile = it },
                        label = { Text("Mobile Number *") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth()
                    )

                    ExposedDropdownMenuBox(
                        expanded = categoryExpanded,
                        onExpandedChange = { categoryExpanded = !categoryExpanded }
                    ) {
                        OutlinedTextField(
                            value = category,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Preferred Job Category *") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = categoryExpanded,
                            onDismissRequest = { categoryExpanded = false }
                        ) {
                            categories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat) },
                                    onClick = {
                                        category = cat
                                        categoryExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = age,
                            onValueChange = { age = it },
                            label = { Text("Age") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = gender,
                            onValueChange = { gender = it },
                            label = { Text("Gender") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = dob,
                        onValueChange = { dob = it },
                        label = { Text("Date of Birth (YYYY-MM-DD)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = panCard,
                        onValueChange = { panCard = it.uppercase() },
                        label = { Text("PAN Card Number") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = { Text("City (Used for Nearby Filter) *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Full Address") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Professional & Career Details
            Text(
                text = "PROFESSIONAL SPECIFICATIONS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = SwipePrimary,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = education,
                        onValueChange = { education = it },
                        label = { Text("Highest Qualification *") },
                        placeholder = { Text("e.g. Graduate B.Com / BCA / 12th") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = expectedSalary,
                            onValueChange = { expectedSalary = it },
                            label = { Text("Expected Salary *") },
                            placeholder = { Text("₹25,000/mo") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = noticePeriod,
                            onValueChange = { noticePeriod = it },
                            label = { Text("Notice Period") },
                            placeholder = { Text("Immediate") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = skills,
                        onValueChange = { skills = it },
                        label = { Text("Skills (Comma Separated) *") },
                        placeholder = { Text("Excel, Tally, Communication") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = languages,
                        onValueChange = { languages = it },
                        label = { Text("Languages Known") },
                        placeholder = { Text("English, Hindi") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = workHistory,
                        onValueChange = { workHistory = it },
                        label = { Text("Work Experience / Summary") },
                        placeholder = { Text("2 years as Telecaller at ABC Pvt Ltd / Fresher") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = certificates,
                        onValueChange = { certificates = it },
                        label = { Text("Certificates / Courses") },
                        placeholder = { Text("CCC, Digital Marketing") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = linkedin,
                        onValueChange = { linkedin = it },
                        label = { Text("LinkedIn URL") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = portfolio,
                        onValueChange = { portfolio = it },
                        label = { Text("Portfolio / Resume Link") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Save Profile Button
            Button(
                onClick = {
                    val updated = (profile ?: CandidateProfile(uid = "")).copy(
                        name = name.trim(),
                        mobile = mobile.trim(),
                        email = email.trim(),
                        category = category,
                        age = age.trim(),
                        gender = gender.trim(),
                        dob = dob.trim(),
                        panCard = panCard.trim(),
                        address = address.trim(),
                        city = city.trim(),
                        education = education.trim(),
                        expectedSalary = expectedSalary.trim(),
                        noticePeriod = noticePeriod.trim(),
                        workHistory = workHistory.trim(),
                        skills = skills.trim(),
                        languages = languages.trim(),
                        certificates = certificates.trim(),
                        linkedin = linkedin.trim(),
                        portfolio = portfolio.trim(),
                        profileComplete = progress >= 0.6f
                    )
                    onSaveProfile(updated)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SwipePrimary),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Icon(imageVector = Icons.Default.Save, contentDescription = "Save")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Save & Sync Profile", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // App Settings: Dark Mode & Logout
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isDarkTheme) Icons.Default.Brightness7 else Icons.Default.Brightness4,
                                contentDescription = null,
                                tint = SwipePrimary
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = "Dark Mode Theme", fontWeight = FontWeight.SemiBold)
                        }
                        Switch(checked = isDarkTheme, onCheckedChange = { onToggleDarkMode() })
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedButton(
                        onClick = onLogout,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = SwipeRejectRed),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SwipeRejectRed.copy(alpha = 0.4f))
                    ) {
                        Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "Logout", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Logout (Keep Account Saved on Device)", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
