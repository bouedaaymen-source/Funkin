-- ============================================================================
-- MARIO'S MADNESS V2: ANIMATED ULTRA M / MR. VIRTUAL / TURMOIL CHARACTER RIG
-- + CUSTOM HEALTH ICON + MARIO'S MADNESS AUDIO SYNTHESIZER (PSYCH ENGINE 0.7.3)
-- ============================================================================

local baseX = 160
local baseY = 210
local curForm = 1

local function box(tag, x, y, w, h, hex, cam)
    makeLuaSprite(tag, '', x, y)
    makeGraphic(tag, w, h, hex)
    if cam then setObjectCamera(tag, cam) end
    addLuaSprite(tag, true)
end

function onCreatePost()
    -- Hide Daddy Dearest and his purple health bar icon!
    setProperty('dad.visible', false)
    setProperty('dad.alpha', 0)
    setProperty('iconP2.visible', false)

    -- Build Animated Ultra M / Horror Mario Rig on Stage
    box('mmShadow', baseX - 20, baseY + 390, 230, 28, '0A0003')
    box('mmAura', baseX - 25, baseY - 20, 240, 420, '4A000E')
    setProperty('mmAura.alpha', 0.45)

    -- Legs & Boots
    box('mmLegL', baseX + 28, baseY + 290, 52, 95, '0D1B2A')
    box('mmLegR', baseX + 108, baseY + 290, 52, 95, '0D1B2A')
    box('mmShoeL', baseX + 12, baseY + 375, 72, 32, '3E1F0D')
    box('mmShoeR', baseX + 104, baseY + 375, 72, 32, '3E1F0D')

    -- Torso, Red Shirt & Overalls with Gold Buttons
    box('mmShirt', baseX + 8, baseY + 150, 174, 140, '9E0B1C')
    box('mmOveralls', baseX + 26, baseY + 185, 138, 115, '102038')
    box('mmStrapL', baseX + 34, baseY + 145, 28, 55, '102038')
    box('mmStrapR', baseX + 128, baseY + 145, 28, 55, '102038')
    box('mmBtnL', baseX + 38, baseY + 192, 20, 20, 'FFD740')
    box('mmBtnR', baseX + 132, baseY + 192, 20, 20, 'FFD740')

    -- Arms, White Gloves, Microphone & Horror Mario Butcher Knife
    box('mmArmL', baseX - 38, baseY + 160, 50, 95, '9E0B1C')
    box('mmGloveL', baseX - 46, baseY + 240, 56, 48, 'EAEAEA')
    box('mmKnife', baseX - 62, baseY + 145, 22, 105, 'CFD8DC')
    box('mmKnifeBlood', baseX - 62, baseY + 145, 10, 75, 'FF002B')

    box('mmArmR', baseX + 178, baseY + 160, 50, 95, '9E0B1C')
    box('mmGloveR', baseX + 182, baseY + 235, 56, 48, 'EAEAEA')
    box('mmMic', baseX + 215, baseY + 205, 28, 52, 'FF183A')

    -- Head, Shadowed Eye Sockets, Glowing Red Pupils, Mustache & Singing Jaw
    box('mmHead', baseX + 24, baseY + 28, 142, 122, 'D7B196')
    box('mmFaceShadow', baseX + 24, baseY + 34, 142, 54, '120205')
    box('mmPupilL', baseX + 52, baseY + 52, 22, 22, 'FF002B')
    box('mmPupilR', baseX + 118, baseY + 52, 22, 22, 'FF002B')
    box('mmVisor', baseX + 20, baseY + 42, 152, 38, 'FF002B')
    setProperty('mmVisor.alpha', 0)

    box('mmMouth', baseX + 54, baseY + 105, 84, 28, '1A0004')
    box('mmTeeth', baseX + 60, baseY + 108, 72, 10, 'FFFFFF')
    box('mmMustache', baseX + 34, baseY + 88, 124, 24, '160C08')
    box('mmTongue', baseX + 90, baseY + 112, 320, 18, 'FF183A')
    setProperty('mmTongue.alpha', 0)

    -- Iconic Red Mario Cap & M Emblem
    box('mmCapTop', baseX + 14, baseY - 18, 162, 48, 'D5001F')
    box('mmCapBrim', baseX + 6, baseY + 18, 188, 20, '9E0016')
    box('mmCapBadge', baseX + 76, baseY - 10, 40, 28, 'FFFFFF')
    makeLuaText('mmCapM', 'M', 40, baseX + 76, baseY - 10)
    setTextSize('mmCapM', 24)
    setTextColor('mmCapM', 'D5001F')
    setTextAlignment('mmCapM', 'center')
    setObjectCamera('mmCapM', 'game')
    addLuaText('mmCapM')

    -- Custom Ultra M Health Bar Icon on camHUD
    box('mmHudIconBg', 0, 0, 62, 62, '1A0006', 'hud')
    box('mmHudIconCap', 0, 0, 62, 24, 'FF183A', 'hud')
    box('mmHudIconEyeL', 0, 0, 14, 14, 'FF002B', 'hud')
    box('mmHudIconEyeR', 0, 0, 14, 14, 'FF002B', 'hud')
    box('mmHudIconStache', 0, 0, 46, 10, '000000', 'hud')
end

function onUpdatePost(elapsed)
    -- Lock custom Horror Mario Health Icon to iconP2 position on Health Bar
    local ix = getProperty('iconP2.x') + 18
    local iy = getProperty('iconP2.y') + 14
    setProperty('mmHudIconBg.x', ix)
    setProperty('mmHudIconBg.y', iy)
    setProperty('mmHudIconCap.x', ix)
    setProperty('mmHudIconCap.y', iy)
    setProperty('mmHudIconEyeL.x', ix + 10)
    setProperty('mmHudIconEyeL.y', iy + 28)
    setProperty('mmHudIconEyeR.x', ix + 38)
    setProperty('mmHudIconEyeR.y', iy + 28)
    setProperty('mmHudIconStache.x', ix + 8)
    setProperty('mmHudIconStache.y', iy + 46)
end

function opponentNoteHit(id, direction, noteType, isSustainNote)
    -- Animate Ultra M head, pupils, singing mouth & knife slash by note direction!
    local dx = (direction == 0 and -22) or (direction == 3 and 22) or 0
    local dy = (direction == 1 and 18) or (direction == 2 and -22) or 0

    setProperty('mmHead.x', baseX + 24 + dx * 0.5)
    setProperty('mmHead.y', baseY + 28 + dy * 0.5)
    setProperty('mmPupilL.x', baseX + 52 + dx)
    setProperty('mmPupilL.y', baseY + 52 + dy)
    setProperty('mmPupilR.x', baseX + 118 + dx)
    setProperty('mmPupilR.y', baseY + 52 + dy)
    setProperty('mmMouth.scale.y', 1.75)
    setProperty('mmKnife.y', baseY + 115 + dy)

    doTweenX('mmHeadX', 'mmHead', baseX + 24, 0.18, 'quadOut')
    doTweenY('mmHeadY', 'mmHead', baseY + 28, 0.18, 'quadOut')
    doTweenY('mmKnifeY', 'mmKnife', baseY + 145, 0.20, 'quadOut')
    doTweenY('mmMouthClose', 'mmMouth.scale', 1.0, 0.16, 'quadOut')
end

function onEvent(name, value1, value2)
    if name == 'SecretExitAct' then
        curForm = tonumber(value1) or 1
        if curForm == 2 then
            -- Act II: Mr. Virtual (Crimson Virtual Boy Form)
            setProperty('mmVisor.alpha', 0.95)
            doTweenColor('cCap2', 'mmCapTop', 'FF0040', 0.3, 'linear')
            doTweenColor('cShirt2', 'mmShirt', '3A0010', 0.3, 'linear')
        elseif curForm == 3 then
            -- Act III: Turmoil (Forest Yoshi-Mario Form)
            setProperty('mmVisor.alpha', 0)
            doTweenColor('cCap3', 'mmCapTop', 'FF6D00', 0.3, 'linear')
        elseif curForm == 4 then
            -- Act IV: Starman Luigi Assist Appears!
            setProperty('luigiBody.alpha', 1)
            setProperty('luigiShirt.alpha', 1)
            setProperty('luigiHead.alpha', 1)
            setProperty('luigiCap.alpha', 1)
        elseif curForm == 5 then
            -- Act V: Giant Omega Ultra M Awakens in Background!
            setProperty('omegaAura.alpha', 0.85)
            setProperty('omegaCap.alpha', 0.95)
            setProperty('omegaEyeL.alpha', 1)
            setProperty('omegaEyeR.alpha', 1)
            setProperty('omegaEyeCenter.alpha', 1)
        end
    elseif name == 'DodgeEvent' then
        -- Lash Turmoil's Tongue across the stage!
        setProperty('mmTongue.alpha', 1)
        runTimer('hideTongueTimer', 0.65)
    end
end

function onTimerCompleted(tag)
    if tag == 'hideTongueTimer' then
        setProperty('mmTongue.alpha', 0)
    end
end
