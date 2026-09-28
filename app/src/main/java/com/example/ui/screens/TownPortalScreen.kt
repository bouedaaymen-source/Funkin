package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.R
import com.example.data.model.TownCitizen
import com.example.data.model.TownEpisode
import com.example.data.model.TownRepositoryData
import kotlinx.coroutines.launch

enum class TownNavTab(val label: String) {
    HUB("Town Hub"),
    EPISODES("6 Episodes"),
    CITIZENS("Citizens"),
    GITHUB_PAGE("GitHub Page")
}

private val TownBgDark = Color(0xFF0B0F14)
private val TownSurface = Color(0xFF131A22)
private val TownElevated = Color(0xFF1B2531)
private val TownCream = Color(0xFFFFE9D9)
private val TownOrange = Color(0xFFFF5A36)
private val TownGreen = Color(0xFF2EE59D)
private val TownDiscord = Color(0xFF5865F2)
private val TownInstagram = Color(0xFFE1306C)
private val TownTextPrimary = Color(0xFFF3F6FA)
private val TownTextMuted = Color(0xFF9BA8B8)
private val TownBorder = Color(0x26FFE9D9)

@Composable
fun TownPortalScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var selectedTab by rememberSaveable { mutableStateOf(TownNavTab.HUB) }
    var holeDepthMeters by rememberSaveable { mutableFloatStateOf(2.4f) }
    var totalShovelClicks by rememberSaveable { mutableIntStateOf(3) }
    var citizenSearchQuery by rememberSaveable { mutableStateOf("") }
    var showAddCitizenDialog by rememberSaveable { mutableStateOf(false) }

    val citizens = remember {
        mutableStateListOf<TownCitizen>().apply {
            addAll(TownRepositoryData.initialCitizens)
        }
    }

    if (selectedTab != TownNavTab.HUB) {
        BackHandler {
            selectedTab = TownNavTab.HUB
        }
    }

    fun openExternalUrl(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            scope.launch {
                snackbarHostState.showSnackbar("Link: $url")
            }
        }
    }

    fun copyToClipboard(label: String, text: String, toastMsg: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        clipboard?.setPrimaryClip(ClipData.newPlainText(label, text))
        scope.launch {
            snackbarHostState.showSnackbar(toastMsg)
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(TownBgDark)
            .statusBarsPadding(),
        containerColor = TownBgDark,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            Surface(
                color = TownSurface,
                border = BorderStroke(1.dp, TownBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_town_discord_icon),
                            contentDescription = "The 2090 Club Discord Icon",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "The 2090 Club",
                                    color = TownTextPrimary,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = TownGreen.copy(alpha = 0.16f),
                                    shape = RoundedCornerShape(999.dp),
                                    border = BorderStroke(1.dp, TownGreen.copy(alpha = 0.4f))
                                ) {
                                    Text(
                                        text = "#🏘️the_town",
                                        color = TownGreen,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Twenty Ninety Creative · 1,900+ Citizens",
                                color = TownTextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Button(
                        onClick = { openExternalUrl(TownRepositoryData.DISCORD_JOIN_URL) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TownDiscord,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        modifier = Modifier.testTag("top_join_discord_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Forum,
                            contentDescription = "Join Discord",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Join Discord",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                modifier = Modifier.navigationBarsPadding(),
                color = TownSurface,
                border = BorderStroke(1.dp, TownBorder)
            ) {
                NavigationBar(
                    containerColor = TownSurface,
                    contentColor = TownTextPrimary,
                    windowInsets = WindowInsets(0, 0, 0, 0)
                ) {
                    NavigationBarItem(
                        selected = selectedTab == TownNavTab.HUB,
                        onClick = { selectedTab = TownNavTab.HUB },
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.Home,
                                contentDescription = "Town Hub"
                            )
                        },
                        label = {
                            Text(
                                text = "Town Hub",
                                fontSize = 11.sp,
                                fontWeight = if (selectedTab == TownNavTab.HUB) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = TownOrange,
                            selectedTextColor = TownOrange,
                            indicatorColor = TownOrange.copy(alpha = 0.16f),
                            unselectedIconColor = TownTextMuted,
                            unselectedTextColor = TownTextMuted
                        ),
                        modifier = Modifier.testTag("nav_town_hub")
                    )

                    NavigationBarItem(
                        selected = selectedTab == TownNavTab.EPISODES,
                        onClick = { selectedTab = TownNavTab.EPISODES },
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.Movie,
                                contentDescription = "All 6 Episodes"
                            )
                        },
                        label = {
                            Text(
                                text = "6 Episodes",
                                fontSize = 11.sp,
                                fontWeight = if (selectedTab == TownNavTab.EPISODES) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = TownGreen,
                            selectedTextColor = TownGreen,
                            indicatorColor = TownGreen.copy(alpha = 0.16f),
                            unselectedIconColor = TownTextMuted,
                            unselectedTextColor = TownTextMuted
                        ),
                        modifier = Modifier.testTag("nav_episodes")
                    )

                    NavigationBarItem(
                        selected = selectedTab == TownNavTab.CITIZENS,
                        onClick = { selectedTab = TownNavTab.CITIZENS },
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.Groups,
                                contentDescription = "Town Citizens"
                            )
                        },
                        label = {
                            Text(
                                text = "Citizens",
                                fontSize = 11.sp,
                                fontWeight = if (selectedTab == TownNavTab.CITIZENS) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = TownCream,
                            selectedTextColor = TownCream,
                            indicatorColor = TownCream.copy(alpha = 0.16f),
                            unselectedIconColor = TownTextMuted,
                            unselectedTextColor = TownTextMuted
                        ),
                        modifier = Modifier.testTag("nav_citizens")
                    )

                    NavigationBarItem(
                        selected = selectedTab == TownNavTab.GITHUB_PAGE,
                        onClick = { selectedTab = TownNavTab.GITHUB_PAGE },
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.Public,
                                contentDescription = "GitHub Page"
                            )
                        },
                        label = {
                            Text(
                                text = "GitHub Page",
                                fontSize = 11.sp,
                                fontWeight = if (selectedTab == TownNavTab.GITHUB_PAGE) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = TownDiscord,
                            selectedTextColor = TownDiscord,
                            indicatorColor = TownDiscord.copy(alpha = 0.16f),
                            unselectedIconColor = TownTextMuted,
                            unselectedTextColor = TownTextMuted
                        ),
                        modifier = Modifier.testTag("nav_github_page")
                    )
                }
            }
        },
        floatingActionButton = {
            if (selectedTab == TownNavTab.CITIZENS) {
                FloatingActionButton(
                    onClick = { showAddCitizenDialog = true },
                    containerColor = TownOrange,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.testTag("add_citizen_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Claim Citizen Avatar"
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
                targetState = selectedTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "townTabTransition"
            ) { tab ->
                when (tab) {
                    TownNavTab.HUB -> {
                        TownHubTabContent(
                            holeDepthMeters = holeDepthMeters,
                            totalShovelClicks = totalShovelClicks,
                            onDigHole = {
                                holeDepthMeters = (holeDepthMeters + 0.8f).coerceAtMost(10.0f)
                                totalShovelClicks++
                            },
                            onResetHole = {
                                holeDepthMeters = 2.4f
                                totalShovelClicks = 3
                            },
                            onOpenDiscord = { openExternalUrl(TownRepositoryData.DISCORD_JOIN_URL) },
                            onCopyDiscord = {
                                copyToClipboard(
                                    "Discord Invite",
                                    TownRepositoryData.DISCORD_JOIN_URL,
                                    "Copied ${TownRepositoryData.DISCORD_JOIN_URL} to clipboard!"
                                )
                            },
                            onOpenReel = { openExternalUrl(TownRepositoryData.FEATURED_REEL_URL) },
                            onOpenStudio = { openExternalUrl(TownRepositoryData.STUDIO_WEBSITE_URL) },
                            onNavigateToEpisodes = { selectedTab = TownNavTab.EPISODES },
                            onNavigateToGitHubPage = { selectedTab = TownNavTab.GITHUB_PAGE }
                        )
                    }

                    TownNavTab.EPISODES -> {
                        TownEpisodesTabContent(
                            episodes = TownRepositoryData.allEpisodes,
                            onOpenReelUrl = { url -> openExternalUrl(url) },
                            onOpenDiscord = { openExternalUrl(TownRepositoryData.DISCORD_JOIN_URL) }
                        )
                    }

                    TownNavTab.CITIZENS -> {
                        TownCitizensTabContent(
                            citizens = citizens,
                            searchQuery = citizenSearchQuery,
                            onSearchQueryChange = { citizenSearchQuery = it },
                            onOpenAddDialog = { showAddCitizenDialog = true },
                            onOpenDiscord = { openExternalUrl(TownRepositoryData.DISCORD_JOIN_URL) }
                        )
                    }

                    TownNavTab.GITHUB_PAGE -> {
                        GitHubPageTabContent(
                            onOpenDiscord = { openExternalUrl(TownRepositoryData.DISCORD_JOIN_URL) },
                            onOpenReel = { openExternalUrl(TownRepositoryData.FEATURED_REEL_URL) },
                            onCopyHtml = { htmlText ->
                                copyToClipboard(
                                    "GitHub Page index.html",
                                    htmlText,
                                    "Copied complete GitHub Pages index.html to clipboard!"
                                )
                            }
                        )
                    }
                }
            }
        }
    }

    if (showAddCitizenDialog) {
        AddTownCitizenDialog(
            onDismiss = { showAddCitizenDialog = false },
            onSubmit = { name, handle, skinDesc, holeProposal ->
                citizens.add(
                    0,
                    TownCitizen(
                        id = "user_${System.currentTimeMillis()}",
                        name = name.ifBlank { "Follower #${1901 + citizens.size}" },
                        handle = handle.ifBlank { "@town_citizen" },
                        role = "New Town Citizen & Hole Voter",
                        appearance = skinDesc.ifBlank { "Custom 3D Town Avatar" },
                        lore = "Hole Proposal: ${holeProposal.ifBlank { "Keep digging until we reach the underground ocean!" }}",
                        episodeFirstSeen = "Community Registry (#🏘️the_town)",
                        isUserAdded = true
                    )
                )
                showAddCitizenDialog = false
                scope.launch {
                    snackbarHostState.showSnackbar("Citizen registered in The Town census!")
                }
            }
        )
    }
}

@Composable
private fun TownHubTabContent(
    holeDepthMeters: Float,
    totalShovelClicks: Int,
    onDigHole: () -> Unit,
    onResetHole: () -> Unit,
    onOpenDiscord: () -> Unit,
    onCopyDiscord: () -> Unit,
    onOpenReel: () -> Unit,
    onOpenStudio: () -> Unit,
    onNavigateToEpisodes: () -> Unit,
    onNavigateToGitHubPage: () -> Unit
) {
    val discord = TownRepositoryData.discordInfo

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // 1. Hero Banner Card
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = TownSurface),
                border = BorderStroke(1.dp, TownBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_town_hero_banner_1790630284850),
                            contentDescription = "The Town 3D Crowd Simulation Hero Banner",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            TownBgDark.copy(alpha = 0.92f)
                                        )
                                    )
                                )
                        )
                        Surface(
                            color = TownOrange.copy(alpha = 0.9f),
                            shape = RoundedCornerShape(999.dp),
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "LIVE EVENT · THE HOLE HAS OFFICIALLY STARTED",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Welcome to The Town (#🏘️the_town)",
                                color = TownCream,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "By Twenty Ninety Creative (@twentyninetycreative) · 1,900+ Follower Avatars",
                                color = TownTextPrimary,
                                fontSize = 13.sp
                            )
                        }
                    }

                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Every follower spawns as an avatar in a chaotic 3D community sandbox. At 1,900 followers, the town unlocked a parched Stone Golem who desperately needs water—and after @ashrafabisaid commented \"DIG A HOLE??\", four mannequins grabbed shovels and started digging.",
                            color = TownTextMuted,
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = onOpenDiscord,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = TownDiscord,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("join_discord_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Forum,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Join The 2090 Club",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 13.sp
                                )
                            }

                            OutlinedButton(
                                onClick = onOpenReel,
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, TownInstagram),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = TownInstagram),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("watch_reel_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.PlayArrow,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Watch IG Reel",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Official Discord Invite Card (https://discord.gg/ENa878794)
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF161B29)),
                border = BorderStroke(2.dp, TownDiscord.copy(alpha = 0.7f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = discord.iconRes),
                                contentDescription = "The 2090 Club Server Icon",
                                modifier = Modifier
                                    .size(58.dp)
                                    .clip(RoundedCornerShape(16.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = discord.serverName,
                                        color = Color.White,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Filled.Verified,
                                        contentDescription = "Verified Community",
                                        tint = TownGreen,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Text(
                                    text = "Landing Channel: ${discord.channelName}",
                                    color = TownCream,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = "● ${discord.onlineCount} Online Now   ● ${discord.memberCount} Founding Citizens",
                                    color = TownGreen,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Surface(
                        color = Color.Black.copy(alpha = 0.32f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            TelemetryRow("Official Join Link", discord.inviteUrl)
                            TelemetryRow("Invite Code", discord.inviteCode)
                            TelemetryRow("Invited By", "${discord.inviterDisplayName} (@${discord.inviterUsername})")
                            TelemetryRow("Guild / Channel ID", "${discord.guildId} / ${discord.channelId}")
                            TelemetryRow("Server Features", discord.features.joinToString(" · "))
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onOpenDiscord,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF23A55A),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("accept_discord_invite_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Join Server — https://discord.gg/ENa878794",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = onCopyDiscord,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, TownBorder),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TownCream),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("copy_discord_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ContentCopy,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Copy Invite Link (https://discord.gg/ENa878794)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // 3. Interactive Hole Excavation Mini-Game
        item {
            val progress = (holeDepthMeters / 10.0f).coerceIn(0f, 1f)
            val isHydrated = holeDepthMeters >= 10.0f

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF14211B)),
                border = BorderStroke(1.5.dp, TownGreen.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isHydrated) Icons.Filled.WaterDrop else Icons.Filled.Terrain,
                                contentDescription = null,
                                tint = TownGreen,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Help Excavate The Hole",
                                    color = TownCream,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = "\"Dig faster, we just got him!\" — Save the Stone Golem",
                                    color = TownTextMuted,
                                    fontSize = 12.sp
                                )
                            }
                        }
                        Surface(
                            color = TownGreen.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "${(progress * 100).toInt()}% Hydrated",
                                color = TownGreen,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(999.dp)),
                        color = TownGreen,
                        trackColor = Color.Black.copy(alpha = 0.45f)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (isHydrated) {
                            "💧 WATER TABLE REACHED (10.0m)! The cracked Stone Golem is saved! Head to #🏘️the_town on Discord to vote on what happens to the Hole next."
                        } else {
                            "Current Pit Depth: ${"%.1f".format(holeDepthMeters)}m / 10.0m · Shovel Loads: $totalShovelClicks · 4 Mannequins Digging"
                        },
                        color = TownTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = onDigHole,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = TownOrange,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("dig_hole_button")
                        ) {
                            Text(
                                text = if (isHydrated) "💧 Golem Hydrated!" else "⛏️ Dig Shovel (+0.8m)",
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        if (isHydrated) {
                            OutlinedButton(
                                onClick = onResetHole,
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, TownBorder),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = TownCream)
                            ) {
                                Text("Reset Pit")
                            }
                        }
                    }
                }
            }
        }

        // 4. Complete Breakdown of Reel DdzIfF2gIDD
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = TownSurface),
                border = BorderStroke(1.dp, TownBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "REEL INTEL · SHORTCODE DdzIfF2gIDD",
                        color = TownOrange,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "\"the hole has officially started\"",
                        color = TownTextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "1,900 In-World Followers · 1,351 Views · 79 Likes · 61 Comments · 32.6s Duration",
                        color = TownGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        color = TownElevated,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, TownOrange.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "VERBATIM SPOKEN AUDIO TRANSCRIPT",
                                color = TownOrange,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = TownRepositoryData.VERBATIM_TRANSCRIPT,
                                color = TownCream,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                fontStyle = FontStyle.Italic
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Image(
                        painter = painterResource(id = R.drawable.img_town_frames_grid),
                        contentDescription = "16-Frame Contact Sheet of Reel DdzIfF2gIDD",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.FillWidth
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Above: 16-frame capture from Reel DdzIfF2gIDD showing @ashrafabisaid's comment ('DIG A HOLE??'), the parched Stone Golem, Satoru Gojo, Follower 64, and the 4 shovel diggers.",
                        color = TownTextMuted,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    TownRepositoryData.featuredReelTimeline.forEach { beat ->
                        Surface(
                            color = TownBgDark.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, TownBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Surface(
                                        color = TownGreen.copy(alpha = 0.16f),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = beat.timeRange,
                                            color = TownGreen,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontFamily = FontFamily.Monospace,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                    Text(
                                        text = beat.spokenAudio,
                                        color = TownCream,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "On-Screen: ${beat.onScreenText}",
                                    color = TownOrange,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = beat.description,
                                    color = TownTextMuted,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = onNavigateToEpisodes,
                            colors = ButtonDefaults.buttonColors(containerColor = TownElevated),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Explore All 6 Episodes", color = TownCream, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = onNavigateToGitHubPage,
                            colors = ButtonDefaults.buttonColors(containerColor = TownOrange),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Open GitHub Page", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 5. About Twenty Ninety Creative Studio
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = TownSurface),
                border = BorderStroke(1.dp, TownBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "CREATOR STUDIO · TWENTYNINETYCREATIVE.COM",
                        color = TownGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Twenty Ninety Creative",
                        color = TownTextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "\"We craft worlds worth exploring. We don't rush worlds. We let them unfold.\"",
                        color = TownCream,
                        fontSize = 13.sp,
                        fontStyle = FontStyle.Italic
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "A Saudi Arabia & Toronto-based independent game development studio drawing inspiration from 1990s games. Creators of The Town simulation series (@twentyninetycreative).",
                        color = TownTextMuted,
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Studio Leadership:",
                        color = TownTextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    TownRepositoryData.studioTeam.forEach { member ->
                        Text(
                            text = "• $member",
                            color = TownTextMuted,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Games in Development:",
                        color = TownTextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    TownRepositoryData.studioGames.forEach { game ->
                        Surface(
                            color = TownElevated,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = game.title,
                                        color = TownCream,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = game.status,
                                        color = TownGreen,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Text(
                                    text = game.genre,
                                    color = TownOrange,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = game.description,
                                    color = TownTextMuted,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = onOpenStudio,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, TownBorder),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TownCream),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Visit twentyninetycreative.com", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun TelemetryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = TownTextMuted,
            fontSize = 12.sp
        )
        Text(
            text = value,
            color = TownTextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
private fun TownEpisodesTabContent(
    episodes: List<TownEpisode>,
    onOpenReelUrl: (String) -> Unit,
    onOpenDiscord: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "COMPLETE SERIES CHRONICLE · @TWENTYNINETYCREATIVE",
                    color = TownGreen,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "All 6 Episodes of \"The Town\"",
                    color = TownTextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "From 1,840 followers and the first spider to 1,900 followers digging a hole for the parched Stone Golem.",
                    color = TownTextMuted,
                    fontSize = 13.sp
                )
            }
        }

        items(episodes, key = { it.shortcode }) { ep ->
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = TownSurface),
                border = BorderStroke(1.dp, TownBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(210.dp)
                    ) {
                        Image(
                            painter = painterResource(id = ep.thumbnailRes),
                            contentDescription = ep.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Surface(
                            color = Color.Black.copy(alpha = 0.78f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "Episode ${ep.episodeNumber} · ${ep.shortcode}",
                                color = TownCream,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Surface(
                            color = TownGreen.copy(alpha = 0.9f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "${ep.inWorldFollowers} followers",
                                color = TownBgDark,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = ep.title,
                            color = TownTextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "👁️ ${ep.views} views   ·   ❤️ ${ep.likes} likes   ·   💬 ${ep.comments} comments   ·   ⏱️ ${ep.durationSec}s",
                            color = TownOrange,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = ep.summary,
                            color = TownTextMuted,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            color = TownElevated,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = ep.highlightCommentOrDetail,
                                color = TownCream,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(
                                onClick = { onOpenReelUrl(ep.reelUrl) },
                                colors = ButtonDefaults.buttonColors(containerColor = TownInstagram),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.PlayArrow,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Watch Reel", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            OutlinedButton(
                                onClick = onOpenDiscord,
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, TownDiscord),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = TownCream),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Vote in Discord", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TownCitizensTabContent(
    citizens: List<TownCitizen>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onOpenAddDialog: () -> Unit,
    onOpenDiscord: () -> Unit
) {
    val filtered = remember(citizens.size, searchQuery) {
        if (searchQuery.isBlank()) {
            citizens.toList()
        } else {
            val q = searchQuery.trim().lowercase()
            citizens.filter {
                it.name.lowercase().contains(q) ||
                    it.handle.lowercase().contains(q) ||
                    it.role.lowercase().contains(q) ||
                    it.appearance.lowercase().contains(q) ||
                    it.lore.lowercase().contains(q)
            }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column {
                Text(
                    text = "IN-WORLD TELEMETRY · 1,900+ FOLLOWER AVATARS",
                    color = TownOrange,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Town Census & Notable Citizens",
                    color = TownTextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = { Text("Search Satoru Gojo, Stone Golem, Follower 64...", color = TownTextMuted) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search Citizens",
                            tint = TownCream
                        )
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TownOrange,
                        unfocusedBorderColor = TownBorder,
                        focusedTextColor = TownTextPrimary,
                        unfocusedTextColor = TownTextPrimary,
                        focusedContainerColor = TownSurface,
                        unfocusedContainerColor = TownSurface
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("citizen_search_input")
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = onOpenAddDialog,
                        colors = ButtonDefaults.buttonColors(containerColor = TownOrange),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Claim Citizen / Vote", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Button(
                        onClick = onOpenDiscord,
                        colors = ButtonDefaults.buttonColors(containerColor = TownDiscord),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.Forum, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Join #🏘️the_town", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        items(filtered, key = { it.id }) { citizen ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = TownSurface),
                border = BorderStroke(
                    1.dp,
                    if (citizen.isUserAdded) TownGreen.copy(alpha = 0.6f) else TownBorder
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Surface(
                            color = TownCream.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = citizen.role.uppercase(),
                                color = TownCream,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Text(
                            text = "Reel: ${citizen.episodeFirstSeen}",
                            color = TownGreen,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${citizen.name} (${citizen.handle})",
                        color = TownTextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Appearance: ${citizen.appearance}",
                        color = TownOrange,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = citizen.lore,
                        color = TownTextMuted,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun GitHubPageTabContent(
    onOpenDiscord: () -> Unit,
    onOpenReel: () -> Unit,
    onCopyHtml: (String) -> Unit
) {
    val context = LocalContext.current
    var showSourceCode by rememberSaveable { mutableStateOf(false) }

    val githubHtmlContent = remember {
        try {
            context.assets.open("github_page/index.html").bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            "<!-- GitHub Page generated at /index.html and /docs/index.html -->"
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Surface(
            color = TownElevated,
            border = BorderStroke(1.dp, TownBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Live GitHub Page (/index.html & /docs/index.html)",
                            color = TownCream,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Ready for GitHub Pages deployment · Includes Discord Join Link",
                            color = TownTextMuted,
                            fontSize = 11.sp
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = { showSourceCode = !showSourceCode },
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, TownBorder),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TownCream),
                            modifier = Modifier.testTag("toggle_source_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Code,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (showSourceCode) "Live Preview" else "HTML Code",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Button(
                            onClick = { onCopyHtml(githubHtmlContent) },
                            colors = ButtonDefaults.buttonColors(containerColor = TownOrange),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("copy_github_html_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ContentCopy,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Copy HTML", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        if (showSourceCode) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF080B0F)),
                contentPadding = PaddingValues(16.dp)
            ) {
                item {
                    Text(
                        text = githubHtmlContent,
                        color = TownGreen,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        } else {
            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("github_page_webview"),
                factory = { ctx ->
                    WebView(ctx).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.allowFileAccess = true
                        webViewClient = object : WebViewClient() {
                            override fun shouldOverrideUrlLoading(
                                view: WebView?,
                                request: WebResourceRequest?
                            ): Boolean {
                                val url = request?.url?.toString() ?: return false
                                if (url.startsWith("http://") || url.startsWith("https://")) {
                                    try {
                                        ctx.startActivity(
                                            Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            }
                                        )
                                    } catch (_: Exception) {
                                    }
                                    return true
                                }
                                return false
                            }
                        }
                        loadUrl("file:///android_asset/github_page/index.html")
                    }
                }
            )
        }
    }
}

@Composable
private fun AddTownCitizenDialog(
    onDismiss: () -> Unit,
    onSubmit: (name: String, handle: String, skinDesc: String, holeProposal: String) -> Unit
) {
    var name by rememberSaveable { mutableStateOf("") }
    var handle by rememberSaveable { mutableStateOf("") }
    var skinDesc by rememberSaveable { mutableStateOf("") }
    var holeProposal by rememberSaveable { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = TownSurface,
        titleContentColor = TownCream,
        textContentColor = TownTextPrimary,
        title = {
            Text(
                text = "Claim Town Avatar & Vote on The Hole",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Add your citizen profile to The Town census and submit what should happen next with the Hole in #🏘️the_town!",
                    color = TownTextMuted,
                    fontSize = 12.sp
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Citizen Name (e.g. Follower 1901)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = handle,
                    onValueChange = { handle = it },
                    label = { Text("Instagram / Discord Handle") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = skinDesc,
                    onValueChange = { skinDesc = it },
                    label = { Text("Requested 3D Avatar Skin") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = holeProposal,
                    onValueChange = { holeProposal = it },
                    label = { Text("What should happen with The Hole?") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(name, handle, skinDesc, holeProposal) },
                colors = ButtonDefaults.buttonColors(containerColor = TownOrange)
            ) {
                Text("Register Citizen", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TownTextMuted)
            }
        }
    )
}
