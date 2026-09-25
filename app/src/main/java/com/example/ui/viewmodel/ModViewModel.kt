package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.DownloadStatus
import com.example.data.model.FullModDetail
import com.example.data.repository.ModRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class MainTab {
    EXPLORE,
    UPLOAD,
    LIBRARY,
    BEAT_LAB
}

enum class LibraryTab {
    DOWNLOADED,
    FAVORITES,
    COMPLETED,
    DOWNLOADING
}

enum class SortOption(val displayName: String) {
    MOST_POPULAR("Most Popular"),
    TOP_RATED("Top Rated"),
    NEWEST("Latest Updates"),
    DIFFICULTY("Hardest First")
}

data class StorageMetrics(
    val downloadedCount: Int = 0,
    val favoriteCount: Int = 0,
    val completedCount: Int = 0,
    val totalStorageAllocatedMb: Int = 0
)

class ModViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ModRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = ModRepository(database.modDao())
    }

    val currentTab = MutableStateFlow(MainTab.EXPLORE)
    val libraryTab = MutableStateFlow(LibraryTab.DOWNLOADED)

    val searchQuery = MutableStateFlow("")
    val selectedCategory = MutableStateFlow("All")
    val selectedEngine = MutableStateFlow("All Engines")
    val selectedSort = MutableStateFlow(SortOption.MOST_POPULAR)

    val selectedModId = MutableStateFlow<String?>(null)
    val psychEngineModId = MutableStateFlow<String?>(null)
    val showAddModDialog = MutableStateFlow(false)

    // Rhythm Lab State
    val rhythmScore = MutableStateFlow(0)
    val rhythmCombo = MutableStateFlow(0)
    val maxCombo = MutableStateFlow(0)
    val latestRating = MutableStateFlow<String?>(null) // "SICK!!", "GOOD", "BAD", "SHIT"
    val accuracyHits = MutableStateFlow(Pair(0, 0)) // hits to total

    val allMods: StateFlow<List<FullModDetail>> = repository.getAllMods()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val filteredMods: StateFlow<List<FullModDetail>> = combine(
        allMods,
        searchQuery,
        selectedCategory,
        selectedEngine,
        selectedSort
    ) { mods, query, category, engine, sort ->
        var result = mods

        // Search query filter
        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            result = result.filter { item ->
                item.mod.title.lowercase().contains(q) ||
                item.mod.author.lowercase().contains(q) ||
                item.mod.tags.any { it.lowercase().contains(q) } ||
                item.mod.engine.lowercase().contains(q) ||
                item.mod.songs.any { it.title.lowercase().contains(q) }
            }
        }

        // Category filter
        if (category != "All") {
            result = result.filter { it.mod.category.equals(category, ignoreCase = true) || it.mod.tags.any { tag -> tag.contains(category, ignoreCase = true) } }
        }

        // Engine filter
        if (engine != "All Engines") {
            result = result.filter { it.mod.engine.contains(engine, ignoreCase = true) }
        }

        // Sorting
        result = when (sort) {
            SortOption.MOST_POPULAR -> result.sortedByDescending { parseDownloads(it.mod.downloadCount) }
            SortOption.TOP_RATED -> result.sortedByDescending { it.mod.rating }
            SortOption.NEWEST -> result.sortedByDescending { it.mod.version }
            SortOption.DIFFICULTY -> result.sortedByDescending { difficultyWeight(it.mod.difficulty) }
        }

        result
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val storageMetrics: StateFlow<StorageMetrics> = allMods.combine(allMods) { mods, _ ->
        var countDownloaded = 0
        var countFavorites = 0
        var countCompleted = 0
        var totalMb = 0

        for (item in mods) {
            if (item.downloadStatus == DownloadStatus.DOWNLOADED) {
                countDownloaded++
                totalMb += parseSizeToMb(item.mod.downloadSize)
            }
            if (item.isFavorite) countFavorites++
            if (item.isCompleted) countCompleted++
        }

        StorageMetrics(
            downloadedCount = countDownloaded,
            favoriteCount = countFavorites,
            completedCount = countCompleted,
            totalStorageAllocatedMb = totalMb
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StorageMetrics()
    )

    fun onSearchQueryChange(query: String) {
        searchQuery.value = query
    }

    fun onCategorySelect(category: String) {
        selectedCategory.value = category
    }

    fun onEngineSelect(engine: String) {
        selectedEngine.value = engine
    }

    fun onSortSelect(sort: SortOption) {
        selectedSort.value = sort
    }

    fun selectMod(modId: String?) {
        selectedModId.value = modId
    }

    fun launchInPsychEngine(modId: String?) {
        psychEngineModId.value = modId
    }

    fun toggleFavorite(modId: String) {
        viewModelScope.launch {
            repository.toggleFavorite(modId)
        }
    }

    fun startDownload(modId: String) {
        repository.startDownload(modId)
    }

    fun cancelOrDeleteDownload(modId: String) {
        repository.cancelOrDeleteDownload(modId)
    }

    fun saveUserLog(modId: String, rating: Float, score: Long, completed: Boolean, notes: String) {
        viewModelScope.launch {
            repository.saveUserLog(modId, rating, score, completed, notes)
        }
    }

    fun addCustomMod(
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
        viewModelScope.launch {
            repository.addCustomMod(
                title, subtitle, author, version, engine, downloadSize,
                category, difficulty, downloadUrl, description, lore, tags, colorHex,
                customSongsEncoded
            )
        }
    }

    fun deleteCustomMod(id: String) {
        viewModelScope.launch {
            repository.deleteCustomMod(id)
            if (selectedModId.value == id) {
                selectedModId.value = null
            }
        }
    }

    // Rhythm Arrow Tap handler
    fun onArrowTapped(direction: String) {
        // Evaluate timing randomly / simulated accuracy
        val ratingPool = listOf("SICK!!", "SICK!!", "GOOD", "SICK!!", "GOOD", "BAD")
        val chosenRating = ratingPool.random()
        latestRating.value = chosenRating

        val (hits, total) = accuracyHits.value
        val newTotal = total + 1
        val newHits = if (chosenRating == "SICK!!" || chosenRating == "GOOD") hits + 1 else hits
        accuracyHits.value = Pair(newHits, newTotal)

        val pts = when (chosenRating) {
            "SICK!!" -> 350
            "GOOD" -> 200
            "BAD" -> 100
            else -> 10
        }
        val currentC = rhythmCombo.value + 1
        rhythmCombo.value = currentC
        if (currentC > maxCombo.value) {
            maxCombo.value = currentC
        }
        rhythmScore.value = rhythmScore.value + pts + (currentC * 5)
    }

    fun resetRhythmScore() {
        rhythmScore.value = 0
        rhythmCombo.value = 0
        accuracyHits.value = Pair(0, 0)
        latestRating.value = null
    }

    private fun parseDownloads(countStr: String): Long {
        return try {
            val upper = countStr.uppercase()
            when {
                upper.endsWith("M") -> (upper.removeSuffix("M").toFloat() * 1_000_000).toLong()
                upper.endsWith("K") -> (upper.removeSuffix("K").toFloat() * 1_000).toLong()
                else -> upper.toLongOrNull() ?: 0L
            }
        } catch (e: Exception) {
            0L
        }
    }

    private fun parseSizeToMb(sizeStr: String): Int {
        return try {
            val upper = sizeStr.uppercase().trim()
            when {
                upper.endsWith("GB") -> (upper.removeSuffix("GB").trim().toFloat() * 1024).toInt()
                upper.endsWith("MB") -> upper.removeSuffix("MB").trim().toInt()
                else -> 100
            }
        } catch (e: Exception) {
            100
        }
    }

    private fun difficultyWeight(diff: String): Int {
        return when (diff.lowercase()) {
            "insane", "mania" -> 5
            "expert" -> 4
            "hard" -> 3
            "medium" -> 2
            "casual", "easy" -> 1
            else -> 2
        }
    }
}
