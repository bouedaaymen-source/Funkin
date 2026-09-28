-- ============================================================================
-- Psych Engine 0.7.3 Custom Event: DodgeEvent (Ultra M Spike & Pipe Ambush)
-- Works on PC (SPACEBAR) and Android Psych 0.7.3 (Screen Tap / Virtual Pad)
-- ============================================================================
local canDodge = false
local dodged = false

function onCreatePost()
    makeLuaText('dodgePromptText', '⚠️ [ PRESS SPACE OR TAP SCREEN TO DODGE ULTRA M! ] ⚠️', 1280, 0, 220)
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
        local windowSec = tonumber(value1) or 0.80
        runTimer('dodgeResolveTimer', windowSec)
    end
end

function onUpdatePost(elapsed)
    if canDodge and (keyJustPressed('space') or mouseClicked('left')) then
        dodged = true
        canDodge = false
        setProperty('dodgePromptText.visible', false)
        characterPlayAnim('boyfriend', 'hey', true)
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
            characterPlayAnim('boyfriend', 'hurt', true)
            setProperty('boyfriend.specialAnim', true)
        end
    end
end
