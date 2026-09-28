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
local enableDrain = true
local enableZoom = true
local starmanActive = false

function onCreatePost()
    -- Style Health Bar in Mario's Madness Crimson & Starman Cyan
    setHealthBarColors('FF183A', '00E5FF')

    -- Top Act Banner Text
    makeLuaText('actBannerTxt', 'ACT I • THE CORRUPTED CITADEL (VS ULTRA M)', 1280, 0, 14)
    setTextSize('actBannerTxt', 22)
    setTextColor('actBannerTxt', 'FF183A')
    setTextBorder('actBannerTxt', 2, '000000')
    setTextAlignment('actBannerTxt', 'center')
    setObjectCamera('actBannerTxt', 'hud')
    addLuaText('actBannerTxt')

    -- Bottom Secret Exit 0.7.3 Status Bar
    makeLuaText('seHudStatus', "Mario's Madness: Secret Exit Reimagined • Psych Engine " .. version .. " [TRUE ENDING ROUTE]", 1280, 0, 680)
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
            'ACT II • DIGITAL PHANTOMS (MR. VIRTUAL & GX)',
            'ACT II: PARANOIA MIRAGE',
            'E040FB',
            '6A0080'
        )
    elseif curBeat == 64 and currentAct < 3 then
        triggerActChange(
            3,
            'ACT III • BROKEN PIPE AMBUSH (DODGE READY!)',
            'ACT III: LAVA PIPE AMBUSH',
            'FF9100',
            'B23C00'
        )
    elseif curBeat == 96 and currentAct < 4 then
        starmanActive = true
        triggerActChange(
            4,
            'ACT IV • STARMAN LIBERATION (BF & GF POWER UP!)',
            'ACT IV: STARMAN AWAKENING!',
            '00E5FF',
            '006978'
        )
        setHealthBarColors('7C4DFF', '00E676')
    elseif curBeat == 128 and currentAct < 5 then
        starmanActive = true
        triggerActChange(
            5,
            'ACT V • SECRET EXIT FOUND! (BREAKING THE CARTRIDGE)',
            'FINAL ACT: SECRET EXIT FOUND!',
            'FFD740',
            'FFAB00'
        )
    end

    -- Beat Camera Zoom Pulse
    if enableZoom then
        if currentAct == 5 or (curBeat % 2 == 0) then
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
            triggerActChange(1, 'ACT I • ' .. value2, 'ACT I: ' .. value2, 'FF183A', '8A0B1E')
        elseif act == 2 then
            triggerActChange(2, 'ACT II • ' .. value2, 'ACT II: ' .. value2, 'E040FB', '6A0080')
        elseif act == 3 then
            triggerActChange(3, 'ACT III • ' .. value2, 'ACT III: ' .. value2, 'FF9100', 'B23C00')
        elseif act == 4 then
            starmanActive = true
            triggerActChange(4, 'ACT IV • ' .. value2, 'ACT IV: ' .. value2, '00E5FF', '006978')
        elseif act == 5 then
            starmanActive = true
            triggerActChange(5, 'ACT V • ' .. value2, 'FINAL ACT: ' .. value2, 'FFD740', 'FFAB00')
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
