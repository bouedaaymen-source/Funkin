package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_mod_tracking")
data class UserModTrackingEntity(
    @PrimaryKey
    val modId: String,
    val isFavorite: Boolean = false,
    val downloadStatus: String = "NOT_DOWNLOADED", // NOT_DOWNLOADED, DOWNLOADING, DOWNLOADED
    val downloadProgress: Int = 0,
    val userRating: Float = 0f,
    val isCompleted: Boolean = false,
    val highestScore: Long = 0L,
    val userNotes: String = "",
    val lastPlayedDate: String = ""
)

@Entity(tableName = "custom_mods")
data class CustomModEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val subtitle: String,
    val author: String,
    val version: String,
    val engine: String,
    val downloadSize: String,
    val category: String,
    val difficulty: String,
    val downloadUrl: String,
    val mirrorUrl: String,
    val description: String,
    val lore: String,
    val tagsJoined: String,
    val colorHex: Long,
    val createdAt: Long = System.currentTimeMillis()
)
