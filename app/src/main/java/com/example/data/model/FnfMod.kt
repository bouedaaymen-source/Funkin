package com.example.data.model

data class SongItem(
    val title: String,
    val bpm: Int,
    val duration: String,
    val difficultyLevel: String, // "Normal", "Hard", "Insane", "Mania"
    val opponent: String
)

data class ModCharacter(
    val name: String,
    val role: String, // "Opponent", "Protagonist", "Guest", "Speaker"
    val iconEmoji: String,
    val description: String
)

data class FnfMod(
    val id: String,
    val title: String,
    val subtitle: String,
    val author: String,
    val version: String,
    val engine: String, // "Psych Engine 0.7.3", "Codename Engine", "V-Slice", etc.
    val downloadSize: String,
    val releaseDate: String,
    val lastUpdated: String,
    val rating: Float, // out of 5.0
    val downloadCount: String,
    val tags: List<String>,
    val category: String, // "Overhaul", "Full Week", "Creepypasta", "Crossover", "Legends", "Android Port"
    val difficulty: String, // "Casual", "Medium", "Hard", "Expert", "Insane"
    val colorHex: Long,
    val description: String,
    val lore: String,
    val downloadUrl: String,
    val mirrorUrl: String,
    val songs: List<SongItem>,
    val mechanics: List<String>,
    val characters: List<ModCharacter>,
    val platforms: List<String>,
    val isFeatured: Boolean = false,
    val isCustomUserMod: Boolean = false
)

data class FullModDetail(
    val mod: FnfMod,
    val isFavorite: Boolean = false,
    val downloadStatus: DownloadStatus = DownloadStatus.NOT_DOWNLOADED,
    val downloadProgress: Int = 0,
    val userRating: Float = 0f,
    val isCompleted: Boolean = false,
    val highestScore: Long = 0,
    val userNotes: String = "",
    val lastPlayedDate: String = ""
)

enum class DownloadStatus {
    NOT_DOWNLOADED,
    DOWNLOADING,
    DOWNLOADED
}

object DefaultCatalog {
    val mods: List<FnfMod> = listOf(
        FnfMod(
            id = "marios-madness-v2",
            title = "Mario's Madness V2",
            subtitle = "A massive horror overhaul with 30+ original songs & custom cutscenes",
            author = "Marco Antonio & Team Madness",
            version = "v2.0.1",
            engine = "Psych Engine 0.7.1",
            downloadSize = "1.8 GB",
            releaseDate = "Dec 2023",
            lastUpdated = "Jan 2024",
            rating = 4.95f,
            downloadCount = "2.8M",
            tags = listOf("Full Overhaul", "Horror", "Animated Cutscenes", "30+ Songs", "Custom Shaders"),
            category = "Overhaul",
            difficulty = "Expert",
            colorHex = 0xFFFF2A4B,
            description = "BF and GF get sucked into an ominous vintage NES cartridge into Mario's demented realm. Featuring multiple distinct worlds, world maps, interactive mini-games, and cinematic boss battles.",
            lore = "When Boyfriend buys a suspicious bootleg Mario cartridge at a garage sale, he is pulled into a nightmarish digital underworld ruled by Ultra M and haunted Nintendo abominations.",
            downloadUrl = "https://gamebanana.com/mods/359554",
            mirrorUrl = "https://github.com/MarioMadness/MMv2-Releases",
            songs = listOf(
                SongItem("It's-A-Me", 160, "2:45", "Hard", "Horror Mario"),
                SongItem("Starman Slaughter", 175, "3:12", "Insane", "John Dick & Peach"),
                SongItem("All-Stars (Act 1-4)", 185, "8:40", "Mania", "Ultra M"),
                SongItem("Alone", 140, "2:30", "Normal", "Beta Luigi"),
                SongItem("Paranoia", 195, "3:05", "Insane", "Mr. Virtual"),
                SongItem("Bad Day", 150, "2:15", "Hard", "Bad Mario")
            ),
            mechanics = listOf(
                "Health Drain on Misses",
                "Screen Distortion & Jumpscares",
                "Overworld Map Navigation",
                "Dodge Prompts (Spacebar)",
                "Custom Death Animations"
            ),
            characters = listOf(
                ModCharacter("Ultra M", "Final Boss", "👾", "The vengeful amalgamate core of the corrupted cartridge."),
                ModCharacter("Horror Mario", "Opponent", "🍄", "A deformed, towering version of the red-capped plumber."),
                ModCharacter("Beta Luigi", "Opponent", "👻", "Melancholic ghost from the discarded Luigi's Mansion prototypes."),
                ModCharacter("Boyfriend", "Protagonist", "🎤", "Armed with his trusty microphone against supernatural horrors.")
            ),
            platforms = listOf("Windows", "Android (Port)", "Mac", "Linux"),
            isFeatured = true
        ),
        FnfMod(
            id = "indie-cross",
            title = "Indie Cross - Crisis Cross",
            subtitle = "Cuphead, Sans, and Bendy crossover with real action mechanics",
            author = "Moro Nighteye & Studio Cross",
            version = "v1.5",
            engine = "Psych Engine Custom",
            downloadSize = "1.2 GB",
            releaseDate = "Apr 2022",
            lastUpdated = "Nov 2023",
            rating = 4.98f,
            downloadCount = "3.4M",
            tags = listOf("Crossover", "Action Mechanics", "Cuphead", "Undertale", "Bendy", "Voice Acted"),
            category = "Crossover",
            difficulty = "Insane",
            colorHex = 0xFF00E5FF,
            description = "The ultimate indie gaming crossover. Face off against Cuphead with parry and projectile dodge keys, Sans with bones and Gaster Blasters, and the Ink Demon with stealth mechanics.",
            lore = "A rogue dimensional portal fractures the multiverse, dragging Boyfriend through Inkwell Isle, the Underground of Undertale, and Joey Drew Studios.",
            downloadUrl = "https://gamebanana.com/mods/377705",
            mirrorUrl = "https://gamejolt.com/games/indiecross/643540",
            songs = listOf(
                SongItem("Snake Eyes", 155, "2:20", "Normal", "Cuphead"),
                SongItem("Technicolor Tussle", 175, "2:40", "Hard", "Cuphead"),
                SongItem("Knockout", 190, "3:00", "Insane", "Cuphead (Angry)"),
                SongItem("Bad Time", 145, "2:50", "Hard", "Sans"),
                SongItem("Despair", 200, "3:30", "Mania", "Nightmare Bendy"),
                SongItem("Bad To The Bone", 160, "2:15", "Normal", "Papyrus")
            ),
            mechanics = listOf(
                "Dodge Key [Spacebar] for attacks",
                "Attack Key [Shift] to shoot enemies",
                "Parry Pink Projectiles",
                "Bone and Blaster dodging grid",
                "Ink Splatter Screen Obstruction"
            ),
            characters = listOf(
                ModCharacter("Cuphead", "Opponent", "☕", "Rubberhose cartoon hero packing finger guns."),
                ModCharacter("Sans", "Opponent", "💀", "The smiling skeleton with blue soul gravity magic."),
                ModCharacter("The Ink Demon", "Opponent", "🖋️", "Menacing animated monster born of black ink."),
                ModCharacter("Boyfriend", "Protagonist", "🧢", "Dressed for battle with dodging reflexes.")
            ),
            platforms = listOf("Windows", "Android (APK)", "Mac"),
            isFeatured = true
        ),
        FnfMod(
            id = "vs-sonic-exe-restored",
            title = "VS Sonic.EXE: Restored",
            subtitle = "The legendary creepypasta mod featuring Too Slow and Triple Trouble",
            author = "Rightburst Ultra & Community Revival Team",
            version = "v3.0 Hotfix",
            engine = "Psych Engine 0.6.3",
            downloadSize = "950 MB",
            releaseDate = "Aug 2021",
            lastUpdated = "Dec 2023",
            rating = 4.88f,
            downloadCount = "4.1M",
            tags = listOf("Creepypasta", "Classic", "Sonic", "Triple Trouble", "Hardcore"),
            category = "Creepypasta",
            difficulty = "Expert",
            colorHex = 0xFF536DFE,
            description = "Fight for your life against corrupted Sonic entities across Green Hill Zone and blood-red pocket dimensions. Features the epic 8-minute marathon 'Triple Trouble'.",
            lore = "Boyfriend encounters an ancient cursed CD-ROM labeled Sonic.EXE and must out-sing the entity before his soul is trapped in the void.",
            downloadUrl = "https://gamebanana.com/mods/387978",
            mirrorUrl = "https://github.com/SonicExeRestored/game",
            songs = listOf(
                SongItem("Too Slow", 150, "2:30", "Normal", "Sonic.EXE"),
                SongItem("You Can't Run", 165, "2:45", "Hard", "Sonic.EXE"),
                SongItem("Triple Trouble", 178, "8:24", "Insane", "Xeno, Tails, Knuckles, Eggman"),
                SongItem("Final Escape", 210, "3:15", "Mania", "Lord X"),
                SongItem("Endless", 185, "2:55", "Hard", "Majin Sonic")
            ),
            mechanics = listOf(
                "Static glitch screen effect",
                "Ring gathering mini-timer",
                "Phantom Tails & Knuckles note swaps",
                "Instant-kill buzzsaw notes"
            ),
            characters = listOf(
                ModCharacter("Xenophanes", "Opponent", "🦔", "The sinister dark god inhabiting Sonic's form."),
                ModCharacter("Majin Sonic", "Opponent", "🎭", "Fun is Infinite! Joyful but eerie presence."),
                ModCharacter("Lord X", "Opponent", "⚡", "Ancient tormentor of forgotten Sega worlds."),
                ModCharacter("Tails Soul", "Puppet", "🦊", "Enslaved friend forced to rap for survival.")
            ),
            platforms = listOf("Windows", "Android (Port)", "Mac", "Linux")
        ),
        FnfMod(
            id = "smoke-em-out-struggle",
            title = "Smoke 'Em Out Struggle (VS Garcello)",
            subtitle = "The most emotional FNF mod ever created with lo-fi beats",
            author = "atsuover & Rageman",
            version = "v2.1",
            engine = "Psych Engine / Kade",
            downloadSize = "85 MB",
            releaseDate = "Apr 2021",
            lastUpdated = "Aug 2023",
            rating = 4.97f,
            downloadCount = "5.2M",
            tags = listOf("Classic Legends", "Emotional Story", "Lo-Fi", "Chill Vibes", "Android Friendly"),
            category = "Classic Legends",
            difficulty = "Casual",
            colorHex = 0xFF00E676,
            description = "Boyfriend and Girlfriend meet Garcello in a quiet alley. A chill smoke break turns into an unforgettable musical friendship and heartfelt goodbye.",
            lore = "Garcello is a laid-back smoker who just wants to vibe with BF in an alley, offering advice and wholesome encouragement even as his spirit fades.",
            downloadUrl = "https://gamebanana.com/mods/166531",
            mirrorUrl = "https://github.com/atsuover/garcello-mod",
            songs = listOf(
                SongItem("Headache", 120, "2:10", "Normal", "Garcello"),
                SongItem("Nerves", 135, "2:25", "Hard", "Garcello (Tired)"),
                SongItem("Release", 150, "2:40", "Hard", "Ghost Garcello"),
                SongItem("Fading", 100, "1:50", "Casual", "Ghost Garcello")
            ),
            mechanics = listOf(
                "Smoke cloud note tinting",
                "Atmospheric lo-fi audio reverb",
                "Dynamic chromatic background shifts"
            ),
            characters = listOf(
                ModCharacter("Garcello", "Opponent", "🚬", "Chill dude who loves smoking and good tunes."),
                ModCharacter("Ghost Garcello", "Opponent", "✨", "His peaceful spirit singing one last song."),
                ModCharacter("Boyfriend", "Protagonist", "🎤", "Honoring a musical brother.")
            ),
            platforms = listOf("Windows", "Android", "Web", "Mac")
        ),
        FnfMod(
            id = "vs-whitty-definitive",
            title = "VS Whitty: Definitive Edition",
            subtitle = "The historic mod that started the FNF modding revolution",
            author = "Sock.clip, Nate Anim8 & KadeDev",
            version = "v2.0 Definitive",
            engine = "Psych Engine 0.7",
            downloadSize = "220 MB",
            releaseDate = "Feb 2021",
            lastUpdated = "Oct 2023",
            rating = 4.92f,
            downloadCount = "6.5M",
            tags = listOf("Classic Legends", "Ballistic", "The Pioneer", "Full Week", "Remastered"),
            category = "Classic Legends",
            difficulty = "Hard",
            colorHex = 0xFFFF9100,
            description = "The explosive rockstar bomb-head Whitty returns with remastered art, re-charted Ballistic, bonus tracks, and cutscenes. A cornerstone of rhythm gaming history.",
            lore = "Whitmore is a bio-engineered stealth warrior on the run from the Updike bureau, hiding in alleyways until BF challenges him to a rap duel.",
            downloadUrl = "https://gamebanana.com/mods/354884",
            mirrorUrl = "https://github.com/KadeDev/Kade-Engine",
            songs = listOf(
                SongItem("Lo-Fight", 130, "2:05", "Normal", "Whitty"),
                SongItem("Overhead", 155, "2:20", "Hard", "Whitty (Fuming)"),
                SongItem("Ballistic (Definitive)", 210, "2:50", "Insane", "Whitty (Exploding)"),
                SongItem("Remorse", 165, "3:10", "Hard", "Updike")
            ),
            mechanics = listOf(
                "Camera shake on ballistic screams",
                "Dynamic fuse burning animation",
                "Custom bomb sound effect samples"
            ),
            characters = listOf(
                ModCharacter("Whitty", "Opponent", "💣", "Hot-headed former rockstar with a literal bomb for a head."),
                ModCharacter("Updike", "Special", "☁️", "Government agent hunting down bio-anomalies.")
            ),
            platforms = listOf("Windows", "Android", "Mac", "Linux")
        ),
        FnfMod(
            id = "hypnos-lullaby-v2",
            title = "Hypno's Lullaby V2",
            subtitle = "Psychological Pokemon horror with pendulum hypnosis & Unown puzzles",
            author = "Banbuds & Team Lullaby",
            version = "v2.1",
            engine = "Psych Engine Custom",
            downloadSize = "1.4 GB",
            releaseDate = "Oct 2022",
            lastUpdated = "Dec 2023",
            rating = 4.96f,
            downloadCount = "2.9M",
            tags = listOf("Creepypasta", "Pokemon", "Puzzle Mechanics", "Hypnosis Pendulum", "Overhaul"),
            category = "Creepypasta",
            difficulty = "Expert",
            colorHex = 0xFFA855F7,
            description = "Explore dark creepypasta urban legends from the Pokemon universe. Master the swinging pendulum to resist hypnosis while typing Unown letters in real-time.",
            lore = "Boyfriend searches for a missing Girlfriend in the misty Berry Forest, only to fall into the hypnotic trap of Hypno and forgotten ghost Pokegods.",
            downloadUrl = "https://gamebanana.com/mods/332345",
            mirrorUrl = "https://github.com/TeamLullaby/LullabyV2",
            songs = listOf(
                SongItem("Safety Lullaby", 130, "3:10", "Normal", "Hypno"),
                SongItem("Left Unchecked", 158, "3:40", "Hard", "Hypno (Hungry)"),
                SongItem("Monochrome", 140, "3:00", "Hard", "Gold / Lost Silver"),
                SongItem("Missingno", 180, "2:45", "Insane", "Glitch Pokemon"),
                SongItem("Frostbite", 170, "3:25", "Insane", "Red & Charizard")
            ),
            mechanics = listOf(
                "Spacebar Pendulum Sync to avoid falling asleep",
                "Unown Letter Typing Spells (Celebi)",
                "Freezing Screen Temperature Meter",
                "Hallucination Note Traps"
            ),
            characters = listOf(
                ModCharacter("Hypno", "Opponent", "🌀", "Sinister psychic Pokemon with a golden pendulum."),
                ModCharacter("Lost Silver (Gold)", "Opponent", "👻", "Dismembered Pokemon champion trapped in the void."),
                ModCharacter("Buried Alive", "Opponent", "🪦", "The forbidden Lavender Town boss.")
            ),
            platforms = listOf("Windows", "Android", "Mac")
        ),
        FnfMod(
            id = "hotline-024",
            title = "Hotline 024 (The Alien Mod)",
            subtitle = "Cyberpunk arcade synthwave aesthetic with shape-shifting Nikku",
            author = "Saruky, Sarcom & Hotline Team",
            version = "v1.2",
            engine = "Codename Engine",
            downloadSize = "1.1 GB",
            releaseDate = "Apr 2022",
            lastUpdated = "Jan 2024",
            rating = 4.91f,
            downloadCount = "1.7M",
            tags = listOf("Codename Engine", "Synthwave", "Cyberpunk", "Original Music", "Arcade"),
            category = "Overhaul",
            difficulty = "Medium",
            colorHex = 0xFFFF007F,
            description = "Immerse yourself in a retro-futuristic arcade city. Sing against Nikku, an alien with mimic vocal powers who transforms into famous characters mid-song.",
            lore = "Set in the vibrant city of O24 in the late 1990s, BF enters a neon subway station and battles Nikku, a stranded alien experimenting with human culture.",
            downloadUrl = "https://gamebanana.com/mods/373298",
            mirrorUrl = "https://github.com/Hotline024/game-releases",
            songs = listOf(
                SongItem("The Web", 135, "2:30", "Normal", "Nikku"),
                SongItem("Suka", 160, "2:40", "Hard", "Nikku"),
                SongItem("Astral Calamity", 185, "3:10", "Insane", "Nikku (Super)"),
                SongItem("Deep-Pockets", 145, "2:15", "Normal", "Cablecrow"),
                SongItem("Hyperfunk", 170, "3:00", "Hard", "Saruky Guest")
            ),
            mechanics = listOf(
                "Real-time sprite shape shifting",
                "Dynamic CRT scanline shaders",
                "Interactive cassette player UI"
            ),
            characters = listOf(
                ModCharacter("Nikku", "Opponent", "👽", "Alien mimic who can mimic any voice or instrument."),
                ModCharacter("Boyfriend", "Protagonist", "🎧", "Rocking neon roller skates and headphones.")
            ),
            platforms = listOf("Windows", "Android", "Linux")
        ),
        FnfMod(
            id = "doki-doki-takeover-plus",
            title = "Doki Doki Takeover Plus!",
            subtitle = "Complete psychological crossover with Monika, Sayori, Yuri & Natsuki",
            author = "Dorkifier & Team Takeover",
            version = "v2.0",
            engine = "Psych Engine 0.7",
            downloadSize = "1.5 GB",
            releaseDate = "Sep 2022",
            lastUpdated = "Feb 2024",
            rating = 4.94f,
            downloadCount = "2.6M",
            tags = listOf("Crossover", "Visual Novel", "Anime", "DDLC", "Full Weeks"),
            category = "Crossover",
            difficulty = "Medium",
            colorHex = 0xFFFF69B4,
            description = "BF and GF join the Literature Club! Featuring full animated story segments, custom UI themes for each club member, and haunting Act 2 reality twists.",
            lore = "Boyfriend and Girlfriend open Doki Doki Literature Club, stepping into the clubroom where Monika realizes BF possesses player input autonomy.",
            downloadUrl = "https://gamebanana.com/mods/47364",
            mirrorUrl = "https://github.com/TeamTakeover/DDTO-Plus",
            songs = listOf(
                SongItem("High School Conflict", 130, "2:20", "Normal", "Monika"),
                SongItem("Bara No Yume", 145, "2:35", "Normal", "Sayori"),
                SongItem("Deep Breaths", 155, "2:45", "Hard", "Yuri"),
                SongItem("My Confession", 160, "2:50", "Hard", "Natsuki"),
                SongItem("Epiphany", 175, "3:30", "Insane", "Monika (Reality Altered)")
            ),
            mechanics = listOf(
                "Poem writing mini-game between rounds",
                "Character glitch effects",
                "Script file manipulation mechanic"
            ),
            characters = listOf(
                ModCharacter("Monika", "Club President", "🎀", "Self-aware leader of the Literature Club."),
                ModCharacter("Sayori", "Vice President", "🍪", "Cheerful childhood friend hiding deep sadness."),
                ModCharacter("Yuri", "Member", "📖", "Timid bibliophile with passionate intensity."),
                ModCharacter("Natsuki", "Member", "🧁", "Feisty manga lover and baking expert.")
            ),
            platforms = listOf("Windows", "Android (Port)", "Mac")
        ),
        FnfMod(
            id = "vs-impostor-v4",
            title = "VS Impostor V4",
            subtitle = "The titanic 50+ song sci-fi rhythm game based on Among Us",
            author = "Clowfoe & Team Impostor",
            version = "v4.1.2",
            engine = "Psych Engine Custom",
            downloadSize = "2.1 GB",
            releaseDate = "Dec 2022",
            lastUpdated = "May 2024",
            rating = 4.97f,
            downloadCount = "4.5M",
            tags = listOf("Full Overhaul", "50+ Songs", "Among Us", "Animated Cutscenes", "Android Port"),
            category = "Overhaul",
            difficulty = "Hard",
            colorHex = 0xFFFF1744,
            description = "Board the Skeld, Mira HQ, and Polus in the largest FNF overhaul mod ever produced. Features 50+ songs, animated 3D cinematics, and customizable astronaut skins.",
            lore = "BF and GF sneak aboard an interstellar spaceship, but crewmates keep turning up dead. BF has to rap for innocence or face ejection into deep space.",
            downloadUrl = "https://gamebanana.com/mods/55652",
            mirrorUrl = "https://github.com/TeamImpostor/V4",
            songs = listOf(
                SongItem("Sussus Moogus", 140, "2:15", "Normal", "Red Impostor"),
                SongItem("Sabotage", 160, "2:40", "Hard", "Red Impostor"),
                SongItem("Meltdown", 175, "2:50", "Hard", "Red & Green Impostor"),
                SongItem("Defeat", 190, "3:10", "Mania", "Black Impostor"),
                SongItem("Finale", 200, "3:40", "Insane", "The Parasite")
            ),
            mechanics = listOf(
                "Reactor Meltdown Countdown timer",
                "Instant Kill Knife Notes",
                "Emergency Meeting voting rounds",
                "Custom Astronaut wardrobe unlocker"
            ),
            characters = listOf(
                ModCharacter("Red Impostor", "Opponent", "🔪", "The classic red imp with a deadly tongue."),
                ModCharacter("Green Impostor", "Opponent", "🔫", "Cocky accomplice rocking a mini-gun."),
                ModCharacter("Black Impostor", "Apex Predator", "🖤", "Silent assassin that never misses a strike.")
            ),
            platforms = listOf("Windows", "Android (Port)", "Mac", "Linux")
        ),
        FnfMod(
            id = "the-tricky-mod-2",
            title = "The Tricky Mod 2.0 (Madness Combat)",
            subtitle = "Madness Combat's immortal psychotic clown with fire notes",
            author = "Banbuds, Rozebud & KadeDev",
            version = "v2.1",
            engine = "Kade / Psych 0.7",
            downloadSize = "450 MB",
            releaseDate = "Jun 2021",
            lastUpdated = "Dec 2023",
            rating = 4.93f,
            downloadCount = "5.8M",
            tags = listOf("Madness Combat", "Classic Legends", "Fire Notes", "Expurgation", "Insane"),
            category = "Classic Legends",
            difficulty = "Insane",
            colorHex = 0xFF76FF03,
            description = "Deep in the Nevada desert, BF faces off against Tricky the Clown powered by the Improbability Drive. Features instant-death flame notes and the brutal test 'Expurgation'.",
            lore = "Tricky is bent on killing BF repeatedly using reality-bending machinery until Hank J. Wimbleton intervenes.",
            downloadUrl = "https://gamebanana.com/mods/44334",
            mirrorUrl = "https://github.com/KadeDev/TrickyMod",
            songs = listOf(
                SongItem("Improbable Outset", 145, "2:20", "Normal", "Tricky"),
                SongItem("Madness", 168, "2:40", "Hard", "Tricky"),
                SongItem("Hellclown", 195, "3:15", "Insane", "Giant Demon Tricky"),
                SongItem("Expurgation", 215, "3:30", "Mania", "Auditor Tricky")
            ),
            mechanics = listOf(
                "Flame Notes: Touching them instantly kills you",
                "Black Gremlin notes that drain health",
                "Improbability Stop Sign obstructions"
            ),
            characters = listOf(
                ModCharacter("Tricky", "Psychotic Clown", "🤡", "Undead assassin who refuses to stay dead."),
                ModCharacter("Hank", "Ally", "🕶️", "Nevada's greatest gun-slinging mercenary.")
            ),
            platforms = listOf("Windows", "Android", "Mac")
        ),
        FnfMod(
            id = "mid-fight-masses-deluxe",
            title = "Mid-Fight Masses: Deluxe Edition",
            subtitle = "The gothic cathedral showdown with Sarvente and Ruv",
            author = "Dokki.doodlez, Mike Geno & Community Port",
            version = "v3.0 Deluxe",
            engine = "Psych Engine 0.7",
            downloadSize = "380 MB",
            releaseDate = "Mar 2021",
            lastUpdated = "Jan 2024",
            rating = 4.86f,
            downloadCount = "4.9M",
            tags = listOf("Classic Legends", "Gothic", "Hard Bass", "Zavodila", "Sarvente"),
            category = "Classic Legends",
            difficulty = "Hard",
            colorHex = 0xFFFF4081,
            description = "BF and GF seek a restroom and walk into a sacred church run by the sweet nun Sarvente and her quiet, ground-shaking Russian partner Ruv.",
            lore = "Sarvente wants BF and GF to join her congregation for eternal salvation. When they decline, Ruv steps in with earth-shattering basslines.",
            downloadUrl = "https://gamebanana.com/mods/44345",
            mirrorUrl = "https://github.com/MFM-Port/MFM-Psych",
            songs = listOf(
                SongItem("Parish", 130, "2:10", "Normal", "Sarvente"),
                SongItem("Worship", 145, "2:25", "Hard", "Sarvente"),
                SongItem("Zavodila", 175, "2:45", "Insane", "Ruv"),
                SongItem("Gospel", 195, "3:05", "Mania", "Demon Sarvente")
            ),
            mechanics = listOf(
                "Screen earthquake shake on Ruv's notes",
                "Dual note chords",
                "Angelic halos note skins"
            ),
            characters = listOf(
                ModCharacter("Sarvente", "Nun / Demon", "⛪", "Devout church guardian harboring demonic origins."),
                ModCharacter("Ruv", "Enforcer", "🇷🇺", "Tremendous vocalist whose voice literally shatters stone.")
            ),
            platforms = listOf("Windows", "Android", "Web", "Mac")
        ),
        FnfMod(
            id = "twinsomnia",
            title = "Twinsomnia (Girl Next Door)",
            subtitle = "A cozy late-night jazz & R&B bedroom battle against Boogieman",
            author = "Sock.clip & The Twinsomnia Crew",
            version = "v1.1",
            engine = "Psych Engine 0.6",
            downloadSize = "420 MB",
            releaseDate = "May 2022",
            lastUpdated = "Dec 2023",
            rating = 4.95f,
            downloadCount = "1.5M",
            tags = listOf("Full Week", "Jazz R&B", "Original Characters", "Cozy Vibes", "Cutscenes"),
            category = "Full Week",
            difficulty = "Medium",
            colorHex = 0xFF7C4DFF,
            description = "A sleepover at GF's apartment gets interrupted by the mischievous Boogieman and his friends lurking in the closet. Smooth jazz brass and incredible singing.",
            lore = "Boogieman doesn't want to hurt anyone—he just loves late-night jam sessions when everyone else is supposed to be sleeping.",
            downloadUrl = "https://gamebanana.com/mods/379844",
            mirrorUrl = "https://github.com/Twinsomnia/Release",
            songs = listOf(
                SongItem("Boogieman", 125, "2:20", "Normal", "Boogieman"),
                SongItem("Girl Next Door", 140, "2:40", "Normal", "Boogieman"),
                SongItem("Sock.clip Special", 155, "3:00", "Hard", "Boogieman & Friends")
            ),
            mechanics = listOf(
                "Pillow throw dodge prompts",
                "Warm lofi lighting switches",
                "Swing rhythm note charts"
            ),
            characters = listOf(
                ModCharacter("Boogieman", "Opponent", "🛏️", "Fun-loving bedtime specter with a smooth voice.")
            ),
            platforms = listOf("Windows", "Android", "Mac")
        )
    )
}
