package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.FnfBorder
import com.example.ui.theme.FnfCyan
import com.example.ui.theme.FnfDarkBg
import com.example.ui.theme.FnfGreen
import com.example.ui.theme.FnfPink
import com.example.ui.theme.FnfPurple
import com.example.ui.theme.FnfRed
import com.example.ui.theme.FnfSurface
import com.example.ui.theme.FnfSurfaceElevated
import com.example.ui.theme.FnfTextMuted
import com.example.ui.theme.FnfTextPrimary
import com.example.ui.theme.FnfTextSecondary
import com.example.ui.theme.FnfYellow

@Composable
fun AddModDialog(
    onDismiss: () -> Unit,
    onAdd: (
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
        colorHex: Long
    ) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var subtitle by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var version by remember { mutableStateOf("v1.0") }
    var engine by remember { mutableStateOf("Psych Engine 0.7.3") }
    var downloadSize by remember { mutableStateOf("350 MB") }
    var category by remember { mutableStateOf("Full Week") }
    var difficulty by remember { mutableStateOf("Hard") }
    var downloadUrl by remember { mutableStateOf("https://gamebanana.com") }
    var description by remember { mutableStateOf("") }
    var tagsInput by remember { mutableStateOf("Full Week, Custom Songs") }
    var selectedColor by remember { mutableLongStateOf(0xFF00E5FF) }

    val categoryOptions = listOf("Full Week", "Overhaul", "Creepypasta", "Crossover", "Classic Legends", "Android Port")
    val difficultyOptions = listOf("Casual", "Medium", "Hard", "Expert", "Insane")
    val colorPalette = listOf(0xFF00E5FF, 0xFFFF2A85, 0xFFA855F7, 0xFF00E676, 0xFFFF9100, 0xFFFF3366, 0xFFFFD600)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = FnfSurface,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, FnfPink),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "ADD CUSTOM FNF MOD",
                    color = FnfPink,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "Track any mod from GameBanana or GameJolt",
                    color = FnfTextSecondary,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Title
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Mod Title *", color = FnfTextMuted) },
                    placeholder = { Text("e.g. VS Garcello HD", color = FnfTextMuted) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("custom_mod_title"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FnfCyan,
                        unfocusedBorderColor = FnfBorder,
                        focusedTextColor = FnfTextPrimary,
                        unfocusedTextColor = FnfTextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Subtitle
                OutlinedTextField(
                    value = subtitle,
                    onValueChange = { subtitle = it },
                    label = { Text("Tagline / Short description", color = FnfTextMuted) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FnfCyan,
                        unfocusedBorderColor = FnfBorder,
                        focusedTextColor = FnfTextPrimary,
                        unfocusedTextColor = FnfTextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Author & Version Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = author,
                        onValueChange = { author = it },
                        label = { Text("Author", color = FnfTextMuted) },
                        singleLine = true,
                        modifier = Modifier.weight(1.5f),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FnfCyan,
                            unfocusedBorderColor = FnfBorder,
                            focusedTextColor = FnfTextPrimary,
                            unfocusedTextColor = FnfTextPrimary
                        )
                    )
                    OutlinedTextField(
                        value = version,
                        onValueChange = { version = it },
                        label = { Text("Version", color = FnfTextMuted) },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FnfCyan,
                            unfocusedBorderColor = FnfBorder,
                            focusedTextColor = FnfTextPrimary,
                            unfocusedTextColor = FnfTextPrimary
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Engine & Size Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = engine,
                        onValueChange = { engine = it },
                        label = { Text("Engine", color = FnfTextMuted) },
                        singleLine = true,
                        modifier = Modifier.weight(1.5f),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FnfCyan,
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
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FnfCyan,
                            unfocusedBorderColor = FnfBorder,
                            focusedTextColor = FnfTextPrimary,
                            unfocusedTextColor = FnfTextPrimary
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Download URL
                OutlinedTextField(
                    value = downloadUrl,
                    onValueChange = { downloadUrl = it },
                    label = { Text("Download Link / Page URL", color = FnfTextMuted) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FnfCyan,
                        unfocusedBorderColor = FnfBorder,
                        focusedTextColor = FnfTextPrimary,
                        unfocusedTextColor = FnfTextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Category chips selector
                Text("Category:", color = FnfTextSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categoryOptions.forEach { cat ->
                        val isSelected = category == cat
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) FnfCyan.copy(alpha = 0.2f) else FnfSurfaceElevated,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) FnfCyan else FnfBorder),
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { category = cat }
                        ) {
                            Text(
                                text = cat,
                                color = if (isSelected) FnfCyan else FnfTextSecondary,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Difficulty selector
                Text("Difficulty:", color = FnfTextSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    difficultyOptions.forEach { diff ->
                        val isSelected = difficulty == diff
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) FnfPink.copy(alpha = 0.2f) else FnfSurfaceElevated,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) FnfPink else FnfBorder),
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { difficulty = diff }
                        ) {
                            Text(
                                text = diff,
                                color = if (isSelected) FnfPink else FnfTextSecondary,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Color accent picker
                Text("Accent Color:", color = FnfTextSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    colorPalette.forEach { hex ->
                        val isSelected = selectedColor == hex
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(hex))
                                .clickable { selectedColor = hex }
                                .padding(if (isSelected) 3.dp else 0.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description & Mod Lore", color = FnfTextMuted) },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FnfCyan,
                        unfocusedBorderColor = FnfBorder,
                        focusedTextColor = FnfTextPrimary,
                        unfocusedTextColor = FnfTextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = FnfTextSecondary)
                    ) {
                        Text("CANCEL")
                    }

                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                val tags = tagsInput.split(",").map { it.trim() }.filter { it.isNotBlank() }
                                onAdd(
                                    title,
                                    subtitle.ifBlank { "Custom tracked rhythm mod" },
                                    author.ifBlank { "Community Modder" },
                                    version,
                                    engine,
                                    downloadSize,
                                    category,
                                    difficulty,
                                    downloadUrl,
                                    description.ifBlank { "Added by user to collection." },
                                    "Community tracked mod.",
                                    tags,
                                    selectedColor
                                )
                                onDismiss()
                            }
                        },
                        enabled = title.isNotBlank(),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("submit_custom_mod_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FnfPink, contentColor = Color.White)
                    ) {
                        Text("ADD TO LIST", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
