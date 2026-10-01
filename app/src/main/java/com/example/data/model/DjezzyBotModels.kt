package com.example.data.model

enum class AppLanguage(val code: String, val label: String) {
    EN("EN", "English"),
    AR("AR", "العربية"),
    FR("FR", "Français")
}

enum class OfferCategory(val labelEn: String, val labelAr: String, val labelFr: String) {
    ALL("All Djezzy Offers", "كل عروض جازي", "Toutes les offres"),
    BOT_EXCLUSIVE("Walk & Win Gift", "هدية المشي الحقيقية", "Cadeau Walk & Win"),
    HAYLA("Hayla Bezzef", "هايلة بزاف", "Hayla Bezzef"),
    LEGEND("Djezzy Legend", "جازي ليجند", "Djezzy Legend"),
    SPEED("Djezzy Speed", "جازي سبيد", "Djezzy Speed")
}

data class DjezzyOffer(
    val id: String,
    val titleEn: String,
    val titleAr: String,
    val titleFr: String,
    val category: OfferCategory,
    val dataGb: Int,
    val priceDa: Int,
    val validityTextEn: String,
    val validityTextAr: String,
    val validityTextFr: String,
    val callsBonusEn: String,
    val callsBonusAr: String,
    val callsBonusFr: String,
    val ussdCode: String,
    val apiProductId: String = "GIFTWALKWIN",
    val apiGiftCode: String = "GIFTWALKWIN1GO",
    val apiServiceId: String = "WALKWIN",
    val requiredSteps: Int = 10000,
    val isExclusivePromo: Boolean = false,
    val badgeTag: String = ""
) {
    fun localizedTitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.EN -> titleEn
        AppLanguage.AR -> titleAr
        AppLanguage.FR -> titleFr
    }

    fun localizedValidity(lang: AppLanguage): String = when (lang) {
        AppLanguage.EN -> validityTextEn
        AppLanguage.AR -> validityTextAr
        AppLanguage.FR -> validityTextFr
    }

    fun localizedBonus(lang: AppLanguage): String = when (lang) {
        AppLanguage.EN -> callsBonusEn
        AppLanguage.AR -> callsBonusAr
        AppLanguage.FR -> callsBonusFr
    }
}

data class UssdServiceCode(
    val code: String,
    val titleEn: String,
    val titleAr: String,
    val titleFr: String,
    val descriptionEn: String,
    val descriptionAr: String,
    val descriptionFr: String,
    val categoryBadge: String
) {
    fun localizedTitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.EN -> titleEn
        AppLanguage.AR -> titleAr
        AppLanguage.FR -> titleFr
    }

    fun localizedDescription(lang: AppLanguage): String = when (lang) {
        AppLanguage.EN -> descriptionEn
        AppLanguage.AR -> descriptionAr
        AppLanguage.FR -> descriptionFr
    }
}

enum class BotQuickActionType {
    SEND_REAL_OTP,
    ACTIVATE_WALK_GIFT,
    SYNC_SIM_OFFERS,
    CHECK_BALANCE_USSD,
    SHOW_HAYLA_PACKS,
    OPEN_WALK_AND_WIN,
    SHOW_USSD_CODES,
    CREATOR_INFO
}

data class BotActionChip(
    val labelEn: String,
    val labelAr: String,
    val labelFr: String,
    val actionType: BotQuickActionType
) {
    fun localizedLabel(lang: AppLanguage): String = when (lang) {
        AppLanguage.EN -> labelEn
        AppLanguage.AR -> labelAr
        AppLanguage.FR -> labelFr
    }
}

object DjezzyOusimRepository {
    const val CREATOR_NAME = "Youcef Ouagead"
    const val CREATOR_INSTAGRAM = "@Cursedcrown.exe"
    const val CREATOR_INSTAGRAM_URL = "https://www.instagram.com/Cursedcrown.exe"
    const val CREATOR_ROLE = "Lead Developer & Architect · OUSIM Bot Djezzy Edition"

    val quickActions = listOf(
        BotActionChip(
            labelEn = "🔐 Send SMS OTP",
            labelAr = "🔐 إرسال رمز OTP",
            labelFr = "🔐 Envoyer Code OTP",
            actionType = BotQuickActionType.SEND_REAL_OTP
        ),
        BotActionChip(
            labelEn = "🎁 Activate Walk & Win Gift",
            labelAr = "🎁 تفعيل هدية المشي (GIFTWALKWIN1GO)",
            labelFr = "🎁 Activer Cadeau Walk & Win",
            actionType = BotQuickActionType.ACTIVATE_WALK_GIFT
        ),
        BotActionChip(
            labelEn = "🔄 Sync My SIM Offers",
            labelAr = "🔄 جلب عروض شريحتي الحقيقية",
            labelFr = "🔄 Synchroniser Offres SIM",
            actionType = BotQuickActionType.SYNC_SIM_OFFERS
        ),
        BotActionChip(
            labelEn = "📊 Check Real Balance",
            labelAr = "📊 كشف الرصيد الحقيقي",
            labelFr = "📊 Vérifier Solde Réel",
            actionType = BotQuickActionType.CHECK_BALANCE_USSD
        ),
        BotActionChip(
            labelEn = "🔥 Hayla Bezzef",
            labelAr = "🔥 هايلة بزاف",
            labelFr = "🔥 Hayla Bezzef",
            actionType = BotQuickActionType.SHOW_HAYLA_PACKS
        ),
        BotActionChip(
            labelEn = "🚶 Step Sensor",
            labelAr = "🚶 حساس المشي",
            labelFr = "🚶 Capteur de Pas",
            actionType = BotQuickActionType.OPEN_WALK_AND_WIN
        ),
        BotActionChip(
            labelEn = "👑 @Cursedcrown.exe",
            labelAr = "👑 @Cursedcrown.exe",
            labelFr = "👑 @Cursedcrown.exe",
            actionType = BotQuickActionType.CREATOR_INFO
        )
    )

    // Official Djezzy App products & Walk & Win gift codes (including NotiByte 2GB Free & 4GB - 70 DA)
    val allOffers = listOf(
        DjezzyOffer(
            id = "djezzy_walk_2gb",
            titleEn = "2 GB Free Internet",
            titleAr = "2 جيغا مجانا",
            titleFr = "2 Go Internet Gratuit",
            category = OfferCategory.BOT_EXCLUSIVE,
            dataGb = 2,
            priceDa = 0,
            validityTextEn = "24 Hours (Free Gift · 1 Activation / 24h)",
            validityTextAr = "صالحة لمدة 24 ساعة (هدية مجانية مرة كل 24 ساعة)",
            validityTextFr = "Valable 24 Heures (Cadeau Gratuit 1 fois / 24h)",
            callsBonusEn = "Djezzy App API: GIFTWALKWIN2GO · +2 GB Free Internet",
            callsBonusAr = "تفعيل فوري لـ 2 جيغا إنترنت مجاني على شريحتك جازي",
            callsBonusFr = "Activation immédiate de 2 Go Internet gratuit sur votre SIM Djezzy",
            ussdCode = "*720#",
            apiProductId = "GIFTWALKWIN",
            apiGiftCode = "GIFTWALKWIN2GO",
            apiServiceId = "WALKWIN",
            requiredSteps = 20000,
            isExclusivePromo = true,
            badgeTag = "مجاني · FREE 2GB"
        ),
        DjezzyOffer(
            id = "djezzy_4gb_70da",
            titleEn = "4 GB - 70 DA",
            titleAr = "4 جيغا - 70 دج",
            titleFr = "4 Go - 70 DA",
            category = OfferCategory.SPEED,
            dataGb = 4,
            priceDa = 70,
            validityTextEn = "24 Hours (Requires 70 DA Credit)",
            validityTextAr = "صالحة لمدة 24 ساعة (تخصم 70 دج من الرصيد)",
            validityTextFr = "Valable 24 Heures (70 DA de crédit)",
            callsBonusEn = "Djezzy App Special 4GB Internet Bundle for 70 DA",
            callsBonusAr = "عرض نوتي بايت وجازي الخاص: 4 جيغا إنترنت بـ 70 دج فقط",
            callsBonusFr = "Offre Spéciale NotiByte & Djezzy : 4 Go pour 70 DA",
            ussdCode = "*720#",
            apiProductId = "SPEED4GO70",
            apiGiftCode = "SPEED4GO70",
            apiServiceId = "SPEED",
            requiredSteps = 0,
            isExclusivePromo = false,
            badgeTag = "70 دج · 4GB"
        ),
        DjezzyOffer(
            id = "ousim_2gb_welcome",
            titleEn = "1 GB Free Walk & Win (GIFTWALKWIN1GO)",
            titleAr = "1 جيغا مجانا (هدية المشي GIFTWALKWIN1GO)",
            titleFr = "1 Go Gratuit Walk & Win (GIFTWALKWIN1GO)",
            category = OfferCategory.BOT_EXCLUSIVE,
            dataGb = 1,
            priceDa = 0,
            validityTextEn = "24 Hours (1 Activation / 24h)",
            validityTextAr = "24 ساعة (تفعيل مرة كل 24 ساعة)",
            validityTextFr = "24 Heures (1 Activation / 24h)",
            callsBonusEn = "Djezzy Mobile App API: id=GIFTWALKWIN · code=GIFTWALKWIN1GO",
            callsBonusAr = "كود الهدية الرسمي في تطبيق جازي: GIFTWALKWIN1GO (كل 24 ساعة)",
            callsBonusFr = "Code Officiel Djezzy App : GIFTWALKWIN1GO (1 fois par 24h)",
            ussdCode = "*720#",
            apiProductId = "GIFTWALKWIN",
            apiGiftCode = "GIFTWALKWIN1GO",
            apiServiceId = "WALKWIN",
            requiredSteps = 10000,
            isExclusivePromo = true,
            badgeTag = "مجاني · FREE 1GB"
        ),
        DjezzyOffer(
            id = "djezzy_walk_5gb",
            titleEn = "5 GB Free Walk & Win (GIFTWALKWIN5GO)",
            titleAr = "5 جيغا مجانا (هدية المشي GIFTWALKWIN5GO)",
            titleFr = "5 Go Gratuit Walk & Win (GIFTWALKWIN5GO)",
            category = OfferCategory.BOT_EXCLUSIVE,
            dataGb = 5,
            priceDa = 0,
            validityTextEn = "48 Hours (Requires 40,000 Steps Quota)",
            validityTextAr = "48 ساعة (هدية تطبيق جازي 5 جيغا)",
            validityTextFr = "48 Heures (Cadeau Djezzy App 5Go)",
            callsBonusEn = "Djezzy Mobile App API: id=GIFTWALKWIN · code=GIFTWALKWIN5GO",
            callsBonusAr = "كود الهدية الرسمي في تطبيق جازي: GIFTWALKWIN5GO",
            callsBonusFr = "Code Officiel Djezzy App : GIFTWALKWIN5GO",
            ussdCode = "*720#",
            apiProductId = "GIFTWALKWIN",
            apiGiftCode = "GIFTWALKWIN5GO",
            apiServiceId = "WALKWIN",
            requiredSteps = 40000,
            isExclusivePromo = true,
            badgeTag = "مجاني · FREE 5GB"
        ),
        DjezzyOffer(
            id = "hayla_bezzef_1500",
            titleEn = "Djezzy Hayla Bezzef 1500",
            titleAr = "جازي هايلة بزاف 1500 دج",
            titleFr = "Djezzy Hayla Bezzef 1500 DA",
            category = OfferCategory.HAYLA,
            dataGb = 60,
            priceDa = 1500,
            validityTextEn = "30 Days (Requires 1500 DA Credit)",
            validityTextAr = "30 يوم (يتطلب رصيد 1500 دج)",
            validityTextFr = "30 Jours (Requiert 1500 DA de crédit)",
            callsBonusEn = "Unlimited Djezzy Calls + 2000 DA All Networks",
            callsBonusAr = "مكالمات غير محدودة نحو جازي + 2000 دج كل الشبكات",
            callsBonusFr = "Appels Illimités Djezzy + 2000 DA Tous Réseaux",
            ussdCode = "*700#",
            apiProductId = "HAYLABEZZEF1500",
            apiGiftCode = "HAYLABEZZEF1500",
            apiServiceId = "PREPAID",
            requiredSteps = 0,
            badgeTag = "OFFICIAL HAYLA"
        ),
        DjezzyOffer(
            id = "hayla_bezzef_1200",
            titleEn = "Djezzy Hayla Bezzef 1200",
            titleAr = "جازي هايلة بزاف 1200 دج",
            titleFr = "Djezzy Hayla Bezzef 1200 DA",
            category = OfferCategory.HAYLA,
            dataGb = 30,
            priceDa = 1200,
            validityTextEn = "30 Days (Requires 1200 DA Credit)",
            validityTextAr = "30 يوم (يتطلب رصيد 1200 دج)",
            validityTextFr = "30 Jours (Requiert 1200 DA de crédit)",
            callsBonusEn = "Unlimited Djezzy Calls + 1200 DA Credit",
            callsBonusAr = "مكالمات غير محدودة نحو جازي + 1200 دج رصيد",
            callsBonusFr = "Appels Illimités Djezzy + 1200 DA Crédit",
            ussdCode = "*700#",
            apiProductId = "HAYLABEZZEF1200",
            apiGiftCode = "HAYLABEZZEF1200",
            apiServiceId = "PREPAID",
            requiredSteps = 0,
            badgeTag = "OFFICIAL HAYLA"
        ),
        DjezzyOffer(
            id = "hayla_bezzef_1000",
            titleEn = "Djezzy Hayla Bezzef 1000",
            titleAr = "جازي هايلة بزاف 1000 دج",
            titleFr = "Djezzy Hayla Bezzef 1000 DA",
            category = OfferCategory.HAYLA,
            dataGb = 15,
            priceDa = 1000,
            validityTextEn = "30 Days (Requires 1000 DA Credit)",
            validityTextAr = "30 يوم (يتطلب رصيد 1000 دج)",
            validityTextFr = "30 Jours (Requiert 1000 DA de crédit)",
            callsBonusEn = "Unlimited Djezzy Calls + 1000 DA Credit",
            callsBonusAr = "مكالمات غير محدودة نحو جازي + 1000 دج رصيد",
            callsBonusFr = "Appels Illimités Djezzy + 1000 DA Crédit",
            ussdCode = "*700#",
            apiProductId = "HAYLABEZZEF1000",
            apiGiftCode = "HAYLABEZZEF1000",
            apiServiceId = "PREPAID",
            requiredSteps = 0,
            badgeTag = "OFFICIAL HAYLA"
        ),
        DjezzyOffer(
            id = "djezzy_legend_2500",
            titleEn = "Djezzy Legend 2500",
            titleAr = "جازي ليجند 2500 دج",
            titleFr = "Djezzy Legend 2500 DA",
            category = OfferCategory.LEGEND,
            dataGb = 120,
            priceDa = 2500,
            validityTextEn = "30 Days (Requires 2500 DA Credit)",
            validityTextAr = "30 يوم (يتطلب رصيد 2500 دج)",
            validityTextFr = "30 Jours (Requiert 2500 DA de crédit)",
            callsBonusEn = "Unlimited Calls + 5000 DA Credit All Networks",
            callsBonusAr = "مكالمات غير محدودة + 5000 دج نحو كل الشبكات",
            callsBonusFr = "Appels Illimités + 5000 DA Tous Réseaux",
            ussdCode = "*700#",
            apiProductId = "LEGEND2500",
            apiGiftCode = "LEGEND2500",
            apiServiceId = "PREPAID",
            requiredSteps = 0,
            badgeTag = "OFFICIAL LEGEND"
        ),
        DjezzyOffer(
            id = "djezzy_speed_month_1000",
            titleEn = "Djezzy Speed Monthly 25GB",
            titleAr = "جازي سبيد شهري 25 جيغا",
            titleFr = "Djezzy Speed Mensuel 25Go",
            category = OfferCategory.SPEED,
            dataGb = 25,
            priceDa = 1000,
            validityTextEn = "30 Days (Requires 1000 DA Credit)",
            validityTextAr = "30 يوم (يتطلب رصيد 1000 دج)",
            validityTextFr = "30 Jours (Requiert 1000 DA de crédit)",
            callsBonusEn = "Pure 4G/5G Internet Package",
            callsBonusAr = "باقة إنترنت جازي سبيد الشهرية",
            callsBonusFr = "Forfait Internet Djezzy Speed Mensuel",
            ussdCode = "*720#",
            apiProductId = "SPEEDMONTH25GO",
            apiGiftCode = "SPEEDMONTH25GO",
            apiServiceId = "SPEED",
            requiredSteps = 0,
            badgeTag = "OFFICIAL SPEED"
        ),
        DjezzyOffer(
            id = "djezzy_speed_day_100",
            titleEn = "Djezzy Speed Daily 2GB",
            titleAr = "جازي سبيد يومي 2 جيغا",
            titleFr = "Djezzy Speed Jour 2Go",
            category = OfferCategory.SPEED,
            dataGb = 2,
            priceDa = 100,
            validityTextEn = "24 Hours (Requires 100 DA Credit)",
            validityTextAr = "24 ساعة (يتطلب رصيد 100 دج)",
            validityTextFr = "24 Heures (Requiert 100 DA de crédit)",
            callsBonusEn = "24-Hour Internet Pass",
            callsBonusAr = "جواز إنترنت صالح لمدة 24 ساعة",
            callsBonusFr = "Pass Internet 24 Heures",
            ussdCode = "*720#",
            apiProductId = "SPEEDDAY2GO",
            apiGiftCode = "SPEEDDAY2GO",
            apiServiceId = "SPEED",
            requiredSteps = 0,
            badgeTag = "OFFICIAL SPEED"
        )
    )

    val ussdCodes = listOf(
        UssdServiceCode(
            code = "*710#",
            titleEn = "Balance & Bonus Inquiry",
            titleAr = "كشف الرصيد والبونيس",
            titleFr = "Consultation Solde & Bonus",
            descriptionEn = "Check your main DZD credit, active internet GB, and remaining validity.",
            descriptionAr = "الاطلاع على الرصيد الرئيسي بالدينار وحجم الإنترنت المتبقي.",
            descriptionFr = "Vérifiez votre crédit principal en DA et votre volume internet restant.",
            categoryBadge = "ESSENTIAL"
        ),
        UssdServiceCode(
            code = "*720#",
            titleEn = "Djezzy Speed & Internet Menu",
            titleAr = "قائمة عروض الإنترنت جازي سبيد",
            titleFr = "Menu Forfaits Internet Speed",
            descriptionEn = "Subscribe directly to daily, weekly, and monthly Djezzy internet packages.",
            descriptionAr = "تفعيل عروض الإنترنت اليومية والأسبوعية والشهرية مباشرة.",
            descriptionFr = "Souscrire aux pass internet journaliers, hebdomadaires et mensuels.",
            categoryBadge = "INTERNET"
        ),
        UssdServiceCode(
            code = "*700#",
            titleEn = "Hayla Bezzef & Legend Menu",
            titleAr = "قائمة عروض هايلة بزاف وليجند",
            titleFr = "Menu Offres Hayla & Legend",
            descriptionEn = "Access all Djezzy Hayla Bezzef, Comfort, and Legend bundles.",
            descriptionAr = "الوصول إلى جميع باقات هايلة بزاف وكمفورت وليجند.",
            descriptionFr = "Accéder à toutes les offres Hayla Bezzef, Comfort et Legend.",
            categoryBadge = "BUNDLES"
        ),
        UssdServiceCode(
            code = "*714#",
            titleEn = "Display My Djezzy Number",
            titleAr = "معرفة رقم هاتفي في جازي",
            titleFr = "Connaître Mon Numéro Djezzy",
            descriptionEn = "Instantly display your 07xxxxxxxx SIM phone number on screen.",
            descriptionAr = "إظهار رقم شريحتك جازي 07xxxxxxxx فورا على الشاشة.",
            descriptionFr = "Afficher instantanément votre numéro SIM 07xxxxxxxx.",
            categoryBadge = "SIM INFO"
        ),
        UssdServiceCode(
            code = "*770#",
            titleEn = "Flexy Credit Transfer",
            titleAr = "تحويل الرصيد فليكسي (Flexy)",
            titleFr = "Transfert de Crédit Flexy",
            descriptionEn = "Transfer DZD credit between Djezzy numbers quickly and securely.",
            descriptionAr = "إرسال وتحويل الرصيد بين أرقام جازي بسهولة وأمان.",
            descriptionFr = "Transférer du crédit entre numéros Djezzy en toute sécurité.",
            categoryBadge = "FLEXY"
        ),
        UssdServiceCode(
            code = "*444#",
            titleEn = "SOS Credit / Emergency Advance",
            titleAr = "خدمة سلفني (SOS Crédit)",
            titleFr = "Service SOS Crédit / Avance",
            descriptionEn = "Request emergency credit or internet when your balance runs out.",
            descriptionAr = "طلب رصيد أو إنترنت احتياطي عند نفاد رصيدك.",
            descriptionFr = "Demander une avance de crédit ou d'internet d'urgence.",
            categoryBadge = "EMERGENCY"
        )
    )
}
