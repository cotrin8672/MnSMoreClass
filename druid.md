# 🌳 ドルイド（Druid）クラス設計書

**作成日**: 2025年11月6日
**バージョン**: 1.20 Forge版
**ロール**: 純サポート／自然属性ヒーラー
**武器タイプ**: 杖（MAGE_WEAPON）
**特徴**: 回復・持続バフ・CC・防御デバフ

---

## 目次

1. [クラス概要](#クラス概要)
2. [アクティブスキル（6種）](#アクティブスキル6種)
3. [パッシブスキル（8種）](#パッシブスキル8種)
4. [スキルシナジー](#スキルシナジー)
5. [ExileEffect実装詳細](#exileeffect実装詳細)
6. [実装チェックリスト](#実装チェックリスト)

---

## クラス概要

ドルイドは自然の力を操る純サポートクラスです。以下の特徴を持ちます：

- **高い生存力**: 70%ダメージカット + 復活スキル
- **強力なCC**: 移動速度-80%の拘束 + マナ吸収
- **防御デバフ**: 敵の防御力を-30%低下
- **範囲サポート**: 回復 + 攻撃速度バフ
- **デバフ解除**: 状態異常除去 + 無効化

---

## アクティブスキル（6種）

### 1. **Barkskin（樹皮の護り）**

**役割**: 防御バフ（ダメージ70%カット＋リジェネ）

```json
{
  "identifier": "barkskin",
  "loc_name": "Barkskin",
  "weight": 1000,

  "attached": {
    "on_cast": [
      {
        "acts": [
          {
            "type": "sound",
            "map": {
              "sound": "minecraft:block.wood.place",
              "volume": 1.0,
              "pitch": 0.8
            }
          }
        ],
        "ifs": [{ "type": "on_spell_cast", "map": {} }],
        "targets": [],
        "en_preds": []
      },
      {
        "acts": [
          {
            "type": "exile_effect",
            "map": {
              "exile_potion_id": "barkskin",
              "potion_duration": 240,
              "effect_stacks": 1
            }
          }
        ],
        "ifs": [{ "type": "on_spell_cast", "map": {} }],
        "targets": [{ "type": "caster", "map": {} }],
        "en_preds": []
      },
      {
        "acts": [
          {
            "type": "particles_in_radius",
            "map": {
              "particle_type": "minecraft:composter",
              "radius": 1.5,
              "particle_count": 30.0,
              "motion": "None",
              "shape": "CIRCLE_2D",
              "height": 1.0
            }
          }
        ],
        "ifs": [{ "type": "on_spell_cast", "map": {} }],
        "targets": [],
        "en_preds": []
      }
    ],
    "entity_components": {}
  },

  "config": {
    "cast_time_ticks": 0,
    "cooldown_ticks": 900,
    "castingWeapon": "MAGE_WEAPON",
    "charges": 0,
    "charge_regen": 0,
    "charge_name": "",
    "mana_cost": { "min": 30.0, "max": 22.5 },
    "ene_cost": { "min": 0.0, "max": 0.0 },
    "times_to_cast": 1,
    "aggro_radius": 5,
    "imbues": 0,
    "style": "int",
    "swing_arm": true,
    "slows_when_casting": false,
    "summonType": "NONE",
    "summon_basic_atk": "",
    "use_support_gems_from": "",
    "tags": { "tags": ["magic", "buff", "physical"] },
    "tracking_radius": 5,
    "tracks": "enemies"
  },

  "cast_animation": { "id": "staff_ground" },
  "cast_finish_animation": { "id": "cast_finish" },

  "min_lvl": 1,
  "max_lvl": 16,
  "default_lvl": 0,
  "statsForSkillGem": [],
  "manual_tip": true,
  "effect_tip": "",
  "disabled_dims": [],
  "show_other_spell_tooltip": "",
  "lvl_based_on_spell": ""
}
```

**ExileEffect**:
```java
ExileEffectBuilder.of(BARKSKIN)
    .stat(-70, -70, DefenseStats.DAMAGE_RECEIVED.get(), ModType.FLAT)
    .stat(4, 4, HealthRegen.getInstance(), ModType.FLAT)
    .vanillaStat(VanillaStatData.create(MOVEMENT_SPEED, -0.2F, ModType.FLAT,
        UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890")))
    .maxStacks(1)
    .addTags(EffectTags.positive)
    .build();
```

---

### 2. **Cleanse（浄化）**

**役割**: デバフ解除 + 状態異常無効化

```json
{
  "identifier": "cleanse",
  "loc_name": "Cleanse",
  "weight": 1000,

  "attached": {
    "on_cast": [
      {
        "acts": [
          {
            "type": "sound",
            "map": {
              "sound": "minecraft:block.bell.use",
              "volume": 1.0,
              "pitch": 1.2
            }
          }
        ],
        "ifs": [{ "type": "on_spell_cast", "map": {} }],
        "targets": [],
        "en_preds": []
      },
      {
        "acts": [
          {
            "type": "potion",
            "map": {
              "potion_action": "remove_negative",
              "potion_strength": 999.0
            }
          }
        ],
        "ifs": [{ "type": "on_spell_cast", "map": {} }],
        "targets": [
          {
            "type": "aoe",
            "map": {
              "radius": 8.0,
              "selection_type": "RADIUS",
              "en_predicate": "allies"
            }
          }
        ],
        "en_preds": []
      },
      {
        "acts": [
          {
            "type": "exile_effect",
            "map": {
              "exile_potion_id": "cleansed",
              "potion_duration": 100,
              "effect_stacks": 1
            }
          }
        ],
        "ifs": [{ "type": "on_spell_cast", "map": {} }],
        "targets": [
          {
            "type": "aoe",
            "map": {
              "radius": 8.0,
              "selection_type": "RADIUS",
              "en_predicate": "allies"
            }
          }
        ],
        "en_preds": []
      },
      {
        "acts": [
          {
            "type": "particles_in_radius",
            "map": {
              "particle_type": "minecraft:end_rod",
              "radius": 1.0,
              "particle_count": 50.0,
              "motion": "Random",
              "shape": "SPHERE",
              "height": 2.0
            }
          }
        ],
        "ifs": [{ "type": "on_spell_cast", "map": {} }],
        "targets": [],
        "en_preds": []
      }
    ],
    "entity_components": {}
  },

  "config": {
    "cast_time_ticks": 0,
    "cooldown_ticks": 0,
    "castingWeapon": "ANY_WEAPON",
    "charges": 2,
    "charge_regen": 600,
    "charge_name": "sanctuary",
    "mana_cost": { "min": 20.0, "max": 15.0 },
    "ene_cost": { "min": 0.0, "max": 0.0 },
    "times_to_cast": 1,
    "aggro_radius": 5,
    "imbues": 0,
    "style": "int",
    "swing_arm": true,
    "slows_when_casting": false,
    "summonType": "NONE",
    "summon_basic_atk": "",
    "use_support_gems_from": "",
    "tags": { "tags": ["magic", "buff", "heal"] },
    "tracking_radius": 5,
    "tracks": "allies"
  },

  "cast_animation": { "id": "steady_cast" },
  "cast_finish_animation": { "id": "cast_finish" },

  "min_lvl": 5,
  "max_lvl": 16,
  "default_lvl": 0,
  "statsForSkillGem": [],
  "manual_tip": true,
  "effect_tip": "",
  "disabled_dims": [],
  "show_other_spell_tooltip": "",
  "lvl_based_on_spell": ""
}
```

**ExileEffect**:
```java
ExileEffectBuilder.of(CLEANSED)
    .stat(50, 50, new AilmentResistance(Ailments.ALL), ModType.FLAT)
    .stat(50, 50, DefenseStats.EFFECT_RESISTANCE.get(), ModType.FLAT)
    .maxStacks(1)
    .addTags(EffectTags.positive)
    .build();
```

---

### 3. **Weakness Aura（衰弱のオーラ）**

**役割**: 防御力デバフ（-30% Armor）

```json
{
  "identifier": "weakness_aura",
  "loc_name": "Weakness Aura",
  "weight": 1000,

  "attached": {
    "on_cast": [
      {
        "acts": [
          {
            "type": "sound",
            "map": {
              "sound": "minecraft:entity.slime.attack",
              "volume": 1.0,
              "pitch": 0.7
            }
          }
        ],
        "ifs": [{ "type": "on_spell_cast", "map": {} }],
        "targets": [],
        "en_preds": []
      },
      {
        "acts": [
          {
            "type": "particles_in_radius",
            "map": {
              "particle_type": "minecraft:spore_blossom_air",
              "radius": 4.0,
              "particle_count": 100.0,
              "motion": "Random",
              "shape": "CIRCLE_2D",
              "height": 0.5,
              "y_rand": 1.0
            }
          }
        ],
        "ifs": [{ "type": "on_spell_cast", "map": {} }],
        "targets": [],
        "en_preds": []
      },
      {
        "acts": [
          {
            "type": "exile_effect",
            "map": {
              "exile_potion_id": "weakness",
              "potion_duration": 160,
              "effect_stacks": 1
            }
          }
        ],
        "ifs": [{ "type": "on_spell_cast", "map": {} }],
        "targets": [
          {
            "type": "in_front",
            "map": {
              "distance": 7.0,
              "width": 4.0,
              "en_predicate": "enemies"
            }
          }
        ],
        "en_preds": []
      }
    ],
    "entity_components": {}
  },

  "config": {
    "cast_time_ticks": 0,
    "cooldown_ticks": 700,
    "castingWeapon": "MAGE_WEAPON",
    "charges": 0,
    "charge_regen": 0,
    "charge_name": "",
    "mana_cost": { "min": 15.0, "max": 11.25 },
    "ene_cost": { "min": 0.0, "max": 0.0 },
    "times_to_cast": 1,
    "aggro_radius": 10,
    "imbues": 0,
    "style": "int",
    "swing_arm": true,
    "slows_when_casting": false,
    "summonType": "NONE",
    "summon_basic_atk": "",
    "use_support_gems_from": "",
    "tags": { "tags": ["magic", "debuff", "nature", "area"] },
    "tracking_radius": 5,
    "tracks": "enemies"
  },

  "cast_animation": { "id": "hand_up_cast" },
  "cast_finish_animation": { "id": "cast_finish" },

  "min_lvl": 10,
  "max_lvl": 16,
  "default_lvl": 0,
  "statsForSkillGem": [],
  "manual_tip": true,
  "effect_tip": "",
  "disabled_dims": [],
  "show_other_spell_tooltip": "",
  "lvl_based_on_spell": ""
}
```

**ExileEffect**:
```java
ExileEffectBuilder.of(WEAKNESS)
    .stat(-30, -30, Armor.getInstance(), ModType.MORE)
    .stat(-20, -20, Armor.getInstance(), ModType.FLAT)
    .stat(-15, -15, new ElementalResist(Elements.Nature), ModType.FLAT)
    .spell(SpellBuilder.forEffect()
        .onTick(PartBuilder.aoeParticles(ParticleTypes.SPORE_BLOSSOM_AIR, 5D, 0.5D)
            .tick(20D))
        .buildForEffect())
    .maxStacks(1)
    .addTags(EffectTags.negative)
    .build();
```

---

### 4. **Entangling Thorns（絡みつく茨）**

**役割**: 拘束 + マナ吸収 + DoT

```json
{
  "identifier": "entangling_thorns",
  "loc_name": "Entangling Thorns",
  "weight": 1000,

  "attached": {
    "on_cast": [
      {
        "acts": [
          {
            "type": "sound",
            "map": {
              "sound": "minecraft:block.fungus.break",
              "volume": 1.0,
              "pitch": 1.0
            }
          }
        ],
        "ifs": [{ "type": "on_spell_cast", "map": {} }],
        "targets": [],
        "en_preds": []
      },
      {
        "acts": [
          {
            "type": "projectile",
            "map": {
              "proj_en": "mmorpg:spell_projectile",
              "proj_count": 1.0,
              "proj_speed": 1.5,
              "projectile_spread_randomness": 0.0,
              "gravity": true,
              "life_ticks": 60.0,
              "item": "minecraft:brown_mushroom",
              "entity_name": "spore"
            }
          }
        ],
        "ifs": [{ "type": "on_spell_cast", "map": {} }],
        "targets": [],
        "en_preds": []
      }
    ],
    "entity_components": {
      "spore": [
        {
          "acts": [
            {
              "type": "particles_in_radius",
              "map": {
                "particle_type": "minecraft:mycelium",
                "radius": 0.3,
                "particle_count": 3.0,
                "motion": "None"
              }
            }
          ],
          "ifs": [{ "type": "x_ticks_condition", "map": { "tick_rate": 2.0 } }],
          "targets": [],
          "en_preds": []
        },
        {
          "acts": [
            {
              "type": "sound",
              "map": {
                "sound": "minecraft:entity.slime.squish",
                "volume": 1.0,
                "pitch": 0.8
              }
            }
          ],
          "ifs": [{ "type": "on_entity_expire", "map": {} }],
          "targets": [],
          "en_preds": []
        },
        {
          "acts": [
            {
              "type": "particles_in_radius",
              "map": {
                "particle_type": "minecraft:spore_blossom_air",
                "radius": 3.0,
                "particle_count": 80.0,
                "motion": "Random",
                "shape": "CIRCLE_2D",
                "height": 0.3,
                "y_rand": 1.5
              }
            }
          ],
          "ifs": [{ "type": "on_entity_expire", "map": {} }],
          "targets": [],
          "en_preds": []
        },
        {
          "acts": [
            {
              "type": "exile_effect",
              "map": {
                "exile_potion_id": "entangling_thorns",
                "potion_duration": 120,
                "effect_stacks": 1
              }
            }
          ],
          "ifs": [{ "type": "on_entity_expire", "map": {} }],
          "targets": [
            {
              "type": "aoe",
              "map": {
                "radius": 3.0,
                "selection_type": "RADIUS",
                "en_predicate": "enemies"
              }
            }
          ],
          "en_preds": []
        }
      ]
    }
  },

  "config": {
    "cast_time_ticks": 0,
    "cooldown_ticks": 1000,
    "castingWeapon": "MAGE_WEAPON",
    "charges": 0,
    "charge_regen": 0,
    "charge_name": "",
    "mana_cost": { "min": 18.0, "max": 13.5 },
    "ene_cost": { "min": 0.0, "max": 0.0 },
    "times_to_cast": 1,
    "aggro_radius": 10,
    "imbues": 0,
    "style": "int",
    "swing_arm": true,
    "slows_when_casting": false,
    "summonType": "NONE",
    "summon_basic_atk": "",
    "use_support_gems_from": "",
    "tags": { "tags": ["magic", "damage", "nature", "area", "projectile"] },
    "tracking_radius": 5,
    "tracks": "enemies"
  },

  "cast_animation": { "id": "cast_throwable" },
  "cast_finish_animation": { "id": "cast_finish" },

  "min_lvl": 15,
  "max_lvl": 16,
  "default_lvl": 0,
  "statsForSkillGem": [],
  "manual_tip": true,
  "effect_tip": "",
  "disabled_dims": [],
  "show_other_spell_tooltip": "",
  "lvl_based_on_spell": ""
}
```

**ExileEffect**:
```java
ExileEffectBuilder.of(ENTANGLING_THORNS)
    .vanillaStat(VanillaStatData.create(MOVEMENT_SPEED, -0.8F, ModType.FLAT,
        UUID.fromString("b2c0d4e5-f6a7-8901-bcde-f12345678901")))
    .spell(SpellBuilder.forEffect()
        // マナスティール（2%/秒）
        .onTick(PartBuilder.justAction(SpellAction.RESTORE_MANA.create(2D))
            .addTarget(TargetSelector.SPELL_OWNER.create())
            .tick(20D))
        // 自然DoT
        .onTick(PartBuilder.damage(SpellCalcs.ENTANGLING_THORNS_DOT, Elements.Nature)
            .tick(20D))
        // パーティクル
        .onTick(PartBuilder.aoeParticles(ParticleTypes.MYCELIUM, 3D, 0.5D)
            .tick(10D))
        .buildForEffect())
    .maxStacks(1)
    .addTags(EffectTags.negative)
    .build();
```

**SpellCalc**:
```java
public static SpellCalcData ENTANGLING_THORNS_DOT = new SpellCalcData("entangling_thorns_dot", 1, 25);
```

---

### 5. **Sakura Bloom（桜の開花）**

**役割**: 範囲回復 + 攻撃速度バフ

```json
{
  "identifier": "sakura_bloom",
  "loc_name": "Sakura Bloom",
  "weight": 1000,

  "attached": {
    "on_cast": [
      {
        "acts": [
          {
            "type": "sound",
            "map": {
              "sound": "minecraft:block.cherry_sapling.place",
              "volume": 1.5,
              "pitch": 1.0
            }
          }
        ],
        "ifs": [{ "type": "on_spell_cast", "map": {} }],
        "targets": [],
        "en_preds": []
      },
      {
        "acts": [
          {
            "type": "summon_at_sight",
            "map": {
              "summon_entity": "mmorpg:simple_projectile",
              "count": 1.0,
              "distance": 0.0
            }
          }
        ],
        "ifs": [{ "type": "on_spell_cast", "map": {} }],
        "targets": [],
        "en_preds": []
      }
    ],
    "entity_components": {
      "default_entity_name": [
        {
          "acts": [
            {
              "type": "summon_block",
              "map": {
                "block": "minecraft:cherry_sapling",
                "life_ticks": 240.0,
                "entity_name": "sakura_tree",
                "block_fall_speed": 0.0,
                "find_nearest_surface": true,
                "is_block_falling": false
              }
            }
          ],
          "ifs": [{ "type": "on_entity_expire", "map": {} }],
          "targets": [],
          "en_preds": []
        }
      ],
      "sakura_tree": [
        {
          "acts": [
            {
              "type": "particles_in_radius",
              "map": {
                "particle_type": "minecraft:cherry_leaves",
                "radius": 8.0,
                "particle_count": 20.0,
                "motion": "Random",
                "shape": "CIRCLE_2D",
                "height": 2.0,
                "y_rand": 2.0
              }
            }
          ],
          "ifs": [{ "type": "x_ticks_condition", "map": { "tick_rate": 10.0 } }],
          "targets": [],
          "en_preds": []
        },
        {
          "acts": [
            {
              "type": "particles_in_radius",
              "map": {
                "particle_type": "minecraft:falling_cherry_blossom",
                "radius": 8.0,
                "particle_count": 15.0,
                "motion": "Random",
                "shape": "CIRCLE_2D",
                "height": 1.5,
                "y_rand": 1.0
              }
            }
          ],
          "ifs": [{ "type": "x_ticks_condition", "map": { "tick_rate": 8.0 } }],
          "targets": [],
          "en_preds": []
        },
        {
          "acts": [
            {
              "type": "particles_in_radius",
              "map": {
                "particle_type": "minecraft:heart",
                "radius": 8.0,
                "particle_count": 10.0,
                "motion": "None",
                "shape": "CIRCLE_2D",
                "height": 1.0
              }
            }
          ],
          "ifs": [{ "type": "x_ticks_condition", "map": { "tick_rate": 15.0 } }],
          "targets": [],
          "en_preds": []
        },
        {
          "acts": [
            {
              "type": "restore_health",
              "map": {
                "value_calculation": "sakura_bloom"
              }
            }
          ],
          "ifs": [{ "type": "x_ticks_condition", "map": { "tick_rate": 20.0 } }],
          "targets": [
            {
              "type": "aoe",
              "map": {
                "radius": 8.0,
                "selection_type": "RADIUS",
                "en_predicate": "allies"
              }
            }
          ],
          "en_preds": []
        },
        {
          "acts": [
            {
              "type": "exile_effect",
              "map": {
                "exile_potion_id": "sakura_blessing",
                "potion_duration": 40,
                "effect_stacks": 1
              }
            }
          ],
          "ifs": [{ "type": "x_ticks_condition", "map": { "tick_rate": 20.0 } }],
          "targets": [
            {
              "type": "aoe",
              "map": {
                "radius": 8.0,
                "selection_type": "RADIUS",
                "en_predicate": "allies"
              }
            }
          ],
          "en_preds": []
        }
      ]
    }
  },

  "config": {
    "cast_time_ticks": 0,
    "cooldown_ticks": 1200,
    "castingWeapon": "MAGE_WEAPON",
    "charges": 0,
    "charge_regen": 0,
    "charge_name": "",
    "mana_cost": { "min": 25.0, "max": 18.75 },
    "ene_cost": { "min": 0.0, "max": 0.0 },
    "times_to_cast": 1,
    "aggro_radius": 5,
    "imbues": 0,
    "style": "int",
    "swing_arm": true,
    "slows_when_casting": false,
    "summonType": "NONE",
    "summon_basic_atk": "",
    "use_support_gems_from": "",
    "tags": { "tags": ["magic", "heal", "buff", "summon", "nature"] },
    "tracking_radius": 5,
    "tracks": "allies"
  },

  "cast_animation": { "id": "staff_ground" },
  "cast_finish_animation": { "id": "cast_finish" },

  "min_lvl": 20,
  "max_lvl": 16,
  "default_lvl": 0,
  "statsForSkillGem": [],
  "manual_tip": true,
  "effect_tip": "",
  "disabled_dims": [],
  "show_other_spell_tooltip": "",
  "lvl_based_on_spell": ""
}
```

**ExileEffect**:
```java
ExileEffectBuilder.of(SAKURA_BLESSING)
    .stat(15, 15, OffenseStats.ATTACK_SPEED.get(), ModType.FLAT)
    .stat(10, 10, SpellChangeStats.CAST_SPEED.get(), ModType.FLAT)
    .maxStacks(1)
    .addTags(EffectTags.positive)
    .build();
```

**SpellCalc**:
```java
public static SpellCalcData SAKURA_BLOOM = new SpellCalcData("sakura_bloom", 5, 50);
```

---

### 6. **Nature's Blessing（自然の加護）**

**役割**: スタック型復活バフ（3回まで死亡を防ぐ）

```json
{
  "identifier": "natures_blessing",
  "loc_name": "Nature's Blessing",
  "weight": 1000,

  "attached": {
    "on_cast": [
      {
        "acts": [
          {
            "type": "sound",
            "map": {
              "sound": "minecraft:item.totem.use",
              "volume": 1.0,
              "pitch": 1.0
            }
          }
        ],
        "ifs": [{ "type": "on_spell_cast", "map": {} }],
        "targets": [],
        "en_preds": []
      },
      {
        "acts": [
          {
            "type": "particles_in_radius",
            "map": {
              "particle_type": "minecraft:totem_of_undying",
              "radius": 2.0,
              "particle_count": 100.0,
              "motion": "Random",
              "shape": "SPHERE",
              "height": 1.5
            }
          }
        ],
        "ifs": [{ "type": "on_spell_cast", "map": {} }],
        "targets": [],
        "en_preds": []
      },
      {
        "acts": [
          {
            "type": "particles_in_radius",
            "map": {
              "particle_type": "minecraft:heart",
              "radius": 2.0,
              "particle_count": 30.0,
              "motion": "Random",
              "shape": "CIRCLE_2D",
              "height": 1.0
            }
          }
        ],
        "ifs": [{ "type": "on_spell_cast", "map": {} }],
        "targets": [],
        "en_preds": []
      },
      {
        "acts": [
          {
            "type": "exile_effect",
            "map": {
              "exile_potion_id": "natures_blessing",
              "potion_duration": 1800,
              "effect_stacks": 1
            }
          }
        ],
        "ifs": [{ "type": "on_spell_cast", "map": {} }],
        "targets": [
          {
            "type": "aoe",
            "map": {
              "radius": 10.0,
              "selection_type": "RADIUS",
              "en_predicate": "allies"
            }
          }
        ],
        "en_preds": []
      }
    ],
    "entity_components": {}
  },

  "config": {
    "cast_time_ticks": 40,
    "cooldown_ticks": 2400,
    "castingWeapon": "ANY_WEAPON",
    "charges": 0,
    "charge_regen": 0,
    "charge_name": "",
    "mana_cost": { "min": 40.0, "max": 30.0 },
    "ene_cost": { "min": 0.0, "max": 0.0 },
    "times_to_cast": 1,
    "aggro_radius": 5,
    "imbues": 0,
    "style": "int",
    "swing_arm": true,
    "slows_when_casting": true,
    "summonType": "NONE",
    "summon_basic_atk": "",
    "use_support_gems_from": "",
    "tags": { "tags": ["magic", "buff", "summon", "nature"] },
    "tracking_radius": 5,
    "tracks": "allies"
  },

  "cast_animation": { "id": "staff_cast_loop" },
  "cast_finish_animation": { "id": "staff_cast_shoot" },

  "min_lvl": 30,
  "max_lvl": 16,
  "default_lvl": 0,
  "statsForSkillGem": [],
  "manual_tip": true,
  "effect_tip": "",
  "disabled_dims": [],
  "show_other_spell_tooltip": "",
  "lvl_based_on_spell": ""
}
```

**ExileEffect**:
```java
public static EffectCtx NATURES_BLESSING = new EffectCtx("natures_blessing",
    "Nature's Blessing", Elements.Physical, EffectType.beneficial);

ExileEffectBuilder.of(NATURES_BLESSING)
    .maxStacks(3)
    .desc("Grants 3 stacks of nature's protection. Each stack prevents death once, restoring 50% HP and consuming one stack.")
    .addTags(EffectTags.positive)
    .build();
```

**イベントハンドラー実装**:
```java
@SubscribeEvent
public static void onLivingDeath(LivingDeathEvent event) {
    LivingEntity entity = event.getEntity();

    if (entity.hasEffect(ModEffects.NATURES_BLESSING.getEffect())) {
        EffectInstance effect = entity.getEffect(ModEffects.NATURES_BLESSING.getEffect());
        
        if (effect.getAmplifier() >= 0) { // スタックがある
            event.setCanceled(true); // 死亡キャンセル

            // HP 50%で復活
            float maxHealth = entity.getMaxHealth();
            entity.setHealth(maxHealth * 0.5f);

            // スタックを1つ消費
            if (effect.getAmplifier() > 0) {
                // スタックが残っている場合
                entity.removeEffect(effect.getEffect());
                entity.addEffect(new EffectInstance(effect.getEffect(), 
                    effect.getDuration(), effect.getAmplifier() - 1));
            } else {
                // 最後のスタックの場合
                entity.removeEffect(effect.getEffect());
            }

            // 無敵2秒付与
            entity.addEffect(new MobEffectInstance(
                MobEffects.DAMAGE_RESISTANCE, 40, 255, false, false
            ));

            // 移動速度バフ（3秒）
            entity.addEffect(new MobEffectInstance(
                MobEffects.MOVEMENT_SPEED, 60, 1, false, true
            ));

            // 復活パーティクル
            if (entity.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(
                    ParticleTypes.EXPLOSION,
                    entity.getX(), entity.getY() + 1, entity.getZ(),
                    20, 0.5, 0.5, 0.5, 0.1
                );
                serverLevel.sendParticles(
                    ParticleTypes.SOUL,
                    entity.getX(), entity.getY() + 1, entity.getZ(),
                    30, 1.0, 1.0, 1.0, 0.05
                );
                serverLevel.sendParticles(
                    ParticleTypes.GLOW,
                    entity.getX(), entity.getY() + 1, entity.getZ(),
                    50, 1.5, 1.5, 1.5, 0.1
                );
            }

            // サウンド
            entity.level().playSound(
                null, entity.blockPosition(),
                SoundEvents.WOLF_HOWL,
                SoundSource.PLAYERS, 1.5f, 1.0f
            );
            entity.level().playSound(
                null, entity.blockPosition(),
                SoundEvents.RAVAGER_ROAR,
                SoundSource.PLAYERS, 1.0f, 0.8f
            );
        }
    }
}
```

---

## パッシブスキル（8種）

### Java実装（DruidPassives.java）

```java
package com.robertx22.mine_and_slash.aoe_data.database.perks;

import com.robertx22.library_of_exile.registry.ExileRegistryInit;
import com.robertx22.mine_and_slash.aoe_data.database.stats.OffenseStats;
import com.robertx22.mine_and_slash.aoe_data.database.stats.SpellChangeStats;
import com.robertx22.mine_and_slash.aoe_data.database.stats.DefenseStats;
import com.robertx22.mine_and_slash.database.OptScaleExactStat;
import com.robertx22.mine_and_slash.database.data.stats.types.generated.ElementalDamage;
import com.robertx22.mine_and_slash.database.data.stats.types.defense.Armor;
import com.robertx22.mine_and_slash.database.data.stats.types.resources.health.Health;
import com.robertx22.mine_and_slash.database.data.stats.types.resources.mana.ManaRegen;
import com.robertx22.mine_and_slash.uncommon.enumclasses.Elements;
import com.robertx22.mine_and_slash.uncommon.enumclasses.ModType;

public class DruidPassives implements ExileRegistryInit {

    // Row 0: 基礎ステータス
    public static String HEALTH_DRUID = "p_health_druid";
    public static String MANA_REGEN_DRUID = "p_mana_regen_druid";

    // Row 1: 攻撃系
    public static String NATURE_DAMAGE = "p_nature_damage";
    public static String DOT_DAMAGE_DRUID = "p_dot_dmg_druid";

    // Row 2: サポート系
    public static String HEAL_STR_DRUID = "p_heal_str_druid";
    public static String EFFECT_DURATION = "p_effect_dur_druid";

    // Row 3: 防御系
    public static String ARMOR_BONUS_DRUID = "p_armor_druid";
    public static String DEBUFF_RESIST = "p_debuff_resist_druid";

    @Override
    public void registerAll() {
        // Row 0: 基礎ステータス
        PerkBuilder.passive(HEALTH_DRUID, 8,
            new OptScaleExactStat(4, Health.getInstance(), ModType.PERCENT));

        PerkBuilder.passive(MANA_REGEN_DRUID, 8,
            new OptScaleExactStat(5, ManaRegen.getInstance(), ModType.PERCENT));

        // Row 1: 攻撃系
        PerkBuilder.passive(NATURE_DAMAGE, 8,
            new OptScaleExactStat(3, AllLightningDamage.getInstance(), ModType.FLAT));

        PerkBuilder.passive(DOT_DAMAGE_DRUID, 8,
            new OptScaleExactStat(4, DotDamage.getInstance(), ModType.FLAT));

        // Row 2: サポート系
        PerkBuilder.passive(HEAL_STR_DRUID, 8,
            new OptScaleExactStat(3, IncreaseHealing.getInstance(), ModType.FLAT));

        PerkBuilder.passive(EFFECT_DURATION, 8,
            new OptScaleExactStat(5, EffectDurationUCast.getInstance(), ModType.FLAT));

        // Row 3: 防御系
        PerkBuilder.passive(ARMOR_BONUS_DRUID, 8,
            new OptScaleExactStat(3, Armor.getInstance(), ModType.PERCENT));

        PerkBuilder.passive(DEBUFF_RESIST, 8,
            new OptScaleExactStat(4, AllDmgReduction.getInstance(), ModType.FLAT));
    }
}
```

### パッシブ一覧表

| パッシブ名            | ID                      | 効果/Lv                | 最大値（8Lv） | 相性スキル                       |
| --------------------- | ----------------------- | ---------------------- | ------------- | -------------------------------- |
| **Health Boost**      | `p_health_druid`        | +4% HP                 | +32% HP       | Barkskin, Nature's Blessing      |
| **Mana Regeneration** | `p_mana_regen_druid`    | +5% マナリジェネ       | +40%          | 全スキル                         |
| **Nature Damage**     | `p_nature_damage`       | +3% 雷ダメージ         | +24%          | Entangling Thorns                |
| **DoT Damage**        | `p_dot_dmg_druid`       | +4% DoTダメージ        | +32%          | Entangling Thorns, Weakness Aura |
| **Healing Strength**  | `p_heal_str_druid`      | +3% 治癒力             | +24%          | Bloom Field, Cleanse             |
| **Effect Duration**   | `p_effect_dur_druid`    | +5% エフェクト持続時間 | +40%          | 全バフ/デバフ                    |
| **Armor Bonus**       | `p_armor_druid`         | +3% アーマー           | +24%          | Barkskin                         |
| **Debuff Resistance** | `p_debuff_resist_druid` | +4% デバフ耐性         | +32%          | 生存力                           |

---

## スキルシナジー

### 防御シナジー

```
Barkskin（ダメージ-70%）
  ↓
Armor Bonus（パッシブ +24%）
  ↓
Nature's Blessing（復活保険×3）
```

### 攻撃シナジー

```
Weakness Aura（防御-30%）
  ↓
Entangling Thorns（拘束 + DoT）
  ↓
DoT Damage（パッシブ +32%）+ Nature Damage（+24%）
```

### サポートシナジー

```
Bloom Field（範囲回復 + 攻撃速度バフ）
  ↓
Healing Strength（パッシブ +24%）
  ↓
Cleanse（デバフ解除 + 無効化）
  ↓
Effect Duration（パッシブ +40%）
```

### CC + リソースシナジー

```
Entangling Thorns（拘束 + マナ吸収 2%/秒）
  ↓
Mana Regen（パッシブ +40%）
  ↓
マナ潤沢でスキル連打可能
```

---

## ExileEffect実装詳細

### ModEffects.java に追加

```java
public class ModEffects implements ExileRegistryInit {

    // 既存のエフェクト...

    // ドルイド専用エフェクト
    public static EffectCtx BARKSKIN = new EffectCtx("barkskin",
        "Barkskin", Elements.Physical, EffectType.beneficial);
    public static EffectCtx CLEANSED = new EffectCtx("cleansed",
        "Cleansed", Elements.Physical, EffectType.beneficial);
    public static EffectCtx WEAKNESS = new EffectCtx("weakness",
        "Weakness", Elements.Nature, EffectType.negative);
    public static EffectCtx ENTANGLING_THORNS = new EffectCtx("entangling_thorns",
        "Entangling Thorns", Elements.Nature, EffectType.negative);
    public static EffectCtx BLOOM_BLESSING = new EffectCtx("bloom_blessing",
        "Bloom Blessing", Elements.Physical, EffectType.beneficial);
    public static EffectCtx NATURES_BLESSING = new EffectCtx("natures_blessing",
        "Nature's Blessing", Elements.Physical, EffectType.beneficial);

    @Override
    public void registerAll() {
        // 既存のエフェクトビルド...

        // Barkskin
        ExileEffectBuilder.of(BARKSKIN)
            .stat(-70, -70, DefenseStats.DAMAGE_RECEIVED.get(), ModType.FLAT)
            .stat(4, 4, HealthRegen.getInstance(), ModType.FLAT)
            .vanillaStat(VanillaStatData.create(MOVEMENT_SPEED, -0.2F, ModType.FLAT,
                UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890")))
            .spell(SpellBuilder.forEffect()
                .onTick(PartBuilder.aoeParticles(ParticleTypes.COMPOSTER, 5D, 1.5D)
                    .tick(20D))
                .buildForEffect())
            .maxStacks(1)
            .addTags(EffectTags.positive)
            .build();

        // Cleansed
        ExileEffectBuilder.of(CLEANSED)
            .stat(50, 50, new AilmentResistance(Ailments.ALL), ModType.FLAT)
            .stat(50, 50, DefenseStats.EFFECT_RESISTANCE.get(), ModType.FLAT)
            .spell(SpellBuilder.forEffect()
                .onTick(PartBuilder.aoeParticles(ParticleTypes.ENCHANT, 3D, 1.0D)
                    .tick(10D))
                .buildForEffect())
            .maxStacks(1)
            .addTags(EffectTags.positive)
            .build();

        // Weakness
        ExileEffectBuilder.of(WEAKNESS)
            .stat(-30, -30, Armor.getInstance(), ModType.MORE)
            .stat(-20, -20, Armor.getInstance(), ModType.FLAT)
            .stat(-15, -15, new ElementalResist(Elements.Nature), ModType.FLAT)
            .spell(SpellBuilder.forEffect()
                .onTick(PartBuilder.aoeParticles(ParticleTypes.SPORE_BLOSSOM_AIR, 5D, 0.5D)
                    .tick(20D))
                .buildForEffect())
            .maxStacks(1)
            .addTags(EffectTags.negative)
            .build();

        // Entangling Thorns
        ExileEffectBuilder.of(ENTANGLING_THORNS)
            .vanillaStat(VanillaStatData.create(MOVEMENT_SPEED, -0.8F, ModType.FLAT,
                UUID.fromString("b2c0d4e5-f6a7-8901-bcde-f12345678901")))
            .spell(SpellBuilder.forEffect()
                // マナスティール
                .onTick(PartBuilder.justAction(SpellAction.RESTORE_MANA.create(2D))
                    .addTarget(TargetSelector.SPELL_OWNER.create())
                    .tick(20D))
                // 自然DoT
                .onTick(PartBuilder.damage(SpellCalcs.ENTANGLING_THORNS_DOT, Elements.Nature)
                    .tick(20D))
                // パーティクル
                .onTick(PartBuilder.aoeParticles(ParticleTypes.MYCELIUM, 3D, 0.5D)
                    .tick(10D))
                .buildForEffect())
            .maxStacks(1)
            .addTags(EffectTags.negative)
            .build();

        // Bloom Blessing
        ExileEffectBuilder.of(BLOOM_BLESSING)
            .stat(15, 15, OffenseStats.ATTACK_SPEED.get(), ModType.FLAT)
            .stat(10, 10, SpellChangeStats.CAST_SPEED.get(), ModType.FLAT)
            .maxStacks(1)
            .addTags(EffectTags.positive)
            .build();

        // 自然の加護
        ExileEffectBuilder.of(NATURES_BLESSING)
            .maxStacks(3)
            .desc("Grants 3 stacks of nature's protection. Each stack prevents death once, restoring 50% HP and consuming one stack.")
            .addTags(EffectTags.positive)
            .build();
    }
}
```

### SpellCalcs.java に追加

```java
public class SpellCalcs {
    // 既存の計算式...

    // ドルイド専用ダメージ計算
    public static SpellCalcData ENTANGLING_THORNS_DOT = new SpellCalcData("entangling_thorns_dot", 1, 25);
    public static SpellCalcData BLOOM_FIELD = new SpellCalcData("bloom_field", 5, 50);
}
```

---

## 実装チェックリスト

### Phase 1: ExileEffect実装
- [ ] `ModEffects.java`に6つのエフェクト追加
- [ ] `SpellCalcs.java`に2つの計算式追加
- [ ] エフェクトのビルドとテスト

### Phase 2: パッシブスキル実装
- [ ] `DruidPassives.java`作成
- [ ] 8つのパッシブ実装
- [ ] パッシブツリーへの登録

### Phase 3: アクティブスキル実装（優先順位順）
- [ ] **優先度A**: Barkskin（防御バフ）
- [ ] **優先度A**: Nature's Blessing（復活）+ イベントハンドラー
- [ ] **優先度A**: Entangling Thorns（CC + マナ吸収）
- [ ] **優先度B**: Cleanse（デバフ解除）
- [ ] **優先度B**: Bloom Field（範囲回復）
- [ ] **優先度C**: Weakness Aura（防御デバフ）

### Phase 4: JSON生成
- [ ] `barkskin.json`
- [ ] `cleanse.json`
- [ ] `weakness_aura.json`
- [ ] `entangling_thorns.json`
- [ ] `bloom_field.json`
- [ ] `natures_blessing.json`

### Phase 5: テスト
- [ ] 各スキルの動作確認
- [ ] エフェクトの持続時間確認
- [ ] パーティクル・サウンドの確認
- [ ] マナコストのバランス調整
- [ ] Nature's Blessingの復活処理テスト
- [ ] シナジーテスト

### Phase 6: バランス調整
- [ ] ダメージ値の調整
- [ ] 回復量の調整
- [ ] クールダウンの調整
- [ ] マナコストの調整
- [ ] エフェクト持続時間の調整

---

## 既存スキルとの差別化まとめ

| 既存スキル        | ドルイドスキル    | 差別化ポイント                                       |
| ----------------- | ----------------- | ---------------------------------------------------- |
| Entangling Seed   | Entangling Thorns | **マナ吸収2%/秒**、より強力な移動制限（-80%）        |
| Thorn Bush        | Weakness Aura     | DoTなし、**防御力大幅ダウン（-30% MORE）**           |
| Circle of Healing | Bloom Field       | 回復＋**攻撃速度＋キャスト速度バフ**、桜エフェクト   |
| Rejuvenation      | Cleanse           | デバフ解除＋**状態異常無効化50%**                    |
| Undying Will      | Nature's Blessing | **3スタック復活（HP 50%）＋無敵2秒**、スタック消費型 |
| Frost Lich Armor  | Barkskin          | 物理特化、**移動速度-20%ペナルティ**                 |

---

**更新履歴**:
- 2025-11-06: 初版作成
