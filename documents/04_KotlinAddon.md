# Mine and Slash Kotlinアドオン開発ガイド

**最終更新**: 2025年11月10日
**対象バージョン**: 1.20 Forge

---

## 目次

1. [プロジェクトセットアップ](#プロジェクトセットアップ)
2. [レジストリパターン](#レジストリパターン)
3. [データ生成](#データ生成)
4. [翻訳ファイル](#翻訳ファイル)
5. [DRY原則](#dry原則)
6. [トラブルシューティング](#トラブルシューティング)

---

## プロジェクトセットアップ

### 依存関係

`build.gradle`:

```gradle
dependencies {
    implementation fg.deobf("curse.maven:mine-and-slash-<id>:<version>")
}
```

### MODエントリーポイント

```kotlin
@Mod(ModIdentity.MOD_ID)
object MnsMoreClass {
    val LOGGER: Logger = LogManager.getLogger(ModIdentity.LOGGER_NAME)

    init {
        LOGGER.info("${ModIdentity.MOD_NAME} is initializing...")
        MOD_BUS.addListener(this::gatherData)

        // カスタムSpellAction登録（必要な場合）
        SpellActions.init()
    }

    @SubscribeEvent
    fun gatherData(event: GatherDataEvent) {
        // Mine and Slashが初期化された後に登録
        SpellRegistry.register()
        ExileEffectRegistry.register()
        ValueCalcRegistry.register()

        // データプロバイダー追加
        event.generator.addProvider(...)
    }
}
```

**重要**: `gatherData`イベントで登録することで、Mine and Slash本体の初期化後に確実に登録される

---

## レジストリパターン

### レジストリインターフェース

```kotlin
interface IMnsRegistry<T> {
    fun add(value: T)
    fun getAll(): List<T>
}
```

### デリゲート実装

```kotlin
class MnsRegistryDelegate<T> : IMnsRegistry<T> {
    private val values = mutableListOf<T>()

    override fun add(value: T) {
        values.add(value)
    }

    override fun getAll(): List<T> {
        return values.toList()
    }
}
```

### レジストリオブジェクト

```kotlin
object SpellRegistry : IMnsRegistry<Spell> by MnsRegistryDelegate() {
    const val BARKSKIN = "barkskin"
    const val PURIFICATION = "purification"

    fun register() {
        SpellBuilder.of(BARKSKIN, ...)
            .build()
            .add()  // ← 拡張関数で自動登録

        SpellBuilder.of(PURIFICATION, ...)
            .buildForEffect()
            .add()
    }
}
```

### 拡張関数（自動登録）

Java側で`.add()`を呼べないため、Kotlin拡張関数で解決：

```kotlin
// Spell用
fun Spell.add() {
    SpellRegistry.add(this)
    ExileDB.Spells().addSerializable(this.identifier, this)
}

// ExileEffect用
fun ExileEffect.add() {
    ExileEffectRegistry.add(this)
    ExileDB.ExileEffects().addSerializable(this.id, this)
}

// ValueCalculation用
fun ValueCalculation.add() {
    ValueCalcRegistry.add(this)
    ExileDB.ValueCalculations().addSerializable(this.id, this)
}
```

**重要**: ExileDBへの登録も忘れずに！

---

## データ生成

### MnsDataProvider

汎用データプロバイダー：

```kotlin
class MnsDataProvider<T>(
    output: PackOutput,
    data: List<T>,
    directory: String,
    keyExtractor: (T) -> ResourceLocation
) : DataProvider {
    override fun run(cache: CachedOutput) {
        data.forEach { item ->
            val key = keyExtractor(item)
            val path = output.outputFolder
                .resolve("data/${key.namespace}/$directory/${key.path}.json")

            val json = GSON.toJson(item)
            DataProvider.saveStable(cache, JsonParser.parseString(json), path)
        }
    }

    override fun getName(): String = "MnS $directory"
}
```

### データプロバイダー登録

```kotlin
@SubscribeEvent
fun gatherData(event: GatherDataEvent) {
    val output = event.generator.packOutput

    // Spells
    event.generator.addProvider(
        event.includeServer(),
        MnsDataProvider(output, SpellRegistry.getAll(), "mmorpg_spells") { spell ->
            ResourceLocation.fromNamespaceAndPath(SlashRef.MODID, spell.identifier)
        }
    )

    // ExileEffects
    event.generator.addProvider(
        event.includeServer(),
        MnsDataProvider(output, ExileEffectRegistry.getAll(), "mmorpg_exile_effect") { effect ->
            ResourceLocation.fromNamespaceAndPath(SlashRef.MODID, effect.id)
        }
    )

    // ValueCalc
    event.generator.addProvider(
        event.includeServer(),
        MnsDataProvider(output, ValueCalcRegistry.getAll(), "mmorpg_value_calc") { calc ->
            ResourceLocation.fromNamespaceAndPath(SlashRef.MODID, calc.id)
        }
    )
}
```

### 生成コマンド

```bash
./gradlew runData
```

生成先:
```
src/generated/resources/data/mmorpg/
├── mmorpg_spells/
├── mmorpg_exile_effect/
└── mmorpg_value_calc/
```

---

## 翻訳ファイル

### ファイル構成

```
src/main/resources/assets/mmorpg/lang/
├── en_us.json
└── ja_jp.json
```

### 翻訳キー規則

```json
{
  // スペル名
  "mmorpg.spell.<spell_id>": "Spell Name",

  // スペル習得
  "mmorpg.stat.learn_<spell_id>": "Level to Spell Name",

  // スペル説明
  "spell.desc.<spell_id>": "Spell description.",

  // ExileEffect名
  "mmorpg.effect.<effect_id>": "Effect Name",

  // Perk名
  "mmorpg.perk.<perk_id>": "Perk Name",

  // SpellSchool名
  "mmorpg.asc_class.<school_id>": "School Name"
}
```

### 例: Purificationスキル

**en_us.json**:
```json
{
  "mmorpg.spell.purification": "Purification",
  "mmorpg.stat.learn_purification": "Level to Purification",
  "spell.desc.purification": "Removes all debuffs from nearby allies."
}
```

**ja_jp.json**:
```json
{
  "mmorpg.spell.purification": "浄化",
  "mmorpg.stat.learn_purification": "浄化を学ぶ",
  "spell.desc.purification": "範囲内の味方のデバフを解除する。"
}
```

---

## DRY原則

### 悪い例（コード重複）

```kotlin
fun purifyAlliesInRadius(radius: Double): ComponentPart {
    val part = ComponentPart()
    part.targets.add(BaseTargetSelector.AOE.create(...))
    part.acts.add(SpellAction.POTION.removeNegative(100.0))
    part.acts.add(SpellAction.EXILE_EFFECT.create(...))
    part.acts.add(SpellActions.REMOVE_FIRE.create())
    return part
}
```

### 良い例（再利用）

```kotlin
// 個別機能
fun removePotionEffectInRadius(radius: Double): ComponentPart { ... }
fun removeExileEffectInRadius(radius: Double): ComponentPart { ... }
fun removeFireInRadius(radius: Double): ComponentPart { ... }

// 組み合わせ
fun purifyAlliesInRadius(radius: Double): List<ComponentPart> {
    return listOf(
        removePotionEffectInRadius(radius),
        removeExileEffectInRadius(radius),
        removeFireInRadius(radius)
    )
}
```

### 使用例

```kotlin
SpellBuilder.of(...)
    .apply {
        PartBuilderExtensions.purifyAlliesInRadius(6.0).forEach { onCast(it) }
    }
    .build()
```

---

## カスタムSpellAction

### 実装

```kotlin
class RemoveFireAction : SpellAction(emptyList()) {
    override fun tryActivate(
        targets: Collection<LivingEntity>,
        ctx: SpellCtx,
        data: MapHolder
    ) {
        targets.forEach { entity ->
            entity.clearFire()
        }
    }

    fun create(): MapHolder {
        val holder = MapHolder()
        holder.type = GUID()
        return holder
    }

    override fun GUID(): String = "remove_fire"
}
```

### 登録

```kotlin
object SpellActions {
    val REMOVE_FIRE: RemoveFireAction = register(RemoveFireAction())

    private fun <T : SpellAction> register(action: T): T {
        SpellAction.MAP[action.GUID()] = action
        return action
    }

    fun init() {
        // クラスロード時に登録
    }
}

// MOD初期化時に呼び出す
init {
    SpellActions.init()
}
```

---

## トラブルシューティング

### ❌ NullPointerException on ExileDB

**原因**: Mine and Slash初期化前にアクセス

**解決**: `gatherData`イベントで登録

### ❌ JSONが生成されない

**原因**: DataProviderが未登録

**解決**: `event.generator.addProvider()`を確認

### ❌ 翻訳が表示されない

**原因**: 翻訳キーが間違っている

**解決**: `mmorpg.spell.<id>`等の規則を確認

### ❌ Kotlin拡張関数が認識されない

**原因**: importされていない

**解決**: `import package.name.*`

### ❌ JVMシグネチャ衝突

**原因**: Java互換の問題

**解決**: `@JvmStatic`, `@JvmName`を使用

---

## ベストプラクティス

### ✅ DO

1. **レジストリパターン使用**: IMnsRegistry + Delegate
2. **拡張関数活用**: `.add()`で自動登録
3. **DRY原則厳守**: 機能を分割して再利用
4. **翻訳ファイル整備**: すべてのテキストをlang/に
5. **データ生成自動化**: DataProviderで管理

### ❌ DON'T

1. **直接ExileDBにアクセスしない**: 初期化前
2. **ハードコード**: 翻訳、数値等
3. **コード重複**: ヘルパー関数を作る
4. **@JvmStaticを忘れる**: Javaから呼べない
5. **IDE警告を無視**: Kotlinの型安全性を活用

---

## 参考実装

### MnSMoreClassプロジェクト構造

```
src/main/kotlin/io/github/cotrin8672/mnsmoreclass/
├── MnsMoreClass.kt              # MODエントリー
├── registry/
│   ├── IMnsRegistry.kt          # レジストリIF
│   ├── MnsRegistryDelegate.kt   # デリゲート
│   ├── SpellRegistry.kt         # スペル登録
│   ├── ExileEffectRegistry.kt   # エフェクト登録
│   └── ValueCalcRegistry.kt     # 計算式登録
├── spell_action/
│   ├── RemoveFireAction.kt      # カスタムAction
│   ├── SpellActions.kt          # Action登録
│   └── PartBuilderExtensions.kt # ヘルパー
├── value_calc/
│   └── ValueCalcBuilderKt.kt    # Kotlinラッパー
└── datagen/
    └── MnsDataProvider.kt       # データ生成

src/main/resources/assets/mmorpg/lang/
├── en_us.json
└── ja_jp.json
```

---

## 次のドキュメント

- [05_PartBuilderExtensions.md](./05_PartBuilderExtensions.md) - ヘルパー関数作成
- [06_TroubleshootingGuide.md](./06_TroubleshootingGuide.md) - よくある問題
