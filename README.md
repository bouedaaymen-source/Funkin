# 🍄 Mario's Madness: Secret Exit Reimagined (Psych Engine 0.7.3 Mod)

**Standalone 5-Act True Ending Mod for the actual Friday Night Funkin': Psych Engine 0.7.3 game (PC & Android)**

---

## ⬇️ HOW TO DOWNLOAD FROM GITHUB (NO ACTIONS / NO BROKEN LINKS!)

1. At the top of this GitHub repository page, click the green **`<> Code`** button (or the **`⋯`** menu on mobile) and click **`Download ZIP`**.
2. Extract the downloaded `.zip` folder directly into your **Psych Engine 0.7.3** `mods/` folder:
   - **PC (Windows / Mac / Linux)**: `PsychEngine-0.7.3/mods/secret-exit-reimagined/`
   - **Android (Psych Engine 0.7.3 Mobile APK)**: `/storage/emulated/0/.PsychEngine/mods/secret-exit-reimagined/`
   *(Note: Both the repository root AND the `/secret-exit-reimagined/` subfolder contain the full `pack.json`, `weeks/`, `data/`, `stages/`, `scripts/`, `custom_notetypes/`, and `custom_events/` structure, so whichever folder you extract into `mods/` will be detected immediately by Psych Engine 0.7.3!)*
3. Launch **Psych Engine 0.7.3**, go to **Mods** -> make sure **Mario's Madness: Secret Exit Reimagined** is toggled **ON**, and play in **Story Mode** or **Freeplay** (`Easy`, `Normal`, `Hard`)!

---

## 🛠️ What Was Fixed

1. **Fixed GitHub Push & Download Blockers**:
   - Removed the `.github/workflows/` folder (which caused GitHub OAuth `workflow` scope push rejections from AI Studio) and the 33 MB `apk_chunks/` directory that blocked GitHub syncing.
   - Placed the complete Psych Engine 0.7.3 mod directly at the **repository root** (`pack.json`, `weeks/`, `data/`, `stages/`, `scripts/`, `custom_notetypes/`, `custom_events/`) as well as inside `/secret-exit-reimagined/` so GitHub's native **`Code -> Download ZIP`** works immediately with zero 404 errors.
2. **Fixed Missing `Inst.ogg` Crash in Psych Engine 0.7.3**:
   - Configured the 3 week tracks (*Secret Exit Reimagined*, *Starman Redemption*, and *Unbeatable Overdrive*) to automatically use Psych Engine 0.7.3's built-in audio streams (`stress`, `guns`, `thorns`) out of the box, while `scripts/secret_exit_5act_director.lua` automatically switches to `songs/secret-exit-reimagined/Inst.ogg` if you drop custom `.ogg` files in.
3. **Fixed Psych Engine 0.7.3 Lua Compatibility**:
   - Removed deprecated 0.6.x `colorSwap` property access in `custom_notetypes/Hurt Note.lua` (which caused Lua errors in 0.7.3) and replaced `characterPlayAnim` with native 0.7.3 `playAnim`.
