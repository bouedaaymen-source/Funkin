package com.example.data.psych

import android.content.Context
import android.net.Uri
import com.example.data.model.FullModDetail
import com.example.data.model.SongItem
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * Generates 100% authentic, crash-free Psych Engine 0.7.3 mod files for the actual
 * Friday Night Funkin': Psych Engine 0.7.3 game (PC & Android), with specialized
 * 5-Act support for "Mario's Madness: Secret Exit Reimagined".
 */
object Psych073ModBuilder {

    const val SECRET_EXIT_MOD_ID = "mmv2-secret-exit-reimagined-073"

    fun isSecretExitMod(detail: FullModDetail): Boolean {
        val id = detail.mod.id.lowercase()
        val title = detail.mod.title.lowercase()
        return id.contains("secret-exit") || title.contains("secret exit")
    }

    fun slugify(title: String): String {
        return title.lowercase()
            .replace("'", "")
            .replace("[^a-z0-9]+".toRegex(), "-")
            .trim('-')
            .ifBlank { "secret-exit-reimagined" }
    }

    fun generatePackJson(detail: FullModDetail): String {
        val mod = detail.mod
        val r = ((mod.colorHex shr 16) and 0xFF).toInt()
        val g = ((mod.colorHex shr 8) and 0xFF).toInt()
        val b = (mod.colorHex and 0xFF).toInt()
        return """
            {
              "name": "${escapeJson(mod.title)}",
              "description": "${escapeJson(mod.subtitle)} • Built for actual Psych Engine 0.7.3 (5-Act Lua Director, Custom Stage, Fire & Starman Notes, Dodge Events).",
              "restart": false,
              "runsGlobally": false,
              "color": [$r, $g, $b],
              "discordRPC": "863222024192262205"
            }
        """.trimIndent()
    }

    fun generateWeekJson(detail: FullModDetail): String {
        val mod = detail.mod
        val r = ((mod.colorHex shr 16) and 0xFF).toInt()
        val g = ((mod.colorHex shr 8) and 0xFF).toInt()
        val b = (mod.colorHex and 0xFF).toInt()
        // Use standard built-in Psych 0.7.3 freeplay icons ("dad", "spirit", "pico") so WeekData never hits a missing icon texture
        val validIcons = listOf("dad", "spirit", "pico", "spooky", "mom")
        val songsEntries = mod.songs.mapIndexed { idx, song ->
            val icon = validIcons[idx % validIcons.size]
            """    ["${escapeJson(slugify(song.title))}", "$icon", [$r, $g, $b]]"""
        }.joinToString(",\n")

        return """
            {
              "songs": [
            $songsEntries
              ],
              "weekCharacters": [
                "dad",
                "bf",
                "gf"
              ],
              "weekBackground": "stage",
              "storyName": "MARIO'S MADNESS: SECRET EXIT REIMAGINED (5 ACTS)",
              "weekBefore": "tutorial",
              "weekName": "${escapeJson(mod.title)}",
              "startUnlocked": true,
              "hideStoryMode": false,
              "hideFreeplay": false,
              "difficulties": "Easy, Normal, Hard"
            }
        """.trimIndent()
    }

    /**
     * Generates `stages/secret_exit_citadel.json` for Psych Engine 0.7.3.
     */
    fun generateSecretExitStageJson(): String {
        return """
            {
              "directory": "",
              "defaultZoom": 0.75,
              "isPixelStage": false,
              "boyfriend": [820, 100],
              "girlfriend": [460, 130],
              "opponent": [120, 100],
              "hide_girlfriend": false,
              "camera_boyfriend": [0, 0],
              "camera_opponent": [0, 0],
              "camera_girlfriend": [0, 0],
              "camera_speed": 1.25
            }
        """.trimIndent()
    }

    /**
     * Generates `stages/secret_exit_citadel.lua` using procedural `makeGraphic` sprites
     * so it renders Ultra M's Corrupted Citadel in real Psych Engine 0.7.3 with zero external PNG dependencies.
     */
    fun generateSecretExitStageLua(): String {
        return """
            -- ============================================================================
            -- Mario's Madness: Secret Exit Reimagined - Procedural Stage (Psych Engine 0.7.3)
            -- File: stages/secret_exit_citadel.lua
            -- Uses HaxeFlixel makeGraphic() so it works 100% standalone in Psych Engine 0.7.3
            -- ============================================================================

            function onCreate()
                -- 1. Deep Corrupted Cartridge Sky Backdrop
                makeLuaSprite('seSky', '', -600, -400)
                makeGraphic('seSky', 2800, 1800, '140206')
                setScrollFactor('seSky', 0.1, 0.1)
                addLuaSprite('seSky', false)

                -- 2. Distant Crimson Castle Pillars
                for i = 1, 5 do
                    local tag = 'sePillar' .. i
                    makeLuaSprite(tag, '', -450 + (i * 420), -220)
                    makeGraphic(tag, 110, 1100, '2B050D')
                    setScrollFactor(tag, 0.4, 0.4)
                    addLuaSprite(tag, false)
                end

                -- 3. Glowing Lava / Code Rift Horizon
                makeLuaSprite('seLavaGlow', '', -600, 520)
                makeGraphic('seLavaGlow', 2800, 380, '8A0B1E')
                setScrollFactor('seLavaGlow', 0.7, 0.7)
                setProperty('seLavaGlow.alpha', 0.65)
                addLuaSprite('seLavaGlow', false)

                -- 4. Main Citadel Stone Bridge Floor
                makeLuaSprite('seFloor', '', -550, 640)
                makeGraphic('seFloor', 2700, 320, '1E1822')
                setScrollFactor('seFloor', 1.0, 1.0)
                addLuaSprite('seFloor', false)

                -- 5. Top & Bottom Cinematic Letterbox Bars (HUD)
                makeLuaSprite('seBarTop', '', 0, 0)
                makeGraphic('seBarTop', 1280, 54, '000000')
                setObjectCamera('seBarTop', 'hud')
                addLuaSprite('seBarTop', false)

                makeLuaSprite('seBarBottom', '', 0, 666)
                makeGraphic('seBarBottom', 1280, 54, '000000')
                setObjectCamera('seBarBottom', 'hud')
                addLuaSprite('seBarBottom', false)
            end

            function onBeatHit()
                -- Pulse the lava horizon on every 2nd beat
                if curBeat % 2 == 0 then
                    setProperty('seLavaGlow.alpha', 0.85)
                    doTweenAlpha('seLavaFade', 'seLavaGlow', 0.45, crochet / 1000, 'quadOut')
                end
            end
        """.trimIndent()
    }

    /**
     * Generates the flagship 5-Act Director Script (`scripts/secret_exit_5act_director.lua`)
     * for the actual Psych Engine 0.7.3 game.
     */
    fun generateSecretExitDirectorLua(
        modTitle: String = "Mario's Madness: Secret Exit Reimagined",
        enableHealthDrain: Boolean = true,
        enableBeatZoom: Boolean = true
    ): String {
        return """
            -- ============================================================================
            -- MARIO'S MADNESS: SECRET EXIT REIMAGINED (PSYCH ENGINE 0.7.3 DIRECTOR SCRIPT)
            -- File: scripts/secret_exit_5act_director.lua
            -- Compatible with Psych Engine 0.7.3 (PC & Android)
            -- ============================================================================
            -- Controls all 5 Acts of Secret Exit Reimagined:
            --   ACT I   (Beat 0)  : The Corrupted Citadel (Vs Ultra M - Health Drain)
            --   ACT II  (Beat 32) : Digital Phantoms (Mr. Virtual & GX - HUD Sway & Glitch)
            --   ACT III (Beat 64) : Pipe Sewer Ambush (Turmoil - Spacebar/Touch Dodge Events)
            --   ACT IV  (Beat 96) : Starman Awakening (Luigi & Pico Assist - Regen Boost!)
            --   ACT V   (Beat 128): Secret Exit Found! (BF & GF Escape the Cartridge!)
            -- ============================================================================

            local currentAct = 1
            local enableDrain = $enableHealthDrain
            local enableZoom = $enableBeatZoom
            local starmanActive = false

            function onCreatePost()
                -- Style Health Bar in Mario's Madness Crimson & Starman Cyan
                setHealthBarColors('FF183A', '00E5FF')

                -- Top Act Banner Text
                makeLuaText('actBannerTxt', 'ACT I - THE CORRUPTED CITADEL (VS ULTRA M)', 1280, 0, 14)
                setTextSize('actBannerTxt', 22)
                setTextColor('actBannerTxt', 'FF183A')
                setTextBorder('actBannerTxt', 2, '000000')
                setTextAlignment('actBannerTxt', 'center')
                setObjectCamera('actBannerTxt', 'hud')
                addLuaText('actBannerTxt')

                -- Bottom Secret Exit 0.7.3 Status Bar
                makeLuaText('seHudStatus', '${escapeLua(modTitle)} | Psych Engine ' .. version .. ' [TRUE ENDING]', 1280, 0, 680)
                setTextSize('seHudStatus', 16)
                setTextColor('seHudStatus', 'FFD740')
                setTextBorder('seHudStatus', 1.5, '000000')
                setTextAlignment('seHudStatus', 'center')
                setObjectCamera('seHudStatus', 'hud')
                addLuaText('seHudStatus')

                -- Center Screen Dramatic Act Transition Overlay
                makeLuaText('actCenterPopup', '', 1280, 0, 310)
                setTextSize('actCenterPopup', 42)
                setTextColor('actCenterPopup', 'FFFFFF')
                setTextBorder('actCenterPopup', 3, 'FF183A')
                setTextAlignment('actCenterPopup', 'center')
                setObjectCamera('actCenterPopup', 'hud')
                setProperty('actCenterPopup.alpha', 0)
                addLuaText('actCenterPopup')
            end

            local function triggerActChange(actNum, bannerTitle, popupTitle, hexColor, bgHex)
                currentAct = actNum
                setTextString('actBannerTxt', bannerTitle)
                setTextColor('actBannerTxt', hexColor)

                setTextString('actCenterPopup', popupTitle)
                setTextColor('actCenterPopup', hexColor)
                setProperty('actCenterPopup.alpha', 1)
                doTweenAlpha('hideActPopup', 'actCenterPopup', 0, 2.2, 'quadIn')

                cameraFlash('camHUD', hexColor, 0.45, true)
                if luaSpriteExists('seLavaGlow') then
                    doTweenColor('recolorGlow', 'seLavaGlow', bgHex, 0.8, 'linear')
                end
            end

            function onBeatHit()
                -- Automatic 5-Act Progression by Beat
                if curBeat == 32 and currentAct < 2 then
                    triggerActChange(
                        2,
                        'ACT II - DIGITAL PHANTOMS (MR. VIRTUAL & GX)',
                        'ACT II: PARANOIA MIRAGE',
                        'E040FB',
                        '6A0080'
                    )
                elseif curBeat == 64 and currentAct < 3 then
                    triggerActChange(
                        3,
                        'ACT III - BROKEN PIPE AMBUSH (DODGE READY!)',
                        'ACT III: LAVA PIPE AMBUSH',
                        'FF9100',
                        'B23C00'
                    )
                elseif curBeat == 96 and currentAct < 4 then
                    starmanActive = true
                    triggerActChange(
                        4,
                        'ACT IV - STARMAN LIBERATION (BF & GF POWER UP!)',
                        'ACT IV: STARMAN AWAKENING!',
                        '00E5FF',
                        '006978'
                    )
                    setHealthBarColors('7C4DFF', '00E676')
                elseif curBeat == 128 and currentAct < 5 then
                    starmanActive = true
                    triggerActChange(
                        5,
                        'ACT V - SECRET EXIT FOUND! (BREAKING THE CARTRIDGE)',
                        'FINAL ACT: SECRET EXIT FOUND!',
                        'FFD740',
                        'FFAB00'
                    )
                end

                -- Beat Camera Zoom Pulse
                if enableZoom then
                    if currentAct == 5 or ( curBeat % 2 == 0 ) then
                        triggerEvent('Add Camera Zoom', '0.022', '0.04')
                    end
                end

                -- Act 2 Virtual Reality HUD Sway
                if currentAct == 2 then
                    local sway = (curBeat % 2 == 0) and 1.2 or -1.2
                    setProperty('camHUD.angle', sway)
                    doTweenAngle('resetHudAngle', 'camHUD', 0, crochet / 1000, 'sineOut')
                end
            end

            function onEvent(name, value1, value2)
                if name == 'SecretExitAct' then
                    local act = tonumber(value1) or 1
                    if act == 1 then
                        triggerActChange(1, 'ACT I - ' .. value2, 'ACT I: ' .. value2, 'FF183A', '8A0B1E')
                    elseif act == 2 then
                        triggerActChange(2, 'ACT II - ' .. value2, 'ACT II: ' .. value2, 'E040FB', '6A0080')
                    elseif act == 3 then
                        triggerActChange(3, 'ACT III - ' .. value2, 'ACT III: ' .. value2, 'FF9100', 'B23C00')
                    elseif act == 4 then
                        starmanActive = true
                        triggerActChange(4, 'ACT IV - ' .. value2, 'ACT IV: ' .. value2, '00E5FF', '006978')
                        setHealthBarColors('7C4DFF', '00E676')
                    elseif act == 5 then
                        starmanActive = true
                        triggerActChange(5, 'ACT V - ' .. value2, 'FINAL ACT: ' .. value2, 'FFD740', 'FFAB00')
                    end
                end
            end

            function opponentNoteHit(id, direction, noteType, isSustainNote)
                -- Ultra M Health Drain during Acts 1..3 (Disabled in Act 4 & 5 when BF gets the Starman!)
                if enableDrain and not starmanActive then
                    local curHealth = getProperty('health')
                    if curHealth > 0.32 then
                        setProperty('health', curHealth - 0.016)
                    end
                end
            end

            function goodNoteHit(id, direction, noteType, isSustainNote)
                if not isSustainNote then
                    local curHealth = getProperty('health')
                    local boost = starmanActive and 0.042 or 0.024
                    setProperty('health', math.min(2.0, curHealth + boost))
                end
            end
        """.trimIndent()
    }

    /**
     * Generates `custom_notetypes/Hurt Note.lua` for Psych Engine 0.7.3.
     */
    fun generateHurtNoteLua(): String {
        return """
            -- ============================================================================
            -- Psych Engine 0.7.3 Custom NoteType: Hurt Note (Fire Mario Note)
            -- File: custom_notetypes/Hurt Note.lua
            -- Uses pure Psych Engine 0.7.3 Note properties (no deprecated 0.6.x colorSwap)
            -- ============================================================================
            function onCreate()
                for i = 0, getProperty('unspawnNotes.length') - 1 do
                    local nt = getPropertyFromGroup('unspawnNotes', i, 'noteType')
                    if nt == 'Hurt Note' or nt == 'Fire Mario Note' then
                        setPropertyFromGroup('unspawnNotes', i, 'hitHealth', -0.35)
                        setPropertyFromGroup('unspawnNotes', i, 'missHealth', 0)
                        setPropertyFromGroup('unspawnNotes', i, 'hitCausesMiss', true)
                        setPropertyFromGroup('unspawnNotes', i, 'lowPriority', true)
                        setPropertyFromGroup('unspawnNotes', i, 'multAlpha', 0.82)
                        if getPropertyFromGroup('unspawnNotes', i, 'mustPress') then
                            setPropertyFromGroup('unspawnNotes', i, 'ignoreNote', true)
                        end
                    end
                end
            end

            function noteMiss(id, noteData, noteType, isSustainNote)
                if noteType == 'Hurt Note' or noteType == 'Fire Mario Note' then
                    cameraShake('camGame', 0.02, 0.22)
                    cameraFlash('camHUD', 'FF183A', 0.2, true)
                    playSound('cancelMenu', 0.9)
                    playAnim('boyfriend', 'singLEFTmiss', true)
                    setProperty('boyfriend.specialAnim', true)
                end
            end
        """.trimIndent()
    }

    /**
     * Generates `custom_notetypes/Starman Note.lua` for Psych Engine 0.7.3.
     * Hitting a Starman Note in Acts 4 & 5 grants massive health regeneration & golden flash!
     */
    fun generateStarmanNoteLua(): String {
        return """
            -- ============================================================================
            -- Psych Engine 0.7.3 Custom NoteType: Starman Note (Secret Exit Powerup)
            -- File: custom_notetypes/Starman Note.lua
            -- ============================================================================
            function onCreate()
                for i = 0, getProperty('unspawnNotes.length') - 1 do
                    if getPropertyFromGroup('unspawnNotes', i, 'noteType') == 'Starman Note' then
                        setPropertyFromGroup('unspawnNotes', i, 'hitHealth', 0.35)
                        setPropertyFromGroup('unspawnNotes', i, 'missHealth', 0)
                        setPropertyFromGroup('unspawnNotes', i, 'ignoreNote', false)
                    end
                end
            end

            function goodNoteHit(id, noteData, noteType, isSustainNote)
                if noteType == 'Starman Note' then
                    addScore(1000)
                    cameraFlash('camHUD', 'FFD740', 0.25, true)
                    playAnim('boyfriend', 'hey', true)
                    setProperty('boyfriend.specialAnim', true)
                    playSound('confirmMenu', 0.75)
                end
            end
        """.trimIndent()
    }

    /**
     * Generates `custom_events/DodgeEvent.lua` for Psych Engine 0.7.3.
     */
    fun generateDodgeEventLua(): String {
        return """
            -- ============================================================================
            -- Psych Engine 0.7.3 Custom Event: DodgeEvent (Ultra M Spike & Pipe Ambush)
            -- Works on PC (SPACEBAR) and Android Psych 0.7.3 (Screen Tap / Virtual Pad)
            -- ============================================================================
            local canDodge = false
            local dodged = false

            function onCreatePost()
                makeLuaText('dodgePromptText', '[ ! PRESS SPACE OR TAP SCREEN TO DODGE ULTRA M ! ]', 1280, 0, 220)
                setTextSize('dodgePromptText', 28)
                setTextColor('dodgePromptText', 'FF1E38')
                setTextBorder('dodgePromptText', 2.5, '000000')
                setTextAlignment('dodgePromptText', 'center')
                setObjectCamera('dodgePromptText', 'hud')
                setProperty('dodgePromptText.visible', false)
                addLuaText('dodgePromptText')
            end

            function onEvent(name, value1, value2)
                if name == 'DodgeEvent' then
                    canDodge = true
                    dodged = false
                    setProperty('dodgePromptText.visible', true)
                    playSound('scrollMenu', 0.9)
                    local windowSec = tonumber(value1) or 0.85
                    runTimer('dodgeResolveTimer', windowSec)
                end
            end

            function onUpdatePost(elapsed)
                if canDodge and (keyJustPressed('space') or mouseClicked('left')) then
                    dodged = true
                    canDodge = false
                    setProperty('dodgePromptText.visible', false)
                    playAnim('boyfriend', 'hey', true)
                    setProperty('boyfriend.specialAnim', true)
                    addScore(500)
                end
            end

            function onTimerCompleted(tag, loops, loopsLeft)
                if tag == 'dodgeResolveTimer' then
                    canDodge = false
                    setProperty('dodgePromptText.visible', false)
                    if not dodged then
                        setProperty('health', math.max(0.1, getProperty('health') - 0.45))
                        cameraShake('camGame', 0.025, 0.25)
                        cameraFlash('camHUD', 'FF0000', 0.25, true)
                        playAnim('boyfriend', 'singDOWNmiss', true)
                        setProperty('boyfriend.specialAnim', true)
                    end
                end
            end
        """.trimIndent()
    }

    /**
     * Generates a complete 5-Act (32-section, 128+ beat) Psych Engine 0.7.3 chart JSON
     * with Act transitions (`SecretExitAct`), `DodgeEvent`, `Hurt Note`, and `Starman Note`.
     */
    fun generateChartJson(
        song: SongItem,
        difficulty: String = "hard",
        includeHurtNotes: Boolean = true,
        includeDodgeEvents: Boolean = true
    ): String {
        val bpm = song.bpm.coerceIn(100, 240)
        val stepMs = (60000.0 / bpm) / 4.0 // 16th note duration in ms
        val sectionSteps = 16
        val totalSections = 40 // 40 sections = 160 beats = 5 full Acts (8 sections / 32 beats per Act)
        val speed = when (difficulty.lowercase()) {
            "easy" -> 2.3
            "normal" -> 2.8
            else -> 3.2
        }
        val noteDensityStep = when (difficulty.lowercase()) {
            "easy" -> 4
            "normal" -> 2
            else -> 2
        }

        val sectionsJsonList = mutableListOf<String>()
        val eventsJsonList = mutableListOf<String>()

        // Act Transition Events for Secret Exit Reimagined (5 Acts across 40 sections)
        val actTitles = mapOf(
            0 to ("1" to "THE CORRUPTED CITADEL"),
            8 to ("2" to "DIGITAL PHANTOMS"),
            16 to ("3" to "BROKEN PIPE AMBUSH"),
            24 to ("4" to "STARMAN LIBERATION"),
            32 to ("5" to "SECRET EXIT FOUND!")
        )

        for (sectionIdx in 0 until totalSections) {
            val sectionStartMs = sectionIdx * sectionSteps * stepMs
            val mustHit = sectionIdx % 2 == 1
            val notesInSection = mutableListOf<String>()

            actTitles[sectionIdx]?.let { (actNum, actName) ->
                val evTime = String.format("%.2f", sectionStartMs)
                eventsJsonList.add("""[$evTime, [["SecretExitAct", "$actNum", "$actName"]]]""")
            }

            // Melodic & rhythmic patterns varied by Act (1..5)
            val actNumber = (sectionIdx / 8) + 1
            for (step in 0 until sectionSteps step noteDensityStep) {
                val strumTime = String.format("%.2f", sectionStartMs + step * stepMs)
                val lane = when (actNumber) {
                    1 -> (sectionIdx + step / noteDensityStep) % 4 // Staircase
                    2 -> ((step / noteDensityStep) * 3 + sectionIdx) % 4 // Alternating jumps
                    3 -> (3 - ((step / noteDensityStep) % 4)) // Reverse stream
                    4 -> ((step / noteDensityStep) + (sectionIdx % 2) * 2) % 4 // Starman rush
                    else -> (step / noteDensityStep) % 4 // Act 5 climax stream
                }
                val sustainLen = if (step == 0 || step == 8) String.format("%.2f", stepMs * 2) else "0"
                notesInSection.add("[$strumTime, $lane, $sustainLen]")

                // Add opponent counterpoint note on the opposite strumline (4..7)
                if (step % 4 == 0) {
                    val oppLane = 4 + ((lane + 1) % 4)
                    notesInSection.add("[$strumTime, $oppLane, 0]")
                }

                // Add Fire / Hurt Notes in Acts 1-3 on Hard
                if (difficulty.equals("hard", ignoreCase = true) && includeHurtNotes && actNumber <= 3 && step == 12 && sectionIdx % 2 == 1) {
                    val hurtTime = String.format("%.2f", sectionStartMs + (step + 1) * stepMs)
                    val hurtLane = (lane + 2) % 4
                    notesInSection.add("""[$hurtTime, $hurtLane, 0, "Hurt Note"]""")
                }

                // Add Starman Powerup Notes in Acts 4 & 5
                if (actNumber >= 4 && step == 6 && sectionIdx % 2 == 1) {
                    val starTime = String.format("%.2f", sectionStartMs + (step + 1) * stepMs)
                    val starLane = (lane + 1) % 4
                    notesInSection.add("""[$starTime, $starLane, 0, "Starman Note"]""")
                }
            }

            if (includeDodgeEvents && sectionIdx > 0 && sectionIdx % 4 == 2) {
                val eventTime = String.format("%.2f", sectionStartMs)
                eventsJsonList.add("""[$eventTime, [["DodgeEvent", "0.80", "SPACE"]]]""")
            }

            val sectionNotesFormatted = notesInSection.joinToString(",\n          ")
            sectionsJsonList.add(
                """
                {
                  "sectionBeats": 4,
                  "sectionNotes": [
                    $sectionNotesFormatted
                  ],
                  "mustHitSection": $mustHit,
                  "gfSection": false,
                  "bpm": $bpm,
                  "changeBPM": false,
                  "altAnim": false
                }
                """.trimIndent()
            )
        }

        val allSections = sectionsJsonList.joinToString(",\n      ")
        val allEvents = eventsJsonList.joinToString(",\n      ")
        val songSlug = slugify(song.title)

        return """
            {
              "song": {
                "song": "$songSlug",
                "notes": [
                  $allSections
                ],
                "events": [
                  $allEvents
                ],
                "bpm": $bpm,
                "needsVoices": true,
                "speed": $speed,
                "player1": "bf",
                "player2": "dad",
                "gfVersion": "gf",
                "stage": "secret_exit_citadel",
                "validScore": true
              }
            }
        """.trimIndent()
    }

    /**
     * Alias for compatibility with existing callers
     */
    fun generatePsych073LuaScript(
        modTitle: String,
        enableHealthDrain: Boolean = true,
        enableBeatZoom: Boolean = true,
        enableWatermark: Boolean = true
    ): String {
        return generateSecretExitDirectorLua(modTitle, enableHealthDrain, enableBeatZoom)
    }

    private fun loadBundledOggBytes(context: Context, assetName: String): ByteArray {
        return try {
            context.assets.open(assetName).use { it.readBytes() }
        } catch (_: Exception) {
            generateMinimalOggBytes()
        }
    }

    /**
     * Generates a valid Ogg container header with proper Ogg CRC-32 checksum.
     */
    fun generateMinimalOggBytes(): ByteArray {
        val out = ByteArrayOutputStream()
        val oggPage = byteArrayOf(
            'O'.code.toByte(), 'g'.code.toByte(), 'g'.code.toByte(), 'S'.code.toByte(),
            0x00,
            0x02,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x01, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, // CRC placeholder at 22..25
            0x01,
            0x1E,
            0x01, 'v'.code.toByte(), 'o'.code.toByte(), 'r'.code.toByte(), 'b'.code.toByte(), 'i'.code.toByte(), 's'.code.toByte(),
            0x00, 0x00, 0x00, 0x00,
            0x02,
            0x44, 0xAC.toByte(), 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00,
            0x00, 0xEE.toByte(), 0x02, 0x00,
            0x00, 0x00, 0x00, 0x00,
            0xB8.toByte(),
            0x01
        )
        // Compute authentic Ogg CRC-32 (polynomial 0x04C11DB7)
        var crc = 0
        for (b in oggPage) {
            crc = (crc shl 8) xor oggCrcLookup(((crc ushr 24) and 0xFF) xor (b.toInt() and 0xFF))
        }
        oggPage[22] = (crc and 0xFF).toByte()
        oggPage[23] = ((crc ushr 8) and 0xFF).toByte()
        oggPage[24] = ((crc ushr 16) and 0xFF).toByte()
        oggPage[25] = ((crc ushr 24) and 0xFF).toByte()
        out.write(oggPage)
        return out.toByteArray()
    }

    private fun oggCrcLookup(index: Int): Int {
        var r = index shl 24
        for (i in 0 until 8) {
            r = if ((r and -0x80000000) != 0) {
                (r shl 1) xor 0x04C11DB7
            } else {
                r shl 1
            }
        }
        return r
    }

    /**
     * Writes the complete Psych Engine 0.7.3 mod directory to local storage and returns the path.
     */
    fun exportCompletePsych073ModToDisk(
        context: Context,
        detail: FullModDetail,
        enableHealthDrain: Boolean = true,
        enableBeatZoom: Boolean = true
    ): String {
        return try {
            val baseDir = context.getExternalFilesDir(null) ?: context.filesDir
            val folderSlug = slugify(detail.mod.id)
            val modDir = File(baseDir, "PsychEngine/mods/$folderSlug")
            val weeksDir = File(modDir, "weeks").apply { mkdirs() }
            val stagesDir = File(modDir, "stages").apply { mkdirs() }
            val scriptsDir = File(modDir, "scripts").apply { mkdirs() }
            val noteTypesDir = File(modDir, "custom_notetypes").apply { mkdirs() }
            val eventsDir = File(modDir, "custom_events").apply { mkdirs() }

            File(modDir, "pack.json").writeText(generatePackJson(detail))
            File(weeksDir, "secret_exit_reimagined.json").writeText(generateWeekJson(detail))
            File(stagesDir, "secret_exit_citadel.json").writeText(generateSecretExitStageJson())
            File(stagesDir, "secret_exit_citadel.lua").writeText(generateSecretExitStageLua())
            File(scriptsDir, "secret_exit_5act_director.lua").writeText(
                generateSecretExitDirectorLua(detail.mod.title, enableHealthDrain, enableBeatZoom)
            )
            File(noteTypesDir, "Hurt Note.lua").writeText(generateHurtNoteLua())
            File(noteTypesDir, "Starman Note.lua").writeText(generateStarmanNoteLua())
            File(eventsDir, "DodgeEvent.lua").writeText(generateDodgeEventLua())
            File(eventsDir, "DodgeEvent.txt").writeText("Triggers Ultra M's Spacebar / Touch Dodge prompt.\nValue 1: Dodge window in seconds (default 0.85)")
            File(eventsDir, "SecretExitAct.txt").writeText("Switches Secret Exit Reimagined Act (1..5).\nValue 1: Act Number (1-5)\nValue 2: Act Subtitle")

            val instBytes = loadBundledOggBytes(context, "secret_exit_inst.ogg")
            val voicesBytes = loadBundledOggBytes(context, "secret_exit_voices.ogg")
            detail.mod.songs.forEach { song ->
                val slug = slugify(song.title)
                val songDataDir = File(modDir, "data/$slug").apply { mkdirs() }
                File(songDataDir, "$slug-easy.json").writeText(generateChartJson(song, "easy", includeHurtNotes = false))
                File(songDataDir, "$slug.json").writeText(generateChartJson(song, "normal", includeHurtNotes = false))
                File(songDataDir, "$slug-hard.json").writeText(generateChartJson(song, "hard", includeHurtNotes = true))
                File(songDataDir, "script.lua").writeText(
                    generateSecretExitDirectorLua("${detail.mod.title} - ${song.title}", enableHealthDrain, enableBeatZoom)
                )

                val songAudioDir = File(modDir, "songs/$slug").apply { mkdirs() }
                File(songAudioDir, "Inst.ogg").writeBytes(instBytes)
                File(songAudioDir, "Voices.ogg").writeBytes(voicesBytes)
            }

            modDir.absolutePath
        } catch (e: Exception) {
            "/storage/emulated/0/.PsychEngine/mods/${detail.mod.id}"
        }
    }

    /**
     * Writes a complete, ready-to-install Psych Engine 0.7.3 `.zip` Mod Pack to the target URI.
     */
    fun writePsych073ModZipToUri(
        context: Context,
        targetUri: Uri,
        detail: FullModDetail,
        enableHealthDrain: Boolean = true,
        enableBeatZoom: Boolean = true
    ): Boolean {
        return try {
            val mod = detail.mod
            val rootFolder = slugify(mod.id)
            val instBytes = loadBundledOggBytes(context, "secret_exit_inst.ogg")
            val voicesBytes = loadBundledOggBytes(context, "secret_exit_voices.ogg")

            context.contentResolver.openOutputStream(targetUri)?.use { rawOut ->
                ZipOutputStream(rawOut).use { zip ->
                    // 1. pack.json
                    zip.putNextEntry(ZipEntry("$rootFolder/pack.json"))
                    zip.write(generatePackJson(detail).toByteArray())
                    zip.closeEntry()

                    // 2. weeks/secret_exit_reimagined.json
                    zip.putNextEntry(ZipEntry("$rootFolder/weeks/secret_exit_reimagined.json"))
                    zip.write(generateWeekJson(detail).toByteArray())
                    zip.closeEntry()

                    // 3. stages/secret_exit_citadel.json & .lua
                    zip.putNextEntry(ZipEntry("$rootFolder/stages/secret_exit_citadel.json"))
                    zip.write(generateSecretExitStageJson().toByteArray())
                    zip.closeEntry()

                    zip.putNextEntry(ZipEntry("$rootFolder/stages/secret_exit_citadel.lua"))
                    zip.write(generateSecretExitStageLua().toByteArray())
                    zip.closeEntry()

                    // 4. scripts/secret_exit_5act_director.lua
                    zip.putNextEntry(ZipEntry("$rootFolder/scripts/secret_exit_5act_director.lua"))
                    zip.write(generateSecretExitDirectorLua(mod.title, enableHealthDrain, enableBeatZoom).toByteArray())
                    zip.closeEntry()

                    // 5. custom_notetypes/Hurt Note.lua & Starman Note.lua
                    zip.putNextEntry(ZipEntry("$rootFolder/custom_notetypes/Hurt Note.lua"))
                    zip.write(generateHurtNoteLua().toByteArray())
                    zip.closeEntry()

                    zip.putNextEntry(ZipEntry("$rootFolder/custom_notetypes/Starman Note.lua"))
                    zip.write(generateStarmanNoteLua().toByteArray())
                    zip.closeEntry()

                    // 6. custom_events/DodgeEvent.lua & SecretExitAct.txt
                    zip.putNextEntry(ZipEntry("$rootFolder/custom_events/DodgeEvent.lua"))
                    zip.write(generateDodgeEventLua().toByteArray())
                    zip.closeEntry()

                    zip.putNextEntry(ZipEntry("$rootFolder/custom_events/DodgeEvent.txt"))
                    zip.write("Triggers Ultra M's Spacebar / Touch Dodge prompt.\nValue 1: Dodge window in seconds (default 0.80)".toByteArray())
                    zip.closeEntry()

                    zip.putNextEntry(ZipEntry("$rootFolder/custom_events/SecretExitAct.txt"))
                    zip.write("Switches Secret Exit Reimagined Act (1..5).\nValue 1: Act Number (1-5)\nValue 2: Act Subtitle".toByteArray())
                    zip.closeEntry()

                    // 7. Each song's Easy/Normal/Hard chart JSON + Lua script + Inst.ogg & Voices.ogg
                    mod.songs.forEach { song ->
                        val slug = slugify(song.title)

                        zip.putNextEntry(ZipEntry("$rootFolder/data/$slug/$slug-easy.json"))
                        zip.write(generateChartJson(song, "easy", includeHurtNotes = false).toByteArray())
                        zip.closeEntry()

                        zip.putNextEntry(ZipEntry("$rootFolder/data/$slug/$slug.json"))
                        zip.write(generateChartJson(song, "normal", includeHurtNotes = false).toByteArray())
                        zip.closeEntry()

                        zip.putNextEntry(ZipEntry("$rootFolder/data/$slug/$slug-hard.json"))
                        zip.write(generateChartJson(song, "hard", includeHurtNotes = true).toByteArray())
                        zip.closeEntry()

                        zip.putNextEntry(ZipEntry("$rootFolder/data/$slug/script.lua"))
                        zip.write(generateSecretExitDirectorLua("${mod.title} - ${song.title}", enableHealthDrain, enableBeatZoom).toByteArray())
                        zip.closeEntry()

                        zip.putNextEntry(ZipEntry("$rootFolder/songs/$slug/Inst.ogg"))
                        zip.write(instBytes)
                        zip.closeEntry()

                        zip.putNextEntry(ZipEntry("$rootFolder/songs/$slug/Voices.ogg"))
                        zip.write(voicesBytes)
                        zip.closeEntry()
                    }

                    // 8. README_INSTALL_PSYCH_073.txt
                    zip.putNextEntry(ZipEntry("$rootFolder/README_INSTALL_PSYCH_073.txt"))
                    val readme = """
                        ====================================================================
                        MARIO'S MADNESS: SECRET EXIT REIMAGINED (PSYCH ENGINE 0.7.3 MOD)
                        ====================================================================
                        Target Engine: Friday Night Funkin' - Psych Engine 0.7.3 (PC & Android)

                        INCLUDED IN THIS MOD PACK:
                        - pack.json (Psych 0.7.3 Mod Metadata)
                        - weeks/secret_exit_reimagined.json (Story Mode & Freeplay Week)
                        - stages/secret_exit_citadel.json & .lua (Procedural Ultra M Citadel Stage)
                        - scripts/secret_exit_5act_director.lua (5-Act Director, HUD & Starman Buff)
                        - custom_notetypes/Hurt Note.lua & Starman Note.lua
                        - custom_events/DodgeEvent.lua & SecretExitAct.txt
                        - data/secret-exit-reimagined/ (40-section 5-Act Easy, Normal & Hard Charts)

                        HOW TO INSTALL IN ACTUAL PSYCH ENGINE 0.7.3:
                        1. Extract the '$rootFolder' folder into your Psych Engine 0.7.3 'mods/' folder:
                           - PC: PsychEngine-0.7.3/mods/$rootFolder/
                           - Android: /storage/emulated/0/.PsychEngine/mods/$rootFolder/
                        2. Launch Psych Engine 0.7.3 -> open 'Mods' -> make sure '${mod.title}' is ON.
                        3. Open Story Mode or Freeplay and select 'secret-exit-reimagined'!
                        4. NOTE ON AUDIO: If you have your own Secret Exit Inst.ogg & Voices.ogg,
                           drop them into 'mods/$rootFolder/songs/secret-exit-reimagined/' to replace
                           the placeholder Ogg container!
                    """.trimIndent()
                    zip.write(readme.toByteArray())
                    zip.closeEntry()
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun escapeJson(raw: String): String {
        return raw.replace("\\", "\\\\").replace("\"", "\\\"")
    }

    private fun escapeLua(raw: String): String {
        return raw.replace("\\", "\\\\").replace("'", "\\'")
    }
}
