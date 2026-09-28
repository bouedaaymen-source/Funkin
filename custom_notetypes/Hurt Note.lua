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
