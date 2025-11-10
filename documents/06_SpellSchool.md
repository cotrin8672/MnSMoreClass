# Mine and Slash SpellSchool システム（クラス作成）

**最終更新**: 2025年11月10日
**対象バージョン**: 1.20 Forge

---

## 目次

1. [SpellSchool概要](#spellschool概要)
2. [SchoolBuilder](#schoolbuilder)
3. [PointData（配置）](#pointdata配置)
4. [実装パターン](#実装パターン)
5. [完全な例](#完全な例)

---

## SpellSchool概要

**SpellSchool**はMine and Slashのクラスシステム。スペルツリーを定義する。

### SpellSchoolの役割

- **クラス定義**: Druid, Necromancer等
- **ツリー構造**: スペルとパッシブの配置
- **進行システム**: レベルアップでポイント獲得

### ツリーサイズ

```java
SpellSchool.MAX_X_ROWS = 10  // 横10列
SpellSchool.MAX_Y_ROWS = 20  // 縦20行
```

---

## SchoolBuilder

### 基本構文

```kotlin
SchoolBuilder.of(id: String, name: String)
    .add(perkId: String, point: PointData)
    .add(perkId: String, point: PointData)
    ...
    .build()
```

### パラメータ

- **id**: SpellSchoolの一意識別子（例: "druid"）
- **name**: 表示名（翻訳キーで上書き可能）
- **perkId**: 配置するPerkのID
- **point**: ツリー上の座標

---

## PointData（配置）

Perkのツリー上の位置を定義。

### コンストラクタ

```kotlin
PointData(
    x: Int,  // 横位置（0-9）
    y: Int   // 縦位置（0-19）
)
```

### 座標系

```
(0,0) ─────────────► (9,0)
  │
  │    ◯ Perk
  │       │
  │       ◯ Perk
  │           │
  │           ◯ Perk
  │
  ▼
(0,19) ────────────► (9,19)
```

### 制約

```kotlin
Preconditions.checkArgument(SpellSchool.MAX_X_ROWS >= point.x && point.x > -1)
Preconditions.checkArgument(SpellSchool.MAX_Y_ROWS >= point.y && point.y > -1)
Preconditions.checkArgument(school.perks.values().stream().noneMatch(x -> x.equals(point)))
```

- **X**: 0-9の範囲
- **Y**: 0-19の範囲
- **重複不可**: 同じ座標に複数Perk不可

---

## 実装パターン

### パターン1: シンプルなツリー

```kotlin
SchoolBuilder.of("warrior", "Warrior")
    .add("str_boost", PointData(5, 0))      // 中央上
    .add("heavy_strike", PointData(5, 2))   // その下にスキル
    .add("p_damage", PointData(5, 4))       // さらに下にパッシブ
    .build()
```

### パターン2: 分岐ツリー

```kotlin
SchoolBuilder.of("mage", "Mage")
    .add("start", PointData(5, 0))          // スタート地点

    // 左ブランチ（火）
    .add("fireball", PointData(3, 2))
    .add("p_fire_dmg", PointData(3, 4))

    // 右ブランチ（氷）
    .add("frostbolt", PointData(7, 2))
    .add("p_ice_dmg", PointData(7, 4))

    .build()
```

### パターン3: 複雑なツリー（Druid）

```kotlin
SchoolBuilder.of("druid", "Druid")
    // 開始ポイント
    .add("asc_point_0", PointData(5, 0))

    // 第1層：スペル
    .add("barkskin", PointData(3, 2))
    .add("purification", PointData(7, 2))

    // 第2層：パッシブ
    .add("p_health_druid", PointData(2, 4))
    .add("p_mana_regen_druid", PointData(5, 4))
    .add("p_nature_damage", PointData(8, 4))

    // 第3層：高度なスキル
    .add("entangling_thorns", PointData(4, 6))
    .add("weakness_aura", PointData(6, 6))

    // 第4層：パッシブ強化
    .add("p_dot_dmg_druid", PointData(3, 8))
    .add("p_heal_str_druid", PointData(7, 8))

    // 第5層：最終スキル
    .add("sakura_bloom", PointData(4, 10))
    .add("natures_blessing", PointData(6, 10))

    .build()
```

---

## レイアウトデザイン

### 縦方向の進行

```
Y=0   [Start Point]
        │
Y=2   [Spell 1]  [Spell 2]
        │          │
Y=4   [Passive]  [Passive]
        │          │
Y=6   [Advanced Spell]
        │
Y=8   [Passive]
        │
Y=10  [Ultimate]
```

### 横方向の分岐

```
       X=3      X=5      X=7
Y=0            [Start]
                 │
Y=2    [Fire]───┴───[Ice]
         │             │
Y=4   [Passive]    [Passive]
```

### 推奨レイアウト

1. **Y=0-2**: スタート + 初級スペル
2. **Y=3-6**: パッシブ + 中級スペル
3. **Y=7-10**: 高度なスペル + 強化パッシブ
4. **Y=11-15**: 上級スペル
5. **Y=16-19**: アルティメット

---

## 完全な例

### Druid SpellSchool

```kotlin
object SpellSchoolRegistry : IMnsRegistry<SpellSchool> by MnsRegistryDelegate() {
    const val DRUID = "druid"

    fun register() {
        SchoolBuilder.of(DRUID, "Druid")
            // === 開始ポイント ===
            .add("asc_point_0", PointData(5, 0))

            // === 第1層: 基礎スペル (Y=2) ===
            .add("barkskin", PointData(3, 2))           // 左: 防御バフ
            .add("purification", PointData(7, 2))       // 右: デバフ解除

            // === 第2層: 基礎パッシブ (Y=4) ===
            .add("p_health_druid", PointData(2, 4))     // 体力
            .add("p_mana_regen_druid", PointData(5, 4)) // マナリジェネ
            .add("p_nature_damage", PointData(8, 4))    // 自然ダメージ

            // === 第3層: 中級スペル (Y=6) ===
            .add("entangling_thorns", PointData(4, 6))  // CC
            .add("weakness_aura", PointData(6, 6))      // デバフ

            // === 第4層: 強化パッシブ (Y=8) ===
            .add("p_dot_dmg_druid", PointData(3, 8))    // DoTダメージ
            .add("p_heal_str_druid", PointData(7, 8))   // 回復力
            .add("p_effect_dur_druid", PointData(5, 8)) // 効果時間

            // === 第5層: 高度なスペル (Y=10) ===
            .add("sakura_bloom", PointData(4, 10))      // 範囲回復

            // === 第6層: 防御パッシブ (Y=12) ===
            .add("p_armor_druid", PointData(3, 12))     // 防御力
            .add("p_debuff_resist_druid", PointData(7, 12)) // デバフ耐性

            // === 第7層: アルティメット (Y=14) ===
            .add("natures_blessing", PointData(5, 14))  // 復活スキル

            .build()
            .add()
    }
}

// 拡張関数
fun SpellSchool.add() {
    SpellSchoolRegistry.add(this)
    ExileDB.SpellSchools().addSerializable(this.id, this)
}
```

---

## ビジュアルツリー例

### Druid SpellSchool ビジュアル

```
        X: 0  1  2  3  4  5  6  7  8  9
Y:0                    [S]              Start Point
                        │
Y:2              [BS]───┴───[PU]        Basic Spells
                   │         │
Y:4         [PH]───┴───[MR]──┴───[ND]   Passives
                        │
Y:6                 [ET]─┴─[WA]         CC Spells
                      │     │
Y:8              [DD]──┴───[EF]───[HS]  Enhanced Passives
                           │
Y:10                   [SB]─┘            AOE Heal
                           │
Y:12               [AR]────┴────[DR]    Defense Passives
                           │
Y:14                      [NB]           Ultimate

凡例:
[S]  = Start (asc_point_0)
[BS] = Barkskin
[PU] = Purification
[PH] = Health Boost
[MR] = Mana Regen
[ND] = Nature Damage
[ET] = Entangling Thorns
[WA] = Weakness Aura
[DD] = DoT Damage
[EF] = Effect Duration
[HS] = Heal Strength
[SB] = Sakura Bloom
[AR] = Armor
[DR] = Debuff Resist
[NB] = Nature's Blessing
```

---

## 翻訳

### 翻訳キー

```json
{
  "mmorpg.asc_class.<school_id>": "Class Name"
}
```

### 例

**en_us.json**:
```json
{
  "mmorpg.asc_class.druid": "Druid"
}
```

**ja_jp.json**:
```json
{
  "mmorpg.asc_class.druid": "ドルイド"
}
```

---

## データ生成

### SpellSchoolRegistry

```kotlin
object SpellSchoolRegistry : IMnsRegistry<SpellSchool> by MnsRegistryDelegate() {
    fun register() {
        SchoolBuilder.of("druid", "Druid")
            .add(...)
            .build()
            .add()
    }
}

fun SpellSchool.add() {
    SpellSchoolRegistry.add(this)
    ExileDB.SpellSchools().addSerializable(this.id, this)
}
```

### DataProvider

```kotlin
@SubscribeEvent
fun gatherData(event: GatherDataEvent) {
    SpellSchoolRegistry.register()

    event.generator.addProvider(
        event.includeServer(),
        MnsDataProvider(
            output,
            SpellSchoolRegistry.getAll(),
            "mmorpg_spell_school"
        ) { school ->
            ResourceLocation.fromNamespaceAndPath(SlashRef.MODID, school.id)
        }
    )
}
```

---

## ベストプラクティス

### ✅ DO

1. **中央配置**: スタート地点は`(5, 0)`
2. **段階的進行**: Y座標で難易度を表現
3. **分岐構造**: 複数のビルドパスを提供
4. **バランス**: スペル:パッシブ = 6:8程度
5. **視覚的連携**: 関連するPerkは近くに配置

### ❌ DON'T

1. **重複配置**: 同じ座標に複数Perk
2. **飛び飛び**: 連続性のないツリー
3. **偏り**: 左右どちらかに集中
4. **深すぎ**: Y>15は避ける（見にくい）

---

## トラブルシューティング

### ❌ 座標エラー

**エラー**: `IllegalArgumentException: X out of range`

**原因**: X座標が0-9の範囲外

**解決**: `PointData(x, y)`のx値を確認

### ❌ 重複配置エラー

**エラー**: `IllegalArgumentException: Duplicate point`

**原因**: 同じ座標に複数Perk

**解決**: 各座標は一意にする

### ❌ Perkが見つからない

**エラー**: `Perk not found: xxx`

**原因**: 指定したPerkIDが未登録

**解決**: PerkRegistry.register()を先に実行

---

## 次のドキュメント

- [05_PerkSystem.md](./05_PerkSystem.md) - Perkシステム
- [07_CompleteExample.md](./07_CompleteExample.md) - 完全な実装例
