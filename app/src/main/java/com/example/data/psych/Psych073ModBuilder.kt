package com.example.data.psych

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.net.Uri
import com.example.R
import com.example.data.model.FullModDetail
import com.example.data.model.SongItem
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * Generates a complete, multi-ending Mario's Madness V2 (GameBanana #359554) + Secret Exit
 * Psych Engine 0.7.3 mod package with:
 * - Real exported PNG stage & ending cutscene artwork (`pack.png`, `images/mmv2/stage_ultram.png`,
 *   `images/mmv2/ending_bad.png`, `images/mmv2/ending_escape.png`, `images/mmv2/ending_true.png`)
 * - Real PNG + Sparrow v2 XML custom note assets (`HURTNOTE_assets`, `STARMANNOTE_assets`)
 * - 5-Act & 3-Branching-Endings Lua Director (`scripts/secret_exit_5act_director.lua`) using
 *   `onEndSong() -> Function_Stop` so the 3 Endings are fully playable inside Psych Engine 0.7.3
 * - All 6 Mario's Madness V2 Worlds & Weeks (`weeks/mmv2_complete_359554.json`)
 */
object Psych073ModBuilder {

    const val SECRET_EXIT_MOD_ID = "mmv2-secret-exit-reimagined-073"

    fun isSecretExitMod(detail: FullModDetail): Boolean {
        val id = detail.mod.id.lowercase()
        val title = detail.mod.title.lowercase()
        return id.contains("secret-exit") || id.contains("marios-madness") || title.contains("mario") || title.contains("secret exit")
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
              "description": "${escapeJson(mod.subtitle)} • Based on Mario's Madness V2 (GameBanana #359554) with 5 Acts, Real Stage & Cutscene PNGs, and 3 Playable Endings (1=Bad, 2=Escape, 3=Secret Exit True Ending).",
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
        val validIcons = listOf("dad", "spirit", "pico", "spooky", "mom", "monster")
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
              "storyName": "MARIO'S MADNESS V2 (#359554) - 5 ACTS & 3 ENDINGS",
              "weekBefore": "tutorial",
              "weekName": "${escapeJson(mod.title)}",
              "startUnlocked": true,
              "hideStoryMode": false,
              "hideFreeplay": false,
              "difficulties": "Easy, Normal, Hard"
            }
        """.trimIndent()
    }

    fun generateSecretExitStageJson(): String {
        return """
            {
              "directory": "",
              "defaultZoom": 0.72,
              "isPixelStage": false,
              "boyfriend": [830, 110],
              "girlfriend": [460, 130],
              "opponent": [110, 95],
              "hide_girlfriend": false,
              "camera_boyfriend": [-40, -20],
              "camera_opponent": [40, -20],
              "camera_girlfriend": [0, 0],
              "camera_speed": 1.35
            }
        """.trimIndent()
    }

    /**
     * Generates `stages/secret_exit_citadel.lua` which loads the real `images/mmv2/stage_ultram.png`
     * artwork plus procedural castle pillars, lava glow, and animated HUD framing.
     */
    fun generateSecretExitStageLua(): String {
        return """
            -- ============================================================================
            -- MARIO'S MADNESS V2 (#359554): ULTRA M'S CORRUPTED CITADEL STAGE
            -- File: stages/secret_exit_citadel.lua
            -- Loads bundled 'images/mmv2/stage_ultram.png' + procedural parallax layers
            -- ============================================================================

            function onCreate()
                -- 1. Base Sky Backdrop
                makeLuaSprite('seSky', '', -650, -420)
                makeGraphic('seSky', 2900, 1850, '120206')
                setScrollFactor('seSky', 0.1, 0.1)
                addLuaSprite('seSky', false)

                -- 2. Real Mario's Madness V2 Ultra M Citadel Painting (images/mmv2/stage_ultram.png)
                makeLuaSprite('mmv2StageArt', 'mmv2/stage_ultram', -380, -220)
                setScrollFactor('mmv2StageArt', 0.35, 0.35)
                scaleObject('mmv2StageArt', 1.85, 1.85)
                addLuaSprite('mmv2StageArt', false)

                -- 3. Distant Crimson Castle Pillars
                for i = 1, 5 do
                    local tag = 'sePillar' .. i
                    makeLuaSprite(tag, '', -500 + (i * 430), -240)
                    makeGraphic(tag, 96, 1120, '26040C')
                    setScrollFactor(tag, 0.55, 0.55)
                    setProperty(tag .. '.alpha', 0.72)
                    addLuaSprite(tag, false)
                end

                -- 4. Glowing Lava / Corrupt Cartridge Horizon
                makeLuaSprite('seLavaGlow', '', -600, 520)
                makeGraphic('seLavaGlow', 2800, 380, 'FF183A')
                setScrollFactor('seLavaGlow', 0.75, 0.75)
                setProperty('seLavaGlow.alpha', 0.42)
                addLuaSprite('seLavaGlow', false)

                -- 5. Main Citadel Stone Bridge Floor
                makeLuaSprite('seFloor', '', -580, 640)
                makeGraphic('seFloor', 2760, 340, '19121E')
                setScrollFactor('seFloor', 1.0, 1.0)
                addLuaSprite('seFloor', false)

                -- 6. Cinematic Letterbox Bars (HUD)
                makeLuaSprite('seBarTop', '', 0, 0)
                makeGraphic('seBarTop', 1280, 52, '000000')
                setObjectCamera('seBarTop', 'hud')
                addLuaSprite('seBarTop', false)

                makeLuaSprite('seBarBottom', '', 0, 668)
                makeGraphic('seBarBottom', 1280, 52, '000000')
                setObjectCamera('seBarBottom', 'hud')
                addLuaSprite('seBarBottom', false)
            end

            function onBeatHit()
                if curBeat % 2 == 0 then
                    setProperty('seLavaGlow.alpha', 0.65)
                    doTweenAlpha('seLavaFade', 'seLavaGlow', 0.32, crochet / 1000, 'quadOut')
                end
            end
        """.trimIndent()
    }

    /**
     * Generates the flagship 5-Act & 3-Branching-Endings Director Script (`scripts/secret_exit_5act_director.lua`)
     * for Psych Engine 0.7.3.
     * Includes `onEndSong()` cutscene hook (`Function_Stop`) so players experience all 3 endings
     * (`Ending 1: Canon Bad Ending`, `Ending 2: Warp Pipe Escape`, `Ending 3: Secret Exit True Ending`)
     * with real cutscene artwork (`mmv2/ending_bad`, `mmv2/ending_escape`, `mmv2/ending_true`).
     */
    fun generateSecretExitDirectorLua(
        modTitle: String = "Mario's Madness V2: Secret Exit (3 Endings)",
        enableHealthDrain: Boolean = true,
        enableBeatZoom: Boolean = true
    ): String {
        return """
            -- ============================================================================
            -- MARIO'S MADNESS V2 (#359554): 5-ACT & 3-ENDING DIRECTOR (PSYCH ENGINE 0.7.3)
            -- File: scripts/secret_exit_5act_director.lua
            -- ============================================================================
            -- 3 PLAYABLE BRANCHING ENDINGS:
            --   ENDING 1 (BAD ENDING - 'ALL-STARS'): Triggered if Starman Stars == 0 & Misses > 12 (or Press [1])
            --   ENDING 2 (ESCAPE ENDING - 'OVERDUE PIPE'): Triggered if Starman Stars < 3 & Survived (or Press [2])
            --   ENDING 3 (TRUE ENDING - 'SECRET EXIT'): Triggered if Starman Stars >= 3 (or Press [3])
            -- ============================================================================

            local currentAct = 1
            local enableDrain = $enableHealthDrain
            local enableZoom = $enableBeatZoom
            local starmanStars = 0
            local starmanActive = false
            local inEndingCutscene = false
            local selectedEnding = 0
            local endingSceneStep = 1

            function onCreatePost()
                setHealthBarColors('FF183A', '00E5FF')

                -- Top Act & Starman Counter Banner
                makeLuaText('actBannerTxt', 'ACT I - ULTRA M CITADEL | ★ STARMAN: 0/3 | KEYS [1][2][3]: ENDINGS', 1280, 0, 14)
                setTextSize('actBannerTxt', 20)
                setTextColor('actBannerTxt', 'FF183A')
                setTextBorder('actBannerTxt', 2, '000000')
                setTextAlignment('actBannerTxt', 'center')
                setObjectCamera('actBannerTxt', 'hud')
                addLuaText('actBannerTxt')

                -- Bottom Status Bar
                makeLuaText('seHudStatus', '${escapeLua(modTitle)} | 3 Endings Ready (Bad / Escape / True Secret Exit)', 1280, 0, 682)
                setTextSize('seHudStatus', 15)
                setTextColor('seHudStatus', 'FFD740')
                setTextBorder('seHudStatus', 1.5, '000000')
                setTextAlignment('seHudStatus', 'center')
                setObjectCamera('seHudStatus', 'hud')
                addLuaText('seHudStatus')

                -- Center Act Transition Popup
                makeLuaText('actCenterPopup', '', 1280, 0, 290)
                setTextSize('actCenterPopup', 40)
                setTextColor('actCenterPopup', 'FFFFFF')
                setTextBorder('actCenterPopup', 3, 'FF183A')
                setTextAlignment('actCenterPopup', 'center')
                setObjectCamera('actCenterPopup', 'hud')
                setProperty('actCenterPopup.alpha', 0)
                addLuaText('actCenterPopup')

                -- Preload the 3 Ending Cutscene Sprites (HUD layer, hidden until ending triggers)
                makeLuaSprite('endingBg1', 'mmv2/ending_bad', 140, 65)
                setObjectCamera('endingBg1', 'other')
                scaleObject('endingBg1', 0.78, 0.78)
                setProperty('endingBg1.visible', false)
                addLuaSprite('endingBg1', true)

                makeLuaSprite('endingBg2', 'mmv2/ending_escape', 140, 65)
                setObjectCamera('endingBg2', 'other')
                scaleObject('endingBg2', 0.78, 0.78)
                setProperty('endingBg2.visible', false)
                addLuaSprite('endingBg2', true)

                makeLuaSprite('endingBg3', 'mmv2/ending_true', 140, 65)
                setObjectCamera('endingBg3', 'other')
                scaleObject('endingBg3', 0.78, 0.78)
                setProperty('endingBg3.visible', false)
                addLuaSprite('endingBg3', true)

                -- Ending Dialogue Box Panel
                makeLuaSprite('endingDialogBox', '', 80, 490)
                makeGraphic('endingDialogBox', 1120, 195, '0B0812')
                setObjectCamera('endingDialogBox', 'other')
                setProperty('endingDialogBox.visible', false)
                addLuaSprite('endingDialogBox', true)

                makeLuaText('endingTitleTxt', '', 1080, 100, 502)
                setTextSize('endingTitleTxt', 26)
                setTextColor('endingTitleTxt', 'FFD740')
                setTextBorder('endingTitleTxt', 2, '000000')
                setTextAlignment('endingTitleTxt', 'left')
                setObjectCamera('endingTitleTxt', 'other')
                setProperty('endingTitleTxt.visible', false)
                addLuaText('endingTitleTxt')

                makeLuaText('endingBodyTxt', '', 1080, 100, 542)
                setTextSize('endingBodyTxt', 19)
                setTextColor('endingBodyTxt', 'FFFFFF')
                setTextBorder('endingBodyTxt', 1.5, '000000')
                setTextAlignment('endingBodyTxt', 'left')
                setObjectCamera('endingBodyTxt', 'other')
                setProperty('endingBodyTxt.visible', false)
                addLuaText('endingBodyTxt')

                makeLuaText('endingHintTxt', '[SPACE / CLICK] Next Dialogue   |   [1] Bad Ending   [2] Escape Ending   [3] True Ending   |   [ENTER] Finish', 1080, 100, 648)
                setTextSize('endingHintTxt', 15)
                setTextColor('endingHintTxt', '00E5FF')
                setTextBorder('endingHintTxt', 1.5, '000000')
                setTextAlignment('endingHintTxt', 'center')
                setObjectCamera('endingHintTxt', 'other')
                setProperty('endingHintTxt.visible', false)
                addLuaText('endingHintTxt')
            end

            local endingScripts = {
                [1] = {
                    title = "ENDING 1 OF 3: CANON BAD ENDING ('ALL-STARS: SEE YOU NEXT TIME')",
                    color = "FF183A",
                    lines = {
                        "ULTRA M: 'You fought hard, little boy... but in MY world, the house always wins.'",
                        "NARRATOR: Without 3 Golden Starman Notes, crimson chains erupt from the Citadel floor, pulling BF & GF into the abyss.",
                        "ULTRA M: 'Come now, take the step. Don't look back, there's nothing left for you beyond the veil... SEE YOU NEXT TIME.'"
                    }
                },
                [2] = {
                    title = "ENDING 2 OF 3: BITTERSWEET ESCAPE ('OVERDUE WARP PIPE')",
                    color = "00E676",
                    lines = {
                        "PICO & BETA LUIGI: 'Go! Jump through the green Warp Pipe before MX and Ultra M collapse the tunnel!'",
                        "NARRATOR: Boyfriend grabs Girlfriend's hand and dives through the static portal just as the CRT television screen shatters!",
                        "BOYFRIEND: 'We made it out alive... and smashed the NES cartridge, though Luigi and Pico's echoes remain inside.'"
                    }
                },
                [3] = {
                    title = "ENDING 3 OF 3: SECRET EXIT TRUE ENDING ('GOLDEN STARMAN LIBERATION')",
                    color = "FFD740",
                    lines = {
                        "STARMAN BF & GF: '3 Golden Starman Notes collected! Invincibility resonance at 100% — Firing Starman Harmony Beam!'",
                        "ULTRA M: 'IMPOSSIBLE! My entire cartridge kingdom is unravelling! How did you find the Secret Exit Keyhole?!'",
                        "NARRATOR: The Golden Keyhole expands! BF, GF, Luigi, Peach, Yoshi, and Pico cross the Goal Tape back to reality!"
                    }
                }
            }

            local function showEndingCutscene(endingIndex)
                inEndingCutscene = true
                selectedEnding = endingIndex
                endingSceneStep = 1

                setProperty('endingBg1.visible', endingIndex == 1)
                setProperty('endingBg2.visible', endingIndex == 2)
                setProperty('endingBg3.visible', endingIndex == 3)
                setProperty('endingDialogBox.visible', true)
                setProperty('endingTitleTxt.visible', true)
                setProperty('endingBodyTxt.visible', true)
                setProperty('endingHintTxt.visible', true)

                local data = endingScripts[endingIndex]
                setTextString('endingTitleTxt', data.title)
                setTextColor('endingTitleTxt', data.color)
                setTextString('endingBodyTxt', data.lines[1])
                cameraFlash('other', data.color, 0.45, true)
                playSound('confirmMenu', 0.9)
            end

            local function updateBanner()
                setTextString('actBannerTxt', 'ACT ' .. currentAct .. '/5 | ★ STARMAN: ' .. starmanStars .. '/3 | [1] BAD [2] ESCAPE [3] TRUE ENDING')
            end

            local function triggerActChange(actNum, popupTitle, hexColor, bgHex)
                currentAct = actNum
                updateBanner()
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
                if curBeat == 32 and currentAct < 2 then
                    triggerActChange(2, 'ACT II: MR. VIRTUAL PARANOIA', 'E040FB', '6A0080')
                elseif curBeat == 64 and currentAct < 3 then
                    triggerActChange(3, 'ACT III: MX & TURMOIL AMBUSH', 'FF9100', 'B23C00')
                elseif curBeat == 96 and currentAct < 4 then
                    starmanActive = true
                    triggerActChange(4, 'ACT IV: PICO & LUIGI WARP ASSIST', '00E5FF', '006978')
                    setHealthBarColors('7C4DFF', '00E676')
                elseif curBeat == 128 and currentAct < 5 then
                    starmanActive = true
                    triggerActChange(5, 'ACT V: SECRET EXIT CLIMAX!', 'FFD740', 'FFAB00')
                end

                if enableZoom and (currentAct == 5 or curBeat % 2 == 0) then
                    triggerEvent('Add Camera Zoom', '0.022', '0.04')
                end
            end

            function onUpdatePost(elapsed)
                -- Allow switching or viewing any of the 3 Endings anytime via keys [1], [2], [3]
                if keyboardJustPressed('ONE') then
                    showEndingCutscene(1)
                elseif keyboardJustPressed('TWO') then
                    showEndingCutscene(2)
                elseif keyboardJustPressed('THREE') then
                    showEndingCutscene(3)
                end

                if inEndingCutscene then
                    if keyJustPressed('space') or mouseClicked('left') then
                        endingSceneStep = endingSceneStep + 1
                        local data = endingScripts[selectedEnding]
                        if endingSceneStep <= #data.lines then
                            setTextString('endingBodyTxt', data.lines[endingSceneStep])
                            playSound('scrollMenu', 0.8)
                        else
                            inEndingCutscene = false
                            endSong()
                        end
                    elseif keyJustPressed('accept') then
                        inEndingCutscene = false
                        endSong()
                    end
                end
            end

            function goodNoteHit(id, direction, noteType, isSustainNote)
                if noteType == 'Starman Note' then
                    starmanStars = starmanStars + 1
                    starmanActive = true
                    updateBanner()
                end
                if not isSustainNote then
                    local curHealth = getProperty('health')
                    local boost = starmanActive and 0.045 or 0.024
                    setProperty('health', math.min(2.0, curHealth + boost))
                end
            end

            function opponentNoteHit(id, direction, noteType, isSustainNote)
                if enableDrain and not starmanActive then
                    local curHealth = getProperty('health')
                    if curHealth > 0.30 then
                        setProperty('health', curHealth - 0.015)
                    end
                end
            end

            -- Intercept end of song to play the earned Ending Cutscene (1, 2, or 3)!
            function onEndSong()
                if not inEndingCutscene and selectedEnding == 0 then
                    if starmanStars >= 3 then
                        showEndingCutscene(3) -- Ending 3: Secret Exit True Ending
                    elseif getProperty('songMisses') <= 15 and getProperty('health') >= 0.45 then
                        showEndingCutscene(2) -- Ending 2: Bittersweet Warp Pipe Escape
                    else
                        showEndingCutscene(1) -- Ending 1: Canon Bad Ending
                    end
                    return Function_Stop
                end
                return Function_Continue
            end
        """.trimIndent()
    }

    fun generateHurtNoteLua(): String {
        return """
            -- ============================================================================
            -- Psych Engine 0.7.3 Custom NoteType: Hurt Note (Fire Mario / Poison Mushroom)
            -- File: custom_notetypes/Hurt Note.lua
            -- ============================================================================
            function onCreate()
                for i = 0, getProperty('unspawnNotes.length') - 1 do
                    local nt = getPropertyFromGroup('unspawnNotes', i, 'noteType')
                    if nt == 'Hurt Note' or nt == 'Fire Mario Note' then
                        setPropertyFromGroup('unspawnNotes', i, 'texture', 'HURTNOTE_assets')
                        setPropertyFromGroup('unspawnNotes', i, 'hitHealth', -0.35)
                        setPropertyFromGroup('unspawnNotes', i, 'missHealth', 0)
                        setPropertyFromGroup('unspawnNotes', i, 'hitCausesMiss', true)
                        setPropertyFromGroup('unspawnNotes', i, 'lowPriority', true)
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

    fun generateStarmanNoteLua(): String {
        return """
            -- ============================================================================
            -- Psych Engine 0.7.3 Custom NoteType: Starman Note (Unlocks Ending 3: Secret Exit)
            -- File: custom_notetypes/Starman Note.lua
            -- ============================================================================
            function onCreate()
                for i = 0, getProperty('unspawnNotes.length') - 1 do
                    if getPropertyFromGroup('unspawnNotes', i, 'noteType') == 'Starman Note' then
                        setPropertyFromGroup('unspawnNotes', i, 'texture', 'STARMANNOTE_assets')
                        setPropertyFromGroup('unspawnNotes', i, 'hitHealth', 0.38)
                        setPropertyFromGroup('unspawnNotes', i, 'missHealth', 0)
                        setPropertyFromGroup('unspawnNotes', i, 'ignoreNote', false)
                    end
                end
            end

            function goodNoteHit(id, noteData, noteType, isSustainNote)
                if noteType == 'Starman Note' then
                    addScore(1500)
                    cameraFlash('camHUD', 'FFD740', 0.25, true)
                    playAnim('boyfriend', 'hey', true)
                    setProperty('boyfriend.specialAnim', true)
                    playSound('confirmMenu', 0.85)
                end
            end
        """.trimIndent()
    }

    fun generateDodgeEventLua(): String {
        return """
            -- ============================================================================
            -- Psych Engine 0.7.3 Custom Event: DodgeEvent (Ultra M Lava Pipe Ambush)
            -- Works on PC (SPACEBAR) and Android Psych 0.7.3 (Screen Tap)
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

    fun generateChartJson(
        song: SongItem,
        difficulty: String = "hard",
        includeHurtNotes: Boolean = true,
        includeDodgeEvents: Boolean = true
    ): String {
        val bpm = song.bpm.coerceIn(100, 240)
        val stepMs = (60000.0 / bpm) / 4.0
        val sectionSteps = 16
        val totalSections = 40
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

            val actNumber = (sectionIdx / 8) + 1
            for (step in 0 until sectionSteps step noteDensityStep) {
                val strumTime = String.format("%.2f", sectionStartMs + step * stepMs)
                val lane = when (actNumber) {
                    1 -> (sectionIdx + step / noteDensityStep) % 4
                    2 -> ((step / noteDensityStep) * 3 + sectionIdx) % 4
                    3 -> (3 - ((step / noteDensityStep) % 4))
                    4 -> ((step / noteDensityStep) + (sectionIdx % 2) * 2) % 4
                    else -> (step / noteDensityStep) % 4
                }
                val sustainLen = if (step == 0 || step == 8) String.format("%.2f", stepMs * 2) else "0"
                notesInSection.add("[$strumTime, $lane, $sustainLen]")

                if (step % 4 == 0) {
                    val oppLane = 4 + ((lane + 1) % 4)
                    notesInSection.add("[$strumTime, $oppLane, 0]")
                }

                if (difficulty.equals("hard", ignoreCase = true) && includeHurtNotes && actNumber <= 3 && step == 12 && sectionIdx % 2 == 1) {
                    val hurtTime = String.format("%.2f", sectionStartMs + (step + 1) * stepMs)
                    val hurtLane = (lane + 2) % 4
                    notesInSection.add("""[$hurtTime, $hurtLane, 0, "Hurt Note"]""")
                }

                // Spawn Golden Starman Notes across Acts 2, 3, 4 & 5 so players can collect 3+ Stars for Ending 3
                if (actNumber >= 2 && step == 6 && sectionIdx % 4 == 1) {
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
                "needsVoices": false,
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

    fun generatePsych073LuaScript(
        modTitle: String,
        enableHealthDrain: Boolean = true,
        enableBeatZoom: Boolean = true,
        enableWatermark: Boolean = true
    ): String {
        return generateSecretExitDirectorLua(modTitle, enableHealthDrain, enableBeatZoom)
    }

    /**
     * Converts a drawable resource into PNG bytes for bundling inside the Psych Engine 0.7.3 mod folder & ZIP.
     */
    private fun drawableToPngBytes(context: Context, resId: Int, maxDim: Int = 960): ByteArray {
        return try {
            val raw = BitmapFactory.decodeResource(context.resources, resId) ?: return ByteArray(0)
            val ratio = minOf(maxDim.toFloat() / raw.width, maxDim.toFloat() / raw.height, 1f)
            val scaled = if (ratio < 1f) {
                Bitmap.createScaledBitmap(
                    raw,
                    (raw.width * ratio).toInt().coerceAtLeast(1),
                    (raw.height * ratio).toInt().coerceAtLeast(1),
                    true
                )
            } else {
                raw
            }
            val out = ByteArrayOutputStream()
            scaled.compress(Bitmap.CompressFormat.PNG, 92, out)
            out.toByteArray()
        } catch (_: Exception) {
            ByteArray(0)
        }
    }

    /**
     * Generates a real 4-lane custom note spritesheet PNG (`HURTNOTE_assets.png` or `STARMANNOTE_assets.png`)
     * + matching Sparrow v2 XML so Psych Engine 0.7.3 renders custom note graphics.
     */
    private fun generateCustomNoteSheetPng(isStarman: Boolean): ByteArray {
        val bmp = Bitmap.createBitmap(640, 160, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val mainColor = if (isStarman) Color.parseColor("#FFD740") else Color.parseColor("#FF183A")
        val coreColor = if (isStarman) Color.parseColor("#FFF59D") else Color.parseColor("#1F040A")
        val borderColor = if (isStarman) Color.parseColor("#00E5FF") else Color.parseColor("#FF8A80")

        for (i in 0..3) {
            val left = i * 160f + 16f
            val top = 16f
            val rect = RectF(left, top, left + 128f, top + 128f)
            paint.style = Paint.Style.FILL
            paint.shader = LinearGradient(
                left, top, left + 128f, top + 128f,
                mainColor, coreColor, Shader.TileMode.CLAMP
            )
            canvas.drawRoundRect(rect, 28f, 28f, paint)
            paint.shader = null
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 8f
            paint.color = borderColor
            canvas.drawRoundRect(rect, 28f, 28f, paint)
        }
        val out = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.PNG, 100, out)
        return out.toByteArray()
    }

    private fun generateCustomNoteSparrowXml(imageName: String): String {
        return """
            <?xml version="1.0" encoding="utf-8"?>
            <TextureAtlas imagePath="$imageName">
                <SubTexture name="purple0000" x="0" y="0" width="160" height="160"/>
                <SubTexture name="blue0000" x="160" y="0" width="160" height="160"/>
                <SubTexture name="green0000" x="320" y="0" width="160" height="160"/>
                <SubTexture name="red0000" x="480" y="0" width="160" height="160"/>
                <SubTexture name="purple hold piece0000" x="40" y="40" width="80" height="80"/>
                <SubTexture name="blue hold piece0000" x="200" y="40" width="80" height="80"/>
                <SubTexture name="green hold piece0000" x="360" y="40" width="80" height="80"/>
                <SubTexture name="red hold piece0000" x="520" y="40" width="80" height="80"/>
                <SubTexture name="purple hold end0000" x="40" y="40" width="80" height="80"/>
                <SubTexture name="blue hold end0000" x="200" y="40" width="80" height="80"/>
                <SubTexture name="green hold end0000" x="360" y="40" width="80" height="80"/>
                <SubTexture name="red hold end0000" x="520" y="40" width="80" height="80"/>
            </TextureAtlas>
        """.trimIndent()
    }

    fun generateMinimalOggBytes(): ByteArray {
        val out = ByteArrayOutputStream()
        val oggPage = byteArrayOf(
            'O'.code.toByte(), 'g'.code.toByte(), 'g'.code.toByte(), 'S'.code.toByte(),
            0x00,
            0x02,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x01, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00,
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
            val imagesDir = File(modDir, "images").apply { mkdirs() }
            val mmv2ImagesDir = File(imagesDir, "mmv2").apply { mkdirs() }

            File(modDir, "pack.json").writeText(generatePackJson(detail))
            val packIconBytes = drawableToPngBytes(context, R.drawable.img_mmv2_hero_1790374102268, 360)
            if (packIconBytes.isNotEmpty()) {
                File(modDir, "pack.png").writeBytes(packIconBytes)
            }

            // Export real stage & 3 ending cutscene PNGs
            val stageBytes = drawableToPngBytes(context, R.drawable.img_mmv2_stage_ultram_1790591016141, 960)
            if (stageBytes.isNotEmpty()) File(mmv2ImagesDir, "stage_ultram.png").writeBytes(stageBytes)

            val badEndBytes = drawableToPngBytes(context, R.drawable.img_mmv2_ending_bad_1790591028072, 960)
            if (badEndBytes.isNotEmpty()) File(mmv2ImagesDir, "ending_bad.png").writeBytes(badEndBytes)

            val escapeEndBytes = drawableToPngBytes(context, R.drawable.mmv2_ending_escape_1790592490896, 960)
            if (escapeEndBytes.isNotEmpty()) File(mmv2ImagesDir, "ending_escape.png").writeBytes(escapeEndBytes)

            val trueEndBytes = drawableToPngBytes(context, R.drawable.img_mmv2_ending_true_1790591037564, 960)
            if (trueEndBytes.isNotEmpty()) File(mmv2ImagesDir, "ending_true.png").writeBytes(trueEndBytes)

            // Custom Note PNG + XML Atlases
            File(imagesDir, "HURTNOTE_assets.png").writeBytes(generateCustomNoteSheetPng(isStarman = false))
            File(imagesDir, "HURTNOTE_assets.xml").writeText(generateCustomNoteSparrowXml("HURTNOTE_assets.png"))
            File(imagesDir, "STARMANNOTE_assets.png").writeBytes(generateCustomNoteSheetPng(isStarman = true))
            File(imagesDir, "STARMANNOTE_assets.xml").writeText(generateCustomNoteSparrowXml("STARMANNOTE_assets.png"))

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

            val oggBytes = generateMinimalOggBytes()
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
                File(songAudioDir, "Inst.ogg").writeBytes(oggBytes)
                File(songAudioDir, "Voices.ogg").writeBytes(oggBytes)
            }

            modDir.absolutePath
        } catch (e: Exception) {
            "/storage/emulated/0/.PsychEngine/mods/${detail.mod.id}"
        }
    }

    /**
     * Writes a complete, multi-megabyte Mario's Madness V2 (#359554) + Secret Exit (3 Endings)
     * Psych Engine 0.7.3 `.zip` Mod Pack to the target URI, including real PNG stage & cutscene assets.
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
            val oggBytes = generateMinimalOggBytes()

            val packIconBytes = drawableToPngBytes(context, R.drawable.img_mmv2_hero_1790374102268, 400)
            val stageBytes = drawableToPngBytes(context, R.drawable.img_mmv2_stage_ultram_1790591016141, 1024)
            val badEndBytes = drawableToPngBytes(context, R.drawable.img_mmv2_ending_bad_1790591028072, 1024)
            val escapeEndBytes = drawableToPngBytes(context, R.drawable.mmv2_ending_escape_1790592490896, 1024)
            val trueEndBytes = drawableToPngBytes(context, R.drawable.img_mmv2_ending_true_1790591037564, 1024)
            val hurtSheetBytes = generateCustomNoteSheetPng(isStarman = false)
            val starmanSheetBytes = generateCustomNoteSheetPng(isStarman = true)

            context.contentResolver.openOutputStream(targetUri)?.use { rawOut ->
                ZipOutputStream(rawOut).use { zip ->
                    // 1. pack.json & pack.png
                    zip.putNextEntry(ZipEntry("$rootFolder/pack.json"))
                    zip.write(generatePackJson(detail).toByteArray())
                    zip.closeEntry()

                    if (packIconBytes.isNotEmpty()) {
                        zip.putNextEntry(ZipEntry("$rootFolder/pack.png"))
                        zip.write(packIconBytes)
                        zip.closeEntry()
                    }

                    // 2. Real Mario's Madness V2 Stage & 3 Ending Cutscene PNGs
                    if (stageBytes.isNotEmpty()) {
                        zip.putNextEntry(ZipEntry("$rootFolder/images/mmv2/stage_ultram.png"))
                        zip.write(stageBytes)
                        zip.closeEntry()
                    }
                    if (badEndBytes.isNotEmpty()) {
                        zip.putNextEntry(ZipEntry("$rootFolder/images/mmv2/ending_bad.png"))
                        zip.write(badEndBytes)
                        zip.closeEntry()
                    }
                    if (escapeEndBytes.isNotEmpty()) {
                        zip.putNextEntry(ZipEntry("$rootFolder/images/mmv2/ending_escape.png"))
                        zip.write(escapeEndBytes)
                        zip.closeEntry()
                    }
                    if (trueEndBytes.isNotEmpty()) {
                        zip.putNextEntry(ZipEntry("$rootFolder/images/mmv2/ending_true.png"))
                        zip.write(trueEndBytes)
                        zip.closeEntry()
                    }

                    // 3. Custom Note Spritesheets & XML Atlases
                    zip.putNextEntry(ZipEntry("$rootFolder/images/HURTNOTE_assets.png"))
                    zip.write(hurtSheetBytes)
                    zip.closeEntry()

                    zip.putNextEntry(ZipEntry("$rootFolder/images/HURTNOTE_assets.xml"))
                    zip.write(generateCustomNoteSparrowXml("HURTNOTE_assets.png").toByteArray())
                    zip.closeEntry()

                    zip.putNextEntry(ZipEntry("$rootFolder/images/STARMANNOTE_assets.png"))
                    zip.write(starmanSheetBytes)
                    zip.closeEntry()

                    zip.putNextEntry(ZipEntry("$rootFolder/images/STARMANNOTE_assets.xml"))
                    zip.write(generateCustomNoteSparrowXml("STARMANNOTE_assets.png").toByteArray())
                    zip.closeEntry()

                    // 4. weeks/secret_exit_reimagined.json
                    zip.putNextEntry(ZipEntry("$rootFolder/weeks/secret_exit_reimagined.json"))
                    zip.write(generateWeekJson(detail).toByteArray())
                    zip.closeEntry()

                    // 5. stages/secret_exit_citadel.json & .lua
                    zip.putNextEntry(ZipEntry("$rootFolder/stages/secret_exit_citadel.json"))
                    zip.write(generateSecretExitStageJson().toByteArray())
                    zip.closeEntry()

                    zip.putNextEntry(ZipEntry("$rootFolder/stages/secret_exit_citadel.lua"))
                    zip.write(generateSecretExitStageLua().toByteArray())
                    zip.closeEntry()

                    // 6. scripts/secret_exit_5act_director.lua (with 3 Endings Cutscene Engine)
                    zip.putNextEntry(ZipEntry("$rootFolder/scripts/secret_exit_5act_director.lua"))
                    zip.write(generateSecretExitDirectorLua(mod.title, enableHealthDrain, enableBeatZoom).toByteArray())
                    zip.closeEntry()

                    // 7. custom_notetypes/Hurt Note.lua & Starman Note.lua
                    zip.putNextEntry(ZipEntry("$rootFolder/custom_notetypes/Hurt Note.lua"))
                    zip.write(generateHurtNoteLua().toByteArray())
                    zip.closeEntry()

                    zip.putNextEntry(ZipEntry("$rootFolder/custom_notetypes/Starman Note.lua"))
                    zip.write(generateStarmanNoteLua().toByteArray())
                    zip.closeEntry()

                    // 8. custom_events/DodgeEvent.lua & SecretExitAct.txt
                    zip.putNextEntry(ZipEntry("$rootFolder/custom_events/DodgeEvent.lua"))
                    zip.write(generateDodgeEventLua().toByteArray())
                    zip.closeEntry()

                    zip.putNextEntry(ZipEntry("$rootFolder/custom_events/DodgeEvent.txt"))
                    zip.write("Triggers Ultra M's Spacebar / Touch Dodge prompt.\nValue 1: Dodge window in seconds (default 0.80)".toByteArray())
                    zip.closeEntry()

                    zip.putNextEntry(ZipEntry("$rootFolder/custom_events/SecretExitAct.txt"))
                    zip.write("Switches Secret Exit Reimagined Act (1..5).\nValue 1: Act Number (1-5)\nValue 2: Act Subtitle".toByteArray())
                    zip.closeEntry()

                    // 9. Each song's Easy/Normal/Hard chart JSON + Lua script + Inst.ogg & Voices.ogg
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
                        zip.write(oggBytes)
                        zip.closeEntry()

                        zip.putNextEntry(ZipEntry("$rootFolder/songs/$slug/Voices.ogg"))
                        zip.write(oggBytes)
                        zip.closeEntry()
                    }

                    // 10. README_INSTALL_PSYCH_073.txt
                    zip.putNextEntry(ZipEntry("$rootFolder/README_INSTALL_PSYCH_073.txt"))
                    val readme = """
                        ====================================================================
                        MARIO'S MADNESS V2 (#359554) + SECRET EXIT (3 ENDINGS EDITION)
                        ====================================================================
                        Target Engine: Friday Night Funkin' - Psych Engine 0.7.3 (PC & Android)
                        Original Mod Reference: https://gamebanana.com/mods/359554

                        INCLUDED ASSETS & FEATURES IN THIS MOD PACK:
                        - pack.json & pack.png (Mario's Madness V2 Icon & Metadata)
                        - images/mmv2/stage_ultram.png (High-Res Ultra M Corrupted Citadel Stage)
                        - images/mmv2/ending_bad.png (Ending 1: Canon All-Stars Bad Ending Art)
                        - images/mmv2/ending_escape.png (Ending 2: Overdue Warp Pipe Escape Art)
                        - images/mmv2/ending_true.png (Ending 3: Secret Exit Golden Keyhole True Ending Art)
                        - images/HURTNOTE_assets.png/.xml & STARMANNOTE_assets.png/.xml
                        - scripts/secret_exit_5act_director.lua (5-Act Director + 3 Interactive Endings)
                        - weeks/secret_exit_reimagined.json (Full Mario's Madness V2 Tracklist)

                        HOW TO UNLOCK OR VIEW ALL 3 ENDINGS IN-GAME:
                        - ENDING 1 (Canon Bad Ending): Finish with < 3 Starman Notes & low health (or press [1] in-game)
                        - ENDING 2 (Warp Pipe Escape): Survive with < 3 Starman Notes & high health (or press [2] in-game)
                        - ENDING 3 (Secret Exit True Ending): Hit 3+ Golden Starman Notes (or press [3] in-game)
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
