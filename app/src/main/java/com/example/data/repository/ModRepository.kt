package com.example.data.repository

import com.example.data.local.CustomModEntity
import com.example.data.local.ModDao
import com.example.data.local.UserModTrackingEntity
import com.example.data.model.DefaultCatalog
import com.example.data.model.DownloadStatus
import com.example.data.model.FnfMod
import com.example.data.model.FullModDetail
import com.example.data.model.ModCharacter
import com.example.data.model.SongItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

class ModRepository(private val modDao: ModDao) {

    private val downloadJobs = ConcurrentHashMap<String, Job>()
    private val repoScope = CoroutineScope(Dispatchers.IO)

    fun getAllMods(): Flow<List<FullModDetail>> {
        return combine(
            modDao.getAllTracking(),
            modDao.getAllCustomMods()
        ) { trackingList, customModEntities ->
            val trackingMap = trackingList.associateBy { it.modId }
            val customMods = customModEntities.map { entityToFnfMod(it) }
            val allCatalog = customMods + DefaultCatalog.mods

            allCatalog.map { mod ->
                val track = trackingMap[mod.id]
                val status = when (track?.downloadStatus) {
                    "DOWNLOADING" -> DownloadStatus.DOWNLOADING
                    "DOWNLOADED" -> DownloadStatus.DOWNLOADED
                    else -> DownloadStatus.NOT_DOWNLOADED
                }

                FullModDetail(
                    mod = mod,
                    isFavorite = track?.isFavorite ?: false,
                    downloadStatus = status,
                    downloadProgress = track?.downloadProgress ?: 0,
                    userRating = track?.userRating ?: 0f,
                    isCompleted = track?.isCompleted ?: false,
                    highestScore = track?.highestScore ?: 0L,
                    userNotes = track?.userNotes ?: "",
                    lastPlayedDate = track?.lastPlayedDate ?: ""
                )
            }
        }
    }

    fun getModById(modId: String): Flow<FullModDetail?> {
        return combine(getAllMods()) { list ->
            list.firstOrNull()?.find { it.mod.id == modId }
        }
    }

    suspend fun toggleFavorite(modId: String) {
        val allTracked = modDao.getAllTracking()
        // Check current state or insert
        repoScope.launch {
            // Find current state
            val current = getDirectTracking(modId)
            val newFav = !(current?.isFavorite ?: false)
            val updated = (current ?: UserModTrackingEntity(modId = modId)).copy(isFavorite = newFav)
            modDao.upsertTracking(updated)
        }
    }

    fun startDownload(modId: String) {
        if (downloadJobs[modId]?.isActive == true) return

        val job = repoScope.launch {
            val existing = getDirectTracking(modId) ?: UserModTrackingEntity(modId = modId)
            modDao.upsertTracking(existing.copy(downloadStatus = "DOWNLOADING", downloadProgress = 5))

            // Realistic progress stepping for mod package download
            for (p in 10..100 step 10) {
                delay(300)
                modDao.updateDownloadStatus(modId, "DOWNLOADING", p)
            }
            delay(200)
            modDao.updateDownloadStatus(modId, "DOWNLOADED", 100)
            downloadJobs.remove(modId)
        }
        downloadJobs[modId] = job
    }

    fun cancelOrDeleteDownload(modId: String) {
        downloadJobs[modId]?.cancel()
        downloadJobs.remove(modId)
        repoScope.launch {
            val existing = getDirectTracking(modId)
            if (existing != null) {
                modDao.upsertTracking(existing.copy(downloadStatus = "NOT_DOWNLOADED", downloadProgress = 0))
            }
        }
    }

    suspend fun saveUserLog(
        modId: String,
        rating: Float,
        score: Long,
        isCompleted: Boolean,
        notes: String
    ) {
        val existing = getDirectTracking(modId) ?: UserModTrackingEntity(modId = modId)
        val updated = existing.copy(
            userRating = rating,
            highestScore = score,
            isCompleted = isCompleted,
            userNotes = notes,
            lastPlayedDate = "Today"
        )
        modDao.upsertTracking(updated)
    }

    suspend fun addCustomMod(
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
        customSongsEncoded: String = ""
    ) {
        val id = "custom-${title.lowercase().replace(" ", "-").replace("[^a-z0-9-]".toRegex(), "")}-${System.currentTimeMillis() % 10000}"
        val entity = CustomModEntity(
            id = id,
            title = title,
            subtitle = subtitle,
            author = author,
            version = version.ifBlank { "v1.0" },
            engine = engine.ifBlank { "Psych Engine 0.7.3" },
            downloadSize = downloadSize.ifBlank { "250 MB" },
            category = category,
            difficulty = difficulty,
            downloadUrl = downloadUrl.ifBlank { "https://gamebanana.com" },
            mirrorUrl = if (customSongsEncoded.isNotBlank()) "SONGS:$customSongsEncoded" else downloadUrl.ifBlank { "https://gamebanana.com" },
            description = description,
            lore = lore,
            tagsJoined = tags.joinToString(","),
            colorHex = colorHex
        )
        modDao.insertCustomMod(entity)
    }

    suspend fun deleteCustomMod(id: String) {
        modDao.deleteCustomMod(id)
        modDao.deleteTracking(id)
    }

    private suspend fun getDirectTracking(modId: String): UserModTrackingEntity? {
        return modDao.getTrackingForModDirect(modId)
    }

    private fun entityToFnfMod(entity: CustomModEntity): FnfMod {
        val tags = entity.tagsJoined.split(",").map { it.trim() }.filter { it.isNotBlank() }
        // Parse custom songs if encoded in mirrorUrl as SONGS:Title~BPM~Diff~Opp|...
        val parsedSongs = if (entity.mirrorUrl.startsWith("SONGS:")) {
            entity.mirrorUrl.removePrefix("SONGS:")
                .split("|")
                .mapNotNull { entry ->
                    val parts = entry.split("~")
                    if (parts.size >= 2) {
                        SongItem(
                            title = parts[0].ifBlank { "Track 1" },
                            bpm = parts.getOrNull(1)?.toIntOrNull() ?: 160,
                            duration = "2:45",
                            difficultyLevel = parts.getOrNull(2)?.ifBlank { entity.difficulty } ?: entity.difficulty,
                            opponent = parts.getOrNull(3)?.ifBlank { entity.author } ?: entity.author
                        )
                    } else null
                }
        } else emptyList()

        val finalSongs = parsedSongs.ifEmpty {
            listOf(
                SongItem("${entity.title} - Act 1", 160, "2:40", entity.difficulty, entity.author),
                SongItem("${entity.title} - Finale", 185, "3:15", "Insane", "Ultra ${entity.author}")
            )
        }

        return FnfMod(
            id = entity.id,
            title = entity.title,
            subtitle = entity.subtitle,
            author = entity.author,
            version = entity.version,
            engine = entity.engine,
            downloadSize = entity.downloadSize,
            releaseDate = "Community Upload",
            lastUpdated = "Verified Stable",
            rating = 5.0f,
            downloadCount = "10K",
            tags = (listOf("Uploaded Cartridge", "Stable: ${entity.engine}") + tags).distinct(),
            category = entity.category,
            difficulty = entity.difficulty,
            colorHex = entity.colorHex,
            description = entity.description,
            lore = entity.lore,
            downloadUrl = entity.downloadUrl,
            mirrorUrl = entity.downloadUrl,
            songs = finalSongs,
            mechanics = listOf(
                "Locked Stable Engine: ${entity.engine}",
                "Lua & HScript Version Verified",
                "Dodge & Custom NoteTypes Ready"
            ),
            characters = listOf(
                ModCharacter(entity.author, "Mod Creator", "🍄", "Uploaded cartridge verified for ${entity.engine}.")
            ),
            platforms = listOf("Android (${entity.engine} APK)", "Windows"),
            isCustomUserMod = true
        )
    }
}
