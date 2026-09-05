package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationSearching
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.RotateLeft
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Job
import com.example.ui.LocationFilter
import com.example.ui.components.JobSwipeCard
import com.example.ui.theme.SwipeAppliedBlue
import com.example.ui.theme.SwipePrimary
import com.example.ui.theme.SwipeSelectedGreen
import com.example.ui.theme.SwipeWarningAmber

@Composable
fun JobsSwipeScreen(
    jobs: List<Job>,
    searchQuery: String,
    selectedCategory: String,
    selectedJobTypes: Set<String>,
    locationFilter: LocationFilter,
    isListView: Boolean,
    lastSwipedJob: Job?,
    displayedLimit: Int = 10,
    isLoadingMore: Boolean = false,
    onLoadMore: () -> Unit = {},
    onSearchQueryChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onLocationFilterChange: (LocationFilter) -> Unit,
    onJobTypeToggle: (String) -> Unit,
    onToggleListView: () -> Unit,
    onSwipePass: (Job) -> Unit,
    onSwipeApply: (Job) -> Unit,
    onUndoSwipe: () -> Unit,
    onViewDetails: (Job) -> Unit,
    onShareJob: (Job) -> Unit,
    onReportJob: (Job) -> Unit
) {
    val categories = listOf(
        "All",
        "Computer",
        "Marketing",
        "Telecaller",
        "Field Marketing",
        "Sales",
        "Accounts",
        "HR",
        "Other"
    )

    val jobTypes = listOf(
        "full-time" to "Full Time",
        "part-time" to "Part Time",
        "remote" to "Remote",
        "hybrid" to "Hybrid"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Search & View Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = { Text("Search role, company or skills...", fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = onToggleListView,
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                ) {
                    Icon(
                        imageVector = if (isListView) Icons.Default.Style else Icons.Default.List,
                        contentDescription = "Toggle View",
                        tint = SwipePrimary
                    )
                }
            }

            // Location Filters (Nearby vs Pan India) + Job Types
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Nearby Button
                FilterPill(
                    label = "Nearby",
                    icon = Icons.Default.MyLocation,
                    isSelected = locationFilter == LocationFilter.NEARBY,
                    onClick = { onLocationFilterChange(LocationFilter.NEARBY) }
                )

                // Pan India Button
                FilterPill(
                    label = "Pan India",
                    icon = Icons.Default.Public,
                    isSelected = locationFilter == LocationFilter.PAN_INDIA,
                    onClick = { onLocationFilterChange(LocationFilter.PAN_INDIA) }
                )

                // Job Type Toggles
                jobTypes.forEach { (typeKey, label) ->
                    val isSelected = selectedJobTypes.contains(typeKey)
                    FilterPill(
                        label = label,
                        icon = null,
                        isSelected = isSelected,
                        onClick = { onJobTypeToggle(typeKey) }
                    )
                }
            }

            // Category Chips Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                categories.forEach { cat ->
                    val isSelected = selectedCategory == cat
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) SwipePrimary else MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) SwipePrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.clickable { onCategoryChange(cat) }
                    ) {
                        Text(
                            text = cat,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Main Content Area: Cards Deck vs List View
            if (jobs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Inbox,
                            contentDescription = "Empty",
                            tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No jobs match your current filters",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Try switching between 'Nearby' and 'Pan India' or reset category filters.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                        )
                        Button(
                            onClick = {
                                onSearchQueryChange("")
                                onCategoryChange("All")
                                onLocationFilterChange(LocationFilter.PAN_INDIA)
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SwipePrimary)
                        ) {
                            Text(text = "Reset All Filters")
                        }
                    }
                }
            } else if (isListView) {
                // Progressive Lazy List View
                val pagedJobs = jobs.take(displayedLimit)
                val hasMore = jobs.size > displayedLimit

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(pagedJobs, key = { it.id }) { job ->
                        JobListItemCard(
                            job = job,
                            onViewDetails = { onViewDetails(job) },
                            onApply = { onSwipeApply(job) },
                            onShare = { onShareJob(job) }
                        )
                    }

                    if (hasMore) {
                        item {
                            LaunchedEffect(displayedLimit) {
                                onLoadMore()
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isLoadingMore) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(18.dp),
                                            strokeWidth = 2.dp,
                                            color = SwipePrimary
                                        )
                                        Text(
                                            text = "Loading next vacancies smoothly...",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                        )
                                    }
                                } else {
                                    OutlinedButton(
                                        onClick = onLoadMore,
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text(
                                            text = "Load More Openings (${jobs.size - pagedJobs.size} more)",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Tinder-style Swiping Card Deck
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Show up to 2 stacked cards behind top card for depth
                    val visibleCards = jobs.take(3).reversed()

                    visibleCards.forEachIndexed { index, job ->
                        val isTop = index == visibleCards.lastIndex
                        val depthIndex = visibleCards.lastIndex - index

                        val scaleFactor = 1f - (depthIndex * 0.04f)
                        val offsetY = (depthIndex * 14).dp

                        if (isTop) {
                            JobSwipeCard(
                                job = job,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(bottom = 12.dp),
                                onSwipedLeft = { onSwipePass(job) },
                                onSwipedRight = { onSwipeApply(job) },
                                onViewDetails = { onViewDetails(job) },
                                onShare = { onShareJob(job) },
                                onReport = { onReportJob(job) }
                            )
                        } else {
                            Card(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(bottom = 12.dp)
                                    .scale(scaleFactor)
                                    .padding(top = offsetY),
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = (4 - depthIndex * 2).coerceAtLeast(0).dp)
                            ) {
                                // Background stacked card silhouette
                            }
                        }
                    }
                }
            }
        }

        // Floating Undo Button
        AnimatedVisibility(
            visible = lastSwipedJob != null && !isListView,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp)
        ) {
            FloatingActionButton(
                onClick = onUndoSwipe,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurface,
                shape = CircleShape,
                modifier = Modifier.height(44.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.RotateLeft, contentDescription = "Undo", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Undo Reject", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun FilterPill(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector?,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) SwipePrimary else MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) SwipePrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        ),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .size(14.dp)
                        .padding(end = 4.dp)
                )
            }
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun JobListItemCard(
    job: Job,
    onViewDetails: () -> Unit,
    onApply: () -> Unit,
    onShare: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onViewDetails)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(SwipePrimary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = job.companyName.firstOrNull()?.uppercase() ?: "C",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = SwipePrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = job.companyName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SwipePrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = job.location,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                if (job.isCampaignActive && job.referralReward > 0) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = SwipeWarningAmber.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "Earn ₹${job.referralReward.toInt()}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = SwipeWarningAmber,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = job.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CurrencyRupee,
                        contentDescription = null,
                        tint = SwipeSelectedGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = job.salary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onApply,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SwipePrimary),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text(text = "Apply", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
