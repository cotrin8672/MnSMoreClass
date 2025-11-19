# Mine and Slash ExileEffect システム

**最終更新**: 2025年11月10日
**対象バージョン**: 1.20 Forge

---

## 目次

1. [ExileEffect概要](#exileeffect概要)
2. [ExileEffectBuilder](#exileeffectbuilder)
3. [Stat操作](#stat操作)
4. [VanillaStat操作](#vanillastat操作)
5. [スペル連携](#スペル連携)
6. [実装パターン](#実装パターン)

---

## ExileEffect概要

**ExileEffect**はMine and Slash独自のバフ/デバフシステム。

### バニラポーションとの違い

| 項目           | ExileEffect            | バニラポーション |
| -------------- | ---------------------- | ---------------- |
| **Stat操作**   | Mine and Slash独自Stat | バニラAttribute  |
| **スタック**   | 可能（maxStacks設定）  | 不可             |
| **スペル連携** | 可能（onTick等）       | 不可             |
| **表示**       | カスタムUI             | バニラUI         |
| **柔軟性**     | 非常に高い             | 限定的           |

### EffectType

```java
EffectType.positive   // バフ
EffectType.negative   // デバフ
EffectType.neutral    // どちらでもない
```

### ライフサイクルと内部処理

エフェクトは適用→維持→解除の 3 段階を持ちます。

- **onApply**: バニラ Attribute (`mc_stats`) を適用し、装備キャッシュを更新。`one_of_a_kind_id` が設定されている場合は同系統のエフェクトを競合排除します。@Mine-And-Slash-Rework/src/main/java/com/robertx22/mine_and_slash/database/data/exile_effects/ExileEffect.java#211-235
- **tick**: `EntityStatusEffectsData.tick()` が毎 tick `ticks_left` を減らし、`ExileEffect.onTick()` を呼びます。`onTick` は付随 Spell があれば `SpellCtx.onTick` で実行します。@Mine-And-Slash-Rework/src/main/java/com/robertx22/mine_and_slash/vanilla_mc/potion_effects/EntityStatusEffectsData.java#29-72 @Mine-And-Slash-Rework/src/main/java/com/robertx22/mine_and_slash/database/data/exile_effects/ExileEffect.java#119-138
- **onRemove**: バニラ Attribute を解除し、付随 Spell の `onExpire` を呼び、スタック数をリセットします。@Mine-And-Slash-Rework/src/main/java/com/robertx22/mine_and_slash/database/data/exile_effects/ExileEffect.java#245-265

`EntityStatusEffectsData.tick()` は 80 tick ごとに「スペル再割り当てを失った自キャスト効果」を追加で除去し、`removed` リスト経由で `onRemove` を順次実行します。@Mine-And-Slash-Rework/src/main/java/com/robertx22/mine_and_slash/vanilla_mc/potion_effects/EntityStatusEffectsData.java#47-71

### インスタンスデータの構造

各効果は `ExileEffectInstanceData` にシリアライズされ、以下の情報を保持します。@Mine-And-Slash-Rework/src/main/java/com/robertx22/mine_and_slash/database/data/exile_effects/ExileEffectInstanceData.java#15-79

| フィールド | 役割 |
| --- | --- |
| `ticks_left` | 残り Tick。`is_infinite == true` の場合は無視され UI では ∞ 表示 |
| `stacks` | 現在のスタック数。0 未満で `shouldRemove()` |
| `str_multi` | 効果強度の倍率（パークやタレントから加算） |
| `self_cast` | 自己付与かどうか。スペルの再割り当て確認に利用 |
| `caster_uuid` | 付与者の UUID。`getCaster(Level)` で照会 |
| `spell_id` / `calcSpell` | 関連 Spell を取得し、`onTick`/`onExpire` で使用 |

### スタックとチャージの制御

- 最大スタックは `max_stacks` と `EntityData.maxCharges` の和で決定されます。@Mine-And-Slash-Rework/src/main/java/com/robertx22/mine_and_slash/database/data/exile_effects/ExileEffect.java#49-53
- `stacks_affect_stats = false` を指定すると、スタック数に応じた Stat 増幅が無効化されます。@Mine-And-Slash-Rework/src/main/java/com/robertx22/mine_and_slash/database/data/exile_effects/ExileEffect.java#56-58
- `getExactStats` ではスタック数と `str_multi` を加味して `StatMod` を `ExactStatData` に変換します。カスタム効果の数値調整時はこの処理を理解しておくとデバッグが容易です。@Mine-And-Slash-Rework/src/main/java/com/robertx22/mine_and_slash/database/data/exile_effects/ExileEffect.java#143-168

### 解除条件とタグ連携

- `remove_on_spell_cast` に指定した `SpellTag` を持つスペルをキャストすると、自動的に効果が解除されます。@Mine-And-Slash-Rework/src/main/java/com/robertx22/mine_and_slash/database/data/exile_effects/ExileEffect.java#56-57 @Mine-And-Slash-Rework/src/main/java/com/robertx22/mine_and_slash/aoe_data/database/exile_effects/ExileEffectBuilder.java#70-73
- `EffectTag` は UI 表示だけでなく、`RemoveNegative` などの解除アクションや `ExileEffectUtils.countEffectsWithTag` を使った条件分岐に利用されます。@Mine-And-Slash-Rework/src/main/java/com/robertx22/mine_and_slash/uncommon/utilityclasses/ExileEffectUtils.java#11-24

### UI 資産とツールチップ

- テクスチャは `textures/item/mob_effects/<effect_id>.png` を参照し、`getEffectDisplayItem()` は `ForgeRegistries.ITEMS` から `mob_effects/<effect_id>` を解決します。@Mine-And-Slash-Rework/src/main/java/com/robertx22/mine_and_slash/database/data/exile_effects/ExileEffect.java#80-88
- `GetTooltipString` は Stat 情報、スタック数、タグ一覧を動的に組み立てます。カスタム説明文 (`locdesc`) を追加するときはこれらの行に合わせて表示を確認してください。@Mine-And-Slash-Rework/src/main/java/com/robertx22/mine_and_slash/database/data/exile_effects/ExileEffect.java#172-207

---

## ExileEffectBuilder

### 基本構文

```kotlin
ExileEffectBuilder.of(effectId: String)
    .maxStacks(count: Int)                    // 最大スタック数
    .stat(min, max, stat, modType)            // Stat操作
    .vanillaStat(VanillaStatData)             // バニラAttribute操作
    .spell(Spell)                             // スペル連携
    .addTags(EffectTags...)                   // タグ
    .build()
```

### 例: シンプルなバフ

```kotlin
ExileEffectBuilder.of("strength_boost")
    .maxStacks(1)
    .stat(5, 10, Stats.STRENGTH, ModType.FLAT)  // +5～10 筋力
    .addTags(EffectTags.positive)
    .build()
```

---

## Stat操作

Mine and Slash独自Statの操作。

### `.stat()` メソッド

```kotlin
.stat(
    min: Number,        // Lv1の値
    max: Number,        // Lv20の値
    stat: Stat,         // 対象Stat
    modType: ModType    // 修正タイプ
)
```

### ModType

```java
ModType.FLAT       // 固定値加算 (+10)
ModType.PERCENT    // パーセント加算 (+10%)
ModType.MORE       // 乗算 (×1.1)
```

### 例

```kotlin
// 筋力 +5～10（固定値）
.stat(5, 10, Stats.STRENGTH, ModType.FLAT)

// ダメージ +10～20%（パーセント）
.stat(10, 20, Stats.DAMAGE, ModType.PERCENT)

// 防御力 +20～40%（MORE）
.stat(0.2, 0.4, Armor.getInstance(), ModType.MORE)
```

### スタック型

```kotlin
ExileEffectBuilder.of("power_charge")
    .maxStacks(3)  // 最大3スタック
    .stat(5, 10, Stats.DAMAGE, ModType.FLAT)  // スタックごとに+5～10
    .build()
// 3スタックで +15～30 ダメージ
```

---

## VanillaStat操作

Minecraftバニラの**Attribute**を操作。

### VanillaStatData

```kotlin
VanillaStatData.create(
    attribute: Attribute,      // バニラAttribute
    value: Float,              // 値
    modType: ModType,          // 修正タイプ
    uuid: UUID                 // 一意UUID
)
```

### 利用可能なAttribute

```java
Attributes.MOVEMENT_SPEED         // 移動速度
Attributes.ATTACK_SPEED           // 攻撃速度
Attributes.ATTACK_DAMAGE          // 攻撃力
Attributes.KNOCKBACK_RESISTANCE   // ノックバック耐性
Attributes.MAX_HEALTH             // 最大体力
Attributes.ARMOR                  // 防御力
Attributes.ARMOR_TOUGHNESS        // 防御力強度
Attributes.ATTACK_KNOCKBACK       // 攻撃時ノックバック
```

### 例: 移動速度操作

```kotlin
// 移動速度 +10% (MORE)
.vanillaStat(VanillaStatData.create(
    Attributes.MOVEMENT_SPEED,
    0.1f,  // +10%
    ModType.MORE,
    UUID.fromString("3fb10485-f309-465f-afc6-a23b0d6cf4c1")
))

// 移動速度 -80% (拘束)
.vanillaStat(VanillaStatData.create(
    Attributes.MOVEMENT_SPEED,
    -0.8f,  // -80%
    ModType.MORE,
    UUID.fromString("3fb10485-f309-468f-afc6-a23b0d6cf4c1")
))

// 移動速度 -1000% (完全停止)
.vanillaStat(VanillaStatData.create(
    Attributes.MOVEMENT_SPEED,
    -10.0f,  // -1000%
    ModType.MORE,
    UUID.fromString("3fb10485-f309-468f-afc6-a23b0d6cf4c1")
))
```

### 例: ノックバック耐性

```kotlin
// ノックバック耐性 +100% (完全無効)
.vanillaStat(VanillaStatData.create(
    Attributes.KNOCKBACK_RESISTANCE,
    1.0f,  // +100%
    ModType.FLAT,
    UUID.fromString("yyyyyyyy-yyyy-yyyy-yyyy-yyyyyyyyyyyy")
))
```

### UUID管理

各VanillaStatには**一意のUUID**が必要：

```kotlin
// UUID生成（開発時に1回だけ）
UUID.randomUUID()
// → "3fb10485-f309-465f-afc6-a23b0d6cf4c1"

// 以降はこのUUIDを使い回す
```

**重要**: 同じAttribute + 同じModTypeには**同じUUID**を使う

---

## スペル連携

ExileEffectに**スペル**を組み込むことで複雑な効果を実現。

### `.spell()` メソッド

```kotlin
.spell(
    SpellBuilder.forEffect()  // ExileEffect用スペル
        .onTick(...)           // 毎Tick実行
        .buildForEffect()
)
```

### 例: DoT（継続ダメージ）

```kotlin
ExileEffectBuilder.of("burn")
    .spell(
        SpellBuilder.forEffect()
            .onTick(
                PartBuilder.damage(SpellCalcs.BURN_DOT, Elements.Fire)
                    .tick(20.0)  // 1秒ごと
            )
            .buildForEffect()
    )
    .addTags(EffectTags.negative)
    .build()
```

### 例: HoT（継続回復）

```kotlin
ExileEffectBuilder.of("regeneration")
    .spell(
        SpellBuilder.forEffect()
            .onTick(
                PartBuilder.healCaster(SpellCalcs.REGEN_HEAL)
                    .tick(20.0)  // 1秒ごと
            )
            .buildForEffect()
    )
    .addTags(EffectTags.positive)
    .build()
```

### 例: パーティクル付きバフ

```kotlin
ExileEffectBuilder.of("holy_shield")
    .stat(20, 40, Armor.getInstance(), ModType.MORE)
    .spell(
        SpellBuilder.forEffect()
            .onTick(
                PartBuilder.aoeParticles(ParticleTypes.ENCHANT, 5.0, 1.0)
                    .tick(10.0)  // 0.5秒ごと
            )
            .buildForEffect()
    )
    .addTags(EffectTags.positive)
    .build()
```

---

## 実装パターン

### パターン1: シンプルなStatバフ

```kotlin
ExileEffectBuilder.of("barkskin")
    .maxStacks(1)
    .stat(30, 50, new PhysicalResist(), ModType.FLAT)  // 物理耐性
    .addTags(EffectTags.positive)
    .build()
```

### パターン2: 拘束デバフ

```kotlin
ExileEffectBuilder.of("entangling_thorns")
    // 移動速度 -80%
    .vanillaStat(VanillaStatData.create(
        Attributes.MOVEMENT_SPEED,
        -0.8f,
        ModType.MORE,
        UUID.fromString("3fb10485-f309-468f-afc6-a23b0d6cf4c1")
    ))
    // ノックバック耐性 +100%
    .vanillaStat(VanillaStatData.create(
        Attributes.KNOCKBACK_RESISTANCE,
        1.0f,
        ModType.FLAT,
        UUID.fromString("4fb10485-f309-468f-afc6-a23b0d6cf4c2")
    ))
    .addTags(EffectTags.negative, EffectTags.immobilizing)
    .build()
```

### パターン3: DoT + Stat減少

```kotlin
ExileEffectBuilder.of("weakness")
    // 防御力 -30%
    .stat(-30, -30, Armor.getInstance(), ModType.MORE)
    // 継続ダメージ
    .spell(
        SpellBuilder.forEffect()
            .onTick(
                PartBuilder.damage(SpellCalcs.WEAKNESS_DOT, Elements.Physical)
                    .tick(20.0)
            )
            .onTick(
                PartBuilder.aoeParticles(ParticleTypes.SMOKE, 5.0, 1.0)
                    .tick(20.0)
            )
            .buildForEffect()
    )
    .addTags(EffectTags.negative, EffectTags.curse)
    .build()
```

### パターン4: スタック型バフ

```kotlin
ExileEffectBuilder.of("power_charge")
    .maxStacks(5)  // 最大5スタック
    .stat(3, 5, Stats.DAMAGE, ModType.PERCENT)  // スタックごと +3～5%
    .spell(
        SpellBuilder.forEffect()
            .onTick(
                PartBuilder.aoeParticles(ParticleTypes.ELECTRIC_SPARK, 3.0, 1.0)
                    .tick(10.0)
            )
            .buildForEffect()
    )
    .addTags(EffectTags.positive)
    .build()
// 5スタックで +15～25% ダメージ
```

### パターン5: 複合エフェクト

```kotlin
ExileEffectBuilder.of("berserk")
    .maxStacks(1)
    // 攻撃速度 +30%
    .vanillaStat(VanillaStatData.create(
        Attributes.ATTACK_SPEED,
        0.3f,
        ModType.MORE,
        UUID.fromString("5fb10485-f309-468f-afc6-a23b0d6cf4c3")
    ))
    // ダメージ +20%
    .stat(20, 40, Stats.DAMAGE, ModType.MORE)
    // 防御力 -20%（デメリット）
    .stat(-20, -20, Armor.getInstance(), ModType.MORE)
    // パーティクル
    .spell(
        SpellBuilder.forEffect()
            .onTick(
                PartBuilder.aoeParticles(ParticleTypes.ANGRY_VILLAGER, 5.0, 1.0)
                    .tick(20.0)
            )
            .buildForEffect()
    )
    .addTags(EffectTags.positive)
    .build()
```

### パターン6: 被ダメージトリガー（自傷型デバフ）

```kotlin
ExileEffectBuilder.of("thorn")
    .maxStacks(5)
    .spell(
        SpellBuilder.forEffect()
            .addSpecificAction(
                SpellCtx.ON_ENTITY_ATTACKED,
                PartBuilder.damage(SpellCalcs.THORN_CONSUME, Elements.Physical)
            )
            .addSpecificAction(
                SpellCtx.ON_ENTITY_ATTACKED,
                PartBuilder.removeExileEffectStacksToTarget("thorn")
            )
            .buildForEffect()
    )
    .addTags(EffectTags.negative)
    .build()
```

> **注意**: `target` を明示的に変更しない限り `PartBuilder.damage(...)` はエフェクト保持者自身に適用される。`thorn` は攻撃された際に**自分が追加ダメージを受けつつスタックを消費するデバフ**であり、攻撃者への反射ダメージではない点に注意。

---

## EffectTags

エフェクトの分類タグ。

### 主要タグ

```java
EffectTags.positive       // バフ
EffectTags.negative       // デバフ
EffectTags.song           // ソング系
EffectTags.curse          // カース系
EffectTags.immobilizing   // 移動不可
EffectTags.food           // 食事バフ
```

### 用途

- **UI表示**: バフ/デバフの色分け
- **解除判定**: `REMOVE_NEGATIVE`で`negative`タグのみ解除
- **耐性**: 特定タグへの耐性Stat

---

## デバフ解除

### GiveOrTake.REMOVE_NEGATIVE

`negative`タグのExileEffectを解除：

```kotlin
SpellAction.EXILE_EFFECT.create(
    "",                                    // effectId不要
    ExileEffectAction.GiveOrTake.REMOVE_NEGATIVE,
    1.0                                    // 解除数
)
```

**動作**: ターゲットの全`negative`タグエフェクトを解除

---

## トラブルシューティング

### ❌ エフェクトが適用されない

**原因1**: スペルで`.buildForEffect()`を忘れている

**解決**: `.build()`ではなく`.buildForEffect()`

**原因2**: EffectTypeが未設定

**解決**: `.addTags(EffectTags.positive/negative)`

### ❌ VanillaStatが効かない

**原因**: UUIDが重複している

**解決**: 各Attribute + ModType組み合わせに一意のUUIDを使う

### ❌ スタックが増えない

**原因**: `.maxStacks()`を設定していない

**解決**: `.maxStacks(5)`等を追加

---

## ベストプラクティス

### ✅ DO

1. **タグを必ず付ける**: `positive`/`negative`
2. **UUID管理**: スプレッドシート等で一覧管理
3. **スペル連携活用**: パーティクルやDoTで視覚的フィードバック
4. **MOREとFLATを使い分ける**: 乗算が必要ならMORE

### ❌ DON'T

1. **UUID重複**: 絶対に避ける
2. **タグなし**: UIで正しく表示されない
3. **過剰なonTick**: パフォーマンス低下

---

## 次のドキュメント

- [04_KotlinAddon.md](./04_KotlinAddon.md) - Kotlinアドオン開発
- [05_PartBuilderExtensions.md](./05_PartBuilderExtensions.md) - カスタムヘルパー作成
