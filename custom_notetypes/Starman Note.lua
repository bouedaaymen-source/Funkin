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
