# クラス別パッシブまとめ

本ドキュメントでは、MnS More Class 追加職向けのパッシブ案を整理する。
基本方針・実装フローは @documents/07_PassiveSkillSystem.md および @documents/05_PerkSystem.md を参照。
全パッシブは `max_lvls = 8` を想定し、SpellSchool グリッドの各行（Row0〜Row3）に 2 件ずつ配置する計画。

## Druid パッシブ

| Row | ID | 表示名案 | 効果 / Lv | 8Lv 合計 | 想定シナジー |
| --- | --- | --- | --- | --- | --- |
| 0 | `p_health_druid` | Verdant Vitality | 最大体力 +4% | +32% | Barkskin / Nature's Blessing |
| 0 | `p_mana_regen_druid` | Dewdrop Flow | マナ再生 +5% | +40% | 全スキル (Entangling Thorns の維持等) |
| 1 | `p_area_druid` | Verdant Reach | エリアタグ付きスペル範囲 +4% | +32% | Sakura Bloom / Weakness Aura |
| 1 | `p_dot_dmg_druid` | Spore Rot | DoT ダメージ +4% | +32% | Entangling Thorns DoT 強化 |
| 2 | `p_heal_str_druid` | Blossom Grace | 回復量 +3% | +24% | Sakura Bloom / Nature's Blessing |
| 2 | `p_effect_dur_druid` | Evergreen Tide | 付与効果持続 +5% | +40% | Cleanse / Barkskin / Weakness Aura |
| 3 | `p_armor_druid` | Barkhide Plate | アーマー +3% | +24% | Barkskin の軽減強化 |
| 3 | `p_debuff_resist_druid` | Cleansing Sap | 被デバフ耐性 +4% | +32% | Cleanse の再使用頻度低減 |

## Ninja パッシブ

| Row | ID | 表示名案 | 効果 / Lv | 8Lv 合計 | 対応Stat (既存クラス) | 想定シナジー |
| --- | --- | --- | --- | --- | --- | --- |
| 0 | `p_attack_speed_ninja` | Silent Footwork | 攻撃速度 +3% | +24% | `VanillaStatData.create(Attributes.ATTACK_SPEED, ...)` | Shadow Walk / Shunjin |
| 0 | `p_health_ninja` | Shinobi Endurance | 最大体力 +4% | +32% | `Health.getInstance()` | 連続戦闘時の生存力強化 |
| 1 | `p_crit_rate_ninja` | Razor Instinct | クリティカル率 +3% | +24% | `OffenseStats.CRIT_CHANCE` | Shuriken Volley / Kaze Giri |
| 1 | `p_dodge_rating_ninja` | Shadow Reflex | 回避率 +3% | +24% | `DodgeRating.getInstance()` | Shadow Walk / Gale Step |
| 2 | `p_skill_haste_ninja` | Smoke Veil Training | スキルクール短縮 +5% | +40% | `SpellChangeStats.COOLDOWN_REDUCTION` | Shadow Step / 瞬刃 (Shunjin) |
| 2 | `p_weapon_damage_ninja` | Phantom Arsenal | 物理スキルダメージ +3% | +24% | `BonusAttackDamage(Elements.Physical)` | Ryuusei Ranbu / Swing Scythe |
| 3 | `p_physical_resist_ninja` | Hardened Reflex | 物理被ダメ軽減 +3% | +24% | `DefenseStats.DAMAGE_RECEIVED` (Physical) | 前線生存性向上 |
| 3 | `p_move_speed_ninja` | Gale Step | 移動速度 +2% | +16% | `MovementSpeed.getInstance()` | 風斬り (Kaze Giri) / 機動戦闘 |

### 備考
- Row 配置は SpellSchool のレベル要求 (`lvl_reqs`) に合わせ、序盤は基礎強化、終盤はテクニカル/防御特化とする。
- 実装時は @src/main/kotlin/io/github/cotrin8672/mnsmoreclass/perk/PerkBuilderKt.kt を用い、`PerkRegistry` から登録する。
