package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.DefaultCatalog
import com.example.data.model.FullModDetail
import com.example.data.model.SongItem
import com.example.data.psych.MarioMadnessAudioEngine
import com.example.data.psych.Psych073ModBuilder
import com.example.ui.components.ModCard
import com.example.ui.components.SearchBarFilter
import com.example.ui.theme.FnfBorder
import com.example.ui.theme.FnfCyan
import com.example.ui.theme.FnfDarkBg
import com.example.ui.theme.FnfGreen
import com.example.ui.theme.FnfOrange
import com.example.ui.theme.FnfPink
import com.example.ui.theme.FnfPurple
import com.example.ui.theme.FnfRed
import com.example.ui.theme.FnfSurface
import com.example.ui.theme.FnfSurfaceElevated
import com.example.ui.theme.FnfTextMuted
import com.example.ui.theme.FnfTextPrimary
import com.example.ui.theme.FnfTextSecondary
import com.example.ui.theme.FnfYellow
import com.example.ui.viewmodel.SortOption
import com.example.ui.viewmodel.StorageMetrics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private data class Mmv2WorldInfo(
    val worldNum: Int,
    val name: String,
    val subtitle: String,
    val accentColor: Color,
    val songs: List<SongItem>
)

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
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val featuredMod = mods.find { it.mod.id == "secret-exit-reimagined" }
        ?: mods.find { it.mod.isFeatured }
        ?: FullModDetail(mod = DefaultCatalog.mods.first())

    var selectedWorldIndex by remember { mutableIntStateOf(6) } // Default to World 7: Ultra M's Citadel & Secret Exit (3 Endings)
    var previewingSong by remember { mutableStateOf<SongItem?>(null) }

    // 268 MB Masterpiece ZIP Exporter State
    var isPacking268MbZip by remember { mutableStateOf(false) }
    var packedMbProgress by remember { mutableIntStateOf(0) }
    var totalMbTarget by remember { mutableIntStateOf(268) }
    var currentPackedFile by remember { mutableStateOf("") }

    val saveMasterpieceZipLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/zip")
    ) { uri: Uri? ->
        if (uri != null) {
            isPacking268MbZip = true
            packedMbProgress = 0
            currentPackedFile = "pack.json & pack.png"
            scope.launch(Dispatchers.IO) {
                val ok = Psych073ModBuilder.writePsych073ModZipToUri(
                    context = context,
                    targetUri = uri,
                    detail = featuredMod,
                    enableHealthDrain = true,
                    enableBeatZoom = true,
                    onProgress = { writtenMb, totalMb, fileLabel ->
                        packedMbProgress = writtenMb
                        totalMbTarget = totalMb
                        currentPackedFile = fileLabel
                    }
                )
                withContext(Dispatchers.Main) {
                    isPacking268MbZip = false
                    if (ok) {
                        Toast.makeText(
                            context,
                            "Saved 268 MB Mario's Madness V2 (#359554) Masterpiece .ZIP to your device!",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(context, "Failed to write 268 MB .ZIP archive.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    LaunchedEffect(previewingSong) {
        val song = previewingSong
        if (song != null) {
            MarioMadnessAudioEngine.startStageMusic(
                scope = this,
                songTitle = song.title,
                bpm = song.bpm,
                getAct = { 1 },
                isAudioEnabled = { true }
            )
        } else {
            MarioMadnessAudioEngine.stopStageMusic()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            MarioMadnessAudioEngine.stopStageMusic()
        }
    }

    val mmv2Worlds = remember {
        listOf(
            Mmv2WorldInfo(
                1,
                "WORLD 1: MUSHROOM KINGDOM",
                "Horror Mario & Turmoil • Overworld",
                FnfRed,
                listOf(
                    SongItem("It's-A-Me (V2)", 160, "2:50", "Hard", "Horror Mario"),
                    SongItem("Starman Slaughter", 175, "3:15", "Insane", "Horror Mario, John Dick & Turmoil")
                )
            ),
            Mmv2WorldInfo(
                2,
                "WORLD 2: IRREGULARITY ISLE",
                "Chris Pratt, 7 Grand Dad & Somari",
                FnfOrange,
                listOf(
                    SongItem("So Cool", 145, "2:18", "Normal", "Chris Pratt Mario"),
                    SongItem("Nourishing Blood", 168, "2:35", "Hard", "7 Grand Dad"),
                    SongItem("MARIO SING AND GAME RYTHM 9", 176, "2:42", "Hard", "Somari")
                )
            ),
            Mmv2WorldInfo(
                3,
                "WORLD 3: WOODLAND OF LIES",
                "Beta Luigi, Burned IHY Luigi, Wario & Peach",
                FnfGreen,
                listOf(
                    SongItem("Alone", 140, "2:48", "Normal", "Beta Luigi"),
                    SongItem("Oh God No", 170, "2:52", "Hard", "IHY Mario & Luigi"),
                    SongItem("I Hate You", 182, "2:55", "Hard", "Burned Luigi"),
                    SongItem("Thalassophobia", 152, "3:04", "Hard", "Drowned Luigi 2401"),
                    SongItem("Apparition", 174, "2:46", "Hard", "Wario Apparition"),
                    SongItem("Last Course", 180, "2:50", "Insane", "Turmoil"),
                    SongItem("Dark Forest", 186, "3:02", "Insane", "Coronation Day Peach")
                )
            ),
            Mmv2WorldInfo(
                4,
                "WORLD 4: CONTENT COSMOS",
                "Bad Mario, Secret History & Devil Mario",
                FnfPink,
                listOf(
                    SongItem("Bad Day", 150, "2:20", "Normal", "Bad Mario"),
                    SongItem("Day Out", 164, "2:38", "Hard", "Day Out Mario & Luigi"),
                    SongItem("Dictator", 184, "2:54", "Hard", "Secret History Mario"),
                    SongItem("Race-Traitors", 192, "2:44", "Insane", "Racist Mario"),
                    SongItem("No Hope", 198, "3:10", "Mania", "Devil Mario")
                )
            ),
            Mmv2WorldInfo(
                5,
                "WORLD 5: HELLISH HEIGHTS",
                "GB, DJ Hallyboo, Mr. Virtual, Mr. L & MX",
                FnfPurple,
                listOf(
                    SongItem("Golden Land", 172, "2:42", "Hard", "GB Mario Land 2"),
                    SongItem("No Party", 168, "2:45", "Hard", "DJ Hallyboo"),
                    SongItem("Paranoia", 195, "3:08", "Insane", "Mr. Virtual"),
                    SongItem("Overdue", 180, "3:05", "Hard", "Mr. L vs Pico"),
                    SongItem("Powerdown", 188, "3:02", "Hard", "MX False Hero"),
                    SongItem("Demise", 205, "2:58", "Mania", "MX Underground Chase")
                )
            ),
            Mmv2WorldInfo(
                6,
                "WORLD 6: CLASSIFIED CASTLE",
                "Stanley, Classified Luigi, Costume & Mr. Sys",
                FnfCyan,
                listOf(
                    SongItem("Promotion", 166, "2:48", "Hard", "Stanley SM64 Classified"),
                    SongItem("Abandoned", 178, "2:56", "Insane", "Classified Luigi"),
                    SongItem("The End", 185, "3:04", "Insane", "Costume SM64"),
                    SongItem("Unbeatable", 195, "4:12", "Mania", "Mr. Sys, Duck Hunt & Bowser")
                )
            ),
            Mmv2WorldInfo(
                7,
                "FINALE: ULTRA M'S CITADEL (3 ENDINGS)",
                "All-Stars (Acts 1–4) & Secret Exit (Acts 1–5)",
                FnfYellow,
                listOf(
                    SongItem("Secret Exit (Acts 1-5)", 200, "9:15", "Mania", "Ultra M, GX & Starman BF (3 Endings)"),
                    SongItem("All-Stars (Acts 1-4)", 190, "8:40", "Mania", "Ultra M, Omega, GX & Mimic")
                )
            )
        )
    }

    val activeWorld = mmv2Worlds[selectedWorldIndex]

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(FnfDarkBg),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // 1. MARIO'S MADNESS V2 (#359554) MASTERPIECE HERO BANNER
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(255.dp)
                    .drawWithContent {
                        drawContent()
                        var y = 0f
                        while (y < size.height) {
                            drawLine(
                                color = Color.Black.copy(alpha = 0.28f),
                                start = Offset(0f, y),
                                end = Offset(size.width, y),
                                strokeWidth = 2f
                            )
                            y += 6f
                        }
                    }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_mmv2_stage_ultram_1790591016141),
                    contentDescription = "Mario's Madness V2 Masterpiece Citadel",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.25f),
                                    FnfDarkBg.copy(alpha = 0.72f),
                                    FnfDarkBg
                                )
                            )
                        )
                )

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
                            color = FnfRed
                        ) {
                            Text(
                                text = "🍄 GAMEBANANA MOD #359554 • 268 MB MASTERPIECE",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = FnfYellow.copy(alpha = 0.25f),
                            border = BorderStroke(1.dp, FnfYellow)
                        ) {
                            Text(
                                text = "3 PLAYABLE ENDINGS",
                                color = FnfYellow,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "MARIO'S MADNESS V2: SECRET EXIT",
                        color = FnfTextPrimary,
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )

                    Text(
                        text = "All 7 Worlds • 29 Original Songs • Ultra M Citadel Stage • 3 Branching Endings (Bad, Escape & Secret Exit)",
                        color = FnfTextSecondary,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Primary 1-Tap Masterpiece Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                previewingSong = null
                                onLaunchPsychClick(featuredMod.mod.id)
                            },
                            modifier = Modifier
                                .weight(1.2f)
                                .testTag("featured_play_073_btn"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = FnfRed,
                                contentColor = Color.White
                            )
                        ) {
                            Icon(Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "PLAY STAGE & 3 ENDINGS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Button(
                            onClick = {
                                saveMasterpieceZipLauncher.launch("marios-madness-v2-359554-masterpiece-268mb.zip")
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("hero_download_268mb_zip_btn"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = FnfGreen,
                                contentColor = FnfDarkBg
                            )
                        ) {
                            Icon(Icons.Filled.Download, contentDescription = null, modifier = Modifier.size(17.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "GET 268 MB .ZIP",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }
        }

        // 2. LIVE 268 MB MASTERPIECE ZIP PACKER PROGRESS CARD (Visible during or ready for download)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = FnfSurfaceElevated),
                border = BorderStroke(1.5.dp, if (isPacking268MbZip) FnfYellow else FnfGreen)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isPacking268MbZip) {
                                    "PACKING 268 MB MASTERPIECE .ZIP... ($packedMbProgress MB / $totalMbTarget MB)"
                                } else {
                                    "📦 FULL 268 MB MOD ARCHIVE (GAMEBANANA #359554 + 3 ENDINGS)"
                                },
                                color = if (isPacking268MbZip) FnfYellow else FnfGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isPacking268MbZip) {
                                    "Streaming: $currentPackedFile"
                                } else {
                                    "Includes 29 Songs (Inst.ogg & Voices.ogg), 7 Boss Atlases, Stage PNGs & 3 Ending Cutscenes"
                                },
                                color = FnfTextSecondary,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        if (!isPacking268MbZip) {
                            OutlinedButton(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://gamebanana.com/mods/359554"))
                                    context.startActivity(intent)
                                },
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, FnfCyan),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = FnfCyan)
                            ) {
                                Text("#359554 WEB", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    if (isPacking268MbZip) {
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { (packedMbProgress.toFloat() / totalMbTarget.coerceAtLeast(1)).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp)),
                            color = FnfYellow,
                            trackColor = FnfDarkBg
                        )
                    }
                }
            }
        }

        // 3. INTERACTIVE 3 BRANCHING ENDINGS SHOWCASE STRIP
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Movie,
                            contentDescription = null,
                            tint = FnfYellow,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "3 PLAYABLE ENDINGS (TAP ANY ENDING TO LAUNCH)",
                            color = FnfYellow,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val homeEndings = listOf(
                        Triple(
                            "ENDING 1: CANON BAD ENDING",
                            "All-Stars: \"See You Next Time\"",
                            R.drawable.img_mmv2_ending_bad_1790591028072 to FnfRed
                        ),
                        Triple(
                            "ENDING 2: WARP PIPE ESCAPE",
                            "Overdue: \"Shattered CRT TV\"",
                            R.drawable.mmv2_ending_escape_1790592490896 to FnfGreen
                        ),
                        Triple(
                            "ENDING 3: SECRET EXIT ★",
                            "True Ending: \"Golden Starman\"",
                            R.drawable.img_mmv2_ending_true_1790591037564 to FnfYellow
                        )
                    )

                    homeEndings.forEach { (badge, sub, imgAndCol) ->
                        val (resId, col) = imgAndCol
                        Card(
                            modifier = Modifier
                                .width(235.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable {
                                    previewingSong = null
                                    onLaunchPsychClick(featuredMod.mod.id)
                                },
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = FnfSurface),
                            border = BorderStroke(1.5.dp, col)
                        ) {
                            Column {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(105.dp)
                                ) {
                                    Image(
                                        painter = painterResource(id = resId),
                                        contentDescription = badge,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                Brush.verticalGradient(
                                                    colors = listOf(
                                                        Color.Transparent,
                                                        Color.Black.copy(alpha = 0.8f)
                                                    )
                                                )
                                            )
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = col,
                                        modifier = Modifier
                                            .align(Alignment.TopStart)
                                            .padding(8.dp)
                                    ) {
                                        Text(
                                            text = badge,
                                            color = Color.Black,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Text(
                                        text = sub,
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .align(Alignment.BottomStart)
                                            .padding(8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. INTERACTIVE 7-WORLD OVERWORLD MAP & LIVE 29-SONG PLAYER (Copy-Paste of #359554 Worlds)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = FnfSurface),
                border = BorderStroke(1.5.dp, activeWorld.accentColor)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "🗺️ MARIO'S MADNESS V2 (#359554) 7-WORLD OVERWORLD MAP",
                        color = FnfCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 7 Worlds Selector Pills
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        mmv2Worlds.forEachIndexed { idx, world ->
                            val isSelected = idx == selectedWorldIndex
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) world.accentColor.copy(alpha = 0.25f) else FnfSurfaceElevated,
                                border = BorderStroke(1.dp, if (isSelected) world.accentColor else FnfBorder),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { selectedWorldIndex = idx }
                            ) {
                                Text(
                                    text = if (world.worldNum == 7) "👑 FINALE" else "WORLD ${world.worldNum}",
                                    color = if (isSelected) world.accentColor else FnfTextSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = activeWorld.name,
                        color = activeWorld.accentColor,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = activeWorld.subtitle,
                        color = FnfTextSecondary,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Songs in the selected World with Live Audio Synth Preview + Play Stage Button
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        activeWorld.songs.forEach { song ->
                            val isPlayingThis = previewingSong?.title == song.title
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isPlayingThis) activeWorld.accentColor.copy(alpha = 0.16f) else FnfSurfaceElevated,
                                border = BorderStroke(1.dp, if (isPlayingThis) activeWorld.accentColor else FnfBorder)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "🎵 ${song.title}",
                                            color = FnfTextPrimary,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "vs ${song.opponent} • ${song.bpm} BPM • ${song.duration}",
                                            color = FnfTextSecondary,
                                            fontSize = 11.sp
                                        )
                                    }

                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        IconButton(
                                            onClick = {
                                                previewingSong = if (isPlayingThis) null else song
                                            },
                                            modifier = Modifier
                                                .size(32.dp)
                                                .background(
                                                    if (isPlayingThis) FnfCyan.copy(alpha = 0.25f) else FnfSurface,
                                                    CircleShape
                                                )
                                        ) {
                                            Icon(
                                                imageVector = if (isPlayingThis) Icons.Filled.GraphicEq else Icons.Filled.PlayArrow,
                                                contentDescription = "Listen",
                                                tint = if (isPlayingThis) FnfCyan else FnfTextSecondary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = activeWorld.accentColor,
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .clickable {
                                                    previewingSong = null
                                                    onLaunchPsychClick(featuredMod.mod.id)
                                                }
                                        ) {
                                            Text(
                                                text = "PLAY",
                                                color = Color.Black,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Black,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
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
                    contentColor = Color.Black
                )
            ) {
                Icon(Icons.Filled.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "CUSTOM MOD MAKER & UPLOADER (PSYCH 0.7.3)",
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
                    .padding(horizontal = 16.dp, vertical = 6.dp),
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

        // Search & Filter Section
        item {
            Spacer(modifier = Modifier.height(4.dp))
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
            Spacer(modifier = Modifier.height(10.dp))
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
                    text = "ALL MOD PACKS (${mods.size})",
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
            items(mods, key = { it.mod.id }) { item ->
                ModCard(
                    detail = item,
                    onModClick = { onModClick(item.mod.id) },
                    onFavoriteClick = { onFavoriteClick(item.mod.id) },
                    onDownloadClick = { onDownloadClick(item.mod.id) },
                    onLaunchPsychClick = {
                        previewingSong = null
                        onLaunchPsychClick(item.mod.id)
                    },
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
