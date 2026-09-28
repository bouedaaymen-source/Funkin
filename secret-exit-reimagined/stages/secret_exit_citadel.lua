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
