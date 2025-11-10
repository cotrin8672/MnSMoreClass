# Mine and Slash Perkシステム（パッシブスキル）

**最終更新**: 2025年11月10日
**対象バージョン**: 1.20 Forge

---

## 目次

1. [Perk概要](#perk概要)
2. [PerkBuilder](#perkbuilder)
3. [Perk種類](#perk種類)
4. [OptScaleExactStat](#optscaleexactstat)
5. [実装パターン](#実装パターン)
6. [アイコン管理](#アイコン管理)

---

## Perk概要

**Perk**はMine and Slashのパッシブスキルシステム。スペルツリーに配置される。

### Perkの役割

- **パッシブ効果**: Stat増加、特殊効果
- **スペル習得**: スペルをアンロック
- **ゲームチェンジャー**: 大きなゲームプレイ変更

### PerkType

```java
Perk.PerkType.STAT    // 通常のStat Perk
Perk.PerkType.MAJOR   // Major Perk（大型）
```

---

## PerkBuilder

### 基本メソッド

#### 1. `PerkBuilder.spell()` - スペル習得Perk

```kotlin
PerkBuilder.spell(spellId: String): Perk
```

**用途**: スペルをアンロック

**例**:
```kotlin
PerkBuilder.spell("fireball")
// → "fireball"スペルを習得するPerk
```

**特徴**:
- スペルのアイコンを自動使用
- `max_lvls`はスペルの最大レベルと同じ
- `LearnSpellStat`が自動付与

---

#### 2. `PerkBuilder.passive()` - パッシブPerk

```kotlin
PerkBuilder.passive(
    id: String,
    maxLvl: Int,
    stats: OptScaleExactStat...
): Perk
```

**用途**: Statを増加させるパッシブ

**例**:
```kotlin
PerkBuilder.passive(
    "p_health_druid",
    8,  // 最大8レベル
    OptScaleExactStat(4, Stats.MAX_HEALTH, ModType.PERCENT)  // +4%/Lv
)
// → 最大8レベル、1レベルごとに体力+4%
```

**アイコンパス**: `textures/gui/spells/passives/{id}.png`

---

#### 3. `PerkBuilder.ascPoint()` - アセンションポイント

```kotlin
PerkBuilder.ascPoint(
    id: String,
    stats: OptScaleExactStat...
): Perk
```

**用途**: アセンションクラス専用のポイント

**例**:
```kotlin
PerkBuilder.ascPoint(
    "druid_point",
    OptScaleExactStat(1, Stats.ASCENSION_POINT)
)
```

**アイコンパス**: `textures/gui/asc_classes/perk/{id}.png`

---

#### 4. `PerkBuilder.stat()` - Stat Perk

```kotlin
PerkBuilder.stat(
    id: String,
    stats: OptScaleExactStat...
): Perk
```

**用途**: シンプルなStat増加

**例**:
```kotlin
PerkBuilder.stat(
    "str_boost",
    OptScaleExactStat(5, Stats.STRENGTH, ModType.FLAT)
)
```

**アイコン**: 最初のStatのアイコンを自動使用

---

#### 5. `PerkBuilder.gameChanger()` - ゲームチェンジャー

```kotlin
PerkBuilder.gameChanger(
    id: String,
    locname: String,
    stats: OptScaleExactStat...
): Perk
```

**用途**: 大型パッシブ、ゲームプレイを変える

**例**:
```kotlin
PerkBuilder.gameChanger(
    "berserker",
    "Berserker",
    OptScaleExactStat(30, Stats.DAMAGE, ModType.MORE),
    OptScaleExactStat(-20, Armor.getInstance(), ModType.MORE)
)
```

**アイコンパス**: `textures/gui/stat_icons/game_changers/{id}.png`

---

#### 6. `PerkBuilder.bigStat()` - 大型Stat Perk

```kotlin
PerkBuilder.bigStat(
    id: String,
    locname: String,
    stats: OptScaleExactStat...
): Perk
```

**用途**: 通常より大きな値のStat Perk

**例**:
```kotlin
PerkBuilder.bigStat(
    "major_str",
    "Major Strength",
    OptScaleExactStat(10, Stats.STRENGTH, ModType.FLAT)
)
```

**アイコン**: 最初のStatのアイコンを自動使用

**特徴**: `PerkType.SPECIAL`

---

#### 7. `PerkBuilder.socket()` - ジュエルソケット

```kotlin
PerkBuilder.socket(): Perk
```

**用途**: ジュエルを装着できるソケットを追加

**例**:
```kotlin
PerkBuilder.socket()  // パラメータ不要
```

**自動設定**:
- ID: `"jewel_socket"`
- Stat: JewelSocketStat +1
- アイコン: JewelSocketStatのアイコン

---

## OptScaleExactStat

Perkで付与するStatを定義。

### コンストラクタ

```kotlin
OptScaleExactStat(
    value: Number,      // 値（1レベルあたり）
    stat: Stat,         // 対象Stat
    modType: ModType    // FLAT/PERCENT/MORE
)
```

### ModType

```java
ModType.FLAT       // 固定値 (+10)
ModType.PERCENT    // パーセント (+10%)
ModType.MORE       // 乗算 (×1.1)
```

### 例

```kotlin
// 体力 +4%/レベル
OptScaleExactStat(4, Stats.MAX_HEALTH, ModType.PERCENT)

// ダメージ +3固定/レベル
OptScaleExactStat(3, Stats.DAMAGE, ModType.FLAT)

// 防御力 +5% MORE/レベル
OptScaleExactStat(5, Armor.getInstance(), ModType.MORE)
```

### スケーリング

Perkのレベルに応じて値が増加：

```kotlin
// 最大8レベル、1レベルあたり+4%
PerkBuilder.passive("p_health", 8, OptScaleExactStat(4, Stats.MAX_HEALTH, ModType.PERCENT))

// レベル1: +4%
// レベル2: +8%
// ...
// レベル8: +32%
```

---

## 実装パターン

### パターン1: スペル習得Perk

```kotlin
// Barkskinスペルを習得
PerkBuilder.spell("barkskin")
```

**自動設定**:
- ID: `"barkskin"`
- アイコン: スペルのアイコン
- 最大レベル: スペルの最大レベル
- Stat: `LearnSpellStat(barkskin)`

---

### パターン2: 単一Statパッシブ

```kotlin
// 体力 +4%/Lv、最大8レベル
PerkBuilder.passive(
    "p_health_druid",
    8,
    OptScaleExactStat(4, Stats.MAX_HEALTH, ModType.PERCENT)
)
```

---

### パターン3: 複数Statパッシブ

```kotlin
// 雷ダメージ+3% と DoTダメージ+2%
PerkBuilder.passive(
    "p_nature_damage",
    8,
    OptScaleExactStat(3, new ElementalDamage(Elements.Lightning), ModType.PERCENT),
    OptScaleExactStat(2, Stats.DOT_DAMAGE, ModType.PERCENT)
)
```

---

### パターン4: ゲームチェンジャー

```kotlin
// ダメージ+30%、防御-20%のトレードオフ
PerkBuilder.gameChanger(
    "glass_cannon",
    "Glass Cannon",
    OptScaleExactStat(30, Stats.DAMAGE, ModType.MORE),
    OptScaleExactStat(-20, Armor.getInstance(), ModType.MORE)
)
```

---

### パターン5: 複雑なパッシブ（Kotlin）

```kotlin
object DruidPassives {
    fun register() {
        // 体力ブースト
        PerkBuilder.passive(
            "p_health_druid",
            8,
            OptScaleExactStat(4, Stats.MAX_HEALTH, ModType.PERCENT)
        ).add()

        // マナリジェネ
        PerkBuilder.passive(
            "p_mana_regen_druid",
            8,
            OptScaleExactStat(5, Stats.MANA_REGEN, ModType.PERCENT)
        ).add()

        // 自然ダメージ
        PerkBuilder.passive(
            "p_nature_damage",
            8,
            OptScaleExactStat(3, ElementalDamage(Elements.Lightning), ModType.PERCENT)
        ).add()
    }
}

// 拡張関数
fun Perk.add() {
    PerkRegistry.add(this)
    ExileDB.Perks().addSerializable(this.id, this)
}
```

---

## アイコン管理

### アイコンパス

Perkの種類によってアイコンパスが異なる：

| Perk種類 | パス |
|---------|------|
| `spell()` | スペルのアイコンを自動使用 |
| `passive()` | `textures/gui/spells/passives/{id}.png` |
| `ascPoint()` | `textures/gui/asc_classes/perk/{id}.png` |
| `stat()` | Statのアイコンを自動使用 |
| `gameChanger()` | `textures/gui/stat_icons/game_changers/{id}.png` |

### アイコン作成

```
src/main/resources/assets/mmorpg/textures/gui/spells/passives/
├── p_health_druid.png       (32x32)
├── p_mana_regen_druid.png   (32x32)
└── p_nature_damage.png      (32x32)
```

**推奨サイズ**: 32x32ピクセル

---

## 翻訳

### 翻訳キー

```json
{
  "mmorpg.perk.<perk_id>": "Perk Name"
}
```

### 例

**en_us.json**:
```json
{
  "mmorpg.perk.p_health_druid": "Health Boost",
  "mmorpg.perk.p_mana_regen_druid": "Mana Regeneration"
}
```

**ja_jp.json**:
```json
{
  "mmorpg.perk.p_health_druid": "体力ブースト",
  "mmorpg.perk.p_mana_regen_druid": "マナリジェネレーション"
}
```

---

## Kotlinでの実装例

### PerkRegistry

```kotlin
object PerkRegistry : IMnsRegistry<Perk> by MnsRegistryDelegate() {
    const val P_HEALTH_DRUID = "p_health_druid"
    const val P_MANA_REGEN_DRUID = "p_mana_regen_druid"

    fun register() {
        // パッシブ登録
        PerkBuilder.passive(
            P_HEALTH_DRUID,
            8,
            OptScaleExactStat(4, Stats.MAX_HEALTH, ModType.PERCENT)
        ).add()

        PerkBuilder.passive(
            P_MANA_REGEN_DRUID,
            8,
            OptScaleExactStat(5, Stats.MANA_REGEN, ModType.PERCENT)
        ).add()

        // スペル習得Perk
        PerkBuilder.spell("barkskin").add()
        PerkBuilder.spell("purification").add()
    }
}

// 拡張関数
fun Perk.add() {
    PerkRegistry.add(this)
    ExileDB.Perks().addSerializable(this.id, this)
}
```

### データ生成

```kotlin
@SubscribeEvent
fun gatherData(event: GatherDataEvent) {
    PerkRegistry.register()

    event.generator.addProvider(
        event.includeServer(),
        MnsDataProvider(output, PerkRegistry.getAll(), "mmorpg_perk") { perk ->
            ResourceLocation.fromNamespaceAndPath(SlashRef.MODID, perk.id)
        }
    )
}
```

---

## よくある組み合わせ

### ダメージ特化

```kotlin
PerkBuilder.passive("p_damage", 10,
    OptScaleExactStat(3, Stats.DAMAGE, ModType.PERCENT)
)
```

### 防御特化

```kotlin
PerkBuilder.passive("p_armor", 8,
    OptScaleExactStat(3, Armor.getInstance(), ModType.PERCENT),
    OptScaleExactStat(2, new PhysicalResist(), ModType.FLAT)
)
```

### 属性ダメージ

```kotlin
PerkBuilder.passive("p_fire_damage", 10,
    OptScaleExactStat(4, ElementalDamage(Elements.Fire), ModType.PERCENT)
)
```

### リソース回復

```kotlin
PerkBuilder.passive("p_resource_regen", 8,
    OptScaleExactStat(5, Stats.MANA_REGEN, ModType.PERCENT),
    OptScaleExactStat(5, Stats.HEALTH_REGEN, ModType.PERCENT)
)
```

---

## トラブルシューティング

### ❌ Perkが表示されない

**原因**: SpellSchoolに追加していない

**解決**: SchoolBuilderで配置を指定

### ❌ アイコンが表示されない

**原因**: パスが間違っている

**解決**: `textures/gui/spells/passives/{id}.png`を確認

### ❌ Statが効かない

**原因**: ModTypeが間違っている

**解決**: FLAT/PERCENT/MOREを確認

---

## 次のドキュメント

- [06_SpellSchool.md](./06_SpellSchool.md) - SpellSchool（クラス）システム
- [02_SpellSystem.md](./02_SpellSystem.md) - スペルシステム
