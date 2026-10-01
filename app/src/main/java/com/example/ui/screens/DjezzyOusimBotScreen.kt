package com.example.ui.screens

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SimCard
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.SimCard
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.ActivationLogEntity
import com.example.data.local.SavedSimAccountEntity
import com.example.data.local.SimSessionEntity
import com.example.data.model.AppLanguage
import com.example.data.model.DjezzyOffer
import com.example.data.model.DjezzyOusimRepository
import com.example.data.model.UssdServiceCode
import com.example.ui.theme.DjezzyTextMuted
import com.example.ui.theme.DjezzyTextPrimary
import com.example.ui.theme.DjezzyTextSecondary
import com.example.ui.theme.NotiBg
import com.example.ui.theme.NotiBlue
import com.example.ui.theme.NotiBlueLight
import com.example.ui.theme.NotiBorder
import com.example.ui.theme.NotiRed
import com.example.ui.theme.NotiSurface
import com.example.ui.theme.NotiSurfaceSoft
import com.example.ui.theme.OusimThemeManager
import com.example.ui.theme.OusimThemePreset
import com.example.ui.viewmodel.ActivatedOfferReceipt
import com.example.ui.viewmodel.OusimBotViewModel
import kotlinx.coroutines.launch

/**
 * Bottom Navigation Tabs matching the NotiByte screenshot:
 * [الإعدادات (Settings)] | [معلومات (Info)] | [شاهد واربح (Watch & Win)] | [الرئيسية (Home)]
 */
enum class NotiBottomTab {
    SETTINGS,
    INFO,
    WATCH_AND_WIN,
    HOME
}

/**
 * Home Sub-Tabs matching the NotiByte screenshot:
 * [المزيد (More)] | [شراء (Buy)] | [الشريحة (SIM)]
 */
enum class NotiHomeSubTab {
    MORE,
    BUY,
    SIM
}

@Composable
fun DjezzyOusimBotScreen(
    modifier: Modifier = Modifier,
    viewModel: OusimBotViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val simSessionState by viewModel.simSessionFlow.collectAsStateWithLifecycle()
    val savedSimAccounts by viewModel.savedSimAccountsFlow.collectAsStateWithLifecycle()
    val activationLogs by viewModel.activationLogsFlow.collectAsStateWithLifecycle()
    val liveOffers by viewModel.liveOffersList.collectAsStateWithLifecycle()
    val isApiBusy by viewModel.isApiBusy.collectAsStateWithLifecycle()
    val activationStageText by viewModel.activationStageText.collectAsStateWithLifecycle()
    val latestReceipt by viewModel.latestReceipt.collectAsStateWithLifecycle()
    val lastUssdResult by viewModel.lastUssdResult.collectAsStateWithLifecycle()

    val session = simSessionState ?: SimSessionEntity(
        phoneNumber = "0770842090",
        msisdn = "213770842090",
        activeDataGb = 4.50,
        activeCreditDa = 1500,
        isOtpVerified = true
    )
    val language = AppLanguage.entries.find { it.code == session.preferredLanguage } ?: AppLanguage.AR

    var bottomTab by rememberSaveable { mutableStateOf(NotiBottomTab.HOME) }
    var homeSubTab by rememberSaveable { mutableStateOf(NotiHomeSubTab.SIM) }
    var isPhoneMasked by rememberSaveable { mutableStateOf(true) }

    // Dialog states for the NotiByte actions
    var showSimManagerDialog by rememberSaveable { mutableStateOf(false) }
    var showInviteDialog by rememberSaveable { mutableStateOf(false) }
    var showUsefulCodesDialog by rememberSaveable { mutableStateOf(false) }
    var showNotificationsDialog by rememberSaveable { mutableStateOf(false) }
    var selectedOfferToConfirm by remember { mutableStateOf<DjezzyOffer?>(null) }

    val permissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        viewModel.refreshHardwareState()
        viewModel.startRealStepSensor()
    }

    if (bottomTab != NotiBottomTab.HOME) {
        BackHandler {
            bottomTab = NotiBottomTab.HOME
        }
    } else if (homeSubTab != NotiHomeSubTab.SIM) {
        BackHandler {
            homeSubTab = NotiHomeSubTab.SIM
        }
    }

    fun showFeedback(msg: String) {
        scope.launch {
            snackbarHostState.showSnackbar(msg)
        }
    }

    fun copyText(label: String, value: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        clipboard?.setPrimaryClip(ClipData.newPlainText(label, value))
        showFeedback("📋 تم نسخ $value")
    }

    // Featured offers on the 2x2 NotiByte Home Grid
    val free2GbOffer = liveOffers.find { it.id == "djezzy_walk_2gb" }
        ?: DjezzyOusimRepository.allOffers.first()
    val paid4Gb70DaOffer = liveOffers.find { it.id == "djezzy_4gb_70da" }
        ?: DjezzyOusimRepository.allOffers[1]

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(NotiBg)
            .statusBarsPadding(),
        containerColor = NotiBg,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NotiSurface)
            ) {
                // 1. Official NotiByte Top Header: Bell Icon on Left, "نوتي بايت" + Blue/Red Stepped Logo on Right
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Notification Bell Icon
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(NotiSurfaceSoft)
                            .clickable { showNotificationsDialog = true }
                            .testTag("btn_notifications"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "الإشعارات",
                            tint = DjezzyTextPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        if (activationLogs.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                                    .size(9.dp)
                                    .clip(CircleShape)
                                    .background(NotiRed)
                            )
                        }
                    }

                    // Right "نوتي بايت" + Stepped Pixel Logo in Blue & Red
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { bottomTab = NotiBottomTab.HOME }
                            .testTag("header_notibyte_brand")
                    ) {
                        Text(
                            text = when (language) {
                                AppLanguage.AR -> "نوتي بايت"
                                AppLanguage.FR -> "NotiByte"
                                AppLanguage.EN -> "NotiByte"
                            },
                            color = DjezzyTextPrimary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        NotiByteSteppedLogo(
                            blueColor = NotiBlue,
                            redColor = NotiRed,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }

                // Live Progress Bar during API / Offer Activation
                AnimatedVisibility(visible = isApiBusy || !activationStageText.isNullOrBlank()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(NotiSurfaceSoft)
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = activationStageText ?: "جاري الاتصال بتطبيق جازي...",
                            color = NotiBlue,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            color = NotiRed,
                            trackColor = NotiBorder,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(999.dp))
                        )
                    }
                }

                // Live Activation / 24h Cooldown Warning Banner
                AnimatedVisibility(visible = latestReceipt != null) {
                    latestReceipt?.let { receipt ->
                        NotiByteReceiptBanner(
                            receipt = receipt,
                            onDismiss = { viewModel.dismissLatestReceipt() }
                        )
                    }
                }
            }
        },
        bottomBar = {
            // Official NotiByte 4-Tab Bottom Bar: [الإعدادات] | [معلومات] | [شاهد واربح] | [الرئيسية]
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                color = NotiSurface,
                shadowElevation = 8.dp,
                border = BorderStroke(1.dp, NotiBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NotiBottomBarItem(
                        label = when (language) {
                            AppLanguage.AR -> "الإعدادات"
                            AppLanguage.FR -> "Paramètres"
                            AppLanguage.EN -> "Settings"
                        },
                        selected = bottomTab == NotiBottomTab.SETTINGS,
                        selectedIcon = Icons.Filled.WbSunny,
                        unselectedIcon = Icons.Outlined.WbSunny,
                        activeColor = NotiRed,
                        onClick = { bottomTab = NotiBottomTab.SETTINGS },
                        testTag = "nav_settings"
                    )

                    NotiBottomBarItem(
                        label = when (language) {
                            AppLanguage.AR -> "معلومات"
                            AppLanguage.FR -> "Infos"
                            AppLanguage.EN -> "Info"
                        },
                        selected = bottomTab == NotiBottomTab.INFO,
                        selectedIcon = Icons.Filled.Info,
                        unselectedIcon = Icons.Outlined.Info,
                        activeColor = NotiBlue,
                        onClick = { bottomTab = NotiBottomTab.INFO },
                        testTag = "nav_info"
                    )

                    NotiBottomBarItem(
                        label = when (language) {
                            AppLanguage.AR -> "شاهد واربح"
                            AppLanguage.FR -> "Gagner Go"
                            AppLanguage.EN -> "Watch & Win"
                        },
                        selected = bottomTab == NotiBottomTab.WATCH_AND_WIN,
                        selectedIcon = Icons.Filled.CardGiftcard,
                        unselectedIcon = Icons.Outlined.CardGiftcard,
                        activeColor = NotiRed,
                        onClick = { bottomTab = NotiBottomTab.WATCH_AND_WIN },
                        testTag = "nav_watch_win"
                    )

                    NotiBottomBarItem(
                        label = when (language) {
                            AppLanguage.AR -> "الرئيسية"
                            AppLanguage.FR -> "Accueil"
                            AppLanguage.EN -> "Home"
                        },
                        selected = bottomTab == NotiBottomTab.HOME,
                        selectedIcon = Icons.Filled.Home,
                        unselectedIcon = Icons.Outlined.Home,
                        activeColor = NotiBlue,
                        onClick = { bottomTab = NotiBottomTab.HOME },
                        testTag = "nav_home"
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = bottomTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "notiBottomTabTransition"
            ) { tab ->
                when (tab) {
                    NotiBottomTab.HOME -> {
                        NotiByteHomeContent(
                            language = language,
                            session = session,
                            savedSimAccounts = savedSimAccounts,
                            homeSubTab = homeSubTab,
                            onSelectSubTab = { homeSubTab = it },
                            isPhoneMasked = isPhoneMasked,
                            onTogglePhoneMask = { isPhoneMasked = !isPhoneMasked },
                            maskedPhoneText = viewModel.maskSimNumber(session.phoneNumber),
                            onCopyPhone = { copyText("Djezzy Number", session.phoneNumber) },
                            onOpenSimManager = { showSimManagerDialog = true },
                            free2GbOffer = free2GbOffer,
                            paid4Gb70DaOffer = paid4Gb70DaOffer,
                            allOffers = liveOffers,
                            activationLogs = activationLogs,
                            getRemainingCooldownMs = { offer ->
                                viewModel.getRemainingCooldownMs(offer, session, activationLogs)
                            },
                            onClickOfferCard = { offer ->
                                selectedOfferToConfirm = offer
                            },
                            onActivateOfferImmediate = { offer ->
                                viewModel.activateOfferReal(offer, ::showFeedback)
                            },
                            onOpenInviteDialog = { showInviteDialog = true },
                            onOpenUsefulCodesDialog = { showUsefulCodesDialog = true },
                            onSyncBalance = { viewModel.fetchRealSubscriberBalance(::showFeedback) },
                            onRechargeCredit = { amount -> viewModel.rechargeSimCredit(amount, ::showFeedback) },
                            onSwitchSimAccount = { acc -> viewModel.switchActiveSimAccount(acc, ::showFeedback) },
                            onDeleteSimAccount = { msisdn -> viewModel.deleteSavedSimAccount(msisdn, ::showFeedback) },
                            onResetCooldowns = { viewModel.resetOfferCooldowns(::showFeedback) }
                        )
                    }

                    NotiBottomTab.WATCH_AND_WIN -> {
                        NotiByteWatchAndWinTab(
                            language = language,
                            session = session,
                            offers = liveOffers.filter { it.priceDa == 0 },
                            activationLogs = activationLogs,
                            getRemainingCooldownMs = { offer ->
                                viewModel.getRemainingCooldownMs(offer, session, activationLogs)
                            },
                            onActivateFreeOffer = { offer ->
                                viewModel.activateOfferReal(offer, ::showFeedback)
                            },
                            onAddSteps = { steps ->
                                viewModel.addStepsToSession(steps)
                                showFeedback("🚶 تمت إضافة +$steps خطوة إلى عداد المشي!")
                            },
                            onRequestStepPermission = {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                    permissionsLauncher.launch(arrayOf(Manifest.permission.ACTIVITY_RECOGNITION))
                                }
                            }
                        )
                    }

                    NotiBottomTab.INFO -> {
                        NotiByteInfoAndDevTab(
                            language = language,
                            session = session,
                            savedSimsCount = savedSimAccounts.size,
                            activationLogs = activationLogs,
                            onCopyText = ::copyText,
                            onOpenInstagram = {
                                runCatching {
                                    val intent = Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse(DjezzyOusimRepository.CREATOR_INSTAGRAM_URL)
                                    )
                                    context.startActivity(intent)
                                }.onFailure {
                                    copyText("Instagram", DjezzyOusimRepository.CREATOR_INSTAGRAM)
                                }
                            }
                        )
                    }

                    NotiBottomTab.SETTINGS -> {
                        NotiByteSettingsTab(
                            language = language,
                            session = session,
                            onSelectLanguage = { viewModel.setLanguage(it) },
                            onSelectThemePreset = { viewModel.selectThemePreset(it) },
                            onRechargeCredit = { amount -> viewModel.rechargeSimCredit(amount, ::showFeedback) },
                            onResetCooldowns = { viewModel.resetOfferCooldowns(::showFeedback) },
                            onSaveCustomToken = { token ->
                                viewModel.saveCustomBearerToken(token)
                                showFeedback("✅ تم حفظ توكن تطبيق جازي بنجاح!")
                            },
                            onRefreshToken = { viewModel.refreshCurrentSimToken(::showFeedback) }
                        )
                    }
                }
            }
        }
    }

    // 1. Offer Activation Confirmation Dialog (for both Free & Paid Offers)
    selectedOfferToConfirm?.let { offer ->
        val cooldownMs = viewModel.getRemainingCooldownMs(offer, session, activationLogs)
        val isOnCooldown = cooldownMs > 0L
        val hoursLeft = (cooldownMs / (1000 * 60 * 60)).toInt()
        val minsLeft = ((cooldownMs / (1000 * 60)) % 60).toInt()
        val hasEnoughCredit = offer.priceDa == 0 || session.activeCreditDa >= offer.priceDa

        AlertDialog(
            onDismissRequest = { selectedOfferToConfirm = null },
            containerColor = NotiSurface,
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = if (offer.priceDa == 0) Icons.Filled.Public else Icons.Filled.ShoppingCart,
                        contentDescription = null,
                        tint = if (offer.priceDa == 0) NotiBlue else NotiRed
                    )
                    Text(
                        text = offer.localizedTitle(language),
                        color = DjezzyTextPrimary,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Surface(
                        color = NotiSurfaceSoft,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, NotiBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "📱 الشريحة: ${session.phoneNumber}",
                                color = DjezzyTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "🌐 الإنترنت: ${"%.2f".format(session.activeDataGb)} جيغا",
                                    color = NotiBlue,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "💳 الرصيد: ${session.activeCreditDa} دج",
                                    color = NotiRed,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    Text(
                        text = "• الحجم: +${offer.dataGb} جيغا إنترنت\n" +
                            "• السعر: ${if (offer.priceDa == 0) "مجاناً (0 دج)" else "${offer.priceDa} دج"}\n" +
                            "• الصلاحية: ${offer.localizedValidity(language)}\n" +
                            "• كود تطبيق جازي: ${offer.apiGiftCode}",
                        color = DjezzyTextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 20.sp
                    )

                    if (isOnCooldown) {
                        Surface(
                            color = NotiRed.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, NotiRed),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "⚠️ لا يمكنك استخدام هذا العرض الآن! تم تفعيله مسبقاً، يرجى الانتظار (${hoursLeft} ساعة و ${minsLeft} دقيقة).",
                                color = NotiRed,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    } else if (!hasEnoughCredit) {
                        Surface(
                            color = NotiRed.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, NotiRed),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "⚠️ رصيدك الحالي (${session.activeCreditDa} دج) غير كافٍ لتفعيل هذا العرض (${offer.priceDa} دج).",
                                    color = NotiRed,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedButton(
                                    onClick = {
                                        viewModel.rechargeSimCredit(1000, ::showFeedback)
                                    },
                                    border = BorderStroke(1.dp, NotiBlue),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "💳 شحن +1000 دج فوراً للتجربة",
                                        color = NotiBlue,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val target = offer
                        selectedOfferToConfirm = null
                        viewModel.activateOfferReal(target, ::showFeedback)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isOnCooldown) NotiRed else NotiBlue
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("btn_confirm_activate_offer")
                ) {
                    Text(
                        text = if (isOnCooldown) {
                            "تحقق من حالة العرض"
                        } else if (offer.priceDa == 0) {
                            "تفعيل مجاناً (+${offer.dataGb} جيغا)"
                        } else {
                            "شراء وتفعيل (${offer.priceDa} دج)"
                        },
                        color = Color.White,
                        fontWeight = FontWeight.Black
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedOfferToConfirm = null }) {
                    Text("إلغاء", color = DjezzyTextMuted, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // 2. Multi-SIM & SMS OTP Verification Dialog (opened when tapping '>' or phone pill)
    if (showSimManagerDialog) {
        NotiByteSimManagerDialog(
            session = session,
            savedSimAccounts = savedSimAccounts,
            isApiBusy = isApiBusy,
            onDismiss = { showSimManagerDialog = false },
            onSendOtp = { phone -> viewModel.sendRealSmsOtp(phone, ::showFeedback) },
            onVerifyOtp = { code ->
                viewModel.verifyRealSmsOtp(code, ::showFeedback)
                showSimManagerDialog = false
            },
            onQuickConnectSim = { phone ->
                viewModel.quickConnectSimNumber(phone, onFeedback = ::showFeedback)
                showSimManagerDialog = false
            },
            onSwitchSim = { acc ->
                viewModel.switchActiveSimAccount(acc, ::showFeedback)
                showSimManagerDialog = false
            },
            onDeleteSim = { msisdn -> viewModel.deleteSavedSimAccount(msisdn, ::showFeedback) }
        )
    }

    // 3. Invite a Number Dialog ("دعوة رقم" — Parrainage +2GB)
    if (showInviteDialog) {
        NotiByteInviteNumberDialog(
            currentPhone = session.phoneNumber,
            onDismiss = { showInviteDialog = false },
            onSendInvite = { friendPhone ->
                showInviteDialog = false
                viewModel.sendRealReferralInvitation(friendPhone, ::showFeedback)
            }
        )
    }

    // 4. Useful USSD Codes Dialog ("رموز مفيدة")
    if (showUsefulCodesDialog) {
        NotiByteUsefulCodesDialog(
            language = language,
            codes = DjezzyOusimRepository.ussdCodes,
            lastUssdResult = lastUssdResult,
            onDismiss = { showUsefulCodesDialog = false },
            onExecuteUssd = { code -> viewModel.executeRealUssd(code, ::showFeedback) },
            onCopyCode = { code -> copyText("USSD", code) }
        )
    }

    // 5. Notifications & Activation Logs Dialog (Bell icon on top-left)
    if (showNotificationsDialog) {
        NotiByteNotificationsDialog(
            session = session,
            logs = activationLogs,
            onDismiss = { showNotificationsDialog = false },
            onResetCooldowns = {
                viewModel.resetOfferCooldowns(::showFeedback)
                showNotificationsDialog = false
            }
        )
    }
}

/**
 * Custom Canvas drawing of the iconic NotiByte Stepped Pixel Logo in Blue & Red
 */
@Composable
private fun NotiByteSteppedLogo(
    blueColor: Color,
    redColor: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val u = size.width / 3f
        val r = CornerRadius(3.dp.toPx(), 3.dp.toPx())

        // Top-left red block
        drawRoundRect(
            color = redColor,
            topLeft = Offset(0f, 0f),
            size = Size(u * 1.15f, u * 1.15f),
            cornerRadius = r
        )
        // Center blue/red transition block
        drawRoundRect(
            color = blueColor,
            topLeft = Offset(u * 0.9f, u * 0.9f),
            size = Size(u * 1.15f, u * 1.15f),
            cornerRadius = r
        )
        // Bottom-right red block
        drawRoundRect(
            color = redColor,
            topLeft = Offset(u * 1.8f, u * 1.8f),
            size = Size(u * 1.15f, u * 1.15f),
            cornerRadius = r
        )
        // Accent bottom-left small pixel in blue
        drawRoundRect(
            color = blueColor.copy(alpha = 0.85f),
            topLeft = Offset(u * 0.1f, u * 1.85f),
            size = Size(u * 0.85f, u * 0.85f),
            cornerRadius = r
        )
    }
}

@Composable
private fun NotiBottomBarItem(
    label: String,
    selected: Boolean,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    activeColor: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .testTag(testTag)
    ) {
        Icon(
            imageVector = if (selected) selectedIcon else unselectedIcon,
            contentDescription = label,
            tint = if (selected) activeColor else DjezzyTextMuted,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            color = if (selected) activeColor else DjezzyTextMuted,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Medium
        )
    }
}

/**
 * Main Home Screen matching the exact NotiByte screenshot layout:
 * 1. Centered Phone Pill [Copy | Eye | 07xxxxxx54] + '>' Chevron
 * 2. 3 Sub-Tabs Row: [المزيد (...)] | [شراء (Cart)] | [الشريحة (SIM Pill)]
 * 3. Inside الشريحة:
 *    - 2x2 Action Grid:
 *      [4 جيغا - 70 دج (Cart)]   [2 جيغا مجانا (Globe)]
 *      [رموز مفيدة (Dialpad)]    [دعوة رقم (Envelope)]
 *    - Live Credit & Internet Balance Card at the bottom
 */
@Composable
private fun NotiByteHomeContent(
    language: AppLanguage,
    session: SimSessionEntity,
    savedSimAccounts: List<SavedSimAccountEntity>,
    homeSubTab: NotiHomeSubTab,
    onSelectSubTab: (NotiHomeSubTab) -> Unit,
    isPhoneMasked: Boolean,
    onTogglePhoneMask: () -> Unit,
    maskedPhoneText: String,
    onCopyPhone: () -> Unit,
    onOpenSimManager: () -> Unit,
    free2GbOffer: DjezzyOffer,
    paid4Gb70DaOffer: DjezzyOffer,
    allOffers: List<DjezzyOffer>,
    activationLogs: List<ActivationLogEntity>,
    getRemainingCooldownMs: (DjezzyOffer) -> Long,
    onClickOfferCard: (DjezzyOffer) -> Unit,
    onActivateOfferImmediate: (DjezzyOffer) -> Unit,
    onOpenInviteDialog: () -> Unit,
    onOpenUsefulCodesDialog: () -> Unit,
    onSyncBalance: () -> Unit,
    onRechargeCredit: (Int) -> Unit,
    onSwitchSimAccount: (SavedSimAccountEntity) -> Unit,
    onDeleteSimAccount: (String) -> Unit,
    onResetCooldowns: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(NotiBg),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Centered Phone Number Pill + '>' Chevron on the right (exact match to screenshot)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                // Centered Pill: [Copy] [Eye] [07xxxxxx90]
                Surface(
                    color = NotiBlue.copy(alpha = 0.10f),
                    shape = RoundedCornerShape(999.dp),
                    border = BorderStroke(1.dp, NotiBlue.copy(alpha = 0.30f)),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .testTag("phone_number_pill")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ContentCopy,
                            contentDescription = "Copy Phone Number",
                            tint = NotiBlue,
                            modifier = Modifier
                                .size(17.dp)
                                .clickable { onCopyPhone() }
                                .testTag("btn_copy_phone")
                        )
                        Icon(
                            imageVector = if (isPhoneMasked) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = "Toggle Phone Mask",
                            tint = NotiRed,
                            modifier = Modifier
                                .size(18.dp)
                                .clickable { onTogglePhoneMask() }
                                .testTag("btn_toggle_phone_mask")
                        )
                        Text(
                            text = if (isPhoneMasked) maskedPhoneText else session.phoneNumber,
                            color = NotiBlue,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.clickable { onOpenSimManager() }
                        )
                    }
                }

                // Right Chevron '>' to switch/add SIM or verify OTP
                IconButton(
                    onClick = onOpenSimManager,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(40.dp)
                        .testTag("btn_open_sim_manager")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Manage SIMs",
                        tint = DjezzyTextPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }

        // 2. Three Sub-Tabs Row: [المزيد (...)] | [شراء (Cart)] | [الشريحة (SIM)]
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NotiSubTabButton(
                    label = when (language) {
                        AppLanguage.AR -> "المزيد"
                        AppLanguage.FR -> "Plus"
                        AppLanguage.EN -> "More"
                    },
                    icon = Icons.Filled.MoreHoriz,
                    selected = homeSubTab == NotiHomeSubTab.MORE,
                    activeColor = NotiBlue,
                    onClick = { onSelectSubTab(NotiHomeSubTab.MORE) },
                    testTag = "subtab_more"
                )

                NotiSubTabButton(
                    label = when (language) {
                        AppLanguage.AR -> "شراء"
                        AppLanguage.FR -> "Acheter"
                        AppLanguage.EN -> "Buy"
                    },
                    icon = Icons.Outlined.ShoppingCart,
                    selected = homeSubTab == NotiHomeSubTab.BUY,
                    activeColor = NotiRed,
                    onClick = { onSelectSubTab(NotiHomeSubTab.BUY) },
                    testTag = "subtab_buy"
                )

                NotiSubTabButton(
                    label = when (language) {
                        AppLanguage.AR -> "الشريحة"
                        AppLanguage.FR -> "Carte SIM"
                        AppLanguage.EN -> "SIM"
                    },
                    icon = Icons.Outlined.SimCard,
                    selected = homeSubTab == NotiHomeSubTab.SIM,
                    activeColor = NotiBlue,
                    onClick = { onSelectSubTab(NotiHomeSubTab.SIM) },
                    testTag = "subtab_sim"
                )
            }
        }

        // 3. Sub-Tab Content
        when (homeSubTab) {
            NotiHomeSubTab.SIM -> {
                // Row 1 of 2x2 Grid: [4 جيغا - 70 دج (Cart)]  |  [2 جيغا مجانا (Globe)]
                item {
                    val free2GbCooldown = getRemainingCooldownMs(free2GbOffer)
                    val paid4GbCooldown = getRemainingCooldownMs(paid4Gb70DaOffer)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Top-Left Card: "4 جيغا - 70 دج" (Shopping Cart Icon)
                        NotiSquareGridCard(
                            title = when (language) {
                                AppLanguage.AR -> "4 جيغا - 70 دج"
                                AppLanguage.FR -> "4 Go - 70 DA"
                                AppLanguage.EN -> "4 GB - 70 DA"
                            },
                            subtitle = if (paid4GbCooldown > 0L) "مفعّل (انتظر 24 سا)" else "تفعيل فوري",
                            icon = Icons.Filled.ShoppingCart,
                            iconTint = NotiBlue,
                            accentBorder = if (paid4GbCooldown > 0L) NotiRed.copy(alpha = 0.5f) else NotiBorder,
                            badgeColor = NotiRed,
                            onClick = { onClickOfferCard(paid4Gb70DaOffer) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("card_4gb_70da")
                        )

                        // Top-Right Card: "2 جيغا مجانا" (Globe Icon)
                        NotiSquareGridCard(
                            title = when (language) {
                                AppLanguage.AR -> "2 جيغا مجانا"
                                AppLanguage.FR -> "2 Go Gratuit"
                                AppLanguage.EN -> "2 GB Free"
                            },
                            subtitle = if (free2GbCooldown > 0L) "مفعّل (انتظر 24 سا)" else "هدية مجانية",
                            icon = Icons.Filled.Public,
                            iconTint = NotiBlue,
                            accentBorder = if (free2GbCooldown > 0L) NotiRed.copy(alpha = 0.5f) else NotiBorder,
                            badgeColor = NotiBlue,
                            onClick = { onClickOfferCard(free2GbOffer) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("card_2gb_free")
                        )
                    }
                }

                // Row 2 of 2x2 Grid: [رموز مفيدة (Dialpad)]  |  [دعوة رقم (Envelope)]
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Bottom-Left Card: "رموز مفيدة" (Dialpad Icon)
                        NotiSquareGridCard(
                            title = when (language) {
                                AppLanguage.AR -> "رموز مفيدة"
                                AppLanguage.FR -> "Codes Utiles"
                                AppLanguage.EN -> "Useful Codes"
                            },
                            subtitle = "*710# · *720#",
                            icon = Icons.Filled.Dialpad,
                            iconTint = NotiBlue,
                            accentBorder = NotiBorder,
                            badgeColor = NotiRed,
                            onClick = onOpenUsefulCodesDialog,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("card_useful_codes")
                        )

                        // Bottom-Right Card: "دعوة رقم" (Envelope Icon)
                        NotiSquareGridCard(
                            title = when (language) {
                                AppLanguage.AR -> "دعوة رقم"
                                AppLanguage.FR -> "Inviter Numéro"
                                AppLanguage.EN -> "Invite Number"
                            },
                            subtitle = "+2 جيغا هدية",
                            icon = Icons.Filled.Email,
                            iconTint = NotiBlue,
                            accentBorder = NotiBorder,
                            badgeColor = NotiBlue,
                            onClick = onOpenInviteDialog,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("card_invite_number")
                        )
                    }
                }

                // Bottom Rounded Card (matching the card below the 2x2 grid in the screenshot):
                // Live Credit (الرصيد) & Internet (الإنترنت) Display + Quick Sync & Recharge
                item {
                    NotiLiveSimBalanceCard(
                        language = language,
                        session = session,
                        onSyncBalance = onSyncBalance,
                        onRechargeCredit = { onRechargeCredit(500) },
                        onNavigateToBuyTab = { onSelectSubTab(NotiHomeSubTab.BUY) }
                    )
                }
            }

            NotiHomeSubTab.BUY -> {
                // Full Free & Paid Offers Store inside "شراء"
                item {
                    NotiLiveSimBalanceCard(
                        language = language,
                        session = session,
                        onSyncBalance = onSyncBalance,
                        onRechargeCredit = { onRechargeCredit(500) },
                        onNavigateToBuyTab = {}
                    )
                }

                item {
                    Text(
                        text = when (language) {
                            AppLanguage.AR -> "جميع عروض جازي (المجانية والمدفوعة)"
                            AppLanguage.FR -> "Toutes les Offres Djezzy (Gratuites & Payantes)"
                            AppLanguage.EN -> "All Djezzy Offers (Free & Paid)"
                        },
                        color = DjezzyTextPrimary,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                items(allOffers, key = { it.id }) { offer ->
                    val cooldownMs = getRemainingCooldownMs(offer)
                    NotiOfferListCard(
                        language = language,
                        offer = offer,
                        cooldownMs = cooldownMs,
                        currentCreditDa = session.activeCreditDa,
                        onActivateClick = { onActivateOfferImmediate(offer) }
                    )
                }
            }

            NotiHomeSubTab.MORE -> {
                // "المزيد" Tab: Multi-SIM Manager ("جميع أرقامك في مكان واحد"), Recharge Credit, Cooldown Reset, & Activation History
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = NotiSurface),
                        border = BorderStroke(1.5.dp, NotiBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "جميع أرقامك في مكان واحد",
                                        color = DjezzyTextPrimary,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        text = "بدّل بين شرائح جازي المحفوظة بضغطة واحدة",
                                        color = DjezzyTextMuted,
                                        fontSize = 12.sp
                                    )
                                }
                                Button(
                                    onClick = onOpenSimManager,
                                    colors = ButtonDefaults.buttonColors(containerColor = NotiBlue),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("إضافة رقم", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            savedSimAccounts.forEach { acc ->
                                val isCurrent = acc.msisdn == session.msisdn
                                Surface(
                                    color = if (isCurrent) NotiBlue.copy(alpha = 0.10f) else NotiSurfaceSoft,
                                    shape = RoundedCornerShape(14.dp),
                                    border = BorderStroke(1.dp, if (isCurrent) NotiBlue else NotiBorder),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable { onSwitchSimAccount(acc) }
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Filled.SimCard,
                                                contentDescription = null,
                                                tint = if (isCurrent) NotiBlue else NotiRed,
                                                modifier = Modifier.size(22.dp)
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(
                                                    text = acc.phoneNumber,
                                                    color = DjezzyTextPrimary,
                                                    fontWeight = FontWeight.Black,
                                                    fontSize = 15.sp,
                                                    fontFamily = FontFamily.Monospace
                                                )
                                                Text(
                                                    text = "🌐 ${"%.2f".format(acc.activeDataGb)} جيغا  |  💳 ${acc.activeCreditDa} دج",
                                                    color = DjezzyTextSecondary,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                        if (isCurrent) {
                                            Surface(
                                                color = NotiBlue,
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text(
                                                    text = "متصل ✓",
                                                    color = Color.White,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                )
                                            }
                                        } else {
                                            IconButton(onClick = { onDeleteSimAccount(acc.msisdn) }) {
                                                Icon(
                                                    imageVector = Icons.Filled.Close,
                                                    contentDescription = "Delete SIM",
                                                    tint = NotiRed
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Quick Testing & Stability Controls Card
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = NotiSurface),
                        border = BorderStroke(1.5.dp, NotiBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "أدوات الرصيد وتصفير المؤقت",
                                color = DjezzyTextPrimary,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = { onRechargeCredit(1000) },
                                    colors = ButtonDefaults.buttonColors(containerColor = NotiBlue),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("btn_more_recharge_1000")
                                ) {
                                    Text("💳 شحن +1000 دج", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                                Button(
                                    onClick = onResetCooldowns,
                                    colors = ButtonDefaults.buttonColors(containerColor = NotiRed),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("btn_more_reset_cooldown")
                                ) {
                                    Text("🔓 تصفير مؤقت 24 سا", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Sub-Tab Button matching the NotiByte screenshot:
 * Selected item has a soft pill background around the icon + label below.
 */
@Composable
private fun NotiSubTabButton(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    activeColor: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .testTag(testTag)
    ) {
        Box(
            modifier = Modifier
                .width(68.dp)
                .height(36.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(
                    if (selected) activeColor.copy(alpha = 0.18f) else Color.Transparent
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (selected) activeColor else DjezzyTextSecondary,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = if (selected) activeColor else DjezzyTextSecondary,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.SemiBold
        )
    }
}

/**
 * Large Square Rounded Card used in the 2x2 NotiByte Grid:
 * Centered Blue/Red icon + bold Arabic title below.
 */
@Composable
private fun NotiSquareGridCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    accentBorder: Color,
    badgeColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = NotiSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.5.dp, accentBorder),
        modifier = modifier
            .height(142.dp)
            .clip(RoundedCornerShape(22.dp))
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp)
        ) {
            // Subtle Red/Blue status dot in top corner
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(badgeColor.copy(alpha = 0.75f))
            )

            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(44.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = title,
                    color = DjezzyTextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    color = DjezzyTextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * Live Credit (الرصيد) & Internet (الإنترنت) Card displayed right below the 2x2 Grid
 * so the user always sees their accurate Credit (DA) and Internet (GB) in Blue & Red.
 */
@Composable
private fun NotiLiveSimBalanceCard(
    language: AppLanguage,
    session: SimSessionEntity,
    onSyncBalance: () -> Unit,
    onRechargeCredit: () -> Unit,
    onNavigateToBuyTab: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = NotiSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.5.dp, NotiBorder),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("live_sim_balance_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(NotiBlue)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when (language) {
                            AppLanguage.AR -> "رصيد الشريحة والإنترنت"
                            AppLanguage.FR -> "Solde Crédit & Internet"
                            AppLanguage.EN -> "SIM Credit & Internet Balance"
                        },
                        color = DjezzyTextPrimary,
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        color = NotiBlue.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .clickable { onRechargeCredit() }
                            .testTag("btn_quick_recharge_500")
                    ) {
                        Text(
                            text = "+500 دج",
                            color = NotiBlue,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                        )
                    }
                    Surface(
                        color = NotiRed.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .clickable { onSyncBalance() }
                            .testTag("btn_sync_balance")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Refresh,
                                contentDescription = "Sync",
                                tint = NotiRed,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = when (language) {
                                    AppLanguage.AR -> "تحديث"
                                    AppLanguage.FR -> "Sync"
                                    AppLanguage.EN -> "Sync"
                                },
                                color = NotiRed,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Internet Balance Box (Blue)
                Surface(
                    color = NotiBlue.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, NotiBlue.copy(alpha = 0.30f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp)
                    ) {
                        Text(
                            text = when (language) {
                                AppLanguage.AR -> "الإنترنت المتبقي"
                                AppLanguage.FR -> "Internet Restant"
                                AppLanguage.EN -> "Internet Data"
                            },
                            color = DjezzyTextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${"%.2f".format(session.activeDataGb)} GB",
                            color = NotiBlue,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Credit Balance Box (Red)
                Surface(
                    color = NotiRed.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, NotiRed.copy(alpha = 0.30f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp)
                    ) {
                        Text(
                            text = when (language) {
                                AppLanguage.AR -> "الرصيد الأساسي"
                                AppLanguage.FR -> "Crédit Principal"
                                AppLanguage.EN -> "Main Credit"
                            },
                            color = DjezzyTextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${session.activeCreditDa} DA",
                            color = NotiRed,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}

/**
 * Offer Card inside the "شراء" (Buy) Sub-Tab
 */
@Composable
private fun NotiOfferListCard(
    language: AppLanguage,
    offer: DjezzyOffer,
    cooldownMs: Long,
    currentCreditDa: Int,
    onActivateClick: () -> Unit
) {
    val isOnCooldown = cooldownMs > 0L
    val hoursLeft = (cooldownMs / (1000 * 60 * 60)).toInt()
    val minsLeft = ((cooldownMs / (1000 * 60)) % 60).toInt()
    val isFree = offer.priceDa == 0

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = NotiSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(
            1.5.dp,
            if (isOnCooldown) NotiRed.copy(alpha = 0.5f) else if (isFree) NotiBlue.copy(alpha = 0.45f) else NotiBorder
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        color = if (isFree) NotiBlue.copy(alpha = 0.12f) else NotiRed.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isFree) Icons.Filled.Public else Icons.Filled.ShoppingCart,
                                contentDescription = null,
                                tint = if (isFree) NotiBlue else NotiRed,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = offer.localizedTitle(language),
                            color = DjezzyTextPrimary,
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp
                        )
                        Text(
                            text = offer.localizedValidity(language),
                            color = DjezzyTextMuted,
                            fontSize = 12.sp
                        )
                    }
                }

                Surface(
                    color = if (isFree) NotiBlue else NotiRed,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = if (isFree) "مجاني · +${offer.dataGb}G" else "${offer.priceDa} دج · +${offer.dataGb}G",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = offer.localizedBonus(language),
                color = DjezzyTextSecondary,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onActivateClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isOnCooldown) NotiRed.copy(alpha = 0.85f) else if (isFree) NotiBlue else NotiRed
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_activate_${offer.id}")
            ) {
                Icon(
                    imageVector = if (isOnCooldown) Icons.Filled.LockClock else Icons.Filled.Bolt,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isOnCooldown) {
                        "لا يمكنك التفعيل الآن (انتظر ${hoursLeft}سا و ${minsLeft}د)"
                    } else if (isFree) {
                        "تفعيل الهدية المجانية (+${offer.dataGb} جيغا)"
                    } else {
                        "شراء وتفعيل العرض (${offer.priceDa} دج -> +${offer.dataGb} جيغا)"
                    },
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp
                )
            }
        }
    }
}

/**
 * Bottom Tab 2: "شاهد واربح" (Watch & Win / Walk & Win Free Gifts)
 */
@Composable
private fun NotiByteWatchAndWinTab(
    language: AppLanguage,
    session: SimSessionEntity,
    offers: List<DjezzyOffer>,
    activationLogs: List<ActivationLogEntity>,
    getRemainingCooldownMs: (DjezzyOffer) -> Long,
    onActivateFreeOffer: (DjezzyOffer) -> Unit,
    onAddSteps: (Int) -> Unit,
    onRequestStepPermission: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(NotiBg),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = NotiSurface),
                border = BorderStroke(1.5.dp, NotiBlue.copy(alpha = 0.45f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.DirectionsWalk,
                            contentDescription = null,
                            tint = NotiBlue,
                            modifier = Modifier.size(28.dp)
                        )
                        Column {
                            Text(
                                text = "شاهد وامشِ واربح إنترنت مجاني",
                                color = DjezzyTextPrimary,
                                fontWeight = FontWeight.Black,
                                fontSize = 17.sp
                            )
                            Text(
                                text = "خطواتك الحالية: ${session.walkStepsCount} خطوة  |  الإنترنت: ${"%.2f".format(session.activeDataGb)} جيغا",
                                color = NotiBlue,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { onAddSteps(2500) },
                            colors = ButtonDefaults.buttonColors(containerColor = NotiBlue),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("🚶 +2,500 خطوة", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        OutlinedButton(
                            onClick = onRequestStepPermission,
                            border = BorderStroke(1.dp, NotiRed),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("تفعيل حساس المشي", color = NotiRed, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        items(offers, key = { it.id }) { offer ->
            val cooldownMs = getRemainingCooldownMs(offer)
            NotiOfferListCard(
                language = language,
                offer = offer,
                cooldownMs = cooldownMs,
                currentCreditDa = session.activeCreditDa,
                onActivateClick = { onActivateFreeOffer(offer) }
            )
        }
    }
}

/**
 * Bottom Tab 3: "معلومات" (SIM Subscriber Info & Developer Attribution: Youcef Ouagead · @Cursedcrown.exe)
 */
@Composable
private fun NotiByteInfoAndDevTab(
    language: AppLanguage,
    session: SimSessionEntity,
    savedSimsCount: Int,
    activationLogs: List<ActivationLogEntity>,
    onCopyText: (String, String) -> Unit,
    onOpenInstagram: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(NotiBg),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Developer Attribution Card (Youcef Ouagead · @Cursedcrown.exe)
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = NotiSurface),
                border = BorderStroke(1.5.dp, NotiBlue),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "معلومات المطور · DEVELOPER INFO",
                                color = NotiRed,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = DjezzyOusimRepository.CREATOR_NAME,
                                color = DjezzyTextPrimary,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "Instagram: ${DjezzyOusimRepository.CREATOR_INSTAGRAM}",
                                color = NotiBlue,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        NotiByteSteppedLogo(
                            blueColor = NotiBlue,
                            redColor = NotiRed,
                            modifier = Modifier.size(38.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onOpenInstagram,
                            colors = ButtonDefaults.buttonColors(containerColor = NotiRed),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_open_instagram")
                        ) {
                            Text(
                                text = "Instagram @Cursedcrown.exe",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp
                            )
                        }
                        OutlinedButton(
                            onClick = { onCopyText("Instagram", DjezzyOusimRepository.CREATOR_INSTAGRAM) },
                            border = BorderStroke(1.dp, NotiBlue),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("نسخ الحساب", color = NotiBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Subscriber SIM Info Card ("معلومات المشترك")
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = NotiSurface),
                border = BorderStroke(1.5.dp, NotiBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "معلومات المشترك والشريحة",
                        color = DjezzyTextPrimary,
                        fontWeight = FontWeight.Black,
                        fontSize = 17.sp
                    )
                    HorizontalDivider(color = NotiBorder)
                    InfoRowItem("رقم الهاتف:", session.phoneNumber, NotiBlue)
                    InfoRowItem("معرف خط جازي (MSISDN):", "+${session.msisdn}", DjezzyTextPrimary)
                    InfoRowItem("الرصيد المتوفر:", "${session.activeCreditDa} دج (DA)", NotiRed)
                    InfoRowItem("حجم الإنترنت المتبقي:", "${"%.2f".format(session.activeDataGb)} جيغا (GB)", NotiBlue)
                    InfoRowItem("عدد العروض المفعلة:", "${session.totalActivationsCount} عرض", DjezzyTextPrimary)
                    InfoRowItem("الشرائح المحفوظة:", "$savedSimsCount شريحة", DjezzyTextPrimary)
                    InfoRowItem("واجهة الاتصال:", "Djezzy Mobile App API (Djezzy/2.6.7)", NotiBlue)
                }
            }
        }
    }
}

@Composable
private fun InfoRowItem(label: String, value: String, valueColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = DjezzyTextSecondary,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = value,
            color = valueColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Monospace
        )
    }
}

/**
 * Bottom Tab 4: "الإعدادات" (Settings: Blue & Red Theme Switcher, Language, Credit Recharge, Cooldown Reset)
 */
@Composable
private fun NotiByteSettingsTab(
    language: AppLanguage,
    session: SimSessionEntity,
    onSelectLanguage: (AppLanguage) -> Unit,
    onSelectThemePreset: (OusimThemePreset) -> Unit,
    onRechargeCredit: (Int) -> Unit,
    onResetCooldowns: () -> Unit,
    onSaveCustomToken: (String) -> Unit,
    onRefreshToken: () -> Unit
) {
    var customToken by rememberSaveable { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(NotiBg),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Theme Selection (Blue & Red Light / Dark)
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = NotiSurface),
                border = BorderStroke(1.5.dp, NotiBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "المظهر والألوان (أزرق وأحمر)",
                        color = DjezzyTextPrimary,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp
                    )
                    OusimThemePreset.entries.forEach { preset ->
                        val isSelected = OusimThemeManager.activePreset == preset
                        Surface(
                            color = if (isSelected) NotiBlue.copy(alpha = 0.12f) else NotiSurfaceSoft,
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.5.dp, if (isSelected) NotiBlue else NotiBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectThemePreset(preset) }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = preset.titleAr,
                                    color = DjezzyTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .background(preset.previewPrimary)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .background(preset.previewSecondary)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. Language Switcher
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = NotiSurface),
                border = BorderStroke(1.5.dp, NotiBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "اللغة · Language",
                        color = DjezzyTextPrimary,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AppLanguage.entries.forEach { lang ->
                            val selected = language == lang
                            Button(
                                onClick = { onSelectLanguage(lang) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selected) NotiBlue else NotiSurfaceSoft,
                                    contentColor = if (selected) Color.White else DjezzyTextPrimary
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(lang.label, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // 3. Balance & 24h Cooldown Controls
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = NotiSurface),
                border = BorderStroke(1.5.dp, NotiBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "إدارة الرصيد ومؤقت العروض",
                        color = DjezzyTextPrimary,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { onRechargeCredit(1000) },
                            colors = ButtonDefaults.buttonColors(containerColor = NotiBlue),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("💳 شحن +1000 دج", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Button(
                            onClick = onResetCooldowns,
                            colors = ButtonDefaults.buttonColors(containerColor = NotiRed),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("🔓 تصفير مؤقت 24 سا", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // 4. Djezzy App OAuth2 Token Configuration
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = NotiSurface),
                border = BorderStroke(1.5.dp, NotiBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "توكن تطبيق جازي (Djezzy App OAuth2)",
                        color = DjezzyTextPrimary,
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp
                    )
                    OutlinedTextField(
                        value = customToken,
                        onValueChange = { customToken = it },
                        label = { Text("أدخل Bearer Token اختياري") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                if (customToken.isNotBlank()) {
                                    onSaveCustomToken(customToken)
                                    customToken = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NotiBlue),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("حفظ التوكن", fontWeight = FontWeight.Bold)
                        }
                        OutlinedButton(
                            onClick = onRefreshToken,
                            border = BorderStroke(1.dp, NotiRed),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("تحديث التوكن", color = NotiRed, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Live Receipt / Cooldown Banner at Top of Screen
 */
@Composable
private fun NotiByteReceiptBanner(
    receipt: ActivatedOfferReceipt,
    onDismiss: () -> Unit
) {
    val accentColor = if (receipt.isCooldownWarning) NotiRed else NotiBlue

    Surface(
        color = accentColor.copy(alpha = 0.10f),
        border = BorderStroke(1.5.dp, accentColor),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = if (receipt.isCooldownWarning) Icons.Filled.WarningAmber else Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = receipt.offerTitle,
                        color = DjezzyTextPrimary,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp
                    )
                    Text(
                        text = receipt.serverMessage,
                        color = DjezzyTextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            IconButton(onClick = onDismiss, modifier = Modifier.size(30.dp)) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Close",
                    tint = DjezzyTextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

/**
 * Dialog 1: Multi-SIM & SMS OTP Manager Dialog
 */
@Composable
private fun NotiByteSimManagerDialog(
    session: SimSessionEntity,
    savedSimAccounts: List<SavedSimAccountEntity>,
    isApiBusy: Boolean,
    onDismiss: () -> Unit,
    onSendOtp: (String) -> Unit,
    onVerifyOtp: (String) -> Unit,
    onQuickConnectSim: (String) -> Unit,
    onSwitchSim: (SavedSimAccountEntity) -> Unit,
    onDeleteSim: (String) -> Unit
) {
    var phoneInput by rememberSaveable { mutableStateOf(session.phoneNumber) }
    var otpInput by rememberSaveable { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = NotiSurface,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Filled.SimCard, contentDescription = null, tint = NotiBlue)
                Text(
                    text = "إدارة شرائح جازي ورمز OTP",
                    color = DjezzyTextPrimary,
                    fontWeight = FontWeight.Black,
                    fontSize = 17.sp
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = phoneInput,
                    onValueChange = { phoneInput = it.filter { c -> c.isDigit() }.take(10) },
                    label = { Text("رقم جازي (07xxxxxxxx)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NotiBlue,
                        unfocusedBorderColor = NotiBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onSendOtp(phoneInput) },
                        enabled = !isApiBusy && phoneInput.length >= 9,
                        colors = ButtonDefaults.buttonColors(containerColor = NotiRed),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.Sms, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("إرسال OTP", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Button(
                        onClick = { onQuickConnectSim(phoneInput) },
                        colors = ButtonDefaults.buttonColors(containerColor = NotiBlue),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("ربط سريع للشريحة", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = otpInput,
                        onValueChange = { otpInput = it.filter { c -> c.isDigit() }.take(6) },
                        label = { Text("رمز OTP (6 أرقام)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        onClick = { onVerifyOtp(otpInput) },
                        enabled = otpInput.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = NotiBlue),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("تأكيد", fontWeight = FontWeight.Bold)
                    }
                }

                if (savedSimAccounts.isNotEmpty()) {
                    HorizontalDivider(color = NotiBorder)
                    Text(
                        text = "الشرائح المحفوظة (اضغط للتبديل):",
                        color = DjezzyTextSecondary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    savedSimAccounts.take(4).forEach { acc ->
                        val isActive = acc.msisdn == session.msisdn
                        Surface(
                            color = if (isActive) NotiBlue.copy(alpha = 0.12f) else NotiSurfaceSoft,
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, if (isActive) NotiBlue else NotiBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSwitchSim(acc) }
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${acc.phoneNumber} (${"%.1f".format(acc.activeDataGb)}G | ${acc.activeCreditDa}DA)",
                                    color = DjezzyTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                                if (isActive) {
                                    Text("نشط ✓", color = NotiBlue, fontWeight = FontWeight.Black, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("إغلاق", color = NotiBlue, fontWeight = FontWeight.Bold)
            }
        }
    )
}

/**
 * Dialog 2: "دعوة رقم" (Invite a Djezzy Number for +2GB Free Internet)
 */
@Composable
private fun NotiByteInviteNumberDialog(
    currentPhone: String,
    onDismiss: () -> Unit,
    onSendInvite: (String) -> Unit
) {
    var friendPhone by rememberSaveable { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = NotiSurface,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Filled.Email, contentDescription = null, tint = NotiBlue)
                Text(
                    text = "دعوة رقم (ربح 2 جيغا مجانا)",
                    color = DjezzyTextPrimary,
                    fontWeight = FontWeight.Black,
                    fontSize = 17.sp
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "أدخل رقم صديقك في جازي (07xxxxxxxx) لإرسال دعوة عبر تطبيق جازي والحصول على 2 جيغا إنترنت مجاني!",
                    color = DjezzyTextSecondary,
                    fontSize = 13.sp
                )
                OutlinedTextField(
                    value = friendPhone,
                    onValueChange = { friendPhone = it.filter { c -> c.isDigit() }.take(10) },
                    label = { Text("رقم الصديق (07xxxxxxxx)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NotiBlue,
                        unfocusedBorderColor = NotiBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_invite_phone")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSendInvite(friendPhone.ifBlank { "0771234567" }) },
                colors = ButtonDefaults.buttonColors(containerColor = NotiBlue),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("btn_submit_invite")
            ) {
                Text("إرسال الدعوة (+2 جيغا)", color = Color.White, fontWeight = FontWeight.Black)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = DjezzyTextMuted)
            }
        }
    )
}

/**
 * Dialog 3: "رموز مفيدة" (Useful Djezzy USSD Codes)
 */
@Composable
private fun NotiByteUsefulCodesDialog(
    language: AppLanguage,
    codes: List<UssdServiceCode>,
    lastUssdResult: String?,
    onDismiss: () -> Unit,
    onExecuteUssd: (String) -> Unit,
    onCopyCode: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = NotiSurface,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Filled.Dialpad, contentDescription = null, tint = NotiBlue)
                Text(
                    text = "رموز جازي المفيدة (USSD)",
                    color = DjezzyTextPrimary,
                    fontWeight = FontWeight.Black,
                    fontSize = 17.sp
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (!lastUssdResult.isNullOrBlank()) {
                    Surface(
                        color = NotiBlue.copy(alpha = 0.10f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, NotiBlue),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = lastUssdResult,
                            color = DjezzyTextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                codes.forEach { item ->
                    Surface(
                        color = NotiSurfaceSoft,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, NotiBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onExecuteUssd(item.code) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.localizedTitle(language),
                                    color = DjezzyTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = item.code,
                                    color = NotiRed,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                OutlinedButton(
                                    onClick = { onCopyCode(item.code) },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("نسخ", fontSize = 11.sp, color = NotiBlue)
                                }
                                Button(
                                    onClick = { onExecuteUssd(item.code) },
                                    colors = ButtonDefaults.buttonColors(containerColor = NotiBlue),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("اتصال", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("إغلاق", color = NotiBlue, fontWeight = FontWeight.Bold)
            }
        }
    )
}

/**
 * Dialog 4: Notifications & Activation Logs Dialog (Bell icon in Top Bar)
 */
@Composable
private fun NotiByteNotificationsDialog(
    session: SimSessionEntity,
    logs: List<ActivationLogEntity>,
    onDismiss: () -> Unit,
    onResetCooldowns: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = NotiSurface,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Filled.Notifications, contentDescription = null, tint = NotiRed)
                Text(
                    text = "سجل التفعيلات والإشعارات",
                    color = DjezzyTextPrimary,
                    fontWeight = FontWeight.Black,
                    fontSize = 17.sp
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "الشريحة: ${session.phoneNumber} | الإنترنت: ${"%.2f".format(session.activeDataGb)}G | الرصيد: ${session.activeCreditDa}DA",
                    color = NotiBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                if (logs.isEmpty()) {
                    Text(
                        text = "لا توجد تفعيلات سابقة حتى الآن. اضغط على '2 جيغا مجانا' أو '4 جيغا - 70 دج' لتفعيل أول عرض!",
                        color = DjezzyTextMuted,
                        fontSize = 13.sp
                    )
                } else {
                    logs.take(5).forEach { log ->
                        Surface(
                            color = NotiSurfaceSoft,
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, NotiBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "${log.offerTitle} (+${log.dataAddedGb.toInt()} GB)",
                                    color = DjezzyTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "${log.productCode} · ${if (log.priceDa == 0) "مجاني" else "${log.priceDa} دج"} · ${log.timestamp}",
                                    color = NotiRed,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onResetCooldowns,
                colors = ButtonDefaults.buttonColors(containerColor = NotiRed),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("تصفير مؤقت 24 سا", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إغلاق", color = NotiBlue, fontWeight = FontWeight.Bold)
            }
        }
    )
}
