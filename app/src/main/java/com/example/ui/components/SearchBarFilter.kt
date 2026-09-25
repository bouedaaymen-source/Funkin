package com.example.ui.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.FnfBorder
import com.example.ui.theme.FnfCyan
import com.example.ui.theme.FnfPink
import com.example.ui.theme.FnfSurface
import com.example.ui.theme.FnfSurfaceElevated
import com.example.ui.theme.FnfTextMuted
import com.example.ui.theme.FnfTextPrimary
import com.example.ui.theme.FnfTextSecondary
import com.example.ui.viewmodel.SortOption

@Composable
fun SearchBarFilter(
    query: String,
    onQueryChange: (String) -> Unit,
    selectedCategory: String,
    onCategorySelect: (String) -> Unit,
    selectedEngine: String,
    onEngineSelect: (String) -> Unit,
    selectedSort: SortOption,
    onSortSelect: (SortOption) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    var showSortMenu by remember { mutableStateOf(false) }

    val categories = listOf("All", "Overhaul", "Full Week", "Creepypasta", "Crossover", "Classic Legends", "Android Port")
    val engines = listOf("All Engines", "Psych Engine", "Codename Engine", "Kade", "V-Slice")

    Column(modifier = modifier.fillMaxWidth()) {
        // Search Input Row + Sort Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .weight(1f)
                    .testTag("mod_search_input"),
                placeholder = {
                    Text(
                        text = "Search mods, songs, authors...",
                        color = FnfTextMuted,
                        fontSize = 14.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search",
                        tint = FnfCyan
                    )
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { onQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Filled.Clear,
                                contentDescription = "Clear search",
                                tint = FnfTextMuted
                            )
                        }
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FnfCyan,
                    unfocusedBorderColor = FnfBorder,
                    focusedContainerColor = FnfSurface,
                    unfocusedContainerColor = FnfSurface,
                    cursorColor = FnfCyan,
                    focusedTextColor = FnfTextPrimary,
                    unfocusedTextColor = FnfTextPrimary
                )
            )

            // Sort button
            Box {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = FnfSurface,
                    border = BorderStroke(1.dp, if (showSortMenu) FnfPink else FnfBorder),
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { showSortMenu = true }
                        .padding(horizontal = 12.dp, vertical = 14.dp)
                        .testTag("sort_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.FilterList,
                            contentDescription = "Sort Options",
                            tint = FnfPink,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                DropdownMenu(
                    expanded = showSortMenu,
                    onDismissRequest = { showSortMenu = false },
                    modifier = Modifier.background(FnfSurfaceElevated)
                ) {
                    SortOption.entries.forEach { option ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = option.displayName,
                                    color = if (selectedSort == option) FnfCyan else FnfTextPrimary,
                                    fontWeight = if (selectedSort == option) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            onClick = {
                                onSortSelect(option)
                                showSortMenu = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Categories Horizontal Scroll Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { cat ->
                val isSelected = selectedCategory.equals(cat, ignoreCase = true)
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) FnfCyan.copy(alpha = 0.2f) else FnfSurface,
                    border = BorderStroke(
                        1.dp,
                        if (isSelected) FnfCyan else FnfBorder
                    ),
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { onCategorySelect(cat) }
                        .testTag("cat_chip_$cat")
                ) {
                    Text(
                        text = cat,
                        color = if (isSelected) FnfCyan else FnfTextSecondary,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Engines Horizontal Scroll Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            engines.forEach { eng ->
                val isSelected = selectedEngine.equals(eng, ignoreCase = true)
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isSelected) FnfPink.copy(alpha = 0.18f) else Color.Transparent,
                    border = BorderStroke(
                        0.8.dp,
                        if (isSelected) FnfPink else FnfBorder.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onEngineSelect(eng) }
                ) {
                    Text(
                        text = eng,
                        color = if (isSelected) FnfPink else FnfTextMuted,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}
