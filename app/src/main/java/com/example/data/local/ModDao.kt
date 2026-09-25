package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ModDao {
    @Query("SELECT * FROM user_mod_tracking")
    fun getAllTracking(): Flow<List<UserModTrackingEntity>>

    @Query("SELECT * FROM user_mod_tracking WHERE modId = :modId LIMIT 1")
    fun getTrackingForMod(modId: String): Flow<UserModTrackingEntity?>

    @Query("SELECT * FROM user_mod_tracking WHERE modId = :modId LIMIT 1")
    suspend fun getTrackingForModDirect(modId: String): UserModTrackingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTracking(tracking: UserModTrackingEntity)

    @Query("UPDATE user_mod_tracking SET downloadStatus = :status, downloadProgress = :progress WHERE modId = :modId")
    suspend fun updateDownloadStatus(modId: String, status: String, progress: Int)

    @Query("UPDATE user_mod_tracking SET isFavorite = :isFavorite WHERE modId = :modId")
    suspend fun updateFavorite(modId: String, isFavorite: Boolean)

    @Query("DELETE FROM user_mod_tracking WHERE modId = :modId")
    suspend fun deleteTracking(modId: String)

    // Custom Mods
    @Query("SELECT * FROM custom_mods ORDER BY createdAt DESC")
    fun getAllCustomMods(): Flow<List<CustomModEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomMod(mod: CustomModEntity)

    @Query("DELETE FROM custom_mods WHERE id = :id")
    suspend fun deleteCustomMod(id: String)
}
