package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.SdStorage
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DownloadStatus
import com.example.data.model.FullModDetail
import com.example.ui.components.ModCard
import com.example.ui.theme.FnfBorder
import com.example.ui.theme.FnfCyan
import com.example.ui.theme.FnfDarkBg
import com.example.ui.theme.FnfGreen
import com.example.ui.theme.FnfOrange
import com.example.ui.theme.FnfPink
import com.example.ui.theme.FnfSurface
import com.example.ui.theme.FnfSurfaceElevated
import com.example.ui.theme.FnfTextMuted
import com.example.ui.theme.FnfTextPrimary
import com.example.ui.theme.FnfTextSecondary
import com.example.ui.theme.FnfYellow
import com.example.ui.viewmodel.LibraryTab
import com.example.ui.viewmodel.StorageMetrics

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    allMods: List<FullModDetail>,
    metrics: StorageMetrics,
    selectedTab: LibraryTab,
    onTabSelect: (LibraryTab) -> Unit,
    onModClick: (String) -> Unit,
    onFavoriteClick: (String) -> Unit,
    onDownloadClick: (String) -> Unit,
    onLaunchPsychClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val displayedMods = when (selectedTab) {
        LibraryTab.DOWNLOADED -> allMods.filter { it.downloadStatus == DownloadStatus.DOWNLOADED }
        LibraryTab.FAVORITES -> allMods.filter { it.isFavorite }
        LibraryTab.COMPLETED -> allMods.filter { it.isCompleted || it.highestScore > 0 }
        LibraryTab.DOWNLOADING -> allMods.filter { it.downloadStatus == DownloadStatus.DOWNLOADING }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(FnfDarkBg),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Top Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "MY FUNKIN' VAULT",
                    color = FnfPink,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "MOD LIBRARY & RECORDS",
                    color = FnfTextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Storage Allocation Summary Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = FnfSurface),
                    border = BorderStroke(1.2.dp, FnfBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.SdStorage,
                                    contentDescription = null,
                                    tint = FnfCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "MOD STORAGE ALLOCATED",
                                    color = FnfTextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = formatMbOrGb(metrics.totalStorageAllocatedMb),
                                color = FnfCyan,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Stats breakdown chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            StoragePill(
                                label = "Installed",
                                count = "${metrics.downloadedCount}",
                                color = FnfGreen,
                                modifier = Modifier.weight(1f)
                            )
                            StoragePill(
                                label = "Wishlist",
                                count = "${metrics.favoriteCount}",
                                color = FnfPink,
                                modifier = Modifier.weight(1f)
                            )
                            StoragePill(
                                label = "Cleared",
                                count = "${metrics.completedCount}",
                                color = FnfYellow,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // Library Navigation Tabs
        item {
            SecondaryTabRow(
                selectedTabIndex = selectedTab.ordinal,
                containerColor = FnfSurfaceElevated,
                contentColor = FnfCyan,
                divider = {}
            ) {
                LibraryTab.entries.forEach { tab ->
                    val isSelected = selectedTab == tab
                    val tabName = when (tab) {
                        LibraryTab.DOWNLOADED -> "Installed (${metrics.downloadedCount})"
                        LibraryTab.FAVORITES -> "Wishlist (${metrics.favoriteCount})"
                        LibraryTab.COMPLETED -> "Cleared (${metrics.completedCount})"
                        LibraryTab.DOWNLOADING -> "Queue"
                    }
                    Tab(
                        selected = isSelected,
                        onClick = { onTabSelect(tab) },
                        text = {
                            Text(
                                text = tabName,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) FnfCyan else FnfTextSecondary
                            )
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Empty state for current tab
        if (displayedMods.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.Inbox,
                        contentDescription = "Empty",
                        tint = FnfTextMuted,
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = when (selectedTab) {
                            LibraryTab.DOWNLOADED -> "No mods installed yet"
                            LibraryTab.FAVORITES -> "Your wishlist is empty"
                            LibraryTab.COMPLETED -> "No mods cleared yet"
                            LibraryTab.DOWNLOADING -> "No active downloads"
                        },
                        color = FnfTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = when (selectedTab) {
                            LibraryTab.DOWNLOADED -> "Head over to Browse to download your favorite FNF mods."
                            LibraryTab.FAVORITES -> "Tap the heart icon on any mod to save it to your wishlist."
                            LibraryTab.COMPLETED -> "Complete mod tracklists and record your high scores!"
                            LibraryTab.DOWNLOADING -> "Download packages to see them in this queue."
                        },
                        color = FnfTextSecondary,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            items(displayedMods, key = { it.mod.id }) { item ->
                ModCard(
                    detail = item,
                    onModClick = { onModClick(item.mod.id) },
                    onFavoriteClick = { onFavoriteClick(item.mod.id) },
                    onDownloadClick = { onDownloadClick(item.mod.id) },
                    onLaunchPsychClick = { onLaunchPsychClick(item.mod.id) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun StoragePill(label: String, count: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = FnfSurfaceElevated,
        border = BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = count, color = color, fontSize = 14.sp, fontWeight = FontWeight.Black)
            Text(text = label, color = FnfTextMuted, fontSize = 10.sp)
        }
    }
}

private fun formatMbOrGb(totalMb: Int): String {
    return if (totalMb >= 1024) {
        String.format("%.1f GB", totalMb / 1024f)
    } else {
        "$totalMb MB"
    }
}
