package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.FullModDetail
import com.example.ui.theme.FnfBorder
import com.example.ui.theme.FnfCyan
import com.example.ui.theme.FnfDarkBg
import com.example.ui.theme.FnfPink
import com.example.ui.theme.FnfSurface
import com.example.ui.theme.FnfSurfaceElevated
import com.example.ui.theme.FnfTextMuted
import com.example.ui.theme.FnfTextPrimary
import com.example.ui.theme.FnfYellow
import com.example.ui.viewmodel.MainTab
import com.example.ui.viewmodel.ModViewModel

@Composable
fun MainAppScreen(
    viewModel: ModViewModel,
    modifier: Modifier = Modifier
) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val libraryTab by viewModel.libraryTab.collectAsStateWithLifecycle()

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val selectedEngine by viewModel.selectedEngine.collectAsStateWithLifecycle()
    val selectedSort by viewModel.selectedSort.collectAsStateWithLifecycle()

    val filteredMods by viewModel.filteredMods.collectAsStateWithLifecycle()
    val allMods by viewModel.allMods.collectAsStateWithLifecycle()
    val storageMetrics by viewModel.storageMetrics.collectAsStateWithLifecycle()

    val selectedModId by viewModel.selectedModId.collectAsStateWithLifecycle()
    val psychEngineModId by viewModel.psychEngineModId.collectAsStateWithLifecycle()
    val showAddModDialog by viewModel.showAddModDialog.collectAsStateWithLifecycle()

    var modForLoggingScore by remember { mutableStateOf<FullModDetail?>(null) }

    // Psych Engine 0.7.3 Runtime or Detail overlay or Main tabs
    val currentPsychModDetail = allMods.find { it.mod.id == psychEngineModId }
    val currentSelectedModDetail = allMods.find { it.mod.id == selectedModId }

    if (currentPsychModDetail != null) {
        PsychEngineScreen(
            detail = currentPsychModDetail,
            onBack = { viewModel.launchInPsychEngine(null) },
            onSaveScore = { score, completed, notes ->
                val rating = if (currentPsychModDetail.userRating > 0f) currentPsychModDetail.userRating else 5f
                val bestScore = maxOf(score, currentPsychModDetail.highestScore)
                viewModel.saveUserLog(currentPsychModDetail.mod.id, rating, bestScore, completed, notes)
            },
            modifier = Modifier
                .statusBarsPadding()
                .navigationBarsPadding()
        )
    } else if (currentSelectedModDetail != null) {
        ModDetailScreen(
            detail = currentSelectedModDetail,
            onBack = { viewModel.selectMod(null) },
            onFavoriteToggle = { viewModel.toggleFavorite(currentSelectedModDetail.mod.id) },
            onStartDownload = { viewModel.startDownload(currentSelectedModDetail.mod.id) },
            onCancelDownload = { viewModel.cancelOrDeleteDownload(currentSelectedModDetail.mod.id) },
            onOpenLogScore = { modForLoggingScore = currentSelectedModDetail },
            onLaunchPsychEngine = { viewModel.launchInPsychEngine(currentSelectedModDetail.mod.id) },
            modifier = Modifier.statusBarsPadding()
        )
    } else {
        Scaffold(
            modifier = modifier
                .fillMaxSize()
                .background(FnfDarkBg)
                .statusBarsPadding(),
            bottomBar = {
                Surface(
                    modifier = Modifier.navigationBarsPadding(),
                    color = FnfSurface,
                    border = BorderStroke(1.dp, FnfBorder)
                ) {
                    NavigationBar(
                        containerColor = FnfSurface,
                        contentColor = FnfTextPrimary,
                        windowInsets = WindowInsets(0, 0, 0, 0)
                    ) {
                        NavigationBarItem(
                            selected = currentTab == MainTab.EXPLORE,
                            onClick = { viewModel.currentTab.value = MainTab.EXPLORE },
                            icon = {
                                Icon(
                                    imageVector = Icons.Filled.Explore,
                                    contentDescription = "Explore",
                                    modifier = Modifier.size(22.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = "Browse",
                                    fontWeight = if (currentTab == MainTab.EXPLORE) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 11.sp
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = FnfCyan,
                                selectedTextColor = FnfCyan,
                                indicatorColor = FnfCyan.copy(alpha = 0.15f),
                                unselectedIconColor = FnfTextMuted,
                                unselectedTextColor = FnfTextMuted
                            ),
                            modifier = Modifier.testTag("nav_explore")
                        )

                        NavigationBarItem(
                            selected = currentTab == MainTab.UPLOAD,
                            onClick = { viewModel.currentTab.value = MainTab.UPLOAD },
                            icon = {
                                Icon(
                                    imageVector = Icons.Filled.CloudUpload,
                                    contentDescription = "Upload Mod",
                                    modifier = Modifier.size(22.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = "Upload",
                                    fontWeight = if (currentTab == MainTab.UPLOAD) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 11.sp
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = FnfYellow,
                                selectedTextColor = FnfYellow,
                                indicatorColor = FnfYellow.copy(alpha = 0.15f),
                                unselectedIconColor = FnfTextMuted,
                                unselectedTextColor = FnfTextMuted
                            ),
                            modifier = Modifier.testTag("nav_upload")
                        )

                        NavigationBarItem(
                            selected = currentTab == MainTab.LIBRARY,
                            onClick = { viewModel.currentTab.value = MainTab.LIBRARY },
                            icon = {
                                Icon(
                                    imageVector = Icons.Filled.Bookmark,
                                    contentDescription = "Library",
                                    modifier = Modifier.size(22.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = "My Vault",
                                    fontWeight = if (currentTab == MainTab.LIBRARY) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 11.sp
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = FnfPink,
                                selectedTextColor = FnfPink,
                                indicatorColor = FnfPink.copy(alpha = 0.15f),
                                unselectedIconColor = FnfTextMuted,
                                unselectedTextColor = FnfTextMuted
                            ),
                            modifier = Modifier.testTag("nav_library")
                        )

                        NavigationBarItem(
                            selected = currentTab == MainTab.BEAT_LAB,
                            onClick = { viewModel.currentTab.value = MainTab.BEAT_LAB },
                            icon = {
                                Icon(
                                    imageVector = Icons.Filled.GraphicEq,
                                    contentDescription = "Beat Lab",
                                    modifier = Modifier.size(22.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = "Beat Lab",
                                    fontWeight = if (currentTab == MainTab.BEAT_LAB) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 11.sp
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = FnfCyan,
                                selectedTextColor = FnfCyan,
                                indicatorColor = FnfCyan.copy(alpha = 0.15f),
                                unselectedIconColor = FnfTextMuted,
                                unselectedTextColor = FnfTextMuted
                            ),
                            modifier = Modifier.testTag("nav_beat_lab")
                        )
                    }
                }
            },
            floatingActionButton = {
                if (currentTab == MainTab.EXPLORE || currentTab == MainTab.LIBRARY) {
                    FloatingActionButton(
                        onClick = { viewModel.showAddModDialog.value = true },
                        containerColor = FnfPink,
                        contentColor = FnfDarkBg,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .navigationBarsPadding()
                            .padding(bottom = 12.dp)
                            .testTag("fab_add_custom_mod")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add Custom Mod",
                            tint = androidx.compose.ui.graphics.Color.White,
                            modifier = Modifier.size(24.dp)
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
                AnimatedContent(
                    targetState = currentTab,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "tabTransition"
                ) { targetTab ->
                    when (targetTab) {
                        MainTab.EXPLORE -> {
                            ExploreScreen(
                                mods = filteredMods,
                                searchQuery = searchQuery,
                                onQueryChange = viewModel::onSearchQueryChange,
                                selectedCategory = selectedCategory,
                                onCategorySelect = viewModel::onCategorySelect,
                                selectedEngine = selectedEngine,
                                onEngineSelect = viewModel::onEngineSelect,
                                selectedSort = selectedSort,
                                onSortSelect = viewModel::onSortSelect,
                                metrics = storageMetrics,
                                onModClick = { id -> viewModel.selectMod(id) },
                                onFavoriteClick = { id -> viewModel.toggleFavorite(id) },
                                onDownloadClick = { id -> viewModel.startDownload(id) },
                                onLaunchPsychClick = { id -> viewModel.launchInPsychEngine(id) },
                                onOpenUploadClick = { viewModel.currentTab.value = MainTab.UPLOAD }
                            )
                        }
                        MainTab.UPLOAD -> {
                            UploadModScreen(
                                uploadedMods = allMods.filter { it.mod.isCustomUserMod },
                                onPublishMod = { title, subtitle, author, version, engine, downloadSize, category, difficulty, downloadUrl, description, lore, tags, colorHex, customSongsEncoded ->
                                    viewModel.addCustomMod(
                                        title, subtitle, author, version, engine, downloadSize,
                                        category, difficulty, downloadUrl, description, lore, tags, colorHex,
                                        customSongsEncoded
                                    )
                                },
                                onDeleteUploadedMod = { id -> viewModel.deleteCustomMod(id) },
                                onLaunchModInStableEngine = { id -> viewModel.launchInPsychEngine(id) },
                                onNavigateToBrowse = { viewModel.currentTab.value = MainTab.EXPLORE }
                            )
                        }
                        MainTab.LIBRARY -> {
                            LibraryScreen(
                                allMods = allMods,
                                metrics = storageMetrics,
                                selectedTab = libraryTab,
                                onTabSelect = { tab -> viewModel.libraryTab.value = tab },
                                onModClick = { id -> viewModel.selectMod(id) },
                                onFavoriteClick = { id -> viewModel.toggleFavorite(id) },
                                onDownloadClick = { id -> viewModel.startDownload(id) },
                                onLaunchPsychClick = { id -> viewModel.launchInPsychEngine(id) }
                            )
                        }
                        MainTab.BEAT_LAB -> {
                            val score by viewModel.rhythmScore.collectAsStateWithLifecycle()
                            val combo by viewModel.rhythmCombo.collectAsStateWithLifecycle()
                            val maxCombo by viewModel.maxCombo.collectAsStateWithLifecycle()
                            val latestRating by viewModel.latestRating.collectAsStateWithLifecycle()
                            val accuracyHits by viewModel.accuracyHits.collectAsStateWithLifecycle()

                            RhythmLabScreen(
                                score = score,
                                combo = combo,
                                maxCombo = maxCombo,
                                latestRating = latestRating,
                                accuracyHits = accuracyHits,
                                onArrowTap = viewModel::onArrowTapped,
                                onResetScore = viewModel::resetRhythmScore
                            )
                        }
                    }
                }
            }
        }
    }

    // Add Mod Dialog
    if (showAddModDialog) {
        AddModDialog(
            onDismiss = { viewModel.showAddModDialog.value = false },
            onAdd = { title, subtitle, author, version, engine, downloadSize, category, difficulty, downloadUrl, description, lore, tags, colorHex ->
                viewModel.addCustomMod(
                    title, subtitle, author, version, engine, downloadSize,
                    category, difficulty, downloadUrl, description, lore, tags, colorHex
                )
            }
        )
    }

    // Log Score Dialog
    val loggingMod = modForLoggingScore
    if (loggingMod != null) {
        LogScoreDialog(
            detail = loggingMod,
            onDismiss = { modForLoggingScore = null },
            onSave = { rating, score, isCompleted, notes ->
                viewModel.saveUserLog(loggingMod.mod.id, rating, score, isCompleted, notes)
            }
        )
    }
}
