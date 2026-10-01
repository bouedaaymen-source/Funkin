package com.example.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "sim_sessions")
data class SimSessionEntity(
    @PrimaryKey val id: Int = 1,
    val phoneNumber: String = "0770842090",
    val msisdn: String = "213770842090",
    val accessToken: String = "",
    val refreshToken: String = "",
    val isOtpVerified: Boolean = false,
    val lastOtpRequestedAt: Long = 0L,
    val lastGiftAppliedAt: Long = 0L,
    val activeDataGb: Double = 0.0,
    val activeCreditDa: Int = 0,
    val walkStepsCount: Int = 0,
    val totalActivationsCount: Int = 0,
    val preferredLanguage: String = "EN",
    val themePresetId: String = "DJEZZY_CRIMSON",
    val customAccentArgb: Long = 0L
)

@Entity(tableName = "saved_sim_accounts")
data class SavedSimAccountEntity(
    @PrimaryKey val msisdn: String,
    val phoneNumber: String,
    val simLabel: String = "Djezzy SIM",
    val accessToken: String = "",
    val refreshToken: String = "",
    val isOtpVerified: Boolean = false,
    val activeDataGb: Double = 0.0,
    val activeCreditDa: Int = 0,
    val planName: String = "Djezzy App SIM",
    val lastGiftAppliedAt: Long = 0L,
    val lastSyncedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val isFromBot: Boolean,
    val text: String,
    val timestamp: String,
    val highlightBadge: String? = null,
    val attachedOfferId: String? = null,
    val dialUssdCode: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "activation_logs")
data class ActivationLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val offerTitle: String,
    val productCode: String,
    val phoneNumberMasked: String,
    val dataAddedGb: Double,
    val priceDa: Int,
    val timestamp: String,
    val statusText: String,
    val httpStatusCode: Int = 200,
    val rawApiResponse: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "api_network_logs")
data class ApiNetworkLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val httpMethod: String,
    val endpointUrl: String,
    val statusCode: Int,
    val durationMs: Long,
    val requestSummary: String,
    val responseBody: String,
    val timestamp: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Dao
interface OusimDao {
    @Query("SELECT * FROM sim_sessions WHERE id = 1 LIMIT 1")
    fun observeSimSession(): Flow<SimSessionEntity?>

    @Query("SELECT * FROM sim_sessions WHERE id = 1 LIMIT 1")
    suspend fun getSimSession(): SimSessionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSimSession(session: SimSessionEntity)

    @Query("SELECT * FROM saved_sim_accounts ORDER BY lastSyncedAt DESC")
    fun observeSavedSimAccounts(): Flow<List<SavedSimAccountEntity>>

    @Query("SELECT * FROM saved_sim_accounts WHERE msisdn = :msisdn LIMIT 1")
    suspend fun getSavedSimAccount(msisdn: String): SavedSimAccountEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSavedSimAccount(account: SavedSimAccountEntity)

    @Query("DELETE FROM saved_sim_accounts WHERE msisdn = :msisdn")
    suspend fun deleteSavedSimAccount(msisdn: String)

    @Query("SELECT * FROM chat_messages ORDER BY createdAt ASC, id ASC")
    fun observeChatMessages(): Flow<List<ChatMessageEntity>>

    @Query("SELECT COUNT(*) FROM chat_messages")
    suspend fun countChatMessages(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: ChatMessageEntity)

    @Query("DELETE FROM chat_messages")
    suspend fun clearChatMessages()

    @Query("SELECT * FROM activation_logs ORDER BY createdAt DESC, id DESC")
    fun observeActivationLogs(): Flow<List<ActivationLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivationLog(log: ActivationLogEntity)

    @Query("DELETE FROM activation_logs")
    suspend fun clearActivationLogs()

    @Query("SELECT * FROM api_network_logs ORDER BY createdAt DESC, id DESC LIMIT 60")
    fun observeNetworkLogs(): Flow<List<ApiNetworkLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNetworkLog(log: ApiNetworkLogEntity)

    @Query("DELETE FROM api_network_logs")
    suspend fun clearNetworkLogs()
}

@Database(
    entities = [
        SimSessionEntity::class,
        SavedSimAccountEntity::class,
        ChatMessageEntity::class,
        ActivationLogEntity::class,
        ApiNetworkLogEntity::class
    ],
    version = 5,
    exportSchema = false
)
abstract class OusimDatabase : RoomDatabase() {
    abstract fun ousimDao(): OusimDao

    companion object {
        @Volatile
        private var INSTANCE: OusimDatabase? = null

        fun getDatabase(context: Context): OusimDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    OusimDatabase::class.java,
                    "ousim_djezzy_bot_db"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
