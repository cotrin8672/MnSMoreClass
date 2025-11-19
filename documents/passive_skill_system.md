# Passive Skill System Guide

## Overview
The passive skill system extends the Mine and Slash class progression with small, repeatable stat bonuses that complement the spell tree. Each passive point is spent inside a spell school grid alongside spells, reinforcing the identity of that school while allowing granular build tuning. Passives are represented by `Perk` entries of type `STAT` with `max_lvls > 1`, and they typically grant a single stat that scales linearly with level.

## Key Components
| Layer | Location | Responsibility |
| --- | --- | --- |
| Data Model | `database/data/perks/Perk.java` | Defines the `Perk` record, determines whether an entry is a passive (`!isSpell() && max_lvls > 1`), resolves tooltips, icons, and stat scaling. |
| Registration | `aoe_data/database/perks/PerkBuilder.java` | Provides helpers such as `passive(id, maxLvl, stats...)` to create passives with icon defaults and register them for data generation. |
| Passive Catalogues | `aoe_data/database/perks/SpellPassives.java`, `DruidPassives.java` | Enumerate concrete passive IDs and attach their stat payloads. |
| Placement | `aoe_data/database/spell_schools/SpellSchoolsAdder.java` | Inserts passives into class-specific grids via `SchoolBuilder.add(id, PointData)`. |
| Player State | `saveclasses/spells/SpellSchoolsData.java` | Stores learned levels per perk, performs validation, computes resulting `ExactStatData`, and handles resets. |
| Balance Config | `database/data/game_balance_config/PlayerPointsType.java` | Supplies point budgets, material costs, and reset logic for `PASSIVES` points. |

## Data Flow
1. **Definition** – Passive IDs and stat packages are declared through `PerkBuilder.passive`. The helper sets the type to `STAT`, stores `max_lvls`, and assigns an icon under `textures/gui/spells/passives/<id>.png`.
2. **Generation** – Each passive is added to the data pack output during data generation because `Perk.addToSerializables` is called inside the builder.
3. **Placement** – `SpellSchoolsAdder` consumes the passive IDs when constructing each spell school. `PointData(x, y)` positions determine the grid column (`x`) and row (`y`), tying the passive to the level gate configured in `SpellSchool.lvl_reqs`.
4. **Unlocking** – When a player spends a passive point, `SpellSchoolsData.canLearn` verifies resource availability, character level, school affinity limits (max two schools), and the passive’s current rank versus `max_lvls`.
5. **Persistence & Stats** – Successful purchases increment `allocated_lvls[id]`. During stat calculation `getStatAndContext` converts each `OptScaleExactStat` into `ExactStatData`, scaling the base value by `(level - 1) * 100%` per additional rank.
6. **Resetting** – Refunds require reset points from `PlayerPointsType.PASSIVES`. The system checks availability (`hasResetPoints`) and consumes them upon unlearning.

## Progression Rules
- **Point Source** – Passive points come from the same pool as spell points but tracked separately via `PlayerPointsType.PASSIVES`. Config values (base, per-level gain, caps) are defined in `GameBalanceConfig`.
- **Level Gates** – The row (`PointData.y`) corresponds to entries in `SpellSchool.lvl_reqs` (default: `[1, 5, 10, 15, 20, 25, 30]`). A player must meet the required character level to unlock or rank up the passive occupying that row.
- **Maximum Rank** – Standard passives set `max_lvls = 8`, yielding eight total ranks. Each rank applies the same base stat again; the perk tooltip displays the stat scaled to the player’s current rank.
- **School Limit** – The system tracks the set of spell schools from which any perks are learned. Players may invest in at most two schools simultaneously; attempting to learn in a third school fails validation.

## Stat Scaling Behaviour
For each passive rank:
- The `OptScaleExactStat` is converted with `ExactStatData.levelScaled` (level 1 unless `scale_to_lvl = true`).
- Tooltip and runtime stats receive a percentage multiplier of `(rank - 1) * 100`, effectively stacking identical flat bonuses per rank.
- Multiple stats listed under one passive (rare) are all processed the same way.

## Icon & Localization
- Icons default to `mmorpg:textures/gui/spells/passives/<id>.png`. Missing textures fall back to `Stat.MISSING_ICON`.
- Display names are supplied through localization keys `mmorpg.talent.<id>` (via `Perk.locNameLangFileGUID`). Ensure translations exist in the relevant language JSON files.

### Localization specifics
- The game looks up passive names in the same namespace as talents. Any passive registered with ID `p_xxx` resolves to the translation key `mmorpg.talent.p_xxx` because `Perk.locNameLangFileGUID()` returns `SlashRef.MODID + ".talent." + id`. @Mine-And-Slash-Rework/src/main/java/com/robertx22/mine_and_slash/database/data/perks/Perk.java#176-184
- The default `en_us.json` does **not** ship with passive-specific entries (searching for `mmorpg.talent.p_*` yields no hits). Without a matching key, the UI falls back to displaying the raw ID. Add new lines such as:
  ```json
  "mmorpg.talent.p_burn_chance": "Burn Chance",
  "mmorpg.talent.p_spell_dmg": "Spell Damage"
  ```
  Place them near the existing talent block in `src/main/resources/assets/mmorpg/lang/en_us.json` and mirror them across other locales as needed.
- Data generators do not auto-create lang entries; maintaining localization is a manual step alongside adding icons.

## Adding a New Passive
1. **Stat Definition** – Confirm the target stat exists in `ExileDB.Stats`. Use `OptScaleExactStat(value, stat, modType)` to describe the bonus.
2. **Registration** – Add a `public static String` ID in the relevant catalogue (`SpellPassives`, `DruidPassives`, etc.) and call `PerkBuilder.passive(ID, 8, stat...)` inside `registerAll`.
3. **Placement** – Update the appropriate spell school in `SpellSchoolsAdder`, placing the passive in an empty cell that aligns with desired level gating.
4. **Assets** – Create an icon at `assets/mmorpg/textures/gui/spells/passives/<id>.png`.
5. **Localization** – Add translations to `assets/mmorpg/lang/<locale>.json`.
6. **Regenerate Data** – Run the data generator to produce the updated JSON under `ref/data/mmorpg/mmorpg_perk/`.

## Debugging Tips
- **Tooltip Checks** – Use the in-game class screen while holding `Alt` to verify stat tooltips. If the text shows `Press Alt for Stat Info`, the perk likely lacks a valid stat or localization.
- **Data Sync** – If a passive is missing in-game, confirm the data pack JSON was refreshed and the icon path resolves correctly.
- **Balance Tweaks** – Adjust `GameBalanceConfig.player_points.passive` to control how many ranks can be earned across levels.

## Reference Snippets
- Perk helper: `PerkBuilder.passive` @ `Mine-And-Slash-Rework/src/main/java/com/robertx22/mine_and_slash/aoe_data/database/perks/PerkBuilder.java`
- Passive definitions: `SpellPassives.registerAll` + `DruidPassives.registerAll`
- Spell school layout: `SpellSchoolsAdder`
- Player data handling: `SpellSchoolsData`
- Point economy: `PlayerPointsType.PASSIVES`
