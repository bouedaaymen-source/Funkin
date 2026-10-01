package com.example.data.remote

import com.example.data.model.DjezzyOffer
import com.example.data.model.OfferCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

data class DjezzyApiCallRecord(
    val httpMethod: String,
    val endpointUrl: String,
    val statusCode: Int,
    val durationMs: Long,
    val requestSummary: String,
    val responseBody: String,
    val isSuccess: Boolean,
    val parsedMessage: String,
    val timestamp: String = SimpleDateFormat("HH:mm:ss", Locale.US).format(Date())
)

data class DjezzyOtpTokenResult(
    val isSuccess: Boolean,
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Int,
    val callRecord: DjezzyApiCallRecord
)

data class DjezzySubscriberSyncResult(
    val isSuccess: Boolean,
    val creditBalanceDa: Int?,
    val dataBalanceGb: Double?,
    val planName: String?,
    val activeProductCodes: List<String>,
    val dynamicOffers: List<DjezzyOffer>,
    val callRecord: DjezzyApiCallRecord
)

class DjezzyApiService {

    companion object {
        const val BASE_APIM_URL = "https://apim.djezzy.dz"
        const val OAUTH_REGISTER_OTP_URL = "$BASE_APIM_URL/oauth2/registration"
        const val OAUTH_TOKEN_URL = "$BASE_APIM_URL/oauth2/token"
        const val SUBSCRIBERS_BASE_URL = "$BASE_APIM_URL/djezzy-api/api/v1/subscribers"

        // Official Djezzy Mobile App OAuth2 Client Credentials (Djezzy/2.6.7)
        const val DJEZZY_CLIENT_ID = "6E6CwTkp8H1CyQxraPmcEJPQ7xka"
        const val DJEZZY_CLIENT_SECRET = "MVpXHW_ImuMsxKIwrJpoVVMHjRsa"
        const val DJEZZY_USER_AGENT = "Djezzy/2.6.7"

        fun formatMsisdn(rawPhone: String): String {
            val digits = rawPhone.filter { it.isDigit() }
            return when {
                digits.startsWith("213") && digits.length == 12 -> digits
                digits.startsWith("07") && digits.length == 10 -> "213${digits.substring(1)}"
                digits.startsWith("7") && digits.length == 9 -> "213$digits"
                else -> digits
            }
        }
    }

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    /**
     * Step 1: Real SMS OTP Request
     * POST https://apim.djezzy.dz/oauth2/registration
     * Payload: msisdn=2137xxxxxxxx&client_id=6E6CwTkp8H1CyQxraPmcEJPQ7xka&scope=smsotp
     */
    suspend fun requestSmsOtp(rawPhoneNumber: String): DjezzyApiCallRecord = withContext(Dispatchers.IO) {
        val msisdn = formatMsisdn(rawPhoneNumber)
        val startMs = System.currentTimeMillis()
        val rawForm = "msisdn=$msisdn&client_id=$DJEZZY_CLIENT_ID&scope=smsotp"

        val formBody = FormBody.Builder()
            .add("msisdn", msisdn)
            .add("client_id", DJEZZY_CLIENT_ID)
            .add("scope", "smsotp")
            .build()

        val request = Request.Builder()
            .url(OAUTH_REGISTER_OTP_URL)
            .post(formBody)
            .header("User-Agent", DJEZZY_USER_AGENT)
            .header("Connection", "close")
            .header("Content-Type", "application/x-www-form-urlencoded")
            .header("Cache-Control", "no-cache")
            .build()

        try {
            okHttpClient.newCall(request).execute().use { response ->
                val duration = System.currentTimeMillis() - startMs
                val rawBody = response.body?.string().orEmpty()
                val ok = response.isSuccessful
                val msg = parseJsonMessage(
                    rawBody,
                    if (ok) "SMS OTP sent to +$msisdn"
                    else "HTTP ${response.code}: Failed to send OTP"
                )
                DjezzyApiCallRecord(
                    httpMethod = "POST",
                    endpointUrl = OAUTH_REGISTER_OTP_URL,
                    statusCode = response.code,
                    durationMs = duration,
                    requestSummary = rawForm,
                    responseBody = rawBody.ifBlank { "{\"status\": ${response.code}}" },
                    isSuccess = ok,
                    parsedMessage = msg
                )
            }
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startMs
            val errDetail = "${e.javaClass.simpleName}: ${e.localizedMessage ?: "Connection error"}"
            DjezzyApiCallRecord(
                httpMethod = "POST",
                endpointUrl = OAUTH_REGISTER_OTP_URL,
                statusCode = 0,
                durationMs = duration,
                requestSummary = rawForm,
                responseBody = "{\"error\": \"${errDetail.replace("\"", "'")}\"}",
                isSuccess = false,
                parsedMessage = errDetail
            )
        }
    }

    /**
     * Step 2: Real SMS OTP Verification & OAuth2 Token Exchange
     * POST https://apim.djezzy.dz/oauth2/token
     */
    suspend fun verifySmsOtp(rawPhoneNumber: String, otpCode: String): DjezzyOtpTokenResult = withContext(Dispatchers.IO) {
        val msisdn = formatMsisdn(rawPhoneNumber)
        val cleanOtp = otpCode.trim()
        val startMs = System.currentTimeMillis()
        val requestSummary = "otp=$cleanOtp&mobileNumber=$msisdn&scope=openid&client_id=$DJEZZY_CLIENT_ID&grant_type=mobile"

        val formBody = FormBody.Builder()
            .add("otp", cleanOtp)
            .add("mobileNumber", msisdn)
            .add("scope", "openid")
            .add("client_id", DJEZZY_CLIENT_ID)
            .add("client_secret", DJEZZY_CLIENT_SECRET)
            .add("grant_type", "mobile")
            .build()

        val request = Request.Builder()
            .url(OAUTH_TOKEN_URL)
            .post(formBody)
            .header("User-Agent", DJEZZY_USER_AGENT)
            .header("Connection", "close")
            .header("Content-Type", "application/x-www-form-urlencoded")
            .header("Cache-Control", "no-cache")
            .build()

        try {
            okHttpClient.newCall(request).execute().use { response ->
                val duration = System.currentTimeMillis() - startMs
                val rawBody = response.body?.string().orEmpty()
                val json = runCatching { JSONObject(rawBody) }.getOrNull()
                val accessToken = json?.optString("access_token").orEmpty()
                val refreshToken = json?.optString("refresh_token").orEmpty()
                val expiresIn = json?.optInt("expires_in", 3600) ?: 3600
                val ok = response.code == 200 && accessToken.isNotBlank()

                val record = DjezzyApiCallRecord(
                    httpMethod = "POST",
                    endpointUrl = OAUTH_TOKEN_URL,
                    statusCode = response.code,
                    durationMs = duration,
                    requestSummary = requestSummary,
                    responseBody = rawBody.ifBlank { "{\"status\": ${response.code}}" },
                    isSuccess = ok,
                    parsedMessage = if (ok) {
                        "OAuth2 Bearer Token verified for +$msisdn"
                    } else {
                        parseJsonMessage(rawBody, "HTTP ${response.code}: Invalid or expired OTP code")
                    }
                )
                DjezzyOtpTokenResult(
                    isSuccess = ok,
                    accessToken = accessToken,
                    refreshToken = refreshToken,
                    expiresIn = expiresIn,
                    callRecord = record
                )
            }
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startMs
            val errDetail = "${e.javaClass.simpleName}: ${e.localizedMessage ?: "Network error"}"
            val record = DjezzyApiCallRecord(
                httpMethod = "POST",
                endpointUrl = OAUTH_TOKEN_URL,
                statusCode = 0,
                durationMs = duration,
                requestSummary = requestSummary,
                responseBody = "{\"error\": \"${errDetail.replace("\"", "'")}\"}",
                isSuccess = false,
                parsedMessage = errDetail
            )
            DjezzyOtpTokenResult(
                isSuccess = false,
                accessToken = "",
                refreshToken = "",
                expiresIn = 0,
                callRecord = record
            )
        }
    }

    /**
     * Step 2B: Real OAuth2 Refresh Token Exchange (NotiByte Multi-SIM Persistent Login)
     * POST https://apim.djezzy.dz/oauth2/token (grant_type=refresh_token)
     */
    suspend fun refreshOAuthToken(rawPhoneNumber: String, refreshToken: String): DjezzyOtpTokenResult = withContext(Dispatchers.IO) {
        val msisdn = formatMsisdn(rawPhoneNumber)
        val cleanRefresh = refreshToken.trim()
        val startMs = System.currentTimeMillis()
        val requestSummary = "grant_type=refresh_token&client_id=$DJEZZY_CLIENT_ID&scope=openid"

        val formBody = FormBody.Builder()
            .add("refresh_token", cleanRefresh)
            .add("scope", "openid")
            .add("client_id", DJEZZY_CLIENT_ID)
            .add("client_secret", DJEZZY_CLIENT_SECRET)
            .add("grant_type", "refresh_token")
            .build()

        val request = Request.Builder()
            .url(OAUTH_TOKEN_URL)
            .post(formBody)
            .header("User-Agent", DJEZZY_USER_AGENT)
            .header("Connection", "close")
            .header("Content-Type", "application/x-www-form-urlencoded")
            .header("Cache-Control", "no-cache")
            .build()

        try {
            okHttpClient.newCall(request).execute().use { response ->
                val duration = System.currentTimeMillis() - startMs
                val rawBody = response.body?.string().orEmpty()
                val json = runCatching { JSONObject(rawBody) }.getOrNull()
                val newAccess = json?.optString("access_token").orEmpty()
                val newRefresh = json?.optString("refresh_token").orEmpty().ifBlank { cleanRefresh }
                val expiresIn = json?.optInt("expires_in", 3600) ?: 3600
                val ok = response.code == 200 && newAccess.isNotBlank()

                val record = DjezzyApiCallRecord(
                    httpMethod = "POST",
                    endpointUrl = OAUTH_TOKEN_URL,
                    statusCode = response.code,
                    durationMs = duration,
                    requestSummary = requestSummary,
                    responseBody = rawBody.ifBlank { "{\"status\": ${response.code}}" },
                    isSuccess = ok,
                    parsedMessage = if (ok) {
                        "Refreshed real Djezzy App OAuth2 token for +$msisdn"
                    } else {
                        parseJsonMessage(rawBody, "HTTP ${response.code}: Refresh token expired, re-verify OTP")
                    }
                )
                DjezzyOtpTokenResult(
                    isSuccess = ok,
                    accessToken = newAccess,
                    refreshToken = newRefresh,
                    expiresIn = expiresIn,
                    callRecord = record
                )
            }
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startMs
            val errDetail = "${e.javaClass.simpleName}: ${e.localizedMessage ?: "Network error"}"
            val record = DjezzyApiCallRecord(
                httpMethod = "POST",
                endpointUrl = OAUTH_TOKEN_URL,
                statusCode = 0,
                durationMs = duration,
                requestSummary = requestSummary,
                responseBody = "{\"error\": \"${errDetail.replace("\"", "'")}\"}",
                isSuccess = false,
                parsedMessage = errDetail
            )
            DjezzyOtpTokenResult(
                isSuccess = false,
                accessToken = "",
                refreshToken = "",
                expiresIn = 0,
                callRecord = record
            )
        }
    }

    /**
     * Step 3: Real Offer / Gift Activation on Djezzy's Mobile App API
     * POST https://apim.djezzy.dz/djezzy-api/api/v1/subscribers/{msisdn}/subscription-product
     * Matches the exact Djezzy Mobile App payload for GIFTWALKWIN1GO, GIFTWALKWIN2GO, and real Djezzy App products.
     */
    suspend fun activateSubscriptionProduct(
        rawPhoneNumber: String,
        accessToken: String,
        productId: String,
        giftCode: String,
        serviceId: String = "WALKWIN",
        stepsCount: Int = 10000
    ): DjezzyApiCallRecord = withContext(Dispatchers.IO) {
        val msisdn = formatMsisdn(rawPhoneNumber)
        val endpoint = "$SUBSCRIBERS_BASE_URL/$msisdn/subscription-product"
        val startMs = System.currentTimeMillis()

        val dataObj = JSONObject().apply {
            put("id", productId)
            put("type", "products")
            if (serviceId == "WALKWIN" || productId == "GIFTWALKWIN") {
                put("meta", JSONObject().apply {
                    put("services", JSONObject().apply {
                        put("steps", stepsCount)
                        put("code", giftCode)
                        put("id", "WALKWIN")
                    })
                })
            }
        }
        val payloadJson = JSONObject().apply {
            put("data", dataObj)
        }.toString()

        val requestBody = payloadJson.toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url(endpoint)
            .post(requestBody)
            .header("User-Agent", DJEZZY_USER_AGENT)
            .header("Connection", "Keep-Alive")
            .header("Content-Type", "application/json; charset=utf-8")
            .header("Authorization", "Bearer ${accessToken.trim()}")
            .build()

        try {
            okHttpClient.newCall(request).execute().use { response ->
                val duration = System.currentTimeMillis() - startMs
                val rawBody = response.body?.string().orEmpty()
                val json = runCatching { JSONObject(rawBody) }.getOrNull()
                val apiMsg = json?.optString("message").orEmpty()
                val expectedSuccessMsg = "the subscription to the product $giftCode successfully done"
                val isActivated = (response.code == 200 || response.code == 201) && (
                    apiMsg.equals(expectedSuccessMsg, ignoreCase = true) ||
                        apiMsg.contains("successfully done", ignoreCase = true) ||
                        (apiMsg.isBlank() && response.isSuccessful)
                    )

                DjezzyApiCallRecord(
                    httpMethod = "POST",
                    endpointUrl = endpoint,
                    statusCode = response.code,
                    durationMs = duration,
                    requestSummary = payloadJson,
                    responseBody = rawBody.ifBlank { "{\"status\": ${response.code}}" },
                    isSuccess = isActivated,
                    parsedMessage = apiMsg.ifBlank {
                        parseJsonMessage(rawBody, "HTTP ${response.code}")
                    }
                )
            }
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startMs
            val errDetail = "${e.javaClass.simpleName}: ${e.localizedMessage ?: "Connection failed"}"
            DjezzyApiCallRecord(
                httpMethod = "POST",
                endpointUrl = endpoint,
                statusCode = 0,
                durationMs = duration,
                requestSummary = payloadJson,
                responseBody = "{\"error\": \"${errDetail.replace("\"", "'")}\"}",
                isSuccess = false,
                parsedMessage = errDetail
            )
        }
    }

    /**
     * Fetches the real subscriber profile, balance, connected products, and eligible offers from the Djezzy App API:
     * GET https://apim.djezzy.dz/djezzy-api/api/v1/subscribers/{msisdn}?include=connected-products,eligible-products,balance,usage
     */
    suspend fun fetchSubscriberProfileAndOffers(
        rawPhoneNumber: String,
        accessToken: String
    ): DjezzySubscriberSyncResult = withContext(Dispatchers.IO) {
        val msisdn = formatMsisdn(rawPhoneNumber)
        val endpoint = "$SUBSCRIBERS_BASE_URL/$msisdn?include=connected-products,eligible-products,products,balance,usage"
        val startMs = System.currentTimeMillis()

        val request = Request.Builder()
            .url(endpoint)
            .get()
            .header("User-Agent", DJEZZY_USER_AGENT)
            .header("Connection", "Keep-Alive")
            .header("Accept", "application/json")
            .header("Authorization", "Bearer ${accessToken.trim()}")
            .build()

        try {
            okHttpClient.newCall(request).execute().use { response ->
                val duration = System.currentTimeMillis() - startMs
                val rawBody = response.body?.string().orEmpty()
                val ok = response.isSuccessful
                val json = runCatching { JSONObject(rawBody) }.getOrNull()
                val dataObj = json?.optJSONObject("data")
                val attrs = dataObj?.optJSONObject("attributes")

                var parsedCreditDa: Int? = attrs?.optDouble("mainBalance", -1.0)?.takeIf { it >= 0 }?.toInt()
                    ?: attrs?.optDouble("balance", -1.0)?.takeIf { it >= 0 }?.toInt()
                    ?: attrs?.optDouble("credit", -1.0)?.takeIf { it >= 0 }?.toInt()

                var parsedDataGb: Double? = attrs?.optDouble("dataBalanceGb", -1.0)?.takeIf { it >= 0.0 }
                    ?: attrs?.optDouble("internetBalanceGb", -1.0)?.takeIf { it >= 0.0 }

                val planName = attrs?.optString("profileName")?.takeIf { it.isNotBlank() }
                    ?: attrs?.optString("offerName")?.takeIf { it.isNotBlank() }
                    ?: attrs?.optString("commercialName")?.takeIf { it.isNotBlank() }

                val activeCodes = mutableListOf<String>()
                val fetchedOffers = mutableListOf<DjezzyOffer>()
                var accumulatedDataGb = 0.0
                var foundDataItem = false

                // Parse balances array if nested in data.attributes.balances
                val attrBalances = attrs?.optJSONArray("balances")
                if (attrBalances != null) {
                    for (bIdx in 0 until attrBalances.length()) {
                        val bObj = attrBalances.optJSONObject(bIdx) ?: continue
                        val unit = bObj.optString("unit").uppercase()
                        val typeStr = (bObj.optString("type") + " " + bObj.optString("name") + " " + bObj.optString("category")).uppercase()
                        val rawVal = bObj.optDouble("remainingValue", Double.NaN)
                            .let { if (it.isNaN()) bObj.optDouble("value", Double.NaN) else it }
                            .let { if (it.isNaN()) bObj.optDouble("balance", Double.NaN) else it }
                            .let { if (it.isNaN()) bObj.optDouble("amount", 0.0) else it }

                        if (unit.contains("DA") || unit.contains("DZD") || typeStr.contains("MAIN") || typeStr.contains("CREDIT") || typeStr.contains("MONEY")) {
                            val normalizedDa = if (rawVal > 50000) (rawVal / 100.0).toInt() else rawVal.toInt()
                            if (parsedCreditDa == null || normalizedDa > parsedCreditDa) {
                                parsedCreditDa = normalizedDa
                            }
                        } else if (unit.contains("GO") || unit.contains("GB") || unit.contains("MO") || unit.contains("MB") || unit.contains("KO") || unit.contains("KB") || unit.contains("OCTET") || unit.contains("BYTE") || typeStr.contains("DATA") || typeStr.contains("INTERNET")) {
                            val gb = convertRawDataToGb(rawVal, unit)
                            if (gb >= 0.0) {
                                accumulatedDataGb += gb
                                foundDataItem = true
                            }
                        }
                    }
                }

                val included: JSONArray? = json?.optJSONArray("included")
                if (included != null) {
                    for (i in 0 until included.length()) {
                        val item = included.optJSONObject(i) ?: continue
                        val itemType = item.optString("type")
                        val itemId = item.optString("id")
                        val itemAttrs = item.optJSONObject("attributes") ?: JSONObject()
                        val code = itemAttrs.optString("code").ifBlank { itemId }
                        val name = itemAttrs.optString("name")
                            .ifBlank { itemAttrs.optString("label") }
                            .ifBlank { code }
                        val price = itemAttrs.optInt("price", 0)
                        val validity = itemAttrs.optString("validity").ifBlank { "Djezzy App Offer" }
                        val quotaGb = itemAttrs.optInt("dataQuotaGb", 0)

                        // Check if this included item is a balance/usage record
                        val unit = itemAttrs.optString("unit").uppercase()
                        val balCategory = (itemType + " " + itemAttrs.optString("balanceType") + " " + name).uppercase()
                        val rawBalanceVal = itemAttrs.optDouble("remainingValue", Double.NaN)
                            .let { if (it.isNaN()) itemAttrs.optDouble("value", Double.NaN) else it }
                            .let { if (it.isNaN()) itemAttrs.optDouble("balance", Double.NaN) else it }
                            .let { if (it.isNaN()) itemAttrs.optDouble("amount", Double.NaN) else it }

                        if (!rawBalanceVal.isNaN() && (itemType.contains("balance", ignoreCase = true) || itemType.contains("usage", ignoreCase = true))) {
                            if (unit.contains("DA") || unit.contains("DZD") || balCategory.contains("MAIN") || balCategory.contains("CREDIT")) {
                                val normalizedDa = if (rawBalanceVal > 50000) (rawBalanceVal / 100.0).toInt() else rawBalanceVal.toInt()
                                if (parsedCreditDa == null) parsedCreditDa = normalizedDa
                            } else if (unit.contains("GO") || unit.contains("GB") || unit.contains("MO") || unit.contains("MB") || unit.contains("OCTET") || unit.contains("BYTE") || balCategory.contains("DATA") || balCategory.contains("INTERNET")) {
                                val gb = convertRawDataToGb(rawBalanceVal, unit)
                                if (gb >= 0.0) {
                                    accumulatedDataGb += gb
                                    foundDataItem = true
                                }
                            }
                        }

                        if (itemType.contains("connected", ignoreCase = true)) {
                            if (code.isNotBlank()) activeCodes.add(code)
                        } else if (itemType.contains("product", ignoreCase = true) || itemType.contains("eligible", ignoreCase = true)) {
                            if (itemId.isNotBlank()) {
                                fetchedOffers.add(
                                    DjezzyOffer(
                                        id = "live_${itemId.lowercase()}",
                                        titleEn = name,
                                        titleAr = name,
                                        titleFr = name,
                                        category = if (price == 0) OfferCategory.BOT_EXCLUSIVE else OfferCategory.HAYLA,
                                        dataGb = quotaGb.coerceAtLeast(1),
                                        priceDa = price,
                                        validityTextEn = validity,
                                        validityTextAr = validity,
                                        validityTextFr = validity,
                                        callsBonusEn = "Synced from Djezzy App API ($code)",
                                        callsBonusAr = "عرض حقيقي من تطبيق جازي ($code)",
                                        callsBonusFr = "Offre synchronisée depuis Djezzy App ($code)",
                                        ussdCode = "*720#",
                                        apiProductId = itemId,
                                        apiGiftCode = code,
                                        apiServiceId = "PREPAID",
                                        requiredSteps = 0,
                                        isExclusivePromo = (price == 0),
                                        badgeTag = "LIVE FROM SIM"
                                    )
                                )
                            }
                        }
                    }
                }

                if (parsedDataGb == null && foundDataItem) {
                    parsedDataGb = accumulatedDataGb
                }
                val creditDa = parsedCreditDa
                val dataGb = parsedDataGb

                val record = DjezzyApiCallRecord(
                    httpMethod = "GET",
                    endpointUrl = endpoint,
                    statusCode = response.code,
                    durationMs = duration,
                    requestSummary = "GET /subscribers/$msisdn",
                    responseBody = rawBody.ifBlank { "{\"status\": ${response.code}}" },
                    isSuccess = ok,
                    parsedMessage = if (ok) "Synced live Djezzy SIM profile for +$msisdn" else parseJsonMessage(rawBody, "HTTP ${response.code}")
                )

                DjezzySubscriberSyncResult(
                    isSuccess = ok,
                    creditBalanceDa = creditDa,
                    dataBalanceGb = dataGb,
                    planName = planName,
                    activeProductCodes = activeCodes,
                    dynamicOffers = fetchedOffers,
                    callRecord = record
                )
            }
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startMs
            val errDetail = "${e.javaClass.simpleName}: ${e.localizedMessage ?: "Network error"}"
            DjezzySubscriberSyncResult(
                isSuccess = false,
                creditBalanceDa = null,
                dataBalanceGb = null,
                planName = null,
                activeProductCodes = emptyList(),
                dynamicOffers = emptyList(),
                callRecord = DjezzyApiCallRecord(
                    httpMethod = "GET",
                    endpointUrl = endpoint,
                    statusCode = 0,
                    durationMs = duration,
                    requestSummary = "GET /subscribers/$msisdn",
                    responseBody = "{\"error\": \"${errDetail.replace("\"", "'")}\"}",
                    isSuccess = false,
                    parsedMessage = errDetail
                )
            )
        }
    }

    /**
     * Real Djezzy Parrainage / Sponsorship 2GB Invitation
     * POST https://apim.djezzy.dz/djezzy-api/api/v1/subscribers/{msisdn}/invitations
     */
    suspend fun sendSponsorshipInvitation(
        senderPhone: String,
        invitedPhone: String,
        accessToken: String
    ): DjezzyApiCallRecord = withContext(Dispatchers.IO) {
        val senderMsisdn = formatMsisdn(senderPhone)
        val invitedMsisdn = formatMsisdn(invitedPhone)
        val endpoint = "$SUBSCRIBERS_BASE_URL/$senderMsisdn/invitations"
        val startMs = System.currentTimeMillis()

        val payloadJson = JSONObject().apply {
            put("data", JSONObject().apply {
                put("type", "invitations")
                put("attributes", JSONObject().apply {
                    put("msisdnReciever", invitedMsisdn)
                })
            })
        }.toString()

        val request = Request.Builder()
            .url(endpoint)
            .post(payloadJson.toRequestBody("application/json; charset=utf-8".toMediaType()))
            .header("User-Agent", DJEZZY_USER_AGENT)
            .header("Connection", "Keep-Alive")
            .header("Content-Type", "application/json; charset=utf-8")
            .header("Authorization", "Bearer ${accessToken.trim()}")
            .build()

        try {
            okHttpClient.newCall(request).execute().use { response ->
                val duration = System.currentTimeMillis() - startMs
                val rawBody = response.body?.string().orEmpty()
                val ok = response.isSuccessful
                DjezzyApiCallRecord(
                    httpMethod = "POST",
                    endpointUrl = endpoint,
                    statusCode = response.code,
                    durationMs = duration,
                    requestSummary = payloadJson,
                    responseBody = rawBody.ifBlank { "{\"status\": ${response.code}}" },
                    isSuccess = ok,
                    parsedMessage = parseJsonMessage(
                        rawBody,
                        if (ok) "2GB Referral Invitation sent to +$invitedMsisdn" else "HTTP ${response.code}: Invitation rejected"
                    )
                )
            }
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startMs
            val errDetail = "${e.javaClass.simpleName}: ${e.localizedMessage ?: "Connection failed"}"
            DjezzyApiCallRecord(
                httpMethod = "POST",
                endpointUrl = endpoint,
                statusCode = 0,
                durationMs = duration,
                requestSummary = payloadJson,
                responseBody = "{\"error\": \"${errDetail.replace("\"", "'")}\"}",
                isSuccess = false,
                parsedMessage = errDetail
            )
        }
    }

    private fun convertRawDataToGb(rawVal: Double, unit: String): Double {
        if (rawVal.isNaN() || rawVal < 0.0) return 0.0
        return when {
            unit.contains("OCTET") || unit.contains("BYTE") -> rawVal / (1024.0 * 1024.0 * 1024.0)
            unit.contains("KO") || unit.contains("KB") -> rawVal / (1024.0 * 1024.0)
            unit.contains("MO") || unit.contains("MB") -> rawVal / 1024.0
            unit.contains("GO") || unit.contains("GB") -> rawVal
            rawVal > 10_000_000 -> rawVal / (1024.0 * 1024.0 * 1024.0) // Bytes
            rawVal > 10_000 -> rawVal / (1024.0 * 1024.0) // KB
            rawVal > 200 -> rawVal / 1024.0 // MB
            else -> rawVal // GB
        }
    }

    private fun parseJsonMessage(rawBody: String, fallback: String): String {
        if (rawBody.isBlank()) return fallback
        return try {
            val obj = JSONObject(rawBody)
            val faultObj = obj.optJSONObject("fault")
            when {
                obj.optString("message").isNotBlank() -> obj.optString("message")
                obj.optString("error_description").isNotBlank() -> obj.optString("error_description")
                faultObj != null && faultObj.optString("description").isNotBlank() -> faultObj.optString("description")
                obj.optString("error").isNotBlank() -> obj.optString("error")
                else -> fallback
            }
        } catch (_: Exception) {
            fallback
        }
    }
}
