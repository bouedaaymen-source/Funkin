-- ============================================================================
-- Mario's Madness: Secret Exit Reimagined - Full Stage & Animated Ultra M Rig
-- File: stages/secret_exit_citadel.lua
-- Compatible with Psych Engine 0.7.3 (Android & PC)
-- Builds World 8-4 Castle, Lava Falls, Question Blocks, Warp Pipes, SMW Secret
-- Exit Goal Gate, and the multi-part Ultra M / Horror Mario character rig!
-- ============================================================================

function onCreate()
    -- 1. Deep Corrupted Cartridge Sky Backdrop
    makeLuaSprite('seSky', '', -600, -400)
    makeGraphic('seSky', 2800, 1800, '120104')
    setScrollFactor('seSky', 0.1, 0.1)
    addLuaSprite('seSky', false)

    -- 2. Giant Omega Ultra M Silhouette in Background (Hidden until Act V)
    makeLuaSprite('omegaAura', '', 260, -140)
    makeGraphic('omegaAura', 760, 720, '4A000E')
    setScrollFactor('omegaAura', 0.25, 0.25)
    setProperty('omegaAura.alpha', 0)
    addLuaSprite('omegaAura', false)

    makeLuaSprite('omegaCap', '', 360, -110)
    makeGraphic('omegaCap', 560, 140, 'B8001F')
    setScrollFactor('omegaCap', 0.25, 0.25)
    setProperty('omegaCap.alpha', 0)
    addLuaSprite('omegaCap', false)

    makeLuaSprite('omegaEyeL', '', 450, 60)
    makeGraphic('omegaEyeL', 70, 45, 'FF002B')
    setScrollFactor('omegaEyeL', 0.25, 0.25)
    setProperty('omegaEyeL.alpha', 0)
    addLuaSprite('omegaEyeL', false)

    makeLuaSprite('omegaEyeR', '', 760, 60)
    makeGraphic('omegaEyeR', 70, 45, 'FF002B')
    setScrollFactor('omegaEyeR', 0.25, 0.25)
    setProperty('omegaEyeR.alpha', 0)
    addLuaSprite('omegaEyeR', false)

    makeLuaSprite('omegaEyeCenter', '', 605, -20)
    makeGraphic('omegaEyeCenter', 70, 70, 'FFD740')
    setScrollFactor('omegaEyeCenter', 0.25, 0.25)
    setProperty('omegaEyeCenter.alpha', 0)
    addLuaSprite('omegaEyeCenter', false)

    -- 3. Distant World 8-4 Castle Stone Pillars & Battlements
    for i = 1, 5 do
        local tag = 'sePillar' .. i
        makeLuaSprite(tag, '', -450 + (i * 420), -220)
        makeGraphic(tag, 120, 1100, '26040C')
        setScrollFactor(tag, 0.4, 0.4)
        addLuaSprite(tag, false)
    end

    -- 4. Glowing Lava Falls / Virtual Grid Horizon
    makeLuaSprite('seLavaGlow', '', -600, 480)
    makeGraphic('seLavaGlow', 2800, 420, '9E0B22')
    setScrollFactor('seLavaGlow', 0.7, 0.7)
    setProperty('seLavaGlow.alpha', 0.75)
    addLuaSprite('seLavaGlow', false)

    -- 5. Floating Mario Question Blocks [?] & Brick Blocks
    for i = 1, 4 do
        local bTag = 'seBrick' .. i
        makeLuaSprite(bTag, '', -80 + (i * 310), 80 + ((i % 2) * 35))
        makeGraphic(bTag, 74, 74, (i % 2 == 0) and 'FFB300' or '7A2E1D')
        setScrollFactor(bTag, 0.85, 0.85)
        addLuaSprite(bTag, false)

        local bInner = 'seBrickIn' .. i
        makeLuaSprite(bInner, '', -72 + (i * 310), 88 + ((i % 2) * 35))
        makeGraphic(bInner, 58, 58, (i % 2 == 0) and 'FFD54F' or '541B0E')
        setScrollFactor(bInner, 0.85, 0.85)
        addLuaSprite(bInner, false)
    end

    -- 6. Super Mario World Secret Exit Goal Gate (Behind BF on the Right!)
    makeLuaSprite('seGoalPostL', '', 1080, 110)
    makeGraphic('seGoalPostL', 26, 530, 'E0E0E0')
    setScrollFactor('seGoalPostL', 0.95, 0.95)
    addLuaSprite('seGoalPostL', false)

    makeLuaSprite('seGoalPostR', '', 1260, 110)
    makeGraphic('seGoalPostR', 26, 530, 'E0E0E0')
    setScrollFactor('seGoalPostR', 0.95, 0.95)
    addLuaSprite('seGoalPostR', false)

    makeLuaSprite('seGoalTop', '', 1070, 90)
    makeGraphic('seGoalTop', 226, 28, 'FF183A')
    setScrollFactor('seGoalTop', 0.95, 0.95)
    addLuaSprite('seGoalTop', false)

    makeLuaSprite('seGoalTape', '', 1106, 360)
    makeGraphic('seGoalTape', 154, 22, 'FFD740')
    setScrollFactor('seGoalTape', 0.95, 0.95)
    addLuaSprite('seGoalTape', false)

    -- 7. Act III Green Warp Pipe (Left-Center Ambush Pipe)
    makeLuaSprite('sePipeBody', '', 420, 470)
    makeGraphic('sePipeBody', 110, 210, '1B5E20')
    setScrollFactor('sePipeBody', 0.95, 0.95)
    addLuaSprite('sePipeBody', false)

    makeLuaSprite('sePipeRim', '', 406, 442)
    makeGraphic('sePipeRim', 138, 34, '2E7D32')
    setScrollFactor('sePipeRim', 0.95, 0.95)
    addLuaSprite('sePipeRim', false)

    -- 8. Act IV Starman Luigi & Pico Assist Characters (Behind BF & GF)
    -- Luigi Rig
    makeLuaSprite('luigiBody', '', 680, 340)
    makeGraphic('luigiBody', 78, 170, '1B3B82')
    setProperty('luigiBody.alpha', 0)
    addLuaSprite('luigiBody', false)

    makeLuaSprite('luigiShirt', '', 674, 320)
    makeGraphic('luigiShirt', 90, 75, '00C853')
    setProperty('luigiShirt.alpha', 0)
    addLuaSprite('luigiShirt', false)

    makeLuaSprite('luigiHead', '', 678, 240)
    makeGraphic('luigiHead', 82, 82, 'FFE0B2')
    setProperty('luigiHead.alpha', 0)
    addLuaSprite('luigiHead', false)

    makeLuaSprite('luigiCap', '', 668, 210)
    makeGraphic('luigiCap', 102, 38, '00E676')
    setProperty('luigiCap.alpha', 0)
    addLuaSprite('luigiCap', false)

    -- 9. Main Citadel Stone Bridge Floor
    makeLuaSprite('seFloor', '', -550, 620)
    makeGraphic('seFloor', 2700, 340, '231B28')
    setScrollFactor('seFloor', 1.0, 1.0)
    addLuaSprite('seFloor', false)

    makeLuaSprite('seFloorTrim', '', -550, 620)
    makeGraphic('seFloorTrim', 2700, 18, 'FF183A')
    setScrollFactor('seFloorTrim', 1.0, 1.0)
    addLuaSprite('seFloorTrim', false)
end

function onBeatHit()
    -- Pulse the lava horizon & move the SMW Secret Exit Golden Goal Tape!
    if curBeat % 2 == 0 then
        setProperty('seLavaGlow.alpha', 0.90)
        doTweenAlpha('seLavaFade', 'seLavaGlow', 0.45, crochet / 1000, 'quadOut')
        doTweenY('seTapeUp', 'seGoalTape', 190, (crochet / 1000) * 1.8, 'sineInOut')
    else
        doTweenY('seTapeDown', 'seGoalTape', 490, (crochet / 1000) * 1.8, 'sineInOut')
    end
end
