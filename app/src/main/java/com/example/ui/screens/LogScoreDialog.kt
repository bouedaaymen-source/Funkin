package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.FullModDetail
import com.example.ui.theme.FnfBorder
import com.example.ui.theme.FnfCyan
import com.example.ui.theme.FnfDarkBg
import com.example.ui.theme.FnfGreen
import com.example.ui.theme.FnfPink
import com.example.ui.theme.FnfSurface
import com.example.ui.theme.FnfSurfaceElevated
import com.example.ui.theme.FnfTextMuted
import com.example.ui.theme.FnfTextPrimary
import com.example.ui.theme.FnfTextSecondary
import com.example.ui.theme.FnfYellow

@Composable
fun LogScoreDialog(
    detail: FullModDetail,
    onDismiss: () -> Unit,
    onSave: (rating: Float, score: Long, isCompleted: Boolean, notes: String) -> Unit
) {
    var rating by remember { mutableFloatStateOf(if (detail.userRating > 0) detail.userRating else 5f) }
    var scoreInput by remember { mutableStateOf(if (detail.highestScore > 0) detail.highestScore.toString() else "") }
    var isCompleted by remember { mutableStateOf(detail.isCompleted) }
    var notes by remember { mutableStateOf(detail.userNotes) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = FnfSurface,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, FnfCyan)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "TRACK PROGRESS & LOG SCORE",
                    color = FnfCyan,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = detail.mod.title,
                    color = FnfTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Star rating row
                Text(
                    text = "Your Rating:",
                    color = FnfTextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    for (i in 1..5) {
                        Icon(
                            imageVector = if (rating >= i) Icons.Filled.Star else Icons.Outlined.StarBorder,
                            contentDescription = "Star $i",
                            tint = if (rating >= i) FnfYellow else FnfTextMuted,
                            modifier = Modifier
                                .size(36.dp)
                                .clickable { rating = i.toFloat() }
                                .padding(2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // High score input
                OutlinedTextField(
                    value = scoreInput,
                    onValueChange = { if (it.all { ch -> ch.isDigit() }) scoreInput = it },
                    label = { Text("Personal Best Score (e.g. 482500)", color = FnfTextMuted) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("score_input_field"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FnfCyan,
                        unfocusedBorderColor = FnfBorder,
                        focusedTextColor = FnfTextPrimary,
                        unfocusedTextColor = FnfTextPrimary,
                        cursorColor = FnfCyan
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Completion status checkbox
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isCompleted = !isCompleted }
                ) {
                    Checkbox(
                        checked = isCompleted,
                        onCheckedChange = { isCompleted = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = FnfGreen,
                            uncheckedColor = FnfBorder
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Completed All Weeks / Songs",
                        color = if (isCompleted) FnfGreen else FnfTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = if (isCompleted) FontWeight.Bold else FontWeight.Normal
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Personal Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Personal Notes & FC / Accuracy logs", color = FnfTextMuted) },
                    placeholder = { Text("e.g. Cleared Hard with 97% accuracy! Loved the 3rd song beat drop.", color = FnfTextMuted, fontSize = 12.sp) },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FnfCyan,
                        unfocusedBorderColor = FnfBorder,
                        focusedTextColor = FnfTextPrimary,
                        unfocusedTextColor = FnfTextPrimary,
                        cursorColor = FnfCyan
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
                            val scoreVal = scoreInput.toLongOrNull() ?: 0L
                            onSave(rating, scoreVal, isCompleted, notes)
                            onDismiss()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("save_score_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FnfCyan, contentColor = FnfDarkBg)
                    ) {
                        Text("SAVE LOG", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
