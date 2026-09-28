package com.example.data.model

import androidx.annotation.DrawableRes
import com.example.R

data class DiscordServerInfo(
    val serverName: String = "The 2090 Club",
    val guildId: String = "1551909274291011664",
    val channelName: String = "#🏘️the_town",
    val channelId: String = "1551910790532177970",
    val inviteCode: String = "ENa878794",
    val inviteUrl: String = "https://discord.gg/ENa878794",
    val inviterUsername: String = "youcef56312",
    val inviterDisplayName: String = "Satoru Gojo",
    val inviterId: String = "1491122637508837546",
    val memberCount: Int = 26,
    val onlineCount: Int = 9,
    val features: List<String> = listOf("COMMUNITY", "NEWS"),
    @DrawableRes val iconRes: Int = R.drawable.img_town_discord_icon
)

data class ReelTimelineBeat(
    val timeRange: String,
    val onScreenText: String,
    val spokenAudio: String,
    val description: String
)

data class TownEpisode(
    val episodeNumber: Int,
    val shortcode: String,
    val title: String,
    val caption: String,
    val reelUrl: String,
    val inWorldFollowers: Int,
    val views: Int,
    val likes: Int,
    val comments: Int,
    val durationSec: Double,
    val summary: String,
    val highlightCommentOrDetail: String,
    @DrawableRes val thumbnailRes: Int
)

data class TownCitizen(
    val id: String,
    val name: String,
    val handle: String,
    val role: String,
    val appearance: String,
    val lore: String,
    val episodeFirstSeen: String,
    val isUserAdded: Boolean = false
)

data class StudioGameProject(
    val title: String,
    val genre: String,
    val status: String,
    val description: String
)

object TownRepositoryData {
    const val DISCORD_JOIN_URL = "https://discord.gg/ENa878794"
    const val FEATURED_REEL_URL = "https://www.instagram.com/reel/DdzIfF2gIDD/?stkn=djh0N3o4dDRmb293"
    const val STUDIO_WEBSITE_URL = "https://twentyninetycreative.com"
    const val INSTAGRAM_PROFILE_URL = "https://www.instagram.com/twentyninetycreative/"

    val discordInfo = DiscordServerInfo()

    const val VERBATIM_TRANSCRIPT =
        "\"Yes! He needs water. Digging started. Dig faster, we just got him. TBH unfollowing would be easier.\""

    val featuredReelTimeline = listOf(
        ReelTimelineBeat(
            timeRange = "0:00 – 0:06",
            onScreenText = "1900 followers · ashrafabisaid: DIG A HOLE?? · yes! · he needs water",
            spokenAudio = "\"Yes! He needs water.\"",
            description = "The reel opens on the 1,900-follower crowd with @ashrafabisaid's comment ('DIG A HOLE??') pinned above a cracked, dry Stone Golem lying parched on the grass."
        ),
        ReelTimelineBeat(
            timeRange = "0:06 – 0:15",
            onScreenText = "1900 followers · Follower 64 · digging started",
            spokenAudio = "\"Digging started.\"",
            description = "Four tan follower mannequins armed with shovels gather around a fresh dark-brown dirt patch and begin excavating the hole while Satoru Gojo and Follower 64 watch."
        ),
        ReelTimelineBeat(
            timeRange = "0:15 – 0:24",
            onScreenText = "1900 followers · dig faster. we just got him",
            spokenAudio = "\"Dig faster, we just got him.\"",
            description = "Dirt piles up around the excavation site as the crowd presses closer, urgently trying to strike water before the newly unlocked Stone Golem dries out completely."
        ),
        ReelTimelineBeat(
            timeRange = "0:24 – 0:32.6",
            onScreenText = "1900 followers · tbh unfollowing would be easier",
            spokenAudio = "\"TBH unfollowing would be easier.\"",
            description = "The narrator deadpans that unfollowing the page would honestly be much less work than forcing the town's citizens to manually dig a well."
        )
    )

    val allEpisodes = listOf(
        TownEpisode(
            episodeNumber = 6,
            shortcode = "DdzIfF2gIDD",
            title = "The Hole Has Officially Started",
            caption = "the hole has officially started",
            reelUrl = "https://www.instagram.com/reel/DdzIfF2gIDD/?stkn=djh0N3o4dDRmb293",
            inWorldFollowers = 1900,
            views = 1351,
            likes = 79,
            comments = 61,
            durationSec = 32.6,
            summary = "Responding to @ashrafabisaid's comment 'DIG A HOLE??', a 4-mannequin shovel crew begins excavating a dirt pit in the middle of the lawn to find water for the parched, cracked Stone Golem.",
            highlightCommentOrDetail = "Trigger Comment: @ashrafabisaid — \"DIG A HOLE??\"",
            thumbnailRes = R.drawable.img_town_reel_hole
        ),
        TownEpisode(
            episodeNumber = 5,
            shortcode = "DdwuRx1gm84",
            title = "Meet the Management",
            caption = "meet the management.\napparently we’re digging a hole next.\n\njoin the discord if you want to help decide what happens with the hole...link in bio",
            reelUrl = "https://www.instagram.com/reel/DdwuRx1gm84/",
            inWorldFollowers = 1900,
            views = 1207,
            likes = 91,
            comments = 29,
            durationSec = 28.4,
            summary = "Introduces the Town's administrative figures (wearing crowns and glasses), showcases the green triceratops, soccer-ball head, red bear hoodie, and the dry cracked earth golem, and announces the upcoming Hole vote on Discord.",
            highlightCommentOrDetail = "Direct call to join #🏘️the_town on Discord to decide the Hole's fate",
            thumbnailRes = R.drawable.img_town_reel_management
        ),
        TownEpisode(
            episodeNumber = 4,
            shortcode = "DdwXvinAOSG",
            title = "It Deflated a Little",
            caption = "it deflated a little.\ntake your requests to Discord. apparently it works.",
            reelUrl = "https://www.instagram.com/reel/DdwXvinAOSG/",
            inWorldFollowers = 1900,
            views = 1302,
            likes = 99,
            comments = 39,
            durationSec = 46.3,
            summary = "A physics-based town entity loses its structural integrity and deflates in front of the 1,900-follower crowd, alongside the monkey and green frog-hat citizen.",
            highlightCommentOrDetail = "Proves that Discord requests directly shape the 3D simulation",
            thumbnailRes = R.drawable.img_town_reel_deflated
        ),
        TownEpisode(
            episodeNumber = 3,
            shortcode = "DduAOTSgx_t",
            title = "He’s Skateable",
            caption = "he’s skateable",
            reelUrl = "https://www.instagram.com/reel/DduAOTSgx_t/",
            inWorldFollowers = 1900,
            views = 1833,
            likes = 81,
            comments = 52,
            durationSec = 12.8,
            summary = "The town tests whether one of its custom inhabitants can be used as a skateboard ramp, surrounded by the Real Madrid jersey follower, realistic dog, black cat, and frog-hat avatar.",
            highlightCommentOrDetail = "Features Real Madrid jersey citizen, dog, black cat, and soccer ball",
            thumbnailRes = R.drawable.img_town_reel_skateable
        ),
        TownEpisode(
            episodeNumber = 2,
            shortcode = "DdrdkfrgeHW",
            title = "We’re Not Doing That Again",
            caption = "we’re not doing that again",
            reelUrl = "https://www.instagram.com/reel/DdrdkfrgeHW/",
            inWorldFollowers = 1879,
            views = 2193,
            likes = 128,
            comments = 57,
            durationSec = 24.3,
            summary = "Recorded at 1,879 followers after a chaotic crowd experiment goes sideways. Highlights persistent community requests for increasingly bizarre avatars.",
            highlightCommentOrDetail = "Featured Comment: @._fallen_seraphim._ — \"day 4 asking to be a biblically accurate angel\"",
            thumbnailRes = R.drawable.img_town_reel_not_again
        ),
        TownEpisode(
            episodeNumber = 1,
            shortcode = "DdpFP83gCDd",
            title = "We Had a Spider Available",
            caption = "we had a spider available.\nthe town has a Discord now. link in bio.",
            reelUrl = "https://www.instagram.com/reel/DdpFP83gCDd/",
            inWorldFollowers = 1840,
            views = 1823,
            likes = 116,
            comments = 62,
            durationSec = 15.6,
            summary = "Recorded at 1,840 followers when The 2090 Club Discord first launched! Introduces a yellow-haired spider creature alongside a samurai warrior, crowned king in a red cape, white duck, and burrito-holding citizen.",
            highlightCommentOrDetail = "Official launch episode of The Town's Discord server",
            thumbnailRes = R.drawable.img_town_reel_spider
        )
    )

    val initialCitizens = listOf(
        TownCitizen(
            id = "citizen_gojo",
            name = "Satoru Gojo",
            handle = "@youcef56312",
            role = "Discord Inviter & Foreground Guardian",
            appearance = "Spiky white hair, black blindfold, dark jujutsu sorcerer uniform",
            lore = "Stands prominently in the foreground of Reel DdzIfF2gIDD watching the hole excavation, and serves as the official inviter for The 2090 Club Discord link (ENa878794).",
            episodeFirstSeen = "DdzIfF2gIDD"
        ),
        TownCitizen(
            id = "citizen_golem",
            name = "The Parched Stone Golem",
            handle = "Town Entity #1900",
            role = "Catalyst of The Hole",
            appearance = "Large cracked brown earth-and-stone golem lying flat on the grass",
            lore = "Unlocked at 1,900 followers. Desperately needs water to survive, prompting the entire town to start digging a well.",
            episodeFirstSeen = "DdwuRx1gm84 / DdzIfF2gIDD"
        ),
        TownCitizen(
            id = "citizen_ashraf",
            name = "Ashraf Abi Said",
            handle = "@ashrafabisaid",
            role = "Chief Hole Architect",
            appearance = "Featured Comment Overlay ('DIG A HOLE??')",
            lore = "Posted the decisive comment 'DIG A HOLE??' that convinced @twentyninetycreative and the town management to launch the excavation.",
            episodeFirstSeen = "DdzIfF2gIDD"
        ),
        TownCitizen(
            id = "citizen_64",
            name = "Follower 64",
            handle = "Follower #64",
            role = "Founding Placard Citizen",
            appearance = "Tan humanoid mannequin carrying the floating 'Follower 64' sign",
            lore = "One of the earliest labeled inhabitants in The Town, positioned right behind the 4-mannequin shovel crew during the excavation.",
            episodeFirstSeen = "DdzIfF2gIDD"
        ),
        TownCitizen(
            id = "citizen_diggers",
            name = "The 4 Hole Diggers",
            handle = "Excavation Crew #1–4",
            role = "Town Public Works",
            appearance = "Four tan mannequins wielding iron shovels around the dirt pit",
            lore = "Tasked with digging to the water table while the narrator tells them to 'dig faster, we just got him.'",
            episodeFirstSeen = "DdzIfF2gIDD"
        ),
        TownCitizen(
            id = "citizen_management",
            name = "The Town Management",
            handle = "@twentyninetycreative",
            role = "Simulation Overseers",
            appearance = "Crowned & bespectacled humanoid figures surrounded by custom avatars",
            lore = "Introduced in Episode 5 ('meet the management') before handing project voting power over to #🏘️the_town on Discord.",
            episodeFirstSeen = "DdwuRx1gm84"
        ),
        TownCitizen(
            id = "citizen_seraphim",
            name = "Fallen Seraphim",
            handle = "@._fallen_seraphim._",
            role = "Angel Campaigner",
            appearance = "Commenter campaigning for a biblically accurate angel skin",
            lore = "Featured on-screen at 1,879 followers with the comment: 'day 4 asking to be a biblically accurate angel'.",
            episodeFirstSeen = "DdrdkfrgeHW"
        ),
        TownCitizen(
            id = "citizen_frog_hat",
            name = "Green Frog-Hat Citizen",
            handle = "Custom Skin",
            role = "Front-Row Regular",
            appearance = "Humanoid wearing an oversized bright green frog hat",
            lore = "Appears in the front row across multiple episodes (Episodes 2, 3, and 4) next to the town monkey.",
            episodeFirstSeen = "DdrdkfrgeHW"
        ),
        TownCitizen(
            id = "citizen_samurai",
            name = "Samurai Armor Warrior & Red-Cape King",
            handle = "Early Custom Skins",
            role = "Town Vanguard",
            appearance = "Full Japanese kabuto helmet/armor alongside a gold-crowned king in a green shirt and red cape",
            lore = "Stood at the front of the 1,840-follower crowd when the spider and Discord server were first announced.",
            episodeFirstSeen = "DdpFP83gCDd"
        ),
        TownCitizen(
            id = "citizen_wildlife",
            name = "Triceratops, Yellow-Haired Spider & Menagerie",
            handle = "Town Fauna",
            role = "Simulation Creatures",
            appearance = "Green triceratops, yellow-haired spider, white duck, horse, realistic dog, black cat, and monkey",
            lore = "Spawned from chaotic follower requests across all 6 episodes, turning the grassy field into an unpredictable menagerie.",
            episodeFirstSeen = "DdpFP83gCDd – DdzIfF2gIDD"
        )
    )

    val studioTeam = listOf(
        "Melisa Cetinalp — Chief Executive Officer (CEO)",
        "Deniz Cetinalp — Chief Technology Officer (CTO)",
        "Renad Alharbi — Chief Operating Officer (COO)"
    )

    val studioGames = listOf(
        StudioGameProject(
            title = "Traversal Adventure",
            genre = "1–4 Player Co-Op Exploration",
            status = "Wishlist on Steam",
            description = "Dig, climb, and crawl through beautiful procedural ecosystems in a living forest full of discoveries, hazards, and unforgettable climbs."
        ),
        StudioGameProject(
            title = "Becoming You",
            genre = "Real-Time Audio-Reactive Action",
            status = "Coming Soon",
            description = "Tracks, streams, and system audio are analyzed in real time, shaping your movement, camera, combat, enemies, VFX, and procedural boss encounters."
        ),
        StudioGameProject(
            title = "Moonfall Voyage",
            genre = "Sailing & Guardian Puzzle Adventure",
            status = "Wishlist on Steam",
            description = "Sail across a broken world to free colossal guardians from a powerful curse through unique sailing challenges and ancient puzzles."
        )
    )
}
