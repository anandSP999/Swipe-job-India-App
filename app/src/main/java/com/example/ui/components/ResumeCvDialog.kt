package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.CandidateProfile
import com.example.ui.theme.SwipePrimary

@Composable
fun ResumeCvDialog(
    profile: CandidateProfile?,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 20.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                // Background Watermark
                Text(
                    text = "SwipeJobs India",
                    color = Color.Black.copy(alpha = 0.04f),
                    fontSize = 52.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .rotate(-35f)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Top Bar with Close
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "DIGITAL CV PREVIEW",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SwipePrimary,
                            letterSpacing = 1.sp
                        )
                        IconButton(onClick = onDismiss) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.Black)
                        }
                    }

                    // Header Row: Candidate Info + Avatar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = profile?.name?.ifEmpty { "Candidate" } ?: "Candidate",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF2C3E50)
                            )
                            Text(
                                text = profile?.education?.ifEmpty { "Professional Candidate" } ?: "Professional Candidate",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = SwipePrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = profile?.mobile?.ifEmpty { "+91 98765 43210" } ?: "+91 98765 43210", fontSize = 12.sp, color = Color.DarkGray)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = profile?.email ?: "candidate@swipejobs.in", fontSize = 12.sp, color = Color.DarkGray)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = profile?.address?.ifEmpty { "India" } ?: "India", fontSize = 12.sp, color = Color.DarkGray)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(SwipePrimary.copy(alpha = 0.1f))
                                .border(2.dp, SwipePrimary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = profile?.name?.firstOrNull()?.uppercase() ?: "C",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = SwipePrimary
                            )
                        }
                    }

                    HorizontalDivider(thickness = 2.dp, color = SwipePrimary)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Personal Summary Grid
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFF9F9F9),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                CvDetailItem(label = "Age / DOB", value = "${profile?.age?.ifEmpty { "24" } ?: "24"} yrs (${profile?.dob?.ifEmpty { "2000-01-01" } ?: "2000-01-01"})", modifier = Modifier.weight(1f))
                                CvDetailItem(label = "Expected Salary", value = profile?.expectedSalary?.ifEmpty { "Competitive" } ?: "Competitive", modifier = Modifier.weight(1f))
                            }
                            Row(modifier = Modifier.fillMaxWidth()) {
                                CvDetailItem(label = "Notice Period", value = profile?.noticePeriod?.ifEmpty { "Immediate" } ?: "Immediate", modifier = Modifier.weight(1f))
                                CvDetailItem(label = "Languages", value = profile?.languages?.ifEmpty { "English, Hindi" } ?: "English, Hindi", modifier = Modifier.weight(1f))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Professional Skills
                    CvSectionTitle(title = "PROFESSIONAL SKILLS")
                    Text(
                        text = profile?.skills?.ifEmpty { "Communication, Problem Solving, Computer Literacy, Team Collaboration" } ?: "Skills",
                        fontSize = 13.sp,
                        color = Color(0xFF333333),
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(start = 6.dp, top = 4.dp, bottom = 12.dp)
                    )

                    // Work Experience
                    CvSectionTitle(title = "WORK EXPERIENCE")
                    Text(
                        text = profile?.workHistory?.ifEmpty { "Fresher - Enthusiastic about starting career in high-growth roles." } ?: "Fresher",
                        fontSize = 13.sp,
                        color = Color(0xFF333333),
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(start = 6.dp, top = 4.dp, bottom = 12.dp)
                    )

                    // Certificates & Education
                    CvSectionTitle(title = "HIGHEST QUALIFICATION & CERTIFICATES")
                    Text(
                        text = "${profile?.education ?: "Bachelor's Degree"} | ${profile?.certificates?.ifEmpty { "Certificate of Completion" } ?: "Certified"}",
                        fontSize = 13.sp,
                        color = Color(0xFF333333),
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(start = 6.dp, top = 4.dp, bottom = 12.dp)
                    )

                    // Referral Code
                    CvSectionTitle(title = "REFERRAL & VERIFICATION")
                    Text(
                        text = "Refer Code: ${profile?.referralCode ?: "SWIPE01"} | PAN: ${profile?.panCard?.ifEmpty { "Verified Candidate" } ?: "Verified"}",
                        fontSize = 13.sp,
                        color = Color(0xFF333333),
                        modifier = Modifier.padding(start = 6.dp, top = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CvSectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF2C3E50),
        modifier = Modifier
            .border(width = 0.dp, color = Color.Transparent)
            .padding(vertical = 4.dp)
    )
}

@Composable
private fun CvDetailItem(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = label.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SwipePrimary)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF2C3E50))
    }
}
