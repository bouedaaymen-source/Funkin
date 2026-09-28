# 🍄 Mario's Madness: Secret Exit Reimagined (Psych Engine 0.7.3 Mod)

**Standalone 5-Act True Ending Mod for the actual Friday Night Funkin': Psych Engine 0.7.3 game (PC & Android) + Companion Mod Hub APK**

---

## ⬇️ DOWNLOAD `Secret-Exit-Reimagined-Psych-0.7.3.zip` (FOR ACTUAL PSYCH ENGINE 0.7.3)

> **⚡ HOW IT WORKS AFTER YOU PUSH TO GITHUB:**
> As soon as you push/sync from AI Studio to GitHub, **GitHub Actions automatically packages `secret-exit-reimagined/` with synthesized 44.1kHz Ogg Vorbis audio (`Inst.ogg` & `Voices.ogg`) into `Secret-Exit-Reimagined-Psych-0.7.3.zip`** (takes ~30 seconds).

### 1️⃣ Direct Download Links (For Actual Psych Engine 0.7.3 Game):
- 👉 [**⬇️ DOWNLOAD `Secret-Exit-Reimagined-Psych-0.7.3.zip` (FROM RELEASES)**](../../releases/latest/download/Secret-Exit-Reimagined-Psych-0.7.3.zip)
- 👉 [**📁 DIRECT REPO FILE (`Secret-Exit-Reimagined-Psych-0.7.3.zip`)**](Secret-Exit-Reimagined-Psych-0.7.3.zip?raw=true)
- 👉 [**📂 BROWSE MOD FOLDER IN REPO (`/secret-exit-reimagined/`)**](secret-exit-reimagined)
- 👉 [**⏳ CHECK GITHUB ACTIONS STATUS**](../../actions)

---

## 🛠️ How to Install in Actual Psych Engine 0.7.3 (PC & Android)

1. Download **`Secret-Exit-Reimagined-Psych-0.7.3.zip`** using the link above.
2. Extract the **`secret-exit-reimagined`** folder into your **Psych Engine 0.7.3** `mods/` folder:
   - **PC (Windows / Mac / Linux)**: `PsychEngine-0.7.3/mods/secret-exit-reimagined/`
   - **Android (Psych Engine 0.7.3 Mobile APK)**: `/storage/emulated/0/.PsychEngine/mods/secret-exit-reimagined/`
3. Open **Psych Engine 0.7.3**, go to **Mods**, make sure **Mario's Madness: Secret Exit Reimagined** is toggled **ON**, and play in **Story Mode** or **Freeplay** (`Easy`, `Normal`, `Hard`)!

---

## 🔥 What's Inside `/secret-exit-reimagined/` (Psych Engine 0.7.3)
- **`pack.json`**: Psych Engine 0.7.3 mod manifest with Freeplay RGB `[255, 24, 58]` and Discord RPC.
- **`weeks/secret_exit_reimagined.json`**: Full week with 3 tracks:
  1. `secret-exit-reimagined` (200 BPM — 5-Act True Ending Marathon vs Ultra M)
  2. `starman-redemption` (185 BPM — Starman Awakening with Pico & Luigi Spirit)
  3. `unbeatable-overdrive` (195 BPM — Final Cartridge Overdrive)
- **`stages/secret_exit_citadel.json` & `.lua`**: Standalone HaxeFlixel procedural stage (`makeLuaSprite` + `makeGraphic`) featuring Ultra M's crimson castle pillars, pulsing lava horizon, and cinematic letterbox bars—zero external PNG dependencies so it never crashes.
- **`scripts/secret_exit_5act_director.lua`**: 5-Act Lua 5.1 Director script controlling Act I–V HUD banners, camera beat zooms, Act II HUD tilt, Act I–III opponent health drain, and Act IV–V Starman health regeneration.
- **`custom_notetypes/Hurt Note.lua` & `Starman Note.lua`**: Hazard Fire Notes (`-38%` health + screen shake) and Starman Powerup Notes (`+35%` health + `+1000` score + golden flash).
- **`custom_events/DodgeEvent.lua`**: Spacebar (PC) and Screen Tap (Android) dodge prompts.

---

## 📱 Companion Android APK (`FunkinMods-MarioMadness-V2.apk`)
- 👉 [**⬇️ DIRECT DOWNLOAD COMPANION APK (`FunkinMods-MarioMadness-V2.apk`)**](../../releases/latest/download/FunkinMods-MarioMadness-V2.apk)
