-- ============================================================================
-- MARIO'S MADNESS: SECRET EXIT REIMAGINED (PSYCH ENGINE 0.7.3 DIRECTOR SCRIPT)
-- File: scripts/secret_exit_5act_director.lua
-- Compatible with Psych Engine 0.7.3 (PC & Android)
-- ============================================================================

local currentAct = 1
local enableDrain = true
local enableZoom = true
local starmanActive = false

function onCreate()
    if checkFileExists('songs/secret-exit-reimagined/Inst.ogg') and songPath == 'secret-exit-reimagined' then
        setPropertyFromClass('states.PlayState', 'SONG.song', 'secret-exit-reimagined')
    end
end

function onCreatePost()
    setHealthBarColors('FF183A', '00E5FF')

    makeLuaText('actBannerTxt', 'ACT I - THE CORRUPTED CITADEL (VS ULTRA M)', 1280, 0, 14)
    setTextSize('actBannerTxt', 22)
    setTextColor('actBannerTxt', 'FF183A')
    setTextBorder('actBannerTxt', 2, '000000')
    setTextAlignment('actBannerTxt', 'center')
    setObjectCamera('actBannerTxt', 'hud')
    addLuaText('actBannerTxt')

    makeLuaText('seHudStatus', "Mario's Madness: Secret Exit Reimagined | Psych Engine " .. version .. " [TRUE ENDING]", 1280, 0, 680)
    setTextSize('seHudStatus', 16)
    setTextColor('seHudStatus', 'FFD740')
    setTextBorder('seHudStatus', 1.5, '000000')
    setTextAlignment('seHudStatus', 'center')
    setObjectCamera('seHudStatus', 'hud')
    addLuaText('seHudStatus')

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
    if curBeat == 16 and currentAct < 2 then
        triggerActChange(2, 'ACT II - DIGITAL PHANTOMS (MR. VIRTUAL & GX)', 'ACT II: PARANOIA MIRAGE', 'E040FB', '6A0080')
    elseif curBeat == 32 and currentAct < 3 then
        triggerActChange(3, 'ACT III - BROKEN PIPE AMBUSH (DODGE READY!)', 'ACT III: LAVA PIPE AMBUSH', 'FF9100', 'B23C00')
    elseif curBeat == 48 and currentAct < 4 then
        starmanActive = true
        triggerActChange(4, 'ACT IV - STARMAN LIBERATION (BF & GF POWER UP!)', 'ACT IV: STARMAN AWAKENING!', '00E5FF', '006978')
        setHealthBarColors('7C4DFF', '00E676')
    elseif curBeat == 64 and currentAct < 5 then
        starmanActive = true
        triggerActChange(5, 'ACT V - SECRET EXIT FOUND! (BREAKING THE CARTRIDGE)', 'FINAL ACT: SECRET EXIT FOUND!', 'FFD740', 'FFAB00')
    end

    if enableZoom then
        if currentAct == 5 or (curBeat % 2 == 0) then
            triggerEvent('Add Camera Zoom', '0.022', '0.04')
        end
    end

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
