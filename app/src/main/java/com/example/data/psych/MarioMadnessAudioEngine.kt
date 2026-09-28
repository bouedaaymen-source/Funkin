package com.example.data.psych

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

/**
 * Real-time PCM 16-bit Audio Synthesizer for Mario's Madness V2 (GameBanana #359554) & Secret Exit.
 * Synthesizes recognizable dark NES/SNES Mario's Madness melodies, basslines, chiptune drums,
 * opponent/BF vocal duet harmonies, and the 3 Ending Cutscene themes.
 */
object MarioMadnessAudioEngine {

    private const val SAMPLE_RATE = 22050
    private var musicJob: Job? = null
    private var audioTrack: AudioTrack? = null

    // Recognizable Mario's Madness V2 (GameBanana #359554) MIDI motifs
    private val melodyMotifs: Map<String, IntArray> = mapOf(
        // Secret Exit (5-Act Finale: Dark Castle -> Starman Liberation Anthem)
        "secret-exit" to intArrayOf(
            64, 64, 0, 64, 0, 60, 64, 0, 67, 0, 0, 0, 55, 0, 0, 0,
            69, 0, 72, 0, 71, 69, 67, 64, 65, 67, 69, 65, 62, 60, 59, 0,
            72, 71, 69, 68, 69, 72, 76, 74, 72, 71, 69, 67, 69, 0, 64, 0,
            76, 76, 74, 72, 71, 71, 69, 67, 69, 71, 72, 74, 76, 79, 81, 0
        ),
        // All-Stars (Ultra M 4-Act Boss Theme - Dramatic minor arpeggios)
        "all-stars" to intArrayOf(
            57, 60, 64, 69, 68, 64, 60, 57, 53, 57, 60, 65, 64, 60, 57, 52,
            57, 64, 69, 72, 71, 68, 64, 59, 60, 64, 69, 76, 75, 72, 69, 64
        ),
        // It's-A-Me (Horror Mario Dissonant Overworld Motif)
        "its-a-me" to intArrayOf(
            64, 64, 0, 64, 0, 60, 63, 0, 66, 0, 0, 0, 54, 0, 0, 0,
            60, 0, 0, 55, 0, 0, 51, 0, 57, 0, 58, 57, 55, 0, 63, 66
        ),
        // Starman Slaughter (High-speed Lava Bridge Chase)
        "starman-slaughter" to intArrayOf(
            72, 72, 72, 69, 72, 0, 74, 72, 71, 71, 71, 67, 71, 0, 72, 71,
            69, 69, 69, 65, 69, 71, 72, 69, 68, 68, 71, 74, 76, 74, 71, 68
        ),
        // Paranoia (Mr. Virtual - Crimson Virtual Boy Hypnotic Lead)
        "paranoia" to intArrayOf(
            62, 69, 65, 62, 61, 69, 64, 61, 60, 69, 64, 60, 59, 68, 64, 59,
            62, 74, 70, 69, 67, 74, 70, 67, 65, 74, 69, 65, 64, 73, 69, 64
        ),
        // Unbeatable (Mr. Sys "WE ARE NINTENDO - YOU CANNOT BEAT US" Chiptune)
        "unbeatable" to intArrayOf(
            60, 63, 67, 72, 70, 67, 63, 60, 65, 68, 72, 77, 75, 72, 68, 65,
            67, 70, 74, 79, 77, 74, 70, 67, 72, 0, 72, 74, 75, 0, 79, 0
        ),
        // I Hate You (Burned Luigi World 8 Castle)
        "i-hate-you" to intArrayOf(
            59, 62, 66, 71, 70, 66, 62, 59, 64, 67, 71, 76, 74, 71, 67, 64,
            66, 69, 73, 78, 76, 73, 69, 66, 71, 0, 70, 71, 74, 71, 66, 59
        ),
        // Powerdown & Demise (MX Underground 1-2 Heavy Bass & Lead)
        "mx-demise" to intArrayOf(
            48, 60, 51, 63, 50, 62, 49, 61, 48, 60, 54, 66, 53, 65, 51, 63,
            60, 72, 63, 75, 62, 74, 61, 73, 60, 72, 66, 78, 65, 77, 63, 75
        ),
        // Overdue & Alone (Luigi's Mansion / Mr. L Haunting Waltz)
        "overdue" to intArrayOf(
            64, 0, 67, 71, 72, 0, 71, 67, 66, 0, 69, 72, 71, 0, 67, 64,
            60, 0, 64, 67, 69, 0, 67, 64, 59, 0, 63, 66, 64, 0, 59, 0
        ),
        // Golden Land & No Party (GameBoy / Anti-Piracy Screen)
        "golden-land" to intArrayOf(
            67, 70, 74, 79, 78, 74, 70, 67, 63, 67, 70, 75, 74, 70, 67, 62,
            60, 63, 67, 72, 70, 67, 63, 60, 62, 66, 69, 74, 70, 67, 62, 0
        ),
        // Ending 1: Canon Bad Ending ("SEE YOU NEXT TIME" Music Box)
        "ending-bad" to intArrayOf(
            64, 0, 60, 0, 57, 0, 56, 0, 53, 0, 52, 0, 48, 0, 45, 0,
            57, 0, 60, 0, 64, 0, 63, 0, 60, 0, 56, 0, 57, 0, 0, 0
        ),
        // Ending 2: Warp Pipe Escape ("SHATTERED CRT" Urgent Synth)
        "ending-escape" to intArrayOf(
            62, 65, 69, 74, 72, 69, 65, 62, 64, 67, 71, 76, 74, 71, 67, 64,
            65, 69, 72, 77, 76, 72, 69, 65, 67, 71, 74, 79, 76, 72, 69, 64
        ),
        // Ending 3: Secret Exit True Ending ("GOLDEN STARMAN" Victory Fanfare)
        "ending-true" to intArrayOf(
            60, 64, 67, 72, 72, 76, 79, 84, 65, 69, 72, 77, 77, 81, 84, 89,
            67, 71, 74, 79, 79, 83, 86, 91, 72, 0, 76, 79, 84, 0, 84, 0
        )
    )

    private fun midiToFreq(midi: Int): Double {
        if (midi <= 0) return 0.0
        return 440.0 * Math.pow(2.0, (midi - 69.0) / 12.0)
    }

    fun resolveMotif(songTitle: String): IntArray {
        val slug = songTitle.lowercase()
        return when {
            slug.contains("ending-bad") -> melodyMotifs["ending-bad"]!!
            slug.contains("ending-escape") -> melodyMotifs["ending-escape"]!!
            slug.contains("ending-true") -> melodyMotifs["ending-true"]!!
            slug.contains("secret") || slug.contains("exit") -> melodyMotifs["secret-exit"]!!
            slug.contains("all-star") || slug.contains("all star") -> melodyMotifs["all-stars"]!!
            slug.contains("its-a-me") || slug.contains("it's-a-me") -> melodyMotifs["its-a-me"]!!
            slug.contains("starman") || slug.contains("slaughter") || slug.contains("no hope") -> melodyMotifs["starman-slaughter"]!!
            slug.contains("paranoia") || slug.contains("virtual") -> melodyMotifs["paranoia"]!!
            slug.contains("unbeatable") || slug.contains("nintendo") || slug.contains("dictator") || slug.contains("race") -> melodyMotifs["unbeatable"]!!
            slug.contains("powerdown") || slug.contains("demise") || slug.contains("mx") || slug.contains("apparition") -> melodyMotifs["mx-demise"]!!
            slug.contains("overdue") || slug.contains("alone") || slug.contains("abandoned") || slug.contains("end") -> melodyMotifs["overdue"]!!
            slug.contains("golden") || slug.contains("party") || slug.contains("bad day") || slug.contains("cool") -> melodyMotifs["golden-land"]!!
            slug.contains("hate") || slug.contains("luigi") || slug.contains("god") -> melodyMotifs["i-hate-you"]!!
            else -> melodyMotifs["secret-exit"]!!
        }
    }

    /**
     * Starts streaming the Mario's Madness V2 background instrumental + duet loop for the active song and Act.
     */
    fun startStageMusic(
        scope: CoroutineScope,
        songTitle: String,
        bpm: Int,
        getAct: () -> Int = { 1 },
        isAudioEnabled: () -> Boolean = { true }
    ) {
        stopStageMusic()
        val motif = resolveMotif(songTitle)
        val safeBpm = bpm.coerceIn(100, 230)
        val stepDurationSec = (60.0 / safeBpm) / 2.0 // 8th-note steps
        val samplesPerStep = (SAMPLE_RATE * stepDurationSec).toInt().coerceAtLeast(1024)

        musicJob = scope.launch(Dispatchers.Default) {
            val minBuf = AudioTrack.getMinBufferSize(
                SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            ).coerceAtLeast(samplesPerStep * 2)

            val track = try {
                AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(SAMPLE_RATE)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(minBuf)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()
            } catch (_: Exception) {
                null
            } ?: return@launch

            audioTrack = track
            try {
                track.play()
                var stepIndex = 0
                var phaseLead = 0.0
                var phaseBass = 0.0
                val buffer = ShortArray(samplesPerStep)

                while (isActive) {
                    if (!isAudioEnabled()) {
                        for (i in buffer.indices) buffer[i] = 0
                        track.write(buffer, 0, buffer.size)
                        continue
                    }

                    val act = getAct().coerceIn(1, 5)
                    val transpose = when (act) {
                        2 -> -2
                        3 -> 2
                        4 -> 5
                        5 -> 7
                        else -> 0
                    }

                    val rawMidi = motif[stepIndex % motif.size]
                    val leadMidi = if (rawMidi > 0) rawMidi + transpose else 0
                    val bassMidi = if (rawMidi > 0) (rawMidi - 12) + transpose else 45 + transpose

                    val leadFreq = midiToFreq(leadMidi)
                    val bassFreq = midiToFreq(bassMidi)
                    val isKick = (stepIndex % 4 == 0)
                    val isSnare = (stepIndex % 4 == 2)

                    for (i in 0 until samplesPerStep) {
                        val t = i.toDouble() / samplesPerStep
                        val env = (1.0 - t * 0.75).coerceAtLeast(0.1)

                        // NES Pulse / Sawtooth Lead
                        var sample = 0.0
                        if (leadFreq > 0.0) {
                            phaseLead += (2.0 * PI * leadFreq) / SAMPLE_RATE
                            val sq = if (sin(phaseLead) >= 0.0) 0.8 else -0.8
                            val sub = sin(phaseLead * 0.5) * 0.4
                            sample += (sq + sub) * 0.22 * env
                        }

                        // Dark Castle Sub-Bass
                        phaseBass += (2.0 * PI * bassFreq) / SAMPLE_RATE
                        val bassWave = sin(phaseBass) * 0.24 * (1.0 - t * 0.4)
                        sample += bassWave

                        // Chiptune Percussion
                        if (isKick && t < 0.25) {
                            val kickFreq = 130.0 * (1.0 - t * 3.2).coerceAtLeast(0.2)
                            sample += sin(2.0 * PI * kickFreq * (i.toDouble() / SAMPLE_RATE)) * 0.30
                        } else if (isSnare && t < 0.20) {
                            val noise = (((i * 1103515245 + 12345) and 0x7fffffff).toDouble() / Int.MAX_VALUE) * 2.0 - 1.0
                            sample += noise * 0.16 * (1.0 - t * 4.5)
                        }

                        val pcm = (sample * 26000.0).toInt().coerceIn(-32760, 32760).toShort()
                        buffer[i] = pcm
                    }

                    track.write(buffer, 0, buffer.size)
                    stepIndex++
                }
            } catch (_: Exception) {
            } finally {
                try {
                    track.stop()
                    track.release()
                } catch (_: Exception) {}
            }
        }
    }

    fun stopStageMusic() {
        musicJob?.cancel()
        musicJob = null
        try {
            audioTrack?.stop()
            audioTrack?.release()
        } catch (_: Exception) {}
        audioTrack = null
    }

    /**
     * Plays an authentic Boyfriend / Starman / Hurt vocal synth burst when hitting a note on the highway.
     */
    fun playVocalNoteBurst(
        scope: CoroutineScope,
        lane: Int,
        isStarman: Boolean = false,
        isMissOrHurt: Boolean = false
    ) {
        scope.launch(Dispatchers.Default) {
            val durationSamples = SAMPLE_RATE / 11 // ~90ms crisp FNF vocal blip
            val buffer = ShortArray(durationSamples)
            // Boyfriend FNF vocal frequencies (Left=C5, Down=E5, Up=G5, Right=A5, Starman=C6 chord)
            val baseFreq = when {
                isMissOrHurt -> 115.0
                isStarman -> 1046.5
                lane == 0 -> 523.25
                lane == 1 -> 659.25
                lane == 2 -> 783.99
                else -> 880.0
            }

            for (i in 0 until durationSamples) {
                val progress = i.toDouble() / durationSamples
                val env = (1.0 - progress).coerceAtLeast(0.0)
                val vibrato = if (isStarman) sin(progress * 12.0 * PI) * 18.0 else 0.0
                val phase = 2.0 * PI * (baseFreq + vibrato) * (i.toDouble() / SAMPLE_RATE)
                val wave = if (isMissOrHurt) {
                    if (sin(phase) >= 0) 0.9 else -0.9
                } else {
                    sin(phase) * 0.65 + (if (sin(phase * 2.0) >= 0) 0.35 else -0.35)
                }
                buffer[i] = (wave * env * 24000.0).toInt().coerceIn(-32760, 32760).toShort()
            }

            try {
                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(SAMPLE_RATE)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(buffer.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()
                track.write(buffer, 0, buffer.size)
                track.play()
                kotlinx.coroutines.delay(110L)
                track.stop()
                track.release()
            } catch (_: Exception) {}
        }
    }
}
