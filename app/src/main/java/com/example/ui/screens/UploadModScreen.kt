package com.example.ui.screens

import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FullModDetail
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

data class StablePsychVersionSpec(
    val versionTag: String,
    val badgeLabel: String,
    val luaApiInfo: String,
    val stabilityNotes: String,
    val apkPackageId: String,
    val color: Color
)

data class DraftUploadedSong(
    val title: String,
    val bpm: Int,
    val difficulty: String,
    val opponent: String
)

val STABLE_PSYCH_VERSIONS = listOf(
    StablePsychVersionSpec(
        versionTag = "Psych Engine 0.7.3",
        badgeLabel = "v0.7.3 STABLE",
        luaApiInfo = "HScript 2.6 • Lua 5.1 • New Shader Callbacks",
        stabilityNotes = "Best for modern 2024+ mods using 0.7.x Lua callbacks & callOnHScript.",
        apkPackageId = "com.shadowmario.psychengine.v073",
        color = Color(0xFFFF1E38)
    ),
    StablePsychVersionSpec(
        versionTag = "Psych Engine 0.7.1h (MMv2)",
        badgeLabel = "v0.7.1h MMv2",
        luaApiInfo = "Mario's Madness V2 Custom Overworld & CRT Shaders",
        stabilityNotes = "Locked stable engine for Mario's Madness V2 acts, TV static shaders & Ultra M mechanics.",
        apkPackageId = "com.marcoantonio.mariosmadness.v2",
        color = Color(0xFFFFB800)
    ),
    StablePsychVersionSpec(
        versionTag = "Psych Engine 0.6.3",
        badgeLabel = "v0.6.3 LEGACY STABLE",
        luaApiInfo = "Classic Lua API • Zero 0.7 Deprecation Crashes",
        stabilityNotes = "Required for 2022–2023 mods (Sonic.EXE, Hypno's Lullaby, Impostor V4) so scripts never break.",
        apkPackageId = "com.shadowmario.psychengine.v063",
        color = Color(0xFF00E676)
    ),
    StablePsychVersionSpec(
        versionTag = "Psych Engine 0.5.2h",
        badgeLabel = "v0.5.2h CLASSIC",
        luaApiInfo = "Early Psych Lua • Indie Cross & Classic Weeks",
        stabilityNotes = "Maximum stability for early Psych mods that rely on legacy note offset tables.",
        apkPackageId = "com.shadowmario.psychengine.v052h",
        color = Color(0xFFB829FF)
    ),
    StablePsychVersionSpec(
        versionTag = "Psych Engine 1.0",
        badgeLabel = "v1.0 NEXT-GEN",
        luaApiInfo = "Psych 1.0 Psych-Slice Bridge • 60FPS Mobile",
        stabilityNotes = "For newest experimental mods built on Psych Engine 1.0.",
        apkPackageId = "com.shadowmario.psychengine.v100",
        color = Color(0xFFFF6D00)
    )
)

@Composable
fun UploadModScreen(
    uploadedMods: List<FullModDetail>,
    onPublishMod: (
        title: String,
        subtitle: String,
        author: String,
        version: String,
        engine: String,
        downloadSize: String,
        category: String,
        difficulty: String,
        downloadUrl: String,
        description: String,
        lore: String,
        tags: List<String>,
        colorHex: Long,
        customSongsEncoded: String
    ) -> Unit,
    onDeleteUploadedMod: (String) -> Unit,
    onLaunchModInStableEngine: (String) -> Unit,
    onNavigateToBrowse: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    var subtitle by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var modVersion by remember { mutableStateOf("v1.0") }
    var selectedPsychSpec by remember { mutableStateOf(STABLE_PSYCH_VERSIONS[0]) }
    var downloadSize by remember { mutableStateOf("420 MB") }
    var category by remember { mutableStateOf("Overhaul") }
    var difficulty by remember { mutableStateOf("Expert") }
    var downloadUrl by remember { mutableStateOf("https://gamebanana.com/mods/") }
    var description by remember { mutableStateOf("") }
    var pickedFileName by remember { mutableStateOf<String?>(null) }
    var selectedColorHex by remember { mutableLongStateOf(0xFFFF1E38L) }

    // Custom songs builder for the uploaded mod
    val draftSongs = remember {
        mutableStateListOf(
            DraftUploadedSong("It's-A-Me (Remix)", 165, "Hard", "Horror Mario"),
            DraftUploadedSong("Starman Slaughter", 180, "Insane", "Ultra M")
        )
    }
    var newSongTitle by remember { mutableStateOf("") }
    var newSongBpm by remember { mutableStateOf("170") }
    var newSongOpponent by remember { mutableStateOf("Ultra M") }

    // Real Android File Picker for .zip / .apk / mod packs
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            var displayName = "uploaded_mod_pack.zip"
            var sizeBytes = 0L
            try {
                context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val nameIdx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        val sizeIdx = cursor.getColumnIndex(OpenableColumns.SIZE)
                        if (nameIdx >= 0) displayName = cursor.getString(nameIdx) ?: displayName
                        if (sizeIdx >= 0) sizeBytes = cursor.getLong(sizeIdx)
                    }
                }
            } catch (_: Exception) {}

            pickedFileName = displayName
            if (sizeBytes > 0L) {
                val mb = (sizeBytes / (1024.0 * 1024.0)).coerceAtLeast(1.0)
                downloadSize = String.format("%.1f MB", mb)
            }
            if (title.isBlank()) {
                title = displayName.substringBeforeLast(".").replace("-", " ").replace("_", " ")
            }
            downloadUrl = uri.toString()
        }
    }

    val categories = listOf("Overhaul", "Full Week", "Creepypasta", "Crossover", "Classic Legends", "Android Port")
    val difficulties = listOf("Casual", "Medium", "Hard", "Expert", "Insane")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(FnfDarkBg),
        contentPadding = PaddingValues(bottom = 110.dp)
    ) {
        // Mario's Madness V2 CRT Cartridge Forge Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                FnfRed.copy(alpha = 0.35f),
                                Color(0xFF1A0408),
                                FnfDarkBg
                            )
                        )
                    )
                    .drawWithContent {
                        drawContent()
                        // Retro NES CRT Scanlines
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
                    .padding(horizontal = 16.dp, vertical = 18.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = FnfRed
                        ) {
                            Text(
                                text = "🍄 ULTRA M'S CARTRIDGE FORGE",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
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
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "UPLOAD & PUBLISH FNF MODS",
                        color = FnfTextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.8.sp
                    )
                    Text(
                        text = "Upload your mod package (.ZIP / .APK) and lock it to the exact stable Psych Engine version so anyone can download and play without script crashes.",
                        color = FnfTextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        // STEP 1: Pick Mod File (.ZIP / .APK) or Mirror URL
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = FnfSurface),
                border = BorderStroke(1.5.dp, FnfRed)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CloudUpload,
                            contentDescription = null,
                            tint = FnfRed,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = "STEP 1 • SELECT MOD PACKAGE (.ZIP / .APK)",
                            color = FnfRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { filePickerLauncher.launch("*/*") },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = FnfRed,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("pick_mod_file_btn")
                        ) {
                            Icon(Icons.Filled.FolderOpen, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("CHOOSE MOD FILE (.ZIP / .APK)", fontSize = 11.sp, fontWeight = FontWeight.Black)
                        }
                    }

                    if (pickedFileName != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = FnfGreen.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, FnfGreen),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = FnfGreen, modifier = Modifier.size(18.dp))
                                Column {
                                    Text(
                                        text = "Attached Cartridge: $pickedFileName",
                                        color = FnfGreen,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Detected Size: $downloadSize • Ready for Psych Engine packaging",
                                        color = FnfTextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // STEP 2: Lock Stable Psych Engine Version
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = FnfSurface),
                border = BorderStroke(1.5.dp, FnfYellow)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Memory,
                            contentDescription = null,
                            tint = FnfYellow,
                            modifier = Modifier.size(22.dp)
                        )
                        Column {
                            Text(
                                text = "STEP 2 • LOCK STABLE PSYCH ENGINE VERSION",
                                color = FnfYellow,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "Players who download your mod will automatically launch it in this exact Psych Engine build.",
                                color = FnfTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        STABLE_PSYCH_VERSIONS.forEach { spec ->
                            val isSelected = selectedPsychSpec.versionTag == spec.versionTag
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) spec.color.copy(alpha = 0.18f) else FnfSurfaceElevated,
                                border = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) spec.color else FnfBorder),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { selectedPsychSpec = spec }
                                    .testTag("select_engine_${spec.badgeLabel}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                text = spec.versionTag,
                                                color = if (isSelected) spec.color else FnfTextPrimary,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Black
                                            )
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = spec.color.copy(alpha = 0.25f)
                                            ) {
                                                Text(
                                                    text = spec.badgeLabel,
                                                    color = spec.color,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Black,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = spec.luaApiInfo,
                                            color = FnfTextPrimary,
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                        Text(
                                            text = spec.stabilityNotes,
                                            color = FnfTextSecondary,
                                            fontSize = 11.sp,
                                            lineHeight = 15.sp
                                        )
                                    }

                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Filled.Verified,
                                            contentDescription = "Selected Stable Version",
                                            tint = spec.color,
                                            modifier = Modifier
                                                .padding(start = 8.dp)
                                                .size(24.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // STEP 3: Mod Cartridge Metadata & Tracklist
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = FnfSurface),
                border = BorderStroke(1.2.dp, FnfBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "STEP 3 • CARTRIDGE DETAILS & SONGS",
                        color = FnfCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Mod Title *", color = FnfTextMuted) },
                        placeholder = { Text("e.g. Mario's Madness: Secret Warp Week", color = FnfTextMuted) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("upload_mod_title_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FnfRed,
                            unfocusedBorderColor = FnfBorder,
                            focusedTextColor = FnfTextPrimary,
                            unfocusedTextColor = FnfTextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = subtitle,
                        onValueChange = { subtitle = it },
                        label = { Text("Tagline / Subtitle", color = FnfTextMuted) },
                        placeholder = { Text("e.g. 4 new corrupted NES tracks built for ${selectedPsychSpec.versionTag}", color = FnfTextMuted) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FnfRed,
                            unfocusedBorderColor = FnfBorder,
                            focusedTextColor = FnfTextPrimary,
                            unfocusedTextColor = FnfTextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = author,
                            onValueChange = { author = it },
                            label = { Text("Creator / Team", color = FnfTextMuted) },
                            placeholder = { Text("Your Name", color = FnfTextMuted) },
                            singleLine = true,
                            modifier = Modifier.weight(1.4f),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = FnfRed,
                                unfocusedBorderColor = FnfBorder,
                                focusedTextColor = FnfTextPrimary,
                                unfocusedTextColor = FnfTextPrimary
                            )
                        )
                        OutlinedTextField(
                            value = modVersion,
                            onValueChange = { modVersion = it },
                            label = { Text("Mod Ver", color = FnfTextMuted) },
                            singleLine = true,
                            modifier = Modifier.weight(0.8f),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = FnfRed,
                                unfocusedBorderColor = FnfBorder,
                                focusedTextColor = FnfTextPrimary,
                                unfocusedTextColor = FnfTextPrimary
                            )
                        )
                        OutlinedTextField(
                            value = downloadSize,
                            onValueChange = { downloadSize = it },
                            label = { Text("Size", color = FnfTextMuted) },
                            singleLine = true,
                            modifier = Modifier.weight(0.9f),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = FnfRed,
                                unfocusedBorderColor = FnfBorder,
                                focusedTextColor = FnfTextPrimary,
                                unfocusedTextColor = FnfTextPrimary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Category & Difficulty chips
                    Text("World Category:", color = FnfTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        categories.forEach { cat ->
                            val isSel = category == cat
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) FnfRed.copy(alpha = 0.25f) else FnfSurfaceElevated,
                                border = BorderStroke(1.dp, if (isSel) FnfRed else FnfBorder),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { category = cat }
                            ) {
                                Text(
                                    text = cat,
                                    color = if (isSel) FnfRed else FnfTextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Song Chart Builder
                    Text(
                        text = "INCLUDED SONGS IN CARTRIDGE (${draftSongs.size}):",
                        color = FnfYellow,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    draftSongs.forEachIndexed { idx, song ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = FnfSurfaceElevated,
                            border = BorderStroke(1.dp, FnfBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Filled.MusicNote, contentDescription = null, tint = FnfRed, modifier = Modifier.size(16.dp))
                                    Column {
                                        Text(
                                            text = "${idx + 1}. ${song.title}",
                                            color = FnfTextPrimary,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "vs ${song.opponent} • ${song.bpm} BPM • ${song.difficulty}",
                                            color = FnfTextSecondary,
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                                if (draftSongs.size > 1) {
                                    IconButton(
                                        onClick = { draftSongs.removeAt(idx) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Filled.Delete, contentDescription = "Remove Song", tint = FnfTextMuted, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Add Song Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newSongTitle,
                            onValueChange = { newSongTitle = it },
                            placeholder = { Text("Song Name", color = FnfTextMuted, fontSize = 11.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1.3f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = newSongBpm,
                            onValueChange = { if (it.all { c -> c.isDigit() }) newSongBpm = it },
                            placeholder = { Text("BPM", color = FnfTextMuted, fontSize = 11.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(0.6f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = newSongOpponent,
                            onValueChange = { newSongOpponent = it },
                            placeholder = { Text("Opponent", color = FnfTextMuted, fontSize = 11.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(0.9f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        IconButton(
                            onClick = {
                                if (newSongTitle.isNotBlank()) {
                                    draftSongs.add(
                                        DraftUploadedSong(
                                            title = newSongTitle.trim(),
                                            bpm = newSongBpm.toIntOrNull() ?: 160,
                                            difficulty = difficulty,
                                            opponent = newSongOpponent.ifBlank { "Ultra M" }
                                        )
                                    )
                                    newSongTitle = ""
                                }
                            },
                            modifier = Modifier
                                .size(42.dp)
                                .background(FnfRed, RoundedCornerShape(10.dp))
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = "Add Song", tint = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Mod Description & Stable Engine Instructions", color = FnfTextMuted) },
                        placeholder = { Text("Explain custom mechanics, shaders, and why ${selectedPsychSpec.versionTag} is used...", color = FnfTextMuted, fontSize = 12.sp) },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FnfRed,
                            unfocusedBorderColor = FnfBorder,
                            focusedTextColor = FnfTextPrimary,
                            unfocusedTextColor = FnfTextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val finalTitle = title.ifBlank { "Mario's Madness: Custom Act" }
                            val encodedSongs = draftSongs.joinToString("|") {
                                "${it.title}~${it.bpm}~${it.difficulty}~${it.opponent}"
                            }
                            onPublishMod(
                                finalTitle,
                                subtitle.ifBlank { "Verified Stable for ${selectedPsychSpec.versionTag}" },
                                author.ifBlank { "Community Creator" },
                                modVersion.ifBlank { "v1.0" },
                                selectedPsychSpec.versionTag,
                                downloadSize.ifBlank { "420 MB" },
                                category,
                                difficulty,
                                downloadUrl,
                                description.ifBlank {
                                    "Uploaded community mod locked to ${selectedPsychSpec.versionTag} (${selectedPsychSpec.luaApiInfo}) for 100% crash-free gameplay."
                                },
                                "Target APK: ${selectedPsychSpec.apkPackageId} • ${selectedPsychSpec.stabilityNotes}",
                                listOf(selectedPsychSpec.badgeLabel, "Community Upload", category),
                                selectedColorHex,
                                encodedSongs
                            )
                            Toast.makeText(
                                context,
                                "Published '$finalTitle' locked to ${selectedPsychSpec.versionTag}!",
                                Toast.LENGTH_SHORT
                            ).show()
                            title = ""
                            subtitle = ""
                            description = ""
                            pickedFileName = null
                            onNavigateToBrowse()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("publish_mod_cartridge_btn"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FnfRed,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(Icons.Filled.CloudUpload, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "PUBLISH MOD FOR ${selectedPsychSpec.badgeLabel}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }

        // Published User Cartridges Section
        if (uploadedMods.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "YOUR PUBLISHED MOD CARTRIDGES (${uploadedMods.size})",
                    color = FnfYellow,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            items(uploadedMods, key = { it.mod.id }) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = FnfSurfaceElevated),
                    border = BorderStroke(1.5.dp, FnfRed)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.mod.title,
                                    color = FnfTextPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "Stable Engine: ${item.mod.engine} • ${item.mod.downloadSize}",
                                    color = FnfYellow,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            IconButton(onClick = { onDeleteUploadedMod(item.mod.id) }) {
                                Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = FnfTextMuted)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = { onLaunchModInStableEngine(item.mod.id) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = FnfGreen, contentColor = FnfDarkBg)
                        ) {
                            Icon(Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "PLAY IN ${item.mod.engine.uppercase()} APK",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }
        }
    }
}
