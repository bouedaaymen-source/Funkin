-- ============================================================================
-- Psych Engine 0.7.3 Custom NoteType: Fire Mario Note / Hurt Note
-- File: custom_notetypes/Hurt Note.lua
-- ============================================================================
function onCreate()
    for i = 0, getProperty('unspawnNotes.length') - 1 do
        local nt = getPropertyFromGroup('unspawnNotes', i, 'noteType')
        if nt == 'Hurt Note' or nt == 'Fire Mario Note' then
            setPropertyFromGroup('unspawnNotes', i, 'rgbShader.enabled', false)
            setPropertyFromGroup('unspawnNotes', i, 'colorSwap.hue', -0.15)
            setPropertyFromGroup('unspawnNotes', i, 'colorSwap.saturation', 0.6)
            setPropertyFromGroup('unspawnNotes', i, 'hitHealth', '-0.38')
            setPropertyFromGroup('unspawnNotes', i, 'missHealth', '0')
            setPropertyFromGroup('unspawnNotes', i, 'hitCausesMiss', true)
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
        characterPlayAnim('boyfriend', 'hurt', true)
        setProperty('boyfriend.specialAnim', true)
    end
end
