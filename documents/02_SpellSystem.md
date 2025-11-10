# Mine and Slash スペルシステム完全ガイド

**最終更新**: 2025年11月10日
**対象バージョン**: 1.20 Forge

---

## 目次

1. [スペルシステム概要](#スペルシステム概要)
2. [SpellBuilder](#spellbuilder)
3. [SpellConfiguration](#spellconfiguration)
4. [ComponentPart](#componentpart)
5. [PartBuilder](#partbuilder)
6. [SpellAction](#spellaction)
7. [TargetSelector](#targetselector)
8. [ValueCalculation](#valuecalculation)
9. [実装パターン](#実装パターン)

---

## スペルシステム概要

Mine and Slashのスペルシステムは以下の要素で構成：

```
Spell
├── SpellConfiguration (設定: マナ、CD、チャージ等)
├── ComponentPart[] (パーツ: onCast, onTick, onExpire等)
│   ├── TargetSelector[] (対象選択)
│   └── SpellAction[] (実行アクション)
└── ValueCalculation (ダメージ/回復計算式)
```

---

## SpellBuilder

### 基本構文

```kotlin
SpellBuilder.of(
    id: String,                    // スペルID（GUID）
    playStyle: PlayStyle,           // STR/DEX/INT
    config: SpellConfiguration,     // 設定
    name: String,                   // 表示名
    tags: List<SpellTag>           // タグ
)
.weaponReq(CastingWeapon)          // 武器要件
.onCast(ComponentPart)              // キャスト時
.onTick(ComponentPart)              // 毎Tick
.onExpire(ComponentPart)            // 終了時
.build()                            // ビルド
```

### 重要メソッド

#### `.buildForEffect()`

**ExileEffectを付与するスキル**では必ず使用：

```kotlin
// ✅ 正しい - ExileEffectを使う場合
SpellBuilder.of(...)
    .onCast(PartBuilder.giveExileEffectToAlliesInRadius(...))
    .buildForEffect()  // ← これを使う
    .add()

// ❌ 間違い - .build()を使うとエラー
SpellBuilder.of(...)
    .onCast(PartBuilder.giveExileEffectToAlliesInRadius(...))
    .build()  // ← エフェクトが正しく登録されない
    .add()
```

#### `.weaponReq()`

武器要件の指定：

```kotlin
.weaponReq(CastingWeapon.MAGE_WEAPON)   // 杖
.weaponReq(CastingWeapon.MELEE_WEAPON)  // 近接武器
.weaponReq(CastingWeapon.RANGED)        // 弓
.weaponReq(CastingWeapon.ANY_WEAPON)    // 任意
```

#### `.manualDesc()`

カスタム説明文：

```kotlin
.manualDesc("Deals ${SpellCalcs.FIREBALL.getLocDmgTooltip()} damage.")
```

---

## SpellConfiguration

### Configuration Types

#### 1. `instant()` - 即時発動

```kotlin
SpellConfiguration.Builder.instant(
    mana: Int,     // マナコスト
    cooldown: Int  // クールダウン（ticks）
)
```

**特徴**:
- キャストタイム0
- マナコストは自動で**100% → 75%**にスケール
- 最も一般的な設定

**例**:
```kotlin
.instant(30, 20 * 5)  // マナ30→22.5, CD 5秒
```

#### 2. `nonInstant()` - キャストタイム付き

```kotlin
SpellConfiguration.Builder.nonInstant(
    mana: Int,
    cooldown: Int,
    castTime: Int  // キャストタイム（ticks）
)
```

**例**:
```kotlin
.nonInstant(50, 20 * 10, 40)  // マナ50, CD 10秒, キャスト2秒
```

#### 3. `multiCast()` - 複数回発動

```kotlin
SpellConfiguration.Builder.multiCast(
    mana: Int,
    cooldown: Int,
    castTime: Int,
    times: Int     // 発動回数
)
```

**動作**: キャストタイム中に複数回発動

**例**:
```kotlin
.multiCast(20, 20 * 5, 20, 3)  // 1秒間に3回発動
```

**制限**: `times`は固定値（レベルスケール不可）

---

### チャージシステム

#### `.setChargesAndRegen()`

スペルに**チャージ**を追加（ブーメラン方式）：

```kotlin
SpellConfiguration.Builder.instant(30, 0)  // cooldownは無視される
    .setChargesAndRegen(
        name: String,  // チャージID
        charges: Int,  // 最大チャージ数
        regenTicks: Int  // リチャージ時間（ticks）
    )
```

**動作**:
1. スペル使用でチャージ消費
2. 時間経過で1チャージずつ回復
3. クールダウンは強制的に**3 ticks**

**例**:
```kotlin
SpellConfiguration.Builder.instant(30, 0)
    .setChargesAndRegen("purification", 3, 20 * 60)
// 3チャージ、60秒で1チャージ回復
```

**注意**: チャージシステムには対応する`ValueCalculation`が必要！

---

### LeveledValue

レベルでスケールする値：

```kotlin
// デフォルト: instant()は自動でマナコストをスケール
config.mana_cost = LeveledValue(100f, 75f)  // Lv1: 100, Lv20: 75

// カスタムスケール
config.mana_cost = LeveledValue(50f, 30f)   // Lv1: 50, Lv20: 30
```

**制限**: 以下はスケール不可（int型）
- `cooldown_ticks`
- `cast_time_ticks`
- `times_to_cast`（multiCast回数）
- `charges`（チャージ数）

---

## ComponentPart

スペルの実行単位。`TargetSelector` + `SpellAction`で構成。

### ライフサイクル

```
onCast    → キャスト時に1回実行
onTick    → 毎Tick実行（20 tick/秒）
onExpire  → 終了時に1回実行
```

### Entity Name System

プロジェクタイルやブロックに**名前**を付けて識別：

```kotlin
// プロジェクタイル召喚時に名前を付ける
.onCast(PartBuilder.justAction(
    SpellAction.SUMMON_AT_SIGHT.create(...)
        .put(MapField.ENTITY_NAME, "my_projectile")  // ← 名前
))

// 後でその名前のエンティティにイベント
.onExpire("my_projectile", PartBuilder.damageInAoe(...))
```

**利点**: 複数のプロジェクタイルを区別できる

---

## PartBuilder

`ComponentPart`を簡単に構築するヘルパークラス。

### 主要メソッド一覧

#### ダメージ系

```kotlin
// 範囲ダメージ
PartBuilder.damageInAoe(
    calc: ValueCalculation,  // ダメージ計算式
    element: Elements,        // 属性
    radius: Double           // 範囲
)

// 単体ダメージ
PartBuilder.damage(calc, element)
```

#### 回復系

```kotlin
// 範囲回復
PartBuilder.healInAoe(calc, radius)

// 自分回復
PartBuilder.healCaster(calc)
```

#### エフェクト付与

```kotlin
// 範囲バフ（味方）
PartBuilder.giveExileEffectToAlliesInRadius(
    radius: Double,
    effectId: String,
    duration: Double  // ticks
)

// 範囲デバフ（敵）
PartBuilder.addExileEffectToEnemiesInAoe(
    effectId: String,
    radius: Double,
    duration: Double
)

// 自分にバフ
PartBuilder.giveSelfExileEffect(effect, duration)
```

#### デバフ解除

```kotlin
// バニラポーション解除
PartBuilder.removeNegativeEffectInRadius(radius: Double)

// ExileEffect解除 (カスタム実装が必要)
PartBuilder.justAction(
    SpellAction.EXILE_EFFECT.create(
        "",
        ExileEffectAction.GiveOrTake.REMOVE_NEGATIVE,
        1.0
    )
)
```

#### プロジェクタイル

```kotlin
// 視線方向に召喚
PartBuilder.justAction(
    SpellAction.SUMMON_AT_SIGHT.create(
        entityType: EntityType,
        lifespan: Double,  // ticks
        height: Double     // Y軸オフセット
    )
)

// 弾を発射
PartBuilder.justAction(
    SpellAction.SUMMON_PROJECTILE.create(...)
)
```

#### サウンド・パーティクル

```kotlin
// サウンド再生
PartBuilder.playSound(
    sound: SoundEvent,
    volume: Double,
    pitch: Double
)

// 範囲パーティクル
PartBuilder.aoeParticles(
    particle: ParticleOptions,
    count: Double,
    radius: Double
)

// 地面エッジパーティクル
PartBuilder.groundEdgeParticles(
    particle: ParticleOptions,
    count: Double,
    radius: Double,
    height: Double
)
```

#### ターゲット選択

```kotlin
// 範囲選択（敵）
PartBuilder.aoeSelectEnemies(radius, height)

// AOE選択（カスタム）
BaseTargetSelector.AOE.create(
    radius: Double,
    selectionType: EntityFinder.SelectionType,
    allyOrEnemy: AllyOrEnemy
)
```

---

## SpellAction

スペルの実際の処理を行うアクション。

### 主要SpellAction

```java
SpellAction.DEAL_DAMAGE           // ダメージ
SpellAction.RESTORE_HEALTH        // 回復
SpellAction.EXILE_EFFECT          // ExileEffect付与/解除
SpellAction.POTION                // バニラポーション付与/解除
SpellAction.SUMMON_PROJECTILE     // プロジェクタイル召喚
SpellAction.SUMMON_AT_SIGHT       // 視線方向に召喚
SpellAction.SUMMON_BLOCK          // ブロック召喚
SpellAction.PLAY_SOUND            // サウンド再生
SpellAction.PARTICLES_IN_RADIUS   // パーティクル生成
SpellAction.PUSH                  // ノックバック
SpellAction.KNOCKBACK             // ノックバック
SpellAction.TP_CASTER_IN_DIRECTION // テレポート
SpellAction.ADD_CHARGE            // チャージ追加
```

### カスタムSpellAction作成

新しいアクションを追加する場合：

```kotlin
class MyCustomAction : SpellAction(emptyList()) {
    override fun tryActivate(
        targets: Collection<LivingEntity>,
        ctx: SpellCtx,
        data: MapHolder
    ) {
        targets.forEach { entity ->
            // カスタム処理
        }
    }

    fun create(): MapHolder {
        val holder = MapHolder()
        holder.type = GUID()
        return holder
    }

    override fun GUID(): String = "my_custom_action"
}

// 登録
object MySpellActions {
    val MY_ACTION: MyCustomAction = register(MyCustomAction())

    private fun <T : SpellAction> register(action: T): T {
        SpellAction.MAP[action.GUID()] = action
        return action
    }
}
```

---

## TargetSelector

ターゲット選択ロジック。

### 主要セレクター

```java
TargetSelector.CASTER              // キャスター自身
TargetSelector.TARGET              // ターゲット
BaseTargetSelector.AOE             // 範囲選択
BaseTargetSelector.PROJECTILE      // プロジェクタイル経路
```

### AOE選択の詳細

```kotlin
BaseTargetSelector.AOE.create(
    radius: Double,
    selectionType: EntityFinder.SelectionType,  // RADIUS, HEIGHT等
    allyOrEnemy: AllyOrEnemy                    // allies, enemies等
)

// 例: 6ブロック範囲の味方
BaseTargetSelector.AOE.create(6.0, EntityFinder.SelectionType.RADIUS, AllyOrEnemy.allies)
```

---

## ValueCalculation

ダメージ・回復量の計算式。

### 定義

```kotlin
ValueCalcBuilderKt.of("spell_id")
    .spellScaling(min: Float, max: Float)  // レベルスケール
    .capScaling(multiplier: Float)         // キャップスケール
    .build()
    .add()
```

**例**:
```kotlin
ValueCalcBuilderKt.of("fireball")
    .spellScaling(1.0f, 2.5f)  // Lv1: 100%, Lv20: 250%
    .capScaling(1.5f)           // キャップで1.5倍
    .build()
    .add()
```

### チャージスキル用

チャージシステムを使う場合、**空のValueCalculationが必要**：

```kotlin
ValueCalcBuilderKt.of("purification")
    .build()  // 空でもOK
    .add()
```

---

## 実装パターン

### パターン1: シンプルな範囲ダメージ

```kotlin
SpellBuilder.of(
    "fireball",
    PlayStyle.INT,
    SpellConfiguration.Builder.instant(30, 20 * 5),
    "Fireball",
    listOf(SpellTags.projectile, SpellTags.damage, SpellTags.FIRE)
)
.weaponReq(CastingWeapon.MAGE_WEAPON)
.onCast(PartBuilder.damageInAoe(SpellCalcs.FIREBALL, Elements.Fire, 3.0))
.onCast(PartBuilder.playSound(SoundEvents.BLAZE_SHOOT, 1.0, 1.0))
.onCast(PartBuilder.aoeParticles(ParticleTypes.FLAME, 50.0, 3.0))
.build()
.add()
```

### パターン2: プロジェクタイル → 範囲ダメージ

```kotlin
SpellBuilder.of(...)
.onCast(PartBuilder.justAction(
    SpellAction.SUMMON_AT_SIGHT.create(
        SlashEntities.SIMPLE_PROJECTILE.get(),
        1.0,   // 1tick後に消える
        0.0
    )
))
.onExpire(PartBuilder.damageInAoe(SpellCalcs.METEOR, Elements.Fire, 4.0))
.onExpire(PartBuilder.aoeParticles(ParticleTypes.EXPLOSION, 20.0, 4.0))
.build()
.add()
```

### パターン3: バフ付与（ExileEffect）

```kotlin
SpellBuilder.of(...)
.onCast(PartBuilder.giveExileEffectToAlliesInRadius(
    5.0,              // 範囲
    "barkskin",       // エフェクトID
    25.0 * 20         // 持続時間（25秒）
))
.buildForEffect()  // ← 重要！
.add()
```

### パターン4: チャージスキル

```kotlin
SpellBuilder.of(
    "dash",
    PlayStyle.DEX,
    SpellConfiguration.Builder.instant(10, 0)
        .setChargesAndRegen("dash", 3, 20 * 30),  // 3チャージ、30秒回復
    "Dash",
    listOf(SpellTags.movement)
)
.onCast(PartBuilder.justAction(
    SpellAction.TP_CASTER_IN_DIRECTION.create(...)
))
.build()
.add()

// ValueCalculation も必要
ValueCalcBuilderKt.of("dash").build().add()
```

### パターン5: 視線方向中心のデバフ

```kotlin
SpellBuilder.of(...)
.onCast(PartBuilder.justAction(
    SpellAction.SUMMON_AT_SIGHT.create(
        SlashEntities.SIMPLE_PROJECTILE.get(),
        1.0,
        0.0
    )
))
.onExpire(PartBuilder.addExileEffectToEnemiesInAoe(
    "weakness",  // デバフ
    6.0,         // 範囲
    20.0 * 15    // 15秒
))
.buildForEffect()
.add()
```

---

## トラブルシューティング

### ❌ エフェクトが正しく動作しない

**原因**: `.build()`を使っている

**解決**: `.buildForEffect()`に変更

### ❌ チャージが表示されない

**原因**: `ValueCalculation`が未定義

**解決**: 空でも良いので定義する
```kotlin
ValueCalcBuilderKt.of("spell_id").build().add()
```

### ❌ マナコストがスケールしない

**原因**: カスタム値を設定していない

**解決**:
```kotlin
.apply {
    config.mana_cost = LeveledValue(50f, 30f)
}
```

### ❌ 範囲がスケールしない

**原因**: PartBuilderメソッドは固定値のみ

**解決**: `INCREASED_AREA` Statを装備/Perkで増やす

---

## 次のドキュメント

- [03_ExileEffects.md](./03_ExileEffects.md) - ExileEffectシステム
- [04_KotlinAddon.md](./04_KotlinAddon.md) - Kotlinアドオン開発
