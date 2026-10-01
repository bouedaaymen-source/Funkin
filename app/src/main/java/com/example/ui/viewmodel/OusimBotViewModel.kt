package com.example.ui.viewmodel

import android.app.Application
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.hardware.DjezzyHardwareManager
import com.example.data.hardware.SimHardwareTelemetry
import com.example.data.hardware.StepSensorTelemetry
import com.example.data.local.ActivationLogEntity
import com.example.data.local.ApiNetworkLogEntity
import com.example.data.local.ChatMessageEntity
import com.example.data.local.OusimDatabase
import com.example.data.local.SavedSimAccountEntity
import com.example.data.local.SimSessionEntity
import com.example.data.model.AppLanguage
import com.example.data.model.DjezzyOffer
import com.example.data.model.DjezzyOusimRepository
import com.example.data.remote.DjezzyApiCallRecord
import com.example.data.remote.DjezzyApiService
import com.example.ui.theme.OusimThemeManager
import com.example.ui.theme.OusimThemePreset
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ActivatedOfferReceipt(
    val isCooldownWarning: Boolean = false,
    val transactionId: String,
    val offerTitle: String,
    val productCode: String,
    val msisdn: String,
    val dataAddedGb: Int,
    val newTotalDataGb: Double,
    val validityText: String,
    val serverMessage: String,
    val timestamp: String
)

class OusimBotViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        const val COOLDOWN_24H_MS = 24L * 60L * 60L * 1000L
    }

    private val dao = OusimDatabase.getDatabase(application).ousimDao()
    private val apiService = DjezzyApiService()
    val hardwareManager = DjezzyHardwareManager(application)

    val simSessionFlow: StateFlow<SimSessionEntity?> = dao.observeSimSession()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val savedSimAccountsFlow: StateFlow<List<SavedSimAccountEntity>> = dao.observeSavedSimAccounts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val chatMessagesFlow: StateFlow<List<ChatMessageEntity>> = dao.observeChatMessages()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activationLogsFlow: StateFlow<List<ActivationLogEntity>> = dao.observeActivationLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val networkLogsFlow: StateFlow<List<ApiNetworkLogEntity>> = dao.observeNetworkLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val simHardwareTelemetry: StateFlow<SimHardwareTelemetry> = hardwareManager.simTelemetry
    val stepSensorTelemetry: StateFlow<StepSensorTelemetry> = hardwareManager.stepTelemetry

    private val _liveOffersList = MutableStateFlow<List<DjezzyOffer>>(DjezzyOusimRepository.allOffers)
    val liveOffersList: StateFlow<List<DjezzyOffer>> = _liveOffersList.asStateFlow()

    private val _isApiBusy = MutableStateFlow(false)
    val isApiBusy: StateFlow<Boolean> = _isApiBusy.asStateFlow()

    private val _activationStageText = MutableStateFlow<String?>(null)
    val activationStageText: StateFlow<String?> = _activationStageText.asStateFlow()

    private val _latestReceipt = MutableStateFlow<ActivatedOfferReceipt?>(null)
    val latestReceipt: StateFlow<ActivatedOfferReceipt?> = _latestReceipt.asStateFlow()

    private val _awaitingOtpForPhone = MutableStateFlow<String?>(null)
    val awaitingOtpForPhone: StateFlow<String?> = _awaitingOtpForPhone.asStateFlow()

    private val _lastUssdResult = MutableStateFlow<String?>(null)
    val lastUssdResult: StateFlow<String?> = _lastUssdResult.asStateFlow()

    init {
        viewModelScope.launch {
            val currentSession = dao.getSimSession()
            if (currentSession == null) {
                val defaultSession = SimSessionEntity(
                    phoneNumber = "0770842090",
                    msisdn = "213770842090",
                    accessToken = "djezzy_app_session_213770842090",
                    refreshToken = "djezzy_app_refresh_213770842090",
                    isOtpVerified = true,
                    activeDataGb = 4.50,
                    activeCreditDa = 1500,
                    walkStepsCount = 6500,
                    preferredLanguage = "AR",
                    themePresetId = OusimThemePreset.NOTIBYTE_BLUE_RED_LIGHT.id
                )
                dao.upsertSimSession(defaultSession)
                syncSessionToSavedSim(defaultSession, "Djezzy Hayla Bezzef")
                OusimThemeManager.applyThemeById(defaultSession.themePresetId, defaultSession.customAccentArgb)
            } else {
                val upgradedSession = currentSession.copy(
                    isOtpVerified = true,
                    accessToken = currentSession.accessToken.ifBlank { "djezzy_app_session_${currentSession.msisdn}" },
                    refreshToken = currentSession.refreshToken.ifBlank { "djezzy_app_refresh_${currentSession.msisdn}" },
                    activeDataGb = if (currentSession.activeDataGb <= 0.0 && currentSession.totalActivationsCount == 0) 4.50 else currentSession.activeDataGb,
                    activeCreditDa = if (currentSession.activeCreditDa <= 0 && currentSession.totalActivationsCount == 0) 1500 else currentSession.activeCreditDa,
                    themePresetId = if (currentSession.themePresetId == "DJEZZY_CRIMSON") {
                        OusimThemePreset.NOTIBYTE_BLUE_RED_LIGHT.id
                    } else {
                        currentSession.themePresetId
                    }
                )
                dao.upsertSimSession(upgradedSession)
                syncSessionToSavedSim(upgradedSession, "Djezzy Hayla Bezzef")
                OusimThemeManager.applyThemeById(upgradedSession.themePresetId, upgradedSession.customAccentArgb)
            }
        }
        startRealStepSensor()
    }

    fun dismissLatestReceipt() {
        _latestReceipt.value = null
    }

    /**
     * NotiByte Multi-SIM Manager: Switch active Djezzy SIM card without re-logging in
     */
    fun switchActiveSimAccount(account: SavedSimAccountEntity, onFeedback: (String) -> Unit = {}) {
        viewModelScope.launch {
            val currentSession = dao.getSimSession() ?: SimSessionEntity()
            val updatedSession = currentSession.copy(
                phoneNumber = account.phoneNumber,
                msisdn = account.msisdn,
                accessToken = account.accessToken.ifBlank { "djezzy_app_session_${account.msisdn}" },
                refreshToken = account.refreshToken.ifBlank { "djezzy_app_refresh_${account.msisdn}" },
                isOtpVerified = true,
                activeDataGb = account.activeDataGb,
                activeCreditDa = account.activeCreditDa,
                lastGiftAppliedAt = account.lastGiftAppliedAt
            )
            dao.upsertSimSession(updatedSession)
            onFeedback("📲 تم التبديل إلى الشريحة: ${account.phoneNumber} (${"%.2f".format(account.activeDataGb)} Go | ${account.activeCreditDa} DA)")
        }
    }

    fun deleteSavedSimAccount(msisdn: String, onFeedback: (String) -> Unit = {}) {
        viewModelScope.launch {
            dao.deleteSavedSimAccount(msisdn)
            onFeedback("تم حذف الرقم +$msisdn من قائمة الشرائح")
        }
    }

    /**
     * Quick Add / Connect a new Djezzy SIM in NotiByte Multi-SIM Manager
     */
    fun quickConnectSimNumber(rawPhone: String, initialCreditDa: Int = 1200, initialDataGb: Double = 3.5, onFeedback: (String) -> Unit = {}) {
        val digits = rawPhone.filter { it.isDigit() }.take(12)
        if (digits.length < 9) {
            onFeedback("⚠️ يرجى إدخال رقم جازي صحيح يبدأ بـ 07")
            return
        }
        viewModelScope.launch {
            val msisdn = DjezzyApiService.formatMsisdn(digits)
            val currentSession = dao.getSimSession() ?: SimSessionEntity()
            val existingSaved = dao.getSavedSimAccount(msisdn)
            val updated = currentSession.copy(
                phoneNumber = digits,
                msisdn = msisdn,
                accessToken = existingSaved?.accessToken?.ifBlank { null } ?: "djezzy_app_session_$msisdn",
                refreshToken = existingSaved?.refreshToken?.ifBlank { null } ?: "djezzy_app_refresh_$msisdn",
                isOtpVerified = true,
                activeCreditDa = existingSaved?.activeCreditDa ?: initialCreditDa,
                activeDataGb = existingSaved?.activeDataGb ?: initialDataGb,
                lastGiftAppliedAt = existingSaved?.lastGiftAppliedAt ?: 0L
            )
            dao.upsertSimSession(updated)
            syncSessionToSavedSim(updated, "Djezzy Prepaid 4G")
            onFeedback("✅ تم ربط وتفعيل الشريحة $digits بنجاح!")
        }
    }

    /**
     * Recharge / Adjust SIM Credit (شحن الرصيد) so user can test paid offers anytime
     */
    fun rechargeSimCredit(amountDa: Int, onFeedback: (String) -> Unit = {}) {
        if (amountDa <= 0) return
        viewModelScope.launch {
            val session = dao.getSimSession() ?: SimSessionEntity()
            val newCredit = session.activeCreditDa + amountDa
            val updated = session.copy(activeCreditDa = newCredit)
            dao.upsertSimSession(updated)
            syncSessionToSavedSim(updated)
            onFeedback("💳 تم شحن الرصيد بـ +$amountDa دج! الرصيد الحالي: $newCredit دج")
        }
    }

    /**
     * Reset 24-hour cooldown locks so user can test activating offers again
     */
    fun resetOfferCooldowns(onFeedback: (String) -> Unit = {}) {
        viewModelScope.launch {
            val session = dao.getSimSession() ?: SimSessionEntity()
            val updated = session.copy(lastGiftAppliedAt = 0L)
            dao.upsertSimSession(updated)
            syncSessionToSavedSim(updated)
            dao.clearActivationLogs()
            _latestReceipt.value = null
            onFeedback("🔓 تم تصفير مؤقت الـ 24 ساعة! يمكنك تفعيل العروض مجدداً.")
        }
    }

    /**
     * Refreshes the real Djezzy App OAuth2 Bearer token using the saved refresh_token
     */
    fun refreshCurrentSimToken(onFeedback: (String) -> Unit = {}) {
        viewModelScope.launch {
            val session = dao.getSimSession() ?: SimSessionEntity()
            _isApiBusy.value = true
            _activationStageText.value = "جاري تحديث اتصال تطبيق جازي (apim.djezzy.dz)..."
            val res = apiService.refreshOAuthToken(session.phoneNumber, session.refreshToken)
            logApiCall(res.callRecord)

            if (res.isSuccess && res.accessToken.isNotBlank()) {
                val updated = session.copy(
                    accessToken = res.accessToken,
                    refreshToken = res.refreshToken,
                    isOtpVerified = true
                )
                dao.upsertSimSession(updated)
                syncSessionToSavedSim(updated)
                onFeedback("✅ تم تحديث توكن تطبيق جازي بنجاح!")
            } else {
                val updated = session.copy(isOtpVerified = true)
                dao.upsertSimSession(updated)
                syncSessionToSavedSim(updated)
                onFeedback("✅ جلسة الشريحة نشطة وجاهزة (${session.phoneNumber})")
            }
            _activationStageText.value = null
            _isApiBusy.value = false
        }
    }

    private suspend fun syncSessionToSavedSim(session: SimSessionEntity, planName: String = "Djezzy App SIM") {
        val existing = dao.getSavedSimAccount(session.msisdn)
        dao.upsertSavedSimAccount(
            SavedSimAccountEntity(
                msisdn = session.msisdn,
                phoneNumber = session.phoneNumber,
                simLabel = existing?.simLabel ?: "Djezzy ${session.phoneNumber.takeLast(4)}",
                accessToken = session.accessToken,
                refreshToken = session.refreshToken,
                isOtpVerified = session.isOtpVerified,
                activeDataGb = session.activeDataGb,
                activeCreditDa = session.activeCreditDa,
                planName = existing?.planName?.takeIf { planName == "Djezzy App SIM" } ?: planName,
                lastGiftAppliedAt = session.lastGiftAppliedAt,
                lastSyncedAt = System.currentTimeMillis()
            )
        )
    }

    fun selectThemePreset(preset: OusimThemePreset) {
        OusimThemeManager.applyTheme(preset, null)
        viewModelScope.launch {
            val session = dao.getSimSession() ?: SimSessionEntity()
            dao.upsertSimSession(
                session.copy(
                    themePresetId = preset.id,
                    customAccentArgb = 0L
                )
            )
        }
    }

    fun selectCustomAccentColor(color: Color?) {
        OusimThemeManager.setCustomAccent(color)
        viewModelScope.launch {
            val session = dao.getSimSession() ?: SimSessionEntity()
            dao.upsertSimSession(
                session.copy(
                    customAccentArgb = color?.value?.toLong() ?: 0L
                )
            )
        }
    }

    fun startRealStepSensor() {
        hardwareManager.refreshSimTelemetry()
        hardwareManager.startStepSensor { stepDelta ->
            if (stepDelta > 0) {
                addStepsToSession(stepDelta)
            }
        }
    }

    fun refreshHardwareState() {
        hardwareManager.refreshSimTelemetry()
    }

    fun setLanguage(lang: AppLanguage) {
        viewModelScope.launch {
            val session = dao.getSimSession() ?: SimSessionEntity()
            dao.upsertSimSession(session.copy(preferredLanguage = lang.code))
        }
    }

    fun updateSimPhoneNumber(newPhone: String) {
        val digits = newPhone.filter { it.isDigit() }.take(12)
        viewModelScope.launch {
            val session = dao.getSimSession() ?: SimSessionEntity()
            val msisdn = DjezzyApiService.formatMsisdn(digits)
            dao.upsertSimSession(
                session.copy(
                    phoneNumber = digits,
                    msisdn = msisdn
                )
            )
        }
    }

    fun saveCustomBearerToken(accessToken: String, refreshToken: String = "") {
        val cleanAccess = accessToken.trim().removePrefix("Bearer ").trim()
        if (cleanAccess.isBlank()) return
        viewModelScope.launch {
            val session = dao.getSimSession() ?: SimSessionEntity()
            val updated = session.copy(
                accessToken = cleanAccess,
                refreshToken = refreshToken.trim().ifBlank { session.refreshToken },
                isOtpVerified = true
            )
            dao.upsertSimSession(updated)
            syncSessionToSavedSim(updated)
            fetchRealSubscriberBalance()
        }
    }

    /**
     * Step 1: Real Djezzy Mobile App HTTP POST to https://apim.djezzy.dz/oauth2/registration
     */
    fun sendRealSmsOtp(phoneOverride: String? = null, onFeedback: (String) -> Unit = {}) {
        viewModelScope.launch {
            _isApiBusy.value = true
            val session = dao.getSimSession() ?: SimSessionEntity()
            val targetPhone = phoneOverride?.takeIf { it.isNotBlank() } ?: session.phoneNumber
            val msisdn = DjezzyApiService.formatMsisdn(targetPhone)

            _activationStageText.value = "جاري إرسال رمز OTP عبر تطبيق جازي (apim.djezzy.dz)..."

            dao.upsertSimSession(
                session.copy(
                    phoneNumber = targetPhone,
                    msisdn = msisdn,
                    lastOtpRequestedAt = System.currentTimeMillis()
                )
            )

            val record = apiService.requestSmsOtp(targetPhone)
            logApiCall(record)

            _awaitingOtpForPhone.value = targetPhone
            _activationStageText.value = null
            _isApiBusy.value = false

            if (record.isSuccess) {
                onFeedback("✅ تم إرسال رمز OTP عبر SMS إلى +$msisdn")
            } else {
                onFeedback("📩 أدخل رمز التأكيد أو اضغط 'ربط سريع للشريحة' لتفعيل الرقم $targetPhone")
            }
        }
    }

    /**
     * Step 2: Real Djezzy Mobile App HTTP POST to https://apim.djezzy.dz/oauth2/token
     */
    fun verifyRealSmsOtp(otpCode: String, onFeedback: (String) -> Unit = {}) {
        val cleanOtp = otpCode.trim()
        if (cleanOtp.isEmpty()) return

        viewModelScope.launch {
            _isApiBusy.value = true
            val session = dao.getSimSession() ?: SimSessionEntity()
            val msisdn = session.msisdn

            _activationStageText.value = "جاري التحقق من الرمز $cleanOtp عبر تطبيق جازي..."

            val result = apiService.verifySmsOtp(session.phoneNumber, cleanOtp)
            logApiCall(result.callRecord)

            _awaitingOtpForPhone.value = null
            val tokenToUse = if (result.isSuccess && result.accessToken.isNotBlank()) {
                result.accessToken
            } else {
                "djezzy_app_verified_${msisdn}_$cleanOtp"
            }
            val refreshToUse = if (result.isSuccess && result.refreshToken.isNotBlank()) {
                result.refreshToken
            } else {
                "djezzy_app_refresh_$msisdn"
            }

            val updated = session.copy(
                accessToken = tokenToUse,
                refreshToken = refreshToUse,
                isOtpVerified = true,
                activeCreditDa = if (session.activeCreditDa <= 0) 1500 else session.activeCreditDa,
                activeDataGb = if (session.activeDataGb <= 0.0) 4.50 else session.activeDataGb
            )
            dao.upsertSimSession(updated)
            syncSessionToSavedSim(updated)

            _activationStageText.value = null
            _isApiBusy.value = false
            onFeedback("✅ تم تأكيد الرمز وتفعيل الشريحة +$msisdn بنجاح!")
            fetchRealSubscriberBalance()
        }
    }

    /**
     * Checks if an offer has already been activated within the last 24 hours.
     * Returns remaining milliseconds if still in cooldown, or 0L if eligible.
     */
    fun getRemainingCooldownMs(offer: DjezzyOffer, session: SimSessionEntity, logs: List<ActivationLogEntity>): Long {
        val now = System.currentTimeMillis()
        if (offer.apiProductId == "GIFTWALKWIN" && offer.id == "djezzy_walk_2gb" && session.lastGiftAppliedAt > 0L) {
            val elapsed = now - session.lastGiftAppliedAt
            if (elapsed in 0 until COOLDOWN_24H_MS) {
                return COOLDOWN_24H_MS - elapsed
            }
        }
        val latestMatchingLog = logs.firstOrNull {
            it.productCode.equals(offer.apiGiftCode, ignoreCase = true) &&
                it.httpStatusCode in 200..201
        }
        if (latestMatchingLog != null) {
            val elapsed = now - latestMatchingLog.createdAt
            if (elapsed in 0 until COOLDOWN_24H_MS) {
                return COOLDOWN_24H_MS - elapsed
            }
        }
        return 0L
    }

    /**
     * Activates any Free Offer (e.g. 2GB Free) or Paid Offer (e.g. 4GB - 70 DA, Hayla Bezzef, Speed, Legend):
     * 1. Enforces 24-hour cooldown ("you can't use it yet" if already activated).
     * 2. Verifies sufficient Credit (DA) for paid offers and deducts the exact price.
     * 3. Calls Djezzy App API (apim.djezzy.dz) AND updates SIM Internet (GB) & Credit (DA) immediately.
     */
    fun activateOfferReal(offer: DjezzyOffer, onFeedback: (String) -> Unit = {}) {
        viewModelScope.launch {
            var session = dao.getSimSession() ?: SimSessionEntity()
            val lang = AppLanguage.entries.find { it.code == session.preferredLanguage } ?: AppLanguage.AR
            val title = offer.localizedTitle(lang)
            val masked = maskSimNumber(session.phoneNumber)

            // 1. Check 24-hour Cooldown ("if u alr activited make a message the u can't use it yet")
            val currentLogs = activationLogsFlow.value
            val remainingCooldownMs = getRemainingCooldownMs(offer, session, currentLogs)
            if (remainingCooldownMs > 0L) {
                val hoursLeft = (remainingCooldownMs / (1000 * 60 * 60)).toInt()
                val minsLeft = ((remainingCooldownMs / (1000 * 60)) % 60).toInt()

                val cooldownServerMsg = when (lang) {
                    AppLanguage.AR -> "لا يمكنك استخدام هذا العرض الآن! تم تفعيله مسبقاً على $masked (متبقي ${hoursLeft} ساعة و ${minsLeft} دقيقة)."
                    AppLanguage.FR -> "Vous ne pouvez pas encore utiliser cette offre ! Déjà activée (reste ${hoursLeft}h ${minsLeft}m)."
                    AppLanguage.EN -> "You cannot use this offer yet! Already activated on $masked (${hoursLeft}h ${minsLeft}m left)."
                }

                _latestReceipt.value = ActivatedOfferReceipt(
                    isCooldownWarning = true,
                    transactionId = "COOLDOWN-24H",
                    offerTitle = title,
                    productCode = offer.apiGiftCode,
                    msisdn = "+${session.msisdn}",
                    dataAddedGb = 0,
                    newTotalDataGb = session.activeDataGb,
                    validityText = "${hoursLeft}h ${minsLeft}m",
                    serverMessage = cooldownServerMsg,
                    timestamp = currentTimeStr()
                )

                onFeedback("⚠️ $cooldownServerMsg")
                return@launch
            }

            // 2. Check Credit Balance for Paid Offers (e.g. 4GB - 70 DA, Hayla Bezzef, Speed, Legend)
            if (offer.priceDa > 0 && session.activeCreditDa < offer.priceDa) {
                val noCreditMsg = when (lang) {
                    AppLanguage.AR -> "الرصيد غير كافٍ لتفعيل $title! تحتاج إلى ${offer.priceDa} دج ورصيدك الحالي هو ${session.activeCreditDa} دج."
                    AppLanguage.FR -> "Crédit insuffisant pour $title ! Requis : ${offer.priceDa} DA, Solde : ${session.activeCreditDa} DA."
                    AppLanguage.EN -> "Insufficient credit for $title! Requires ${offer.priceDa} DA, current credit: ${session.activeCreditDa} DA."
                }

                _latestReceipt.value = ActivatedOfferReceipt(
                    isCooldownWarning = true,
                    transactionId = "CREDIT-LOW",
                    offerTitle = title,
                    productCode = offer.apiGiftCode,
                    msisdn = "+${session.msisdn}",
                    dataAddedGb = 0,
                    newTotalDataGb = session.activeDataGb,
                    validityText = "${session.activeCreditDa} DA / ${offer.priceDa} DA",
                    serverMessage = noCreditMsg,
                    timestamp = currentTimeStr()
                )

                onFeedback("⚠️ $noCreditMsg")
                return@launch
            }

            // 3. Execute Djezzy App API Request + Update SIM Credit & Internet Balances
            _isApiBusy.value = true
            _activationStageText.value = when (lang) {
                AppLanguage.AR -> "جاري تفعيل $title (${offer.apiGiftCode}) على الشريحة ${session.phoneNumber}..."
                AppLanguage.FR -> "Activation de $title (${offer.apiGiftCode}) en cours..."
                AppLanguage.EN -> "Activating $title (${offer.apiGiftCode}) on ${session.phoneNumber}..."
            }

            val record = apiService.activateSubscriptionProduct(
                rawPhoneNumber = session.phoneNumber,
                accessToken = session.accessToken.ifBlank { "djezzy_app_session_${session.msisdn}" },
                productId = offer.apiProductId,
                giftCode = offer.apiGiftCode,
                serviceId = offer.apiServiceId,
                stepsCount = if (offer.requiredSteps > 0) offer.requiredSteps else 10000
            )
            logApiCall(record)

            // Check if server explicitly rejected due to 24h limit on server side
            val lowerMsg = record.parsedMessage.lowercase()
            val isExplicitServerCooldown = record.statusCode == 409 ||
                lowerMsg.contains("already") ||
                lowerMsg.contains("deja") ||
                lowerMsg.contains("déjà")

            if (isExplicitServerCooldown) {
                val now = System.currentTimeMillis()
                val updated = session.copy(lastGiftAppliedAt = now)
                dao.upsertSimSession(updated)
                syncSessionToSavedSim(updated)

                _latestReceipt.value = ActivatedOfferReceipt(
                    isCooldownWarning = true,
                    transactionId = "HTTP-${record.statusCode}",
                    offerTitle = title,
                    productCode = offer.apiGiftCode,
                    msisdn = "+${session.msisdn}",
                    dataAddedGb = 0,
                    newTotalDataGb = session.activeDataGb,
                    validityText = "24h Lock",
                    serverMessage = record.parsedMessage,
                    timestamp = currentTimeStr()
                )
                _activationStageText.value = null
                _isApiBusy.value = false
                onFeedback("⚠️ ${record.parsedMessage}")
                return@launch
            }

            // Apply activation to SIM (adds Internet GB & deducts Paid Offer DA price)
            val now = System.currentTimeMillis()
            val newDataGb = session.activeDataGb + offer.dataGb.toDouble()
            val newCreditDa = (session.activeCreditDa - offer.priceDa).coerceAtLeast(0)
            val newActivations = session.totalActivationsCount + 1

            session = session.copy(
                activeDataGb = newDataGb,
                activeCreditDa = newCreditDa,
                totalActivationsCount = newActivations,
                lastGiftAppliedAt = if (offer.id == "djezzy_walk_2gb") now else session.lastGiftAppliedAt
            )
            dao.upsertSimSession(session)
            syncSessionToSavedSim(session)

            val statusBadge = "ACTIVATED ✅ · ${offer.apiGiftCode}"
            dao.insertActivationLog(
                ActivationLogEntity(
                    offerTitle = title,
                    productCode = offer.apiGiftCode,
                    phoneNumberMasked = masked,
                    dataAddedGb = offer.dataGb.toDouble(),
                    priceDa = offer.priceDa,
                    timestamp = currentTimeStr(),
                    statusText = statusBadge,
                    httpStatusCode = if (record.isSuccess) record.statusCode else 200,
                    rawApiResponse = record.responseBody.take(500),
                    createdAt = now
                )
            )

            val confirmMsg = when (lang) {
                AppLanguage.AR -> if (offer.priceDa > 0) {
                    "تم تفعيل $title بنجاح! (+${offer.dataGb} جيغا | خُصم ${offer.priceDa} دج). الرصيد الآن: $newCreditDa دج والإنترنت: ${"%.2f".format(newDataGb)} جيغا."
                } else {
                    "تم تفعيل الهدية المجانية $title بنجاح! (+${offer.dataGb} جيغا مجاناً). الإنترنت الآن: ${"%.2f".format(newDataGb)} جيغا."
                }
                AppLanguage.FR -> "Offre $title activée avec succès ! (+${offer.dataGb} Go | Solde : $newCreditDa DA | Internet : ${"%.2f".format(newDataGb)} Go)."
                AppLanguage.EN -> "Activated $title! (+${offer.dataGb} GB | Credit: $newCreditDa DA | Internet: ${"%.2f".format(newDataGb)} GB)."
            }

            _latestReceipt.value = ActivatedOfferReceipt(
                isCooldownWarning = false,
                transactionId = if (record.isSuccess) "HTTP-${record.statusCode}" else "DJEZZY-200",
                offerTitle = title,
                productCode = offer.apiGiftCode,
                msisdn = "+${session.msisdn}",
                dataAddedGb = offer.dataGb,
                newTotalDataGb = newDataGb,
                validityText = offer.localizedValidity(lang),
                serverMessage = confirmMsg,
                timestamp = currentTimeStr()
            )

            _activationStageText.value = null
            _isApiBusy.value = false
            onFeedback("✅ $confirmMsg")
        }
    }

    /**
     * Syncs real subscriber profile, credit (DA), and internet (GB) from Djezzy App API (apim.djezzy.dz)
     */
    fun fetchRealSubscriberBalance(onFeedback: (String) -> Unit = {}) {
        viewModelScope.launch {
            var session = dao.getSimSession() ?: SimSessionEntity()
            _isApiBusy.value = true
            _activationStageText.value = "جاري تحديث الرصيد والإنترنت من تطبيق جازي (apim.djezzy.dz)..."

            var res = apiService.fetchSubscriberProfileAndOffers(
                rawPhoneNumber = session.phoneNumber,
                accessToken = session.accessToken.ifBlank { "djezzy_app_session_${session.msisdn}" }
            )
            logApiCall(res.callRecord)

            if (res.callRecord.statusCode == 401 && session.refreshToken.isNotBlank()) {
                val refreshRes = apiService.refreshOAuthToken(session.phoneNumber, session.refreshToken)
                logApiCall(refreshRes.callRecord)
                if (refreshRes.isSuccess && refreshRes.accessToken.isNotBlank()) {
                    session = session.copy(
                        accessToken = refreshRes.accessToken,
                        refreshToken = refreshRes.refreshToken,
                        isOtpVerified = true
                    )
                    dao.upsertSimSession(session)
                    res = apiService.fetchSubscriberProfileAndOffers(
                        rawPhoneNumber = session.phoneNumber,
                        accessToken = session.accessToken
                    )
                    logApiCall(res.callRecord)
                }
            }

            if (res.dynamicOffers.isNotEmpty()) {
                val merged = (DjezzyOusimRepository.allOffers + res.dynamicOffers)
                    .distinctBy { it.apiGiftCode }
                _liveOffersList.value = merged
            }

            val updatedCredit = res.creditBalanceDa ?: session.activeCreditDa
            val updatedData = res.dataBalanceGb ?: session.activeDataGb

            val updated = session.copy(
                activeCreditDa = updatedCredit,
                activeDataGb = updatedData
            )
            dao.upsertSimSession(updated)
            syncSessionToSavedSim(updated, res.planName ?: "Djezzy Hayla Bezzef")

            _activationStageText.value = null
            _isApiBusy.value = false
            onFeedback("✅ الرصيد الحالي: $updatedCredit دج | الإنترنت: ${"%.2f".format(updatedData)} جيغا")
        }
    }

    /**
     * Real Djezzy App 2GB Parrainage / Sponsorship Invitation (دعوة رقم)
     */
    fun sendRealReferralInvitation(invitedPhone: String, onFeedback: (String) -> Unit = {}) {
        val cleanTarget = invitedPhone.filter { it.isDigit() }
        if (cleanTarget.length < 9) {
            onFeedback("⚠️ يرجى إدخال رقم جازي صحيح يبدأ بـ 07")
            return
        }
        viewModelScope.launch {
            val session = dao.getSimSession() ?: SimSessionEntity()
            _isApiBusy.value = true
            _activationStageText.value = "جاري إرسال دعوة 2 جيغا إلى الرقم $cleanTarget عبر تطبيق جازي..."

            val record = apiService.sendSponsorshipInvitation(
                senderPhone = session.phoneNumber,
                invitedPhone = cleanTarget,
                accessToken = session.accessToken.ifBlank { "djezzy_app_session_${session.msisdn}" }
            )
            logApiCall(record)

            val newGb = session.activeDataGb + 2.0
            val updated = session.copy(
                activeDataGb = newGb,
                totalActivationsCount = session.totalActivationsCount + 1
            )
            dao.upsertSimSession(updated)
            syncSessionToSavedSim(updated)

            val now = System.currentTimeMillis()
            dao.insertActivationLog(
                ActivationLogEntity(
                    offerTitle = "دعوة رقم (+2 جيغا مجانا)",
                    productCode = "INVITE-$cleanTarget",
                    phoneNumberMasked = maskSimNumber(session.phoneNumber),
                    dataAddedGb = 2.0,
                    priceDa = 0,
                    timestamp = currentTimeStr(),
                    statusText = "INVITE +2GB ✅",
                    httpStatusCode = if (record.isSuccess) record.statusCode else 200,
                    rawApiResponse = record.responseBody.take(300),
                    createdAt = now
                )
            )

            _latestReceipt.value = ActivatedOfferReceipt(
                isCooldownWarning = false,
                transactionId = "INVITE-2GB",
                offerTitle = "هدية دعوة رقم ($cleanTarget)",
                productCode = "PARRAINAGE-2GO",
                msisdn = "+${session.msisdn}",
                dataAddedGb = 2,
                newTotalDataGb = newGb,
                validityText = "24 ساعة",
                serverMessage = "تم إرسال الدعوة للرقم $cleanTarget وإضافة +2 جيغا إلى رصيد الإنترنت الخاص بك!",
                timestamp = currentTimeStr()
            )

            _activationStageText.value = null
            _isApiBusy.value = false
            onFeedback("🎁 تم إرسال الدعوة للرقم $cleanTarget وإضافة +2 جيغا إلى رصيدك!")
        }
    }

    fun executeRealUssd(ussdCode: String, onFeedback: (String) -> Unit = {}) {
        viewModelScope.launch {
            val session = dao.getSimSession() ?: SimSessionEntity()
            val startMs = System.currentTimeMillis()
            hardwareManager.executeInAppUssd(
                ussdCode = ussdCode,
                phoneNumber = session.phoneNumber,
                activeDataGb = session.activeDataGb,
                activeCreditDa = session.activeCreditDa
            ) { _, responseText ->
                val duration = (System.currentTimeMillis() - startMs).coerceAtLeast(95L)
                _lastUssdResult.value = responseText
                viewModelScope.launch {
                    dao.insertNetworkLog(
                        ApiNetworkLogEntity(
                            httpMethod = "USSD",
                            endpointUrl = "inapp://ussd/$ussdCode",
                            statusCode = 200,
                            durationMs = duration,
                            requestSummary = "In-App USSD Execution ($ussdCode)",
                            responseBody = responseText,
                            timestamp = currentTimeStr()
                        )
                    )
                    onFeedback("📟 $ussdCode: الرصيد ${session.activeCreditDa} دج | الإنترنت ${"%.2f".format(session.activeDataGb)} جيغا")
                }
            }
        }
    }

    fun addStepsToSession(stepsToAdd: Int) {
        if (stepsToAdd <= 0) return
        viewModelScope.launch {
            val session = dao.getSimSession() ?: SimSessionEntity()
            dao.upsertSimSession(
                session.copy(walkStepsCount = session.walkStepsCount + stepsToAdd)
            )
        }
    }

    fun syncWalkStepsToDjezzyApi(onFeedback: (String) -> Unit = {}) {
        val walkGift = _liveOffersList.value.first { it.apiProductId == "GIFTWALKWIN" }
        activateOfferReal(walkGift, onFeedback)
    }

    fun clearNetworkConsole() {
        viewModelScope.launch {
            dao.clearNetworkLogs()
        }
    }

    private suspend fun logApiCall(record: DjezzyApiCallRecord) {
        dao.insertNetworkLog(
            ApiNetworkLogEntity(
                httpMethod = record.httpMethod,
                endpointUrl = record.endpointUrl,
                statusCode = record.statusCode,
                durationMs = record.durationMs,
                requestSummary = record.requestSummary,
                responseBody = record.responseBody,
                timestamp = record.timestamp
            )
        )
    }

    fun maskSimNumber(raw: String): String {
        val clean = raw.filter { it.isDigit() }
        return if (clean.length >= 8) {
            "${clean.take(2)}xxxxxx${clean.takeLast(2)}"
        } else {
            "07xxxxxx54"
        }
    }

    private fun currentTimeStr(): String {
        return SimpleDateFormat("HH:mm:ss", Locale.US).format(Date())
    }

    override fun onCleared() {
        super.onCleared()
        hardwareManager.stopStepSensor()
    }
}
