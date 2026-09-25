package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MusicVideo
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.FullModDetail
import com.example.ui.components.ModCard
import com.example.ui.components.SearchBarFilter
import com.example.ui.theme.FnfBorder
import com.example.ui.theme.FnfCyan
import com.example.ui.theme.FnfDarkBg
import com.example.ui.theme.FnfGreen
import com.example.ui.theme.FnfPink
import com.example.ui.theme.FnfPurple
import com.example.ui.theme.FnfSurface
import com.example.ui.theme.FnfSurfaceElevated
import com.example.ui.theme.FnfTextMuted
import com.example.ui.theme.FnfTextPrimary
import com.example.ui.theme.FnfTextSecondary
import com.example.ui.theme.FnfYellow
import com.example.ui.viewmodel.SortOption
import com.example.ui.viewmodel.StorageMetrics

@Composable
fun ExploreScreen(
    mods: List<FullModDetail>,
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    selectedCategory: String,
    onCategorySelect: (String) -> Unit,
    selectedEngine: String,
    onEngineSelect: (String) -> Unit,
    selectedSort: SortOption,
    onSortSelect: (SortOption) -> Unit,
    metrics: StorageMetrics,
    onModClick: (String) -> Unit,
    onFavoriteClick: (String) -> Unit,
    onDownloadClick: (String) -> Unit,
    onLaunchPsychClick: (String) -> Unit = {},
    onOpenUploadClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val featuredMod = mods.find { it.mod.isFeatured } ?: mods.firstOrNull()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(FnfDarkBg),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Mario's Madness V2 Hero Section with CRT Scanlines
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(225.dp)
                    .drawWithContent {
                        drawContent()
                        var y = 0f
                        while (y < size.height) {
                            drawLine(
                                color = Color.Black.copy(alpha = 0.25f),
                                start = Offset(0f, y),
                                end = Offset(size.width, y),
                                strokeWidth = 2f
                            )
                            y += 6f
                        }
                    }
            ) {
                // Mario's Madness V2 Banner Image
                Image(
                    painter = painterResource(id = R.drawable.img_mmv2_hero_1790374102268),
                    contentDescription = "Mario's Madness V2 Mod Hub Hero",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Dark Crimson & Pitch Black Gradient Overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.2f),
                                    FnfDarkBg.copy(alpha = 0.65f),
                                    FnfDarkBg
                                )
                            )
                        )
                )

                // Hero Content
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = FnfCyan
                        ) {
                            Text(
                                text = "🍄 MARIO'S MADNESS V2 EDITION",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = FnfYellow.copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, FnfYellow)
                        ) {
                            Text(
                                text = "STABLE PSYCH ENGINE MATCHER",
                                color = FnfYellow,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "ULTRA M'S MOD VAULT & UPLOADER",
                        color = FnfTextPrimary,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )

                    Text(
                        text = "Upload mods, download community cartridges & play in their exact stable Psych Engine version",
                        color = FnfTextSecondary,
                        fontSize = 12.sp,
                        maxLines = 2
                    )
                }
            }
        }

        // Quick Upload Mod Cartridge Banner CTA
        item {
            Button(
                onClick = onOpenUploadClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("hero_upload_mod_btn"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = FnfCyan,
                    contentColor = Color.White
                )
            ) {
                Icon(Icons.Filled.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "UPLOAD MOD (.ZIP / .APK) & LOCK STABLE PSYCH VERSION",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }

        // Live stats overview row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatBadge(
                    label = "Catalog",
                    value = "${mods.size} Mods",
                    color = FnfCyan,
                    icon = Icons.Filled.Explore,
                    modifier = Modifier.weight(1f)
                )
                StatBadge(
                    label = "Downloaded",
                    value = "${metrics.downloadedCount} Ready",
                    color = FnfGreen,
                    icon = Icons.Filled.ElectricBolt,
                    modifier = Modifier.weight(1f)
                )
                StatBadge(
                    label = "Wishlist",
                    value = "${metrics.favoriteCount} Saved",
                    color = FnfPink,
                    icon = Icons.Filled.Favorite,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Featured Spotlight banner if not searching
        if (searchQuery.isBlank() && featuredMod != null) {
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ElectricBolt,
                            contentDescription = "Featured",
                            tint = FnfYellow,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "SPOTLIGHT OF THE WEEK",
                            color = FnfYellow,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { onModClick(featuredMod.mod.id) }
                            .testTag("featured_spotlight_card"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = FnfSurfaceElevated),
                        border = BorderStroke(1.5.dp, Color(featuredMod.mod.colorHex))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = featuredMod.mod.title,
                                    color = FnfTextPrimary,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = featuredMod.mod.subtitle,
                                    color = FnfTextSecondary,
                                    fontSize = 12.sp,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${featuredMod.mod.songs.size} Original Songs",
                                        color = FnfCyan,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "•",
                                        color = FnfTextMuted,
                                        fontSize = 11.sp
                                    )
                                    Text(
                                        text = featuredMod.mod.engine,
                                        color = FnfTextMuted,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(featuredMod.mod.colorHex).copy(alpha = 0.2f),
                                border = BorderStroke(1.dp, Color(featuredMod.mod.colorHex)),
                                modifier = Modifier.padding(start = 8.dp)
                            ) {
                                Text(
                                    text = "VIEW",
                                    color = Color(featuredMod.mod.colorHex),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Search & Filter Section
        item {
            Spacer(modifier = Modifier.height(6.dp))
            SearchBarFilter(
                query = searchQuery,
                onQueryChange = onQueryChange,
                selectedCategory = selectedCategory,
                onCategorySelect = onCategorySelect,
                selectedEngine = selectedEngine,
                onEngineSelect = onEngineSelect,
                selectedSort = selectedSort,
                onSortSelect = onSortSelect
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Results Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "COMMUNITY MODS (${mods.size})",
                    color = FnfTextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = selectedSort.displayName,
                    color = FnfCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
        }

        // Empty State
        if (mods.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.SearchOff,
                        contentDescription = "No results",
                        tint = FnfTextMuted,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No FNF mods found",
                        color = FnfTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Try adjusting your search terms or clearing the active category filters.",
                        color = FnfTextSecondary,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            // Mod Cards List
            items(mods, key = { it.mod.id }) { item ->
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
private fun StatBadge(
    label: String,
    value: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = FnfSurface,
        border = BorderStroke(1.dp, FnfBorder)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
            Column {
                Text(
                    text = label.uppercase(),
                    color = FnfTextMuted,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = value,
                    color = FnfTextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
