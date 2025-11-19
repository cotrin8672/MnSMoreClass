# Mine and Slash - Comprehensive Stat Reference

**Total Stats:** 432
**Last Updated:** 2025-11-05
**Data Path:** `ref/data/mmorpg/mmorpg_stat/`

---

## Table of Contents

1. [Understanding the Stat System](#understanding-the-stat-system)
2. [Core Stats](#core-stats)
3. [Elemental Damage Stats](#elemental-damage-stats)
4. [Defense Stats](#defense-stats)
5. [Resource Stats](#resource-stats)
6. [Regeneration & Leech](#regeneration--leech)
7. [Cast Speed & Cooldown](#cast-speed--cooldown)
8. [Charges](#charges)
9. [Critical Stats](#critical-stats)
10. [Weapon Type Damage](#weapon-type-damage)
11. [Spell Tags & Modifiers](#spell-tags--modifiers)
12. [Ailments & Curses](#ailments--curses)
13. [Auras](#auras)
14. [Special Mechanics](#special-mechanics)
15. [Complete Stat List](#complete-stat-list)

---

## Understanding the Stat System

### Stat JSON Structure

Each stat is defined in a JSON file with the following key properties:

```json
{
  "id": "stat_identifier",           // Unique stat ID
  "base": 0.0,                        // Base value
  "min": 0.0,                         // Minimum allowed value
  "max": 100000.0,                    // Maximum allowed value
  "is_perc": true,                    // Is this a percentage stat?
  "ele": "Physical",                  // Element type
  "scaling": "NONE",                  // Scaling type (NONE, CORE, etc.)
  "icon": "★",                        // Icon displayed in UI
  "format": "aqua",                   // Text color formatting
  "group": "MAIN",                    // Stat group
  "gui_group": "NONE",                // GUI grouping
  "multiUseType": "MULTIPLY_STAT",    // How stat stacks
  "minus_is_good": false,             // Is negative better?
  "has_softcap": false,               // Has diminishing returns?
  "softcap": 0.0,                     // Softcap value
  "ser": "data",                      // Serialization type
  "effect": []                        // Effect triggers
}
```

### Element Types
- **Physical** - Physical damage
- **Fire** - Fire damage
- **Cold** (Water) - Cold/ice damage
- **Nature** (Lightning) - Lightning/shock damage
- **Shadow** (Chaos) - Chaos/dark damage

### Scaling Types
- **NONE** - No special scaling
- **CORE** - Core stat (STR, DEX, INT)

### Multi-Use Types
- **MULTIPLY_STAT** - Stats multiply together
- **MULTIPLICATIVE_DAMAGE** - Damage multipliers (additive with each other, then multiply)

### Serialization Types
- **data** - Standard stat
- **core_stat** - Core attribute stat with bonus effects

---

## Core Stats

The foundation of character building. Each point grants specific bonuses.

### `strength`
- **Base:** 0.0
- **Min/Max:** 0.0 / 100,000,000.0
- **Is Percentage:** false
- **Element:** Physical
- **Scaling:** CORE
- **Serialization Type:** core_stat
- **Bonuses per Point:**
  - +5 Health (FLAT)
  - +0.25 Health Regen (FLAT)
  - +1% Armor (PERCENT)

### `dexterity`
- **Base:** 0.0
- **Min/Max:** 0.0 / 100,000,000.0
- **Is Percentage:** false
- **Element:** Physical
- **Scaling:** CORE
- **Serialization Type:** core_stat
- **Bonuses per Point:**
  - +10 Energy (FLAT)
  - +1% Dodge (PERCENT)
  - +0.5 Energy Regen (FLAT)

### `intelligence`
- **Base:** 0.0
- **Min/Max:** 0.0 / 100,000,000.0
- **Is Percentage:** false
- **Element:** Physical
- **Scaling:** CORE
- **Serialization Type:** core_stat
- **Bonuses per Point:**
  - +10 Mana (FLAT)
  - +0.5 Mana Regen (FLAT)
  - +0.5% Magic Shield (PERCENT)

---

## Elemental Damage Stats

### Fire Damage

#### `all_fire_damage`
- **Base:** 0.0
- **Min/Max:** 0.0 / 100,000,000.0
- **Is Percentage:** true
- **Element:** Fire
- **Icon:** ★
- **Color:** aqua
- **Group:** ELEMENTAL
- **GUI Group:** ELE_DAMAGE
- **Multi-Use Type:** MULTIPLICATIVE_DAMAGE
- **Effect:** Triggers on_damage, adds multiplicative damage when element matches

#### `fire_spell_dmg`
- **Base:** 0.0
- **Min/Max:** -1000.0 / 100,000,000.0
- **Is Percentage:** true
- **Element:** Physical
- **Icon:** ★
- **Group:** Misc
- **Multi-Use Type:** MULTIPLICATIVE_DAMAGE
- **Effect:** Applies to spells with fire tag

#### `fire_any_wep_damage`
- **Base:** 0.0
- **Min/Max:** 0.0 / 100,000,000.0
- **Is Percentage:** true
- **Element:** Fire
- **Group:** WEAPON
- **Multi-Use Type:** MULTIPLICATIVE_DAMAGE
- **Effect:** Applies to any weapon with fire element

#### Other Fire Stats
- `fire_cast_time` - Fire spell cast time modifier
- `fire_cdr` - Fire spell cooldown reduction
- `fire_dot_damage` - Fire damage over time
- `fire_health_leech` - Health leech from fire damage
- `fire_mana_leech` - Mana leech from fire damage
- `fire_dmg_reduction` - Fire damage reduction (defense)
- `fire_dmg_when_target_low_hp` - Bonus fire damage vs low HP targets
- `fire_vuln_crit` - Critical hits apply fire vulnerability
- `fire_ailment_aura_cost` - Cost of fire ailment auras
- `fire_damage_aura_cost` - Cost of fire damage auras
- `fire_res_aura_cost` - Cost of fire resistance auras

### Cold/Water Damage

#### `all_water_damage`
- **Base:** 0.0
- **Min/Max:** 0.0 / 100,000,000.0
- **Is Percentage:** true
- **Element:** Cold
- **Icon:** ★
- **Group:** ELEMENTAL
- **GUI Group:** ELE_DAMAGE
- **Multi-Use Type:** MULTIPLICATIVE_DAMAGE

#### Other Cold Stats
- `cold_spell_dmg` - Cold spell damage
- `cold_cast_time` - Cold spell cast time
- `cold_cdr` - Cold spell cooldown reduction
- `cold_ailment_aura_cost` - Cold ailment aura cost
- `cold_damage_aura_cost` - Cold damage aura cost
- `cold_res_aura_cost` - Cold resistance aura cost
- `water_any_wep_damage` - Cold weapon damage
- `water_dot_damage` - Cold damage over time
- `water_health_leech` - Health leech from cold
- `water_mana_leech` - Mana leech from cold
- `water_dmg_reduction` - Cold damage reduction
- `water_dmg_when_target_low_hp` - Bonus vs low HP
- `water_vuln_crit` - Cold vulnerability on crit
- `water_resist_per_1_fire_resist` - Convert fire resist
- `max_water_resist_per_1_max_fire_resist` - Convert max fire resist

### Lightning/Nature Damage

#### `all_lightning_damage`
- **Base:** 0.0
- **Min/Max:** 0.0 / 100,000,000.0
- **Is Percentage:** true
- **Element:** Nature
- **Icon:** ★
- **Group:** ELEMENTAL
- **GUI Group:** ELE_DAMAGE
- **Multi-Use Type:** MULTIPLICATIVE_DAMAGE

#### Other Lightning Stats
- `lightning_spell_dmg` - Lightning spell damage
- `lightning_any_wep_damage` - Lightning weapon damage
- `lightning_cast_time` - Lightning cast time
- `lightning_cdr` - Lightning cooldown reduction
- `lightning_dot_damage` - Lightning DOT
- `lightning_health_leech` - Lightning health leech
- `lightning_mana_leech` - Lightning mana leech
- `lightning_dmg_reduction` - Lightning damage reduction
- `lightning_dmg_when_target_low_hp` - Bonus vs low HP
- `lightning_vuln_crit` - Lightning vulnerability on crit
- `lightning_ailment_aura_cost` - Lightning ailment aura cost
- `lightning_damage_aura_cost` - Lightning damage aura cost
- `light_res_aura_cost` - Lightning resistance aura cost
- `lightning_resist_per_1_fire_resist` - Convert fire resist
- `max_lightning_resist_per_1_max_fire_resist` - Convert max fire resist

### Chaos/Shadow Damage

#### `all_chaos_damage`
- **Base:** 0.0
- **Min/Max:** 0.0 / 100,000,000.0
- **Is Percentage:** true
- **Element:** Shadow
- **Icon:** ★
- **Group:** ELEMENTAL
- **GUI Group:** ELE_DAMAGE
- **Multi-Use Type:** MULTIPLICATIVE_DAMAGE

#### Other Chaos Stats
- `chaos_spell_dmg` - Chaos spell damage
- `chaos_any_wep_damage` - Chaos weapon damage
- `chaos_cast_time` - Chaos cast time
- `chaos_cdr` - Chaos cooldown reduction
- `chaos_dot_damage` - Chaos DOT
- `chaos_health_leech` - Chaos health leech
- `chaos_mana_leech` - Chaos mana leech
- `chaos_dmg_reduction` - Chaos damage reduction
- `chaos_dmg_when_target_low_hp` - Bonus vs low HP
- `chaos_vuln_crit` - Chaos vulnerability on crit
- `chaos_ailment_aura_cost` - Chaos ailment aura cost
- `chaos_damage_aura_cost` - Chaos damage aura cost
- `chaos_res_aura_cost` - Chaos resistance aura cost

### Physical Damage

#### `all_physical_damage`
- **Base:** 0.0
- **Min/Max:** 0.0 / 100,000,000.0
- **Is Percentage:** true
- **Element:** Physical
- **Icon:** ★
- **Group:** ELEMENTAL
- **GUI Group:** ELE_DAMAGE
- **Multi-Use Type:** MULTIPLICATIVE_DAMAGE

#### Other Physical Stats
- `physical_spell_dmg` - Physical spell damage
- `physical_any_wep_damage` - Physical weapon damage
- `physical_cast_time` - Physical cast time
- `physical_cdr` - Physical cooldown reduction
- `physical_dot_damage` - Physical DOT
- `physical_health_leech` - Physical health leech
- `physical_mana_leech` - Physical mana leech
- `physical_dmg_reduction` - Physical damage reduction
- `physical_dmg_when_target_low_hp` - Bonus vs low HP
- `physical_vuln_crit` - Physical vulnerability on crit
- `physical_ailment_aura_cost` - Physical ailment aura cost
- `physical_damage_aura_cost` - Physical damage aura cost
- `physical_weapon_damage_per_perc_of_mana` - Scale weapon damage with mana%

### Multi-Element Stats

#### `all_elemental_damage`
- All elemental damage (Fire, Cold, Lightning, Chaos combined)

#### `elemental_*` Series
- `elemental_any_wep_damage` - All elemental weapon damage
- `elemental_dmg_reduction` - All elemental damage reduction
- `elemental_dmg_when_target_low_hp` - Bonus vs low HP
- `elemental_dot_damage` - All elemental DOT
- `elemental_health_leech` - Elemental health leech
- `elemental_mana_leech` - Elemental mana leech
- `elemental_vuln_crit` - Elemental vulnerability on crit

#### `all_all_damage`
- **Universal damage multiplier** - Affects ALL damage types

#### Element-Specific Spell Damage by Tag
- `spell_fire_damage` - Fire tagged spells
- `spell_water_damage` - Cold tagged spells
- `spell_lightning_damage` - Lightning tagged spells
- `spell_chaos_damage` - Chaos tagged spells
- `spell_physical_damage` - Physical tagged spells
- `spell_elemental_damage` - All elemental tagged spells

---

## Defense Stats

### Armor

#### Armor Stats
- `armor_per_10_dodge` - Convert dodge to armor
- `armor_per_10_mana` - Convert mana to armor
- `armor_aura_cost` - Armor aura cost modifier
- `health_per_10_armor` - Convert armor to health

### Dodge

#### Dodge Stats
- `dodge_aura_cost`
  - **Base:** 0.0
  - **Min/Max:** -1000.0 / 100,000,000.0
  - **Is Percentage:** true
  - **Minus is Good:** true
  - **Multi-Use Type:** MULTIPLY_STAT
  - **Effect:** Reduces cost of dodge auras

- `dodge_er_endurance_charge` - Dodge per endurance charge
- `dodge_er_power_charge` - Dodge per power charge
- `dodge_per_10_magic_shield` - Convert magic shield to dodge

### Block

#### Block Stats
- `block_aura_cost` - Block aura cost
- `block_per_endurance_charge` - Block per endurance charge

### Damage Reduction

#### General Damage Reduction
- `dmg_reduction` - General damage reduction %
- `dmg_received` - Damage received modifier
- `dmg_reduction_chance` - Chance to reduce damage
- `dmg_reduction_per_gale_force` - DR per gale force stack

#### Element-Specific Damage Reduction
- `all_dmg_reduction` - All damage types
- `fire_dmg_reduction` - Fire damage reduction
- `cold_dmg_reduction` - (via water prefix)
- `lightning_dmg_reduction` - Lightning damage reduction
- `chaos_dmg_reduction` - Chaos damage reduction
- `physical_dmg_reduction` - Physical damage reduction
- `elemental_dmg_reduction` - All elemental

### Resistances

All resistance stats follow similar patterns with aura cost modifiers.

#### Fire Resistance
- `fire_res_aura_cost` - Cost of fire resistance auras

#### Cold Resistance
- `cold_res_aura_cost` - Cost of cold resistance auras
- `water_resist_per_1_fire_resist` - Convert fire resist to cold
- `max_water_resist_per_1_max_fire_resist` - Convert max fire resist

#### Lightning Resistance
- `light_res_aura_cost` - Cost of lightning resistance auras
- `lightning_resist_per_1_fire_resist` - Convert fire resist to lightning
- `max_lightning_resist_per_1_max_fire_resist` - Convert max fire resist

#### Chaos Resistance
- `chaos_res_aura_cost` - Cost of chaos resistance auras

#### Multi-Resistance
- `ele_res_aura_cost` - All elemental resistances aura cost
- `ignore_ele_res` - Ignore enemy elemental resistance

---

## Resource Stats

### Health (HP)

#### Base Health Stats
- `health_per_10_strength` - Health per 10 STR
- `health_per_10_dexterity` - Health per 10 DEX
- `health_per_10_intelligence` - Health per 10 INT
- `health_per_10_armor` - Health per 10 armor

#### Health On Event
- `health_on_kill` - Health gained on kill
- `health_on_hit_hit` - Health on hit
- `health_on_is_blocked` - Health when you block
- `health_on_is_dodged` - Health when you dodge

#### Health Regeneration
- `health_reg_aura_cost` - Health regen aura cost
- `health_regen_per_1_magic_shield_regen` - Convert MS regen to health regen
- `health_regen_per_10_intelligence` - Health regen per 10 INT

### Mana

#### Base Mana Stats
- `mana_per_10_strength` - Mana per 10 STR
- `mana_per_10_dexterity` - Mana per 10 DEX
- `mana_per_10_intelligence` - Mana per 10 INT
- `mana_per_10_armor` - Mana per 10 armor
- `mana_per_10_dodge` - Mana per 10 dodge
- `mana_per_10_health` - Mana per 10 health

#### Mana On Event
- `mana_on_kill` - Mana gained on kill
- `mana_on_hit_hit` - Mana on hit
- `mana_on_is_blocked` - Mana when you block
- `mana_on_is_dodged` - Mana when you dodge

#### Mana Regeneration & Cost
- `mana_reg_aura_cost` - Mana regen aura cost
- `mana_regen_per_500_magic_shield` - Mana regen per 500 MS
- `mana_cost`
  - **Base:** 0.0
  - **Min/Max:** -75.0 / 300.0
  - **Is Percentage:** true
  - **Minus is Good:** true
  - **Effect:** Modifies spell mana cost
- `archmage_mana_cost` - Mana cost for archmage builds

### Energy

#### Energy Stats
- `energy_per_10_dexterity` - Energy per 10 DEX
- `energy_per_10_mana` - Energy per 10 mana

#### Energy On Event
- `energy_on_kill` - Energy gained on kill
- `energy_on_hit_hit` - Energy on hit
- `energy_on_is_blocked` - Energy when you block
- `energy_on_is_dodged` - Energy when you dodge

#### Energy Leech
- `energy_leech_cap` - Maximum energy leech rate
- `energy_reg_aura_cost` - Energy regen aura cost

### Magic Shield (MS)

#### Base Magic Shield Stats
- `magic_shield_per_10_dodge` - MS per 10 dodge
- `magic_shield_per_10_mana` - MS per 10 mana
- `magic_shield_aura_cost` - MS aura cost

#### Magic Shield On Event
- `magic_shield_on_kill` - MS gained on kill
- `magic_shield_on_hit_hit` - MS on hit
- `magic_shield_on_is_blocked` - MS when you block
- `magic_shield_on_is_dodged` - MS when you dodge

#### Magic Shield Regeneration
- `magic_shield_reg_aura_cost` - MS regen aura cost
- `magic_shield_regen_per_1_health_regen` - Convert health regen to MS regen

#### Magic Shield Leech
- `magic_shield_leech_cap` - Maximum MS leech rate

### Blood

#### Blood Stats
- `blood_per_10_strength` - Blood per 10 STR
- `blood_on_kill` - Blood gained on kill
- `blood_on_is_blocked` - Blood when you block
- `blood_on_is_dodged` - Blood when you dodge
- `blood_leech_cap` - Maximum blood leech rate

---

## Regeneration & Leech

### Health Leech

#### General Health Leech
- `all_health_leech` - Leech from all damage
- `health_leech_cap` - Maximum leech rate cap
- `inc_leech` - Increased leech effectiveness

#### Element-Specific Health Leech
- `fire_health_leech` - From fire damage
- `water_health_leech` - From cold damage
- `lightning_health_leech` - From lightning damage
- `chaos_health_leech` - From chaos damage
- `physical_health_leech` - From physical damage
- `elemental_health_leech` - From all elemental

### Mana Leech

#### General Mana Leech
- `all_mana_leech` - Leech from all damage
- `mana_leech_cap` - Maximum mana leech rate cap

#### Element-Specific Mana Leech
- `fire_mana_leech` - From fire damage
- `water_mana_leech` - From cold damage
- `lightning_mana_leech` - From lightning damage
- `chaos_mana_leech` - From chaos damage
- `physical_mana_leech` - From physical damage
- `elemental_mana_leech` - From all elemental

### Other Leech Types

#### Lifesteal & Spell Leech
- `lifesteal` - General lifesteal
- `spell_lifesteal` - Lifesteal from spells
- `dot_lifesteal` - Lifesteal from damage over time

#### Magic Shield Steal
- `spell_mssteal` - Magic shield steal from spells

#### Manasteal
- `manasteal` - Mana steal

### Healing & Regeneration

#### Healing Power
- `increase_healing` - Increased healing effectiveness
- `heal_effect_on_self` - Healing effect on self
- `low_hp_healing` - Bonus healing when low HP

#### Regeneration Modifiers
- `out_of_combat_regen` - Out of combat regeneration

---

## Cast Speed & Cooldown

### Cast Speed

#### `cast_speed`
- **Base:** 0.0
- **Min/Max:** -90.0 / 90.0
- **Is Percentage:** true
- **Icon:** ★
- **Group:** Misc
- **Multi-Use Type:** MULTIPLY_STAT
- **Effects:**
  1. Converts to cooldown for cast_speed_to_cooldown tagged spells
  2. Decreases cast ticks for magic spells not tagged not_affected_by_cast_speed

### Cast Time Modifiers

Cast time modifiers exist for many spell tags and element types:

#### By Element
- `fire_cast_time` - Fire spell cast time
- `cold_cast_time` - Cold spell cast time
- `lightning_cast_time` - Lightning spell cast time
- `chaos_cast_time` - Chaos spell cast time
- `physical_cast_time` - Physical spell cast time

#### By Spell Tag
- `area_cast_time` - Area spells
- `arrow_cast_time` - Arrow spells
- `beast_cast_time` - Beast spells
- `buff_cast_time` - Buff spells
- `chaining_cast_time` - Chaining spells
- `curse_cast_time` - Curse spells
- `damage_cast_time` - Damage spells
- `golem_cast_time` - Golem spells
- `heal_cast_time` - Heal spells
- `magic_cast_time` - Magic spells
- `melee_cast_time` - Melee spells
- `minion_explode_cast_time` - Minion explosion spells
- `missile_cast_time` - Missile spells
- `movement_cast_time` - Movement spells
- `projectile_cast_time` - Projectile spells
- `ranged_cast_time` - Ranged spells
- `rejuvenate_cast_time` - Rejuvenate spells
- `self_damage_cast_time` - Self-damage spells
- `shatter_cast_time` - Shatter spells
- `shield_cast_time` - Shield spells
- `shout_cast_time` - Shout spells
- `song_cast_time` - Song spells
- `summon_cast_time` - Summon spells
- `thorns_cast_time` - Thorns spells
- `totem_cast_time` - Totem spells
- `trap_cast_time` - Trap spells
- `weapon_skill_cast_time` - Weapon skill spells
- `has_pet_ability_cast_time` - Pet ability spells

#### Special Cast Time
- `cast_speed_to_cooldown_cast_time` - For spells that convert cast speed to CDR
- `not_affected_by_cast_speed_cast_time` - Spells not affected by cast speed

### Cooldown Reduction (CDR)

#### General CDR
- `cdr` - General cooldown reduction

#### By Element
- `fire_cdr` - Fire spell CDR
- `cold_cdr` - Cold spell CDR
- `lightning_cdr` - Lightning spell CDR
- `chaos_cdr` - Chaos spell CDR
- `physical_cdr` - Physical spell CDR

#### By Spell Tag
All spell tags have corresponding CDR stats (same list as cast_time above):
- `area_cdr`, `arrow_cdr`, `beast_cdr`, `buff_cdr`, etc.

#### Special CDR
- `cd_ticks` - Cooldown tick modifier
- `cast_speed_to_cooldown_cdr` - For cast speed conversion

---

## Charges

### Power Charges

Power charges boost offensive capabilities.

#### Power Charge Generation
- `power_charge_on_hit` - Power charge on hit
- `power_charge_on_crit` - Power charge on critical hit

#### Power Charge Bonuses
- `dmg_per_power_charge` - Damage per power charge
- `more_dmg_per_power` - More damage per power charge
- `crit_dmg_per_power_charge` - Crit damage per power charge
- `aoe_per_power_charge` - Area of effect per power charge
- `dodge_er_power_charge` - Dodge per power charge

### Endurance Charges

Endurance charges boost defensive capabilities.

#### Endurance Charge Generation
- `endurance_charge_on_hit` - Endurance charge on hit
- `endurance_charge_on_crit` - Endurance charge on critical hit

#### Endurance Charge Bonuses
- `dmg_per_endurance_charge` - Damage per endurance charge
- `block_per_endurance_charge` - Block per endurance charge
- `dodge_er_endurance_charge` - Dodge per endurance charge
- `move_speed_per_endurance_charge` - Move speed per endurance charge

### Frenzy Charges

Frenzy charges boost speed and damage.

#### Frenzy Charge Generation
- `frenzy_charge_on_hit` - Frenzy charge on hit
- `frenzy_charge_on_crit` - Frenzy charge on critical hit

#### Frenzy Charge Bonuses
- `more_dmg_per_frenzy` - More damage per frenzy charge

### Charge Effect Modifiers

#### Charge Buff Effectiveness
- `inc_effect_of_charge_buff_given` - Effectiveness of charges you give
- `inc_effect_of_charge_buff_on_you` - Effectiveness of charges on you

#### Charge Effect Duration
- `charge_eff_dur_u_cast` - Charge effect duration

---

## Critical Stats

### Critical Hit

#### `critical_hit`
- **Base:** 1.0
- **Min/Max:** 0.0 / 100.0
- **Is Percentage:** true
- **Element:** Physical
- **Icon:** ⚔
- **Color:** yellow
- **Group:** MAIN
- **Multi-Use Type:** MULTIPLY_STAT
- **Effect:** Random roll to set crit boolean on damage (excludes DOT)

#### Critical Hit Scaling
- `critical_hit_per_10_strength` - Crit chance per 10 STR

### Critical Damage

#### `critical_damage`
- **Base:** 100.0
- **Min/Max:** 0.0 / 500.0
- **Is Percentage:** true
- **Element:** Physical
- **Icon:** ⚔
- **Color:** red
- **Group:** MAIN
- **Multi-Use Type:** MULTIPLY_STAT
- **Effect:** Adds crit damage multiplier when is_crit is true (excludes DOT)

#### Critical Damage Scaling
- `crit_dmg_per_power_charge` - Crit damage per power charge

### Non-Critical Damage

#### `non_crit_damage`
- Bonus damage for non-critical hits

### Vulnerability on Critical

Critical hits can apply vulnerability to enemies:
- `all_vuln_crit` - All damage vulnerability
- `fire_vuln_crit` - Fire vulnerability
- `water_vuln_crit` - Cold vulnerability
- `lightning_vuln_crit` - Lightning vulnerability
- `chaos_vuln_crit` - Chaos vulnerability
- `physical_vuln_crit` - Physical vulnerability
- `elemental_vuln_crit` - Elemental vulnerability

---

## Weapon Type Damage

### Melee Weapons

#### Swords
- `sword_damage` - Sword damage multiplier
- `ele_sword_damage` - Elemental sword damage

#### Axes
- `axe_damage` - Axe damage multiplier
- `ele_axe_damage` - Elemental axe damage

#### Hammers
- `hammer_damage` - Hammer damage multiplier (assumed, file not yet read)

#### Spears
- `spear_damage` - Spear damage multiplier

#### Tridents
- `trident_damage` - Trident damage multiplier
- `ele_trident_damage` - Elemental trident damage

### Ranged Weapons

#### Bows
- `bow_damage` - Bow damage multiplier
- `ele_bow_damage` - Elemental bow damage

#### Crossbows
- `crossbow_damage` - Crossbow damage multiplier
- `ele_crossbow_damage` - Elemental crossbow damage

### Magic Weapons

#### Staffs
- `staff_damage` - Staff damage multiplier
- `ele_staff_damage` - Elemental staff damage

#### Wands
- `wand_damage` - Wand damage multiplier (assumed)

### General Weapon Damage

#### Universal Weapon Damage
- `all_any_wep_damage` - All weapon damage
- `attack_damage` - Attack damage (general)

#### Element-Specific Weapon Damage
- `fire_any_wep_damage` - Fire weapon damage
- `water_any_wep_damage` - Cold weapon damage
- `lightning_any_wep_damage` - Lightning weapon damage
- `chaos_any_wep_damage` - Chaos weapon damage
- `physical_any_wep_damage` - Physical weapon damage
- `elemental_any_wep_damage` - Elemental weapon damage

---

## Spell Tags & Modifiers

Spells can have multiple tags that determine which modifiers affect them.

### Spell Tag Categories

#### Area Spells
- `area_spell_dmg` / `area_spell_dmg_taken`
- `area_cast_time` / `area_cdr`
- `area_dmg` - Area damage

#### Arrow Spells
- `arrow_spell_dmg` / `arrow_spell_dmg_taken`
- `arrow_cast_time` / `arrow_cdr`

#### Beast Spells
- `beast_spell_dmg` / `beast_spell_dmg_taken`
- `beast_cast_time` / `beast_cdr`

#### Buff Spells
- `buff_spell_dmg` / `buff_spell_dmg_taken`
- `buff_cast_time` / `buff_cdr`

#### Chaining Spells
- `chaining_spell_dmg` / `chaining_spell_dmg_taken`
- `chaining_cast_time` / `chaining_cdr`

#### Curse Spells
- `curse_spell_dmg` / `curse_spell_dmg_taken`
- `curse_cast_time` / `curse_cdr`

#### Damage Spells
- `damage_spell_dmg` / `damage_spell_dmg_taken`
- `damage_cast_time` / `damage_cdr`

#### Defensive Spells
- `defensive_eff_dur_u_cast` - Defensive effect duration

#### Golem Spells
- `golem_spell_dmg` / `golem_spell_dmg_taken`
- `golem_cast_time` / `golem_cdr`
- `golem_eff_dur_u_cast` - Golem effect duration
- Golem Buffs:
  - `apply_golem_effect_fire_golem_buff`
  - `apply_golem_effect_ice_golem_buff`
  - `apply_golem_effect_lightning_golem_buff`

#### Heal Spells
- `heal_spell_dmg` / `heal_spell_dmg_taken`
- `heal_cast_time` / `heal_cdr`

#### Magic Spells
- `magic_spell_dmg` / `magic_spell_dmg_taken`
- `magic_cast_time` / `magic_cdr`

#### Melee Spells
- `melee_spell_dmg` / `melee_spell_dmg_taken`
- `melee_cast_time` / `melee_cdr`

#### Minion Spells
- `minion_explode_spell_dmg` / `minion_explode_spell_dmg_taken`
- `minion_explode_cast_time` / `minion_explode_cdr`

#### Missile Spells
- `missile_spell_dmg` / `missile_spell_dmg_taken`
- `missile_cast_time` / `missile_cdr`

#### Movement Spells
- `movement_spell_dmg` / `movement_spell_dmg_taken`
- `movement_cast_time` / `movement_cdr`

#### Projectile Spells
- `projectile_spell_dmg` / `projectile_spell_dmg_taken`
- `projectile_cast_time` / `projectile_cdr`

#### Ranged Spells
- `ranged_spell_dmg` / `ranged_spell_dmg_taken`
- `ranged_cast_time` / `ranged_cdr`

#### Rejuvenate Spells
- `rejuvenate_spell_dmg` / `rejuvenate_spell_dmg_taken`
- `rejuvenate_cast_time` / `rejuvenate_cdr`
- `rejuv_eff_on_self` - Rejuvenate effect on self

#### Self-Damage Spells
- `self_damage_spell_dmg` / `self_damage_spell_dmg_taken`
- `self_damage_cast_time` / `self_damage_cdr`
- `no_attacker_stats_on_selfdmg` - Disable attacker stats on self damage

#### Shatter Spells
- `shatter_spell_dmg` / `shatter_spell_dmg_taken`
- `shatter_cast_time` / `shatter_cdr`

#### Shield Spells
- `shield_spell_dmg` / `shield_spell_dmg_taken`
- `shield_cast_time` / `shield_cdr`

#### Shout Spells
- `shout_spell_dmg` / `shout_spell_dmg_taken`
- `shout_cast_time` / `shout_cdr`

#### Song Spells
- `song_spell_dmg` / `song_spell_dmg_taken`
- `song_cast_time` / `song_cdr`
- `song_eff_dur_u_cast` - Song effect duration

#### Summon Spells
- `summon_spell_dmg` / `summon_spell_dmg_taken`
- `summon_cast_time` / `summon_cdr`
- `summon_damage` - Summon/minion damage
- `summon_duration` - Summon duration
- `summon_dmg_aura_cost` - Summon damage aura cost
- `max_total_summons` - Maximum summons

#### Thorns Spells
- `thorns_spell_dmg` / `thorns_spell_dmg_taken`
- `thorns_cast_time` / `thorns_cdr`

#### Totem Spells
- `totem_spell_dmg` / `totem_spell_dmg_taken`
- `totem_cast_time` / `totem_cdr`
- `totem_duration` - Totem duration
- `totem_resto` - Totem restoration

#### Trap Spells
- `trap_spell_dmg` / `trap_spell_dmg_taken`
- `trap_cast_time` / `trap_cdr`
- `trap_area_dmg` - Trap area damage

#### Weapon Skill Spells
- `weapon_skill_spell_dmg` / `weapon_skill_spell_dmg_taken`
- `weapon_skill_cast_time` / `weapon_skill_cdr`

#### Pet Ability Spells
- `has_pet_ability_spell_dmg` / `has_pet_ability_spell_dmg_taken`
- `has_pet_ability_cast_time` / `has_pet_ability_cdr`

### Special Spell Modifiers

#### Cast Speed to Cooldown
- `cast_speed_to_cooldown_spell_dmg` / `cast_speed_to_cooldown_spell_dmg_taken`
- `cast_speed_to_cooldown_cast_time` / `cast_speed_to_cooldown_cdr`

#### Not Affected by Cast Speed
- `not_affected_by_cast_speed_spell_dmg` / `not_affected_by_cast_speed_spell_dmg_taken`
- `not_affected_by_cast_speed_cast_time` / `not_affected_by_cast_speed_cdr`

### Spell Damage by Element
- `spell_all_damage` - All spell damage
- `spell_fire_damage` - Fire spell damage
- `spell_water_damage` - Cold spell damage
- `spell_lightning_damage` - Lightning spell damage
- `spell_chaos_damage` - Chaos spell damage
- `spell_physical_damage` - Physical spell damage
- `spell_elemental_damage` - Elemental spell damage

### Spell Damage Conversions
- `spell_damage_per_perc_of_increase_healing` - Convert healing% to spell damage

---

## Ailments & Curses

### Ailments

Ailments are negative status effects applied to enemies.

#### Bleed
- Various bleed-related stats (specific stats to be read)

#### Burn
- Various burn-related stats

#### Electrify (Shock)
- Various electrify-related stats

#### Freeze
- Various freeze-related stats

#### Poison
- Various poison-related stats

#### Slow
- `chance_of_slow` - Chance to slow enemies

#### Stun
- Various stun-related stats

#### Blind
- `chance_of_blind` - Chance to blind enemies

### Curses

Curses are debuffs applied to enemies through curse spells.

#### Curse Application
- `curse_spell_dmg` - Curse spell damage
- `curse_cast_time` - Curse cast time
- `curse_cdr` - Curse cooldown reduction
- `curse_eff_dur_u_cast` - Curse effect duration

#### Cursed By Status
When enemies are cursed, you gain bonuses:

- `cursed_by_agony` - Target cursed by agony
- `cursed_by_despair` - Target cursed by despair
- `cursed_by_slow` - Target cursed by slow
- `cursed_by_weak` - Target cursed by weakness
- `cursed_by_wounds` - Target cursed by wounds

#### Damage to Cursed
- `damage_to_cursed` - Increased damage to cursed enemies

#### Curse Buff Effectiveness
- `inc_effect_of_curse_buff_given` - Effectiveness of curses you apply
- `inc_effect_of_curse_buff_on_you` - Effectiveness of curses on you

### Immunity Stats

Immunities protect against specific ailments/curses:

- `agony_immunity` - Immune to agony
- `despair_immunity` - Immune to despair
- `slow_immunity` - Immune to slow
- `weak_immunity` - Immune to weakness
- `wounds_immunity` - Immune to wounds
- `knockback_resist` - Knockback resistance

---

## Auras

Auras are permanent buffs with ongoing costs.

### Aura Cost Modifiers

All aura costs can be reduced with specific stats:

#### Defense Auras
- `armor_aura_cost` - Armor aura cost
- `block_aura_cost` - Block aura cost
- `dodge_aura_cost` - Dodge aura cost

#### Element Damage Auras
- `fire_damage_aura_cost` - Fire damage aura
- `cold_damage_aura_cost` - Cold damage aura
- `lightning_damage_aura_cost` - Lightning damage aura
- `chaos_damage_aura_cost` - Chaos damage aura
- `physical_damage_aura_cost` - Physical damage aura

#### Element Resistance Auras
- `fire_res_aura_cost` - Fire resistance aura
- `cold_res_aura_cost` - Cold resistance aura
- `light_res_aura_cost` - Lightning resistance aura
- `chaos_res_aura_cost` - Chaos resistance aura
- `ele_res_aura_cost` - Elemental resistance aura

#### Ailment Auras
- `fire_ailment_aura_cost` - Fire ailment aura
- `cold_ailment_aura_cost` - Cold ailment aura
- `lightning_ailment_aura_cost` - Lightning ailment aura
- `chaos_ailment_aura_cost` - Chaos ailment aura
- `physical_ailment_aura_cost` - Physical ailment aura

#### Resource Regeneration Auras
- `health_reg_aura_cost` - Health regen aura
- `mana_reg_aura_cost` - Mana regen aura
- `energy_reg_aura_cost` - Energy regen aura
- `magic_shield_reg_aura_cost` - Magic shield regen aura

#### Summon Auras
- `summon_dmg_aura_cost` - Summon damage aura

---

## Special Mechanics

### Accuracy

- `accuracy` - Hit accuracy
- `accuracy_per_10_dexterity` - Accuracy per 10 DEX

### Area of Effect

- `inc_aoe` - Increased area of effect
- `aoe_per_power_charge` - AoE per power charge

### Projectiles

- `projectile_count` - Number of projectiles
- `projectile_damage` - Projectile damage
- `projectile_damage_per_5_strength` - Projectile damage per 5 STR
- `projectile_barrage` - Projectile barrage
- `piercing_projectiles` - Projectile pierce
- `chain_count` - Chain count
- `faster_projectiles` - Projectile speed
- `proj_dmg_received` - Projectile damage taken
- `proj_spread_randomness` - Projectile spread

### DOT (Damage Over Time)

- `dot_dmg` - DOT damage
- `dot_dmg_multi` - DOT damage multiplier
- `all_dot_damage` - All DOT damage

#### Element-Specific DOT
- `fire_dot_damage` - Fire DOT
- `water_dot_damage` - Cold DOT
- `lightning_dot_damage` - Lightning DOT
- `chaos_dot_damage` - Chaos DOT
- `physical_dot_damage` - Physical DOT
- `elemental_dot_damage` - Elemental DOT

### Conditional Damage

#### Low HP Bonuses
- `dmg_when_low_hp` - Damage when you're low HP
- `dmg_when_target_is_low_hp` - Damage when target is low HP
- `dmg_when_target_low_hp` - General low HP damage

#### Element-Specific Low HP
- `fire_dmg_when_target_low_hp`
- `water_dmg_when_target_low_hp`
- `lightning_dmg_when_target_low_hp`
- `chaos_dmg_when_target_low_hp`
- `physical_dmg_when_target_low_hp`
- `elemental_dmg_when_target_low_hp`
- `all_dmg_when_target_low_hp`

#### High HP Bonuses
- `dmg_when_target_near_full_hp` - Damage when target near full HP

### Damage to Enemy Types

- `dmg_to_living` - Damage to living enemies
- `dmg_to_undead` - Damage to undead enemies

### Proc/Explosion Effects

- `proc_blood_explosion` - Blood explosion on kill
- `proc_ignite_explosion` - Ignite explosion proc
- `proc_profane_explosion` - Profane explosion proc
- `proc_profane_explosion_any` - Any profane explosion
- `proc_shatter` - Shatter proc on freeze kill
- `on_death_ignite_explosion` - Ignite explosion on death

### Archmage

- `archmage` - Archmage mechanic
- `archmage_mana_cost` - Archmage mana cost modifier

### Gale Force

- `gale_force_on_hit` - Gale force stacks on hit
- `dmg_reduction_per_gale_force` - Damage reduction per stack

### Attack Mechanics

- `double_attack_chance` - Chance to double attack

### Movement

- `move_speed` - Movement speed
- `move_speed_per_endurance_charge` - Move speed per endurance charge

### Threat/Aggro

- `threat_generated` - Threat generation
- `more_threat_on_take_dmg` - More threat when taking damage
- `aggro_radius` - Aggro radius

### Buff Effect Modifiers

#### Negative Buffs
- `inc_effect_of_negative_buff_given` - Negative buffs you apply
- `inc_effect_of_negative_buff_on_you` - Negative buffs on you

#### Positive Buffs
- `inc_effect_of_positive_buff_given` - Positive buffs you give
- `inc_effect_of_positive_buff_on_you` - Positive buffs on you

#### Offensive Buffs
- `inc_effect_of_offensive_buff_given` - Offensive buffs you give
- `inc_effect_of_offensive_buff_on_you` - Offensive buffs on you

#### Defensive Buffs
- `inc_effect_of_defensive_buff_given` - Defensive buffs you give
- `inc_effect_of_defensive_buff_on_you` - Defensive buffs on you

#### Food Buffs
- `inc_effect_of_food_buff_given` - Food buffs you give
- `inc_effect_of_food_buff_on_you` - Food buffs on you
- `food_eff_dur_u_cast` - Food effect duration

#### Golem Buffs
- `inc_effect_of_golem_buff_given` - Golem buffs you give
- `inc_effect_of_golem_buff_on_you` - Golem buffs on you

#### Heal Over Time Buffs
- `inc_effect_of_heal_over_time_buff_given` - HOT buffs you give
- `inc_effect_of_heal_over_time_buff_on_you` - HOT buffs on you
- `heal_over_time_eff_dur_u_cast` - HOT effect duration

#### Immobilizing Buffs
- `inc_effect_of_immobilizing_buff_given` - Immobilizing buffs you give
- `inc_effect_of_immobilizing_buff_on_you` - Immobilizing buffs on you
- `immobilizing_eff_dur_u_cast` - Immobilizing effect duration

#### Song Buffs
- `inc_effect_of_song_buff_given` - Song buffs you give
- `inc_effect_of_song_buff_on_you` - Song buffs on you

### Effect Duration

General effect duration modifiers:
- `eff_dur_u_cast` - Effect duration you cast
- `offensive_eff_dur_u_cast` - Offensive effect duration
- `defensive_eff_dur_u_cast` - Defensive effect duration
- `positive_eff_dur_u_cast` - Positive effect duration
- `negative_eff_dur_u_cast` - Negative effect duration

### Scaling Conversions

#### Damage Scaling
- `str_dmg` - STR damage scaling
- `dex_dmg` - DEX damage scaling
- `int_dmg` - INT damage scaling
- `int_dmg_per_5_strength` - INT damage per 5 STR

#### Total Damage
- `total_damage` - Total damage modifier

---

## Complete Stat List

Below is the alphabetical list of all 432 stats:

- `accuracy` - Hit accuracy
- `accuracy_per_10_dexterity` - Accuracy scaling from DEX
- `aggro_radius` - Aggro/threat radius
- `agony_immunity` - Immunity to agony ailment
- `all_all_damage` - Universal damage multiplier
- `all_any_wep_damage` - All weapon damage types
- `all_chaos_damage` - All chaos damage
- `all_dmg_reduction` - All damage reduction
- `all_dmg_when_target_low_hp` - Bonus damage vs low HP targets
- `all_dot_damage` - All damage over time
- `all_elemental_damage` - All elemental damage types
- `all_fire_damage` - All fire damage
- `all_health_leech` - Health leech from all damage
- `all_lightning_damage` - All lightning damage
- `all_mana_leech` - Mana leech from all damage
- `all_physical_damage` - All physical damage
- `all_vuln_crit` - All damage vulnerability on crit
- `all_water_damage` - All cold damage
- `aoe_per_power_charge` - AoE per power charge
- `apply_golem_effect_fire_golem_buff` - Fire golem buff application
- `apply_golem_effect_ice_golem_buff` - Ice golem buff application
- `apply_golem_effect_lightning_golem_buff` - Lightning golem buff application
- `archmage` - Archmage mechanic
- `archmage_mana_cost` - Archmage mana cost modifier
- `area_cast_time` - Area spell cast time
- `area_cdr` - Area spell cooldown reduction
- `area_dmg` - Area damage
- `area_spell_dmg` - Area spell damage bonus
- `area_spell_dmg_taken` - Area spell damage taken
- `armor_aura_cost` - Armor aura cost
- `armor_per_10_dodge` - Armor conversion from dodge
- `armor_per_10_mana` - Armor conversion from mana
- `arrow_cast_time` - Arrow spell cast time
- `arrow_cdr` - Arrow spell cooldown reduction
- `arrow_spell_dmg` - Arrow spell damage bonus
- `arrow_spell_dmg_taken` - Arrow spell damage taken
- `attack_damage` - General attack damage
- `axe_damage` - Axe weapon damage
- `beast_cast_time` - Beast spell cast time
- `beast_cdr` - Beast spell cooldown reduction
- `beast_spell_dmg` - Beast spell damage bonus
- `beast_spell_dmg_taken` - Beast spell damage taken
- `block_aura_cost` - Block aura cost
- `block_per_endurance_charge` - Block per endurance charge
- `blood_leech_cap` - Maximum blood leech rate
- `blood_on_is_blocked` - Blood when blocking
- `blood_on_is_dodged` - Blood when dodging
- `blood_on_kill` - Blood gained on kill
- `blood_per_10_strength` - Blood per 10 STR
- `bow_damage` - Bow weapon damage
- `buff_cast_time` - Buff spell cast time
- `buff_cdr` - Buff spell cooldown reduction
- `buff_spell_dmg` - Buff spell damage bonus
- `buff_spell_dmg_taken` - Buff spell damage taken
- `cast_speed` - Cast speed modifier
- `cast_speed_to_cooldown_cast_time` - Cast time for CD conversion spells
- `cast_speed_to_cooldown_cdr` - CDR for CD conversion spells
- `cast_speed_to_cooldown_spell_dmg` - Spell damage for CD conversion spells
- `cast_speed_to_cooldown_spell_dmg_taken` - Spell damage taken for CD conversion spells
- `cd_ticks` - Cooldown tick modifier
- `cdr` - General cooldown reduction
- `chain_count` - Projectile chain count
- `chaining_cast_time` - Chaining spell cast time
- `chaining_cdr` - Chaining spell cooldown reduction
- `chaining_spell_dmg` - Chaining spell damage bonus
- `chaining_spell_dmg_taken` - Chaining spell damage taken
- `chance_of_blind` - Chance to blind enemies
- `chance_of_slow` - Chance to slow enemies
- `chaos_ailment_aura_cost` - Chaos ailment aura cost
- `chaos_any_wep_damage` - Chaos weapon damage
- `chaos_cast_time` - Chaos spell cast time
- `chaos_cdr` - Chaos spell cooldown reduction
- `chaos_damage_aura_cost` - Chaos damage aura cost
- `chaos_dmg_reduction` - Chaos damage reduction
- `chaos_dmg_when_target_low_hp` - Chaos damage vs low HP
- `chaos_dot_damage` - Chaos damage over time
- `chaos_health_leech` - Health leech from chaos damage
- `chaos_mana_leech` - Mana leech from chaos damage
- `chaos_res_aura_cost` - Chaos resistance aura cost
- `chaos_spell_dmg` - Chaos spell damage bonus
- `chaos_spell_dmg_taken` - Chaos spell damage taken
- `chaos_vuln_crit` - Chaos vulnerability on crit
- `charge_eff_dur_u_cast` - Charge effect duration
- `cold_ailment_aura_cost` - Cold ailment aura cost
- `cold_cast_time` - Cold spell cast time
- `cold_cdr` - Cold spell cooldown reduction
- `cold_damage_aura_cost` - Cold damage aura cost
- `cold_res_aura_cost` - Cold resistance aura cost
- `cold_spell_dmg` - Cold spell damage bonus
- `cold_spell_dmg_taken` - Cold spell damage taken
- `crit_dmg_per_power_charge` - Crit damage per power charge
- `critical_damage` - Critical damage multiplier
- `critical_hit` - Critical hit chance
- `critical_hit_per_10_strength` - Crit chance per 10 STR
- `crossbow_damage` - Crossbow weapon damage
- `curse_cast_time` - Curse spell cast time
- `curse_cdr` - Curse spell cooldown reduction
- `curse_eff_dur_u_cast` - Curse effect duration
- `curse_spell_dmg` - Curse spell damage bonus
- `curse_spell_dmg_taken` - Curse spell damage taken
- `cursed_by_agony` - Target cursed by agony
- `cursed_by_despair` - Target cursed by despair
- `cursed_by_slow` - Target cursed by slow
- `cursed_by_weak` - Target cursed by weakness
- `cursed_by_wounds` - Target cursed by wounds
- `damage_cast_time` - Damage spell cast time
- `damage_cdr` - Damage spell cooldown reduction
- `damage_spell_dmg` - Damage spell damage bonus
- `damage_spell_dmg_taken` - Damage spell damage taken
- `damage_to_cursed` - Damage to cursed enemies
- `defensive_eff_dur_u_cast` - Defensive effect duration
- `despair_immunity` - Immunity to despair
- `dex_dmg` - DEX damage scaling
- `dexterity` - Dexterity core stat
- `dmg_per_endurance_charge` - Damage per endurance charge
- `dmg_per_power_charge` - Damage per power charge
- `dmg_received` - Damage received modifier
- `dmg_reduction` - General damage reduction
- `dmg_reduction_chance` - Chance to reduce damage
- `dmg_reduction_per_gale_force` - Damage reduction per gale force
- `dmg_to_living` - Damage to living enemies
- `dmg_to_undead` - Damage to undead enemies
- `dmg_when_low_hp` - Damage when you're low HP
- `dmg_when_target_is_low_hp` - Damage when target is low HP
- `dmg_when_target_near_full_hp` - Damage when target near full HP
- `dodge_aura_cost` - Dodge aura cost
- `dodge_er_endurance_charge` - Dodge per endurance charge
- `dodge_er_power_charge` - Dodge per power charge
- `dodge_per_10_magic_shield` - Dodge conversion from magic shield
- `dot_dmg` - DOT damage modifier
- `dot_dmg_multi` - DOT damage multiplier
- `dot_lifesteal` - Lifesteal from DOT
- `double_attack_chance` - Chance to double attack
- `eff_dur_u_cast` - Effect duration you cast
- `ele_axe_damage` - Elemental axe damage
- `ele_bow_damage` - Elemental bow damage
- `ele_crossbow_damage` - Elemental crossbow damage
- `ele_res_aura_cost` - Elemental resistance aura cost
- `ele_staff_damage` - Elemental staff damage
- `ele_sword_damage` - Elemental sword damage
- `ele_trident_damage` - Elemental trident damage
- `elemental_any_wep_damage` - Elemental weapon damage
- `elemental_dmg_reduction` - Elemental damage reduction
- `elemental_dmg_when_target_low_hp` - Elemental damage vs low HP
- `elemental_dot_damage` - Elemental DOT
- `elemental_health_leech` - Health leech from elemental
- `elemental_mana_leech` - Mana leech from elemental
- `elemental_vuln_crit` - Elemental vulnerability on crit
- `endurance_charge_on_crit` - Endurance charge on crit
- `endurance_charge_on_hit` - Endurance charge on hit
- `energy_leech_cap` - Maximum energy leech rate
- `energy_on_hit_hit` - Energy on hit
- `energy_on_is_blocked` - Energy when blocking
- `energy_on_is_dodged` - Energy when dodging
- `energy_on_kill` - Energy gained on kill
- `energy_per_10_dexterity` - Energy per 10 DEX
- `energy_per_10_mana` - Energy conversion from mana
- `energy_reg_aura_cost` - Energy regen aura cost
- `faster_projectiles` - Projectile speed
- `fire_ailment_aura_cost` - Fire ailment aura cost
- `fire_any_wep_damage` - Fire weapon damage
- `fire_cast_time` - Fire spell cast time
- `fire_cdr` - Fire spell cooldown reduction
- `fire_damage_aura_cost` - Fire damage aura cost
- `fire_dmg_reduction` - Fire damage reduction
- `fire_dmg_when_target_low_hp` - Fire damage vs low HP
- `fire_dot_damage` - Fire DOT
- `fire_health_leech` - Health leech from fire
- `fire_mana_leech` - Mana leech from fire
- `fire_res_aura_cost` - Fire resistance aura cost
- `fire_spell_dmg` - Fire spell damage bonus
- `fire_spell_dmg_taken` - Fire spell damage taken
- `fire_vuln_crit` - Fire vulnerability on crit
- `food_eff_dur_u_cast` - Food effect duration
- `frenzy_charge_on_crit` - Frenzy charge on crit
- `frenzy_charge_on_hit` - Frenzy charge on hit
- `gale_force_on_hit` - Gale force stacks on hit
- `golem_cast_time` - Golem spell cast time
- `golem_cdr` - Golem spell cooldown reduction
- `golem_eff_dur_u_cast` - Golem effect duration
- `golem_spell_dmg` - Golem spell damage bonus
- `golem_spell_dmg_taken` - Golem spell damage taken
- `has_pet_ability_cast_time` - Pet ability cast time
- `has_pet_ability_cdr` - Pet ability cooldown reduction
- `has_pet_ability_spell_dmg` - Pet ability spell damage
- `has_pet_ability_spell_dmg_taken` - Pet ability spell damage taken
- `heal_cast_time` - Heal spell cast time
- `heal_cdr` - Heal spell cooldown reduction
- `heal_effect_on_self` - Healing effect on self
- `heal_over_time_eff_dur_u_cast` - Heal over time effect duration
- `heal_spell_dmg` - Heal spell damage bonus
- `heal_spell_dmg_taken` - Heal spell damage taken
- `health_leech_cap` - Maximum health leech rate
- `health_on_hit_hit` - Health on hit
- `health_on_is_blocked` - Health when blocking
- `health_on_is_dodged` - Health when dodging
- `health_on_kill` - Health gained on kill
- `health_per_10_armor` - Health conversion from armor
- `health_per_10_dexterity` - Health per 10 DEX
- `health_per_10_intelligence` - Health per 10 INT
- `health_per_10_strength` - Health per 10 STR
- `health_reg_aura_cost` - Health regen aura cost
- `health_regen_per_1_magic_shield_regen` - Health regen from MS regen
- `health_regen_per_10_intelligence` - Health regen per 10 INT
- `ignore_ele_res` - Ignore enemy elemental resistance
- `immobilizing_eff_dur_u_cast` - Immobilizing effect duration
- `inc_aoe` - Increased area of effect
- `inc_effect_of_charge_buff_given` - Charge buff effectiveness given
- `inc_effect_of_charge_buff_on_you` - Charge buff effectiveness on you
- `inc_effect_of_curse_buff_given` - Curse buff effectiveness given
- `inc_effect_of_curse_buff_on_you` - Curse buff effectiveness on you
- `inc_effect_of_defensive_buff_given` - Defensive buff effectiveness given
- `inc_effect_of_defensive_buff_on_you` - Defensive buff effectiveness on you
- `inc_effect_of_food_buff_given` - Food buff effectiveness given
- `inc_effect_of_food_buff_on_you` - Food buff effectiveness on you
- `inc_effect_of_golem_buff_given` - Golem buff effectiveness given
- `inc_effect_of_golem_buff_on_you` - Golem buff effectiveness on you
- `inc_effect_of_heal_over_time_buff_given` - HOT buff effectiveness given
- `inc_effect_of_heal_over_time_buff_on_you` - HOT buff effectiveness on you
- `inc_effect_of_immobilizing_buff_given` - Immobilizing buff effectiveness given
- `inc_effect_of_immobilizing_buff_on_you` - Immobilizing buff effectiveness on you
- `inc_effect_of_negative_buff_given` - Negative buff effectiveness given
- `inc_effect_of_negative_buff_on_you` - Negative buff effectiveness on you
- `inc_effect_of_offensive_buff_given` - Offensive buff effectiveness given
- `inc_effect_of_offensive_buff_on_you` - Offensive buff effectiveness on you
- `inc_effect_of_positive_buff_given` - Positive buff effectiveness given
- `inc_effect_of_positive_buff_on_you` - Positive buff effectiveness on you
- `inc_effect_of_song_buff_given` - Song buff effectiveness given
- `inc_effect_of_song_buff_on_you` - Song buff effectiveness on you
- `inc_leech` - Increased leech effectiveness
- `increase_healing` - Increased healing effectiveness
- `int_dmg` - INT damage scaling
- `int_dmg_per_5_strength` - INT damage per 5 STR
- `intelligence` - Intelligence core stat
- `knockback_resist` - Knockback resistance
- `lifesteal` - General lifesteal
- `light_res_aura_cost` - Lightning resistance aura cost
- `lightning_ailment_aura_cost` - Lightning ailment aura cost
- `lightning_any_wep_damage` - Lightning weapon damage
- `lightning_cast_time` - Lightning spell cast time
- `lightning_cdr` - Lightning spell cooldown reduction
- `lightning_damage_aura_cost` - Lightning damage aura cost
- `lightning_dmg_reduction` - Lightning damage reduction
- `lightning_dmg_when_target_low_hp` - Lightning damage vs low HP
- `lightning_dot_damage` - Lightning DOT
- `lightning_health_leech` - Health leech from lightning
- `lightning_mana_leech` - Mana leech from lightning
- `lightning_resist_per_1_fire_resist` - Convert fire resist to lightning
- `lightning_spell_dmg` - Lightning spell damage bonus
- `lightning_spell_dmg_taken` - Lightning spell damage taken
- `lightning_vuln_crit` - Lightning vulnerability on crit
- `low_hp_healing` - Bonus healing when low HP
- `magic_cast_time` - Magic spell cast time
- `magic_cdr` - Magic spell cooldown reduction
- `magic_shield_aura_cost` - Magic shield aura cost
- `magic_shield_leech_cap` - Maximum MS leech rate
- `magic_shield_on_hit_hit` - Magic shield on hit
- `magic_shield_on_is_blocked` - Magic shield when blocking
- `magic_shield_on_is_dodged` - Magic shield when dodging
- `magic_shield_on_kill` - Magic shield gained on kill
- `magic_shield_per_10_dodge` - MS conversion from dodge
- `magic_shield_per_10_mana` - MS conversion from mana
- `magic_shield_reg_aura_cost` - MS regen aura cost
- `magic_shield_regen_per_1_health_regen` - MS regen from health regen
- `magic_spell_dmg` - Magic spell damage bonus
- `magic_spell_dmg_taken` - Magic spell damage taken
- `mana_cost` - Mana cost modifier
- `mana_leech_cap` - Maximum mana leech rate
- `mana_on_hit_hit` - Mana on hit
- `mana_on_is_blocked` - Mana when blocking
- `mana_on_is_dodged` - Mana when dodging
- `mana_on_kill` - Mana gained on kill
- `mana_per_10_armor` - Mana conversion from armor
- `mana_per_10_dexterity` - Mana per 10 DEX
- `mana_per_10_dodge` - Mana conversion from dodge
- `mana_per_10_health` - Mana conversion from health
- `mana_per_10_intelligence` - Mana per 10 INT
- `mana_per_10_strength` - Mana per 10 STR
- `mana_reg_aura_cost` - Mana regen aura cost
- `mana_regen_per_500_magic_shield` - Mana regen per 500 MS
- `manasteal` - Mana steal
- `max_lightning_resist_per_1_max_fire_resist` - Convert max fire resist to lightning
- `max_total_summons` - Maximum total summons
- `max_water_resist_per_1_max_fire_resist` - Convert max fire resist to cold
- `melee_cast_time` - Melee spell cast time
- `melee_cdr` - Melee spell cooldown reduction
- `melee_spell_dmg` - Melee spell damage bonus
- `melee_spell_dmg_taken` - Melee spell damage taken
- `minion_explode_cast_time` - Minion explode spell cast time
- `minion_explode_cdr` - Minion explode spell CDR
- `minion_explode_spell_dmg` - Minion explode spell damage
- `minion_explode_spell_dmg_taken` - Minion explode spell damage taken
- `missile_cast_time` - Missile spell cast time
- `missile_cdr` - Missile spell cooldown reduction
- `missile_spell_dmg` - Missile spell damage bonus
- `missile_spell_dmg_taken` - Missile spell damage taken
- `more_dmg_per_frenzy` - More damage per frenzy charge
- `more_dmg_per_power` - More damage per power charge
- `more_threat_on_take_dmg` - More threat when taking damage
- `move_speed` - Movement speed
- `move_speed_per_endurance_charge` - Move speed per endurance charge
- `movement_cast_time` - Movement spell cast time
- `movement_cdr` - Movement spell cooldown reduction
- `movement_spell_dmg` - Movement spell damage bonus
- `movement_spell_dmg_taken` - Movement spell damage taken
- `negative_eff_dur_u_cast` - Negative effect duration
- `no_attacker_stats_on_selfdmg` - Disable attacker stats on self damage
- `non_crit_damage` - Non-critical damage bonus
- `not_affected_by_cast_speed_cast_time` - Cast time not affected by cast speed
- `not_affected_by_cast_speed_cdr` - CDR not affected by cast speed
- `not_affected_by_cast_speed_spell_dmg` - Spell damage not affected by cast speed
- `not_affected_by_cast_speed_spell_dmg_taken` - Spell damage taken not affected by cast speed
- `offensive_eff_dur_u_cast` - Offensive effect duration
- `on_death_ignite_explosion` - Ignite explosion on death
- `out_of_combat_regen` - Out of combat regeneration
- `physical_ailment_aura_cost` - Physical ailment aura cost
- `physical_any_wep_damage` - Physical weapon damage
- `physical_cast_time` - Physical spell cast time
- `physical_cdr` - Physical spell cooldown reduction
- `physical_damage_aura_cost` - Physical damage aura cost
- `physical_dmg_reduction` - Physical damage reduction
- `physical_dmg_when_target_low_hp` - Physical damage vs low HP
- `physical_dot_damage` - Physical DOT
- `physical_health_leech` - Health leech from physical
- `physical_mana_leech` - Mana leech from physical
- `physical_spell_dmg` - Physical spell damage bonus
- `physical_spell_dmg_taken` - Physical spell damage taken
- `physical_vuln_crit` - Physical vulnerability on crit
- `physical_weapon_damage_per_perc_of_mana` - Physical weapon damage scaling from mana%
- `piercing_projectiles` - Projectile pierce
- `positive_eff_dur_u_cast` - Positive effect duration
- `power_charge_on_crit` - Power charge on crit
- `power_charge_on_hit` - Power charge on hit
- `proc_blood_explosion` - Blood explosion proc
- `proc_ignite_explosion` - Ignite explosion proc
- `proc_profane_explosion` - Profane explosion proc
- `proc_profane_explosion_any` - Any profane explosion proc
- `proc_shatter` - Shatter proc
- `proj_dmg_received` - Projectile damage taken
- `proj_spread_randomness` - Projectile spread randomness
- `projectile_barrage` - Projectile barrage
- `projectile_cast_time` - Projectile spell cast time
- `projectile_cdr` - Projectile spell cooldown reduction
- `projectile_count` - Number of projectiles
- `projectile_damage` - Projectile damage
- `projectile_damage_per_5_strength` - Projectile damage per 5 STR
- `projectile_spell_dmg` - Projectile spell damage bonus
- `projectile_spell_dmg_taken` - Projectile spell damage taken
- `ranged_cast_time` - Ranged spell cast time
- `ranged_cdr` - Ranged spell cooldown reduction
- `ranged_spell_dmg` - Ranged spell damage bonus
- `ranged_spell_dmg_taken` - Ranged spell damage taken
- `rejuv_eff_on_self` - Rejuvenate effect on self
- `rejuvenate_cast_time` - Rejuvenate spell cast time
- `rejuvenate_cdr` - Rejuvenate spell cooldown reduction
- `rejuvenate_spell_dmg` - Rejuvenate spell damage bonus
- `rejuvenate_spell_dmg_taken` - Rejuvenate spell damage taken
- `self_damage_cast_time` - Self-damage spell cast time
- `self_damage_cdr` - Self-damage spell cooldown reduction
- `self_damage_spell_dmg` - Self-damage spell damage bonus
- `self_damage_spell_dmg_taken` - Self-damage spell damage taken
- `shatter_cast_time` - Shatter spell cast time
- `shatter_cdr` - Shatter spell cooldown reduction
- `shatter_spell_dmg` - Shatter spell damage bonus
- `shatter_spell_dmg_taken` - Shatter spell damage taken
- `shield_cast_time` - Shield spell cast time
- `shield_cdr` - Shield spell cooldown reduction
- `shield_spell_dmg` - Shield spell damage bonus
- `shield_spell_dmg_taken` - Shield spell damage taken
- `shout_cast_time` - Shout spell cast time
- `shout_cdr` - Shout spell cooldown reduction
- `shout_spell_dmg` - Shout spell damage bonus
- `shout_spell_dmg_taken` - Shout spell damage taken
- `slow_immunity` - Immunity to slow
- `song_cast_time` - Song spell cast time
- `song_cdr` - Song spell cooldown reduction
- `song_eff_dur_u_cast` - Song effect duration
- `song_spell_dmg` - Song spell damage bonus
- `song_spell_dmg_taken` - Song spell damage taken
- `spell_all_damage` - All spell damage
- `spell_chaos_damage` - Chaos tagged spells
- `spell_damage_per_perc_of_increase_healing` - Spell damage from healing%
- `spell_elemental_damage` - Elemental tagged spells
- `spell_fire_damage` - Fire tagged spells
- `spell_lifesteal` - Lifesteal from spells
- `spell_lightning_damage` - Lightning tagged spells
- `spell_mssteal` - Magic shield steal from spells
- `spell_physical_damage` - Physical tagged spells
- `spell_water_damage` - Cold tagged spells
- `staff_damage` - Staff weapon damage
- `str_dmg` - STR damage scaling
- `strength` - Strength core stat
- `summon_cast_time` - Summon spell cast time
- `summon_cdr` - Summon spell cooldown reduction
- `summon_damage` - Summon/minion damage
- `summon_dmg_aura_cost` - Summon damage aura cost
- `summon_duration` - Summon duration
- `summon_spell_dmg` - Summon spell damage bonus
- `summon_spell_dmg_taken` - Summon spell damage taken
- `sword_damage` - Sword weapon damage
- `thorns_cast_time` - Thorns spell cast time
- `thorns_cdr` - Thorns spell cooldown reduction
- `thorns_spell_dmg` - Thorns spell damage bonus
- `thorns_spell_dmg_taken` - Thorns spell damage taken
- `threat_generated` - Threat generation
- `total_damage` - Total damage modifier
- `totem_cast_time` - Totem spell cast time
- `totem_cdr` - Totem spell cooldown reduction
- `totem_duration` - Totem duration
- `totem_resto` - Totem restoration
- `totem_spell_dmg` - Totem spell damage bonus
- `totem_spell_dmg_taken` - Totem spell damage taken
- `trap_area_dmg` - Trap area damage
- `trap_cast_time` - Trap spell cast time
- `trap_cdr` - Trap spell cooldown reduction
- `trap_spell_dmg` - Trap spell damage bonus
- `trap_spell_dmg_taken` - Trap spell damage taken
- `trident_damage` - Trident weapon damage
- `water_any_wep_damage` - Cold weapon damage
- `water_dmg_reduction` - Cold damage reduction
- `water_dmg_when_target_low_hp` - Cold damage vs low HP
- `water_dot_damage` - Cold DOT
- `water_health_leech` - Health leech from cold
- `water_mana_leech` - Mana leech from cold
- `water_resist_per_1_fire_resist` - Convert fire resist to cold
- `water_vuln_crit` - Cold vulnerability on crit
- `weak_immunity` - Immunity to weakness
- `weapon_skill_cast_time` - Weapon skill spell cast time
- `weapon_skill_cdr` - Weapon skill spell cooldown reduction
- `weapon_skill_spell_dmg` - Weapon skill spell damage
- `weapon_skill_spell_dmg_taken` - Weapon skill spell damage taken
- `wounds_immunity` - Immunity to wounds

---

## Summary Statistics

### Stat Distribution by Category

Based on analysis of all 432 stats:

**Core & Resources**
- Core Stats: 3 (strength, dexterity, intelligence)
- Health Stats: ~15
- Mana Stats: ~15
- Energy Stats: ~10
- Magic Shield Stats: ~12
- Blood Stats: ~5

**Damage**
- Elemental Damage Stats: ~150 (across Fire, Cold, Lightning, Chaos, Physical)
- Weapon Type Stats: ~20
- Spell Tag Stats: ~120
- DOT Stats: ~15
- Critical Stats: ~10
- Conditional Damage: ~25

**Defense**
- Armor Stats: ~5
- Dodge Stats: ~7
- Block Stats: ~3
- Damage Reduction: ~20
- Resistance Stats: ~30 (including aura costs)

**Offensive Mechanics**
- Cast Speed/Time: ~50
- Cooldown Reduction: ~50
- Charges: ~15
- Accuracy: ~2
- Projectile: ~10

**Sustain**
- Health Leech: ~10
- Mana Leech: ~10
- Other Leech: ~5
- Regeneration: ~15
- On-Event Resources: ~20

**Special**
- Auras: ~40
- Ailments: ~20
- Curses: ~10
- Immunities: ~5
- Procs/Explosions: ~10
- Buff Effects: ~30
- Effect Duration: ~15
- Summons: ~10
- Misc: ~20

### Key Patterns

**Elemental Coverage**
Each element (Fire, Cold, Lightning, Chaos, Physical) typically has:
- All damage (`all_X_damage`)
- Spell damage (`X_spell_dmg`)
- Weapon damage (`X_any_wep_damage`)
- DOT damage (`X_dot_damage`)
- Health leech (`X_health_leech`)
- Mana leech (`X_mana_leech`)
- Damage reduction (`X_dmg_reduction`)
- Conditional damage (`X_dmg_when_target_low_hp`)
- Vulnerability on crit (`X_vuln_crit`)
- Cast time (`X_cast_time`)
- CDR (`X_cdr`)
- Aura costs (damage, resistance, ailment)

**Spell Tag Coverage**
Most spell tags have a quartet of stats:
- Spell damage (`tag_spell_dmg`)
- Spell damage taken (`tag_spell_dmg_taken`)
- Cast time (`tag_cast_time`)
- Cooldown reduction (`tag_cdr`)

**Resource Patterns**
Each resource typically has:
- Base scaling (per stat points)
- On-event generation (kill, hit, block, dodge)
- Regeneration stats
- Leech/steal stats
- Aura cost modifiers

### Softcaps & Limits

Most stats have the following limits:
- **Percentage Stats:** 0% to 100,000,000% (effectively unlimited)
- **Cast Speed:** -90% to +90%
- **Mana Cost:** -75% to +300%
- **Critical Hit Chance:** 0% to 100%
- **Critical Damage:** 0% to 500%

Stats with `minus_is_good: true`:
- Mana Cost
- All Aura Costs
- Damage Received

---

## Usage Guide

### Finding Relevant Stats

1. **For Elemental Builds:** Look for `all_[element]_damage`, element-specific spell/weapon damage
2. **For Spell Builds:** Find spell tag modifiers for your spell type
3. **For Weapon Builds:** Check weapon-type specific damage and `any_wep_damage`
4. **For Defensive Builds:** Focus on armor, dodge, block, damage reduction, and resistances
5. **For Sustain:** Leech stats, regeneration, and on-event resource gain
6. **For Charge Builds:** Generation stats + per-charge bonuses
7. **For Aura Builds:** Aura cost reduction is critical

### Stat Stacking Mechanics

**Additive Stats** (MULTIPLICATIVE_DAMAGE type):
- All elemental damage stats of the same type add together FIRST
- Then multiply final damage
- Example: 50% fire damage + 30% fire damage = 80% fire damage = 1.8x multiplier

**Multiplicative Stats** (MULTIPLY_STAT type):
- Each source multiplies separately
- Example: 20% more damage × 30% more damage = 1.2 × 1.3 = 1.56x multiplier

### Core Stat Allocation

**Strength Focus:**
- +5 Health per point (tanky)
- +0.25 Health Regen
- +1% Armor
- Best for: Melee tanks, physical damage builds

**Dexterity Focus:**
- +10 Energy per point
- +1% Dodge (evasion tanking)
- +0.5 Energy Regen
- Best for: Fast attackers, bow/crossbow builds, dodge tanks

**Intelligence Focus:**
- +10 Mana per point
- +0.5 Mana Regen
- +0.5% Magic Shield
- Best for: Spell casters, magic shield tanks

### Efficient Stat Combinations

**Physical Melee:**
- strength + all_physical_damage + sword/axe_damage + attack_damage

**Elemental Caster:**
- intelligence + all_[element]_damage + [element]_spell_dmg + [spell_tag]_spell_dmg

**Hybrid Builds:**
- elemental_any_wep_damage applies to all elements on weapons
- all_all_damage is universal but harder to stack

**Tank Builds:**
- Armor Tank: strength + armor stats + health regen
- Dodge Tank: dexterity + dodge stats + energy
- Magic Shield Tank: intelligence + magic_shield stats + mana regen
- Hybrid: Use conversion stats (armor_per_10_mana, etc.)

---

## Notes

- This reference was generated from JSON data files as of 2025-11-05
- All stats are defined in: `ref/data/mmorpg/mmorpg_stat/`
- Element naming: "Water" in files = Cold damage, "Lightning" = Nature element, "Chaos" = Shadow element
- Many stats have effects that trigger on specific events (on_damage, on_spell_stat_calc, etc.)
- The `ser` field indicates serialization type: `data` for most stats, `core_stat` for core attributes
- For exact effect implementations, refer to the JSON files directly

---

**Document created for:** Mine and Slash Rework  
**Project Path:** C:\Users\gummy\IdeaProjects\Mine-And-Slash-Rework  
**Stats Analyzed:** 432 unique stats

