# Mine and Slash アドオン開発ドキュメント

**最終更新**: 2025年11月10日
**対象バージョン**: Mine and Slash 1.20 Forge

---

## ドキュメント一覧

### 📚 基礎編

1. **[01_SystemOverview.md](./01_SystemOverview.md)**
   - Mine and Slashのシステム設計
   - レジストリシステム
   - データパック駆動アーキテクチャ
   - クライアント/サーバー分離

2. **[02_SpellSystem.md](./02_SpellSystem.md)**
   - スペルシステム完全ガイド
   - SpellBuilder使用方法
   - SpellConfiguration（instant, multiCast等）
   - ComponentPart、PartBuilder
   - ValueCalculation

3. **[03_ExileEffects.md](./03_ExileEffects.md)**
   - ExileEffectシステム
   - Stat操作（Mine and Slash独自）
   - VanillaStat操作（Minecraft Attribute）
   - スペル連携（DoT, HoT等）

### 🛠️ 実践編

4. **[04_KotlinAddon.md](./04_KotlinAddon.md)**
   - Kotlinアドオン開発ガイド
   - プロジェクトセットアップ
   - レジストリパターン実装
   - データ生成
   - カスタムSpellAction作成

5. **[05_PerkSystem.md](./05_PerkSystem.md)**
   - Perkシステム（パッシブスキル）
   - PerkBuilder使用方法
   - OptScaleExactStat
   - アイコン管理

6. **[06_SpellSchool.md](./06_SpellSchool.md)**
   - SpellSchoolシステム（クラス作成）
   - SchoolBuilder使用方法
   - ツリー構造設計
   - PointData（座標配置）

---

## クイックスタート

### 1. プロジェクト作成

```bash
# Forgeプロジェクト作成
# Kotlinプラグイン追加
```

### 2. 依存関係追加

`build.gradle`:
```gradle
dependencies {
    implementation fg.deobf("curse.maven:mine-and-slash-<id>:<version>")
}
```

### 3. 最小限のMOD

```kotlin
@Mod("mymod")
object MyMod {
    @SubscribeEvent
    fun gatherData(event: GatherDataEvent) {
        // スペル登録
        SpellBuilder.of("fireball", PlayStyle.INT, ...)
            .build()

        // データプロバイダー登録
        event.generator.addProvider(...)
    }
}
```

---

## 重要な概念

### レジストリシステム

```kotlin
// 中央レジストリ
ExileDB.Spells()         // スペル
ExileDB.ExileEffects()   // エフェクト
ExileDB.Stats()          // ステータス

// GUID（一意識別子）で参照
val spell = ExileDB.Spells().get("fireball")
```

### ビルダーパターン

```kotlin
SpellBuilder.of(id, playStyle, config, name, tags)
    .weaponReq(weapon)
    .onCast(part)
    .build()
```

### データパック駆動

- ゲームバランスはJSONで管理
- 開発時に自動生成（`runData`）
- 本番では`src/generated/resources/`を同梱

---

## よくあるパターン

### 範囲ダメージスペル

```kotlin
SpellBuilder.of("fireball", PlayStyle.INT, instant(30, 20*5), "Fireball", tags)
    .onCast(PartBuilder.damageInAoe(SpellCalcs.FIREBALL, Elements.Fire, 3.0))
    .onCast(PartBuilder.aoeParticles(ParticleTypes.FLAME, 50.0, 3.0))
    .build()
```

### バフスペル（ExileEffect）

```kotlin
SpellBuilder.of("shield", PlayStyle.INT, instant(20, 20*10), "Shield", tags)
    .onCast(PartBuilder.giveExileEffectToAlliesInRadius(5.0, "shield", 20.0*15))
    .buildForEffect()  // ← 重要！
```

### チャージスペル

```kotlin
SpellBuilder.of("dash", PlayStyle.DEX, instant(10, 0)
    .setChargesAndRegen("dash", 3, 20*30), "Dash", tags)
    .onCast(...)
    .build()

// ValueCalculationも必要
ValueCalcBuilderKt.of("dash").build()
```

---

## よくあるミス

### ❌ `.build()` vs `.buildForEffect()`

```kotlin
// ❌ ExileEffectを使うのに.build()
SpellBuilder.of(...)
    .onCast(PartBuilder.giveExileEffectToAlliesInRadius(...))
    .build()  // ← 間違い

// ✅ .buildForEffect()を使う
SpellBuilder.of(...)
    .onCast(PartBuilder.giveExileEffectToAlliesInRadius(...))
    .buildForEffect()  // ← 正しい
```

### ❌ Mine and Slash初期化前のアクセス

```kotlin
// ❌ init{}で直接アクセス
init {
    ExileDB.Spells().get("fireball")  // NPE
}

// ✅ gatherDataイベントで
@SubscribeEvent
fun gatherData(event: GatherDataEvent) {
    ExileDB.Spells().get("fireball")  // OK
}
```

### ❌ チャージスキルでValueCalculation忘れ

```kotlin
// ❌ ValueCalculationなし
SpellBuilder.of(...).setChargesAndRegen(...).build()

// ✅ ValueCalculationも定義
SpellBuilder.of(...).setChargesAndRegen(...).build()
ValueCalcBuilderKt.of("spell_id").build()
```

---

## デバッグTips

### ログ出力

```kotlin
LOGGER.info("Registering spell: $spellId")
```

### データ確認

```bash
# データ生成
./gradlew runData

# 生成されたJSON確認
cat src/generated/resources/data/mmorpg/mmorpg_spells/spell_id.json
```

### ゲーム内コマンド

```
/reload  # データパックリロード
```

---

## 参考リソース

### 公式

- [Forge Documentation](https://docs.minecraftforge.net/)
- [Minecraft Wiki](https://minecraft.fandom.com/)

### Mine and Slash

- [GitHub](https://github.com/RobertSkalko/Mine-And-Slash-Rework)
- [CurseForge](https://www.curseforge.com/minecraft/mc-mods/mine-and-slash-reloaded)

### サンプルプロジェクト

- **MnSMoreClass**: Druidクラス追加アドオン
  - `src/main/kotlin/io/github/cotrin8672/mnsmoreclass/`

---

## ライセンス

このドキュメントはMIT Licenseで公開されています。

---

## 貢献

誤記や改善提案は[Issues](https://github.com/your-repo/issues)へ。
