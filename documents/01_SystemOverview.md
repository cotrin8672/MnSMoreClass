# Mine and Slash システム概要

**最終更新**: 2025年11月10日
**対象バージョン**: 1.20 Forge

---

## 目次

1. [プロジェクト構造](#プロジェクト構造)
2. [コア設計思想](#コア設計思想)
3. [レジストリシステム](#レジストリシステム)
4. [データパック駆動設計](#データパック駆動設計)
5. [クライアント/サーバー分離](#クライアントサーバー分離)

---

## プロジェクト構造

### パッケージ構成

```
com.robertx22.mine_and_slash/
├── mmorpg/                      # MODエントリーポイント
│   └── registers/               # レジストリ（アイテム、ブロック等）
├── database/                    # ゲームデータベース
│   └── data/
│       ├── stats/               # ステータス定義
│       ├── spells/              # スペル/スキル
│       ├── affixes/             # アフィックス（接頭辞/接尾辞）
│       └── value_calc/          # 値計算システム
├── aoe_data/                    # データパック生成（開発時）
│   └── database/
│       ├── spells/              # スペルビルダー
│       ├── stats/               # Stat定義
│       └── exile_effects/       # ExileEffect定義
├── uncommon/                    # ユーティリティ
│   ├── stat_calculation/        # ステータス計算
│   └── effectdatas/             # エフェクトデータ
└── vanilla_mc/                  # バニラMC統合
    └── packets/                 # ネットワークパケット
```

### リソース構成

```
src/main/resources/
├── assets/mmorpg/               # クライアント側リソース
│   ├── lang/                    # 翻訳ファイル (ja_jp.json, en_us.json)
│   ├── textures/                # テクスチャ
│   └── models/                  # モデル
└── data/mmorpg/                 # データパック（サーバー側）
    ├── mmorpg_spells/           # スペルJSON
    ├── mmorpg_exile_effect/     # ExileEffectJSON
    ├── mmorpg_spell_school/     # スペルスクールJSON
    ├── mmorpg_perk/             # PerkJSON
    └── mmorpg_value_calc/       # ValueCalculationJSON
```

---

## コア設計思想

### 1. **Data-Driven Architecture（データ駆動）**

Mine and Slashは**データパック駆動**で設計されています：

- ゲームバランスは**JSONファイル**で管理
- コード変更不要でバランス調整可能
- モジュール性が高く、他MODとの統合が容易

### 2. **Registry Pattern（レジストリパターン）**

すべてのゲームコンテンツは**レジストリ**に登録：

```java
// 中央レジストリ
ExileDB.Spells()         // スペル
ExileDB.Stats()          // ステータス
ExileDB.ExileEffects()   // エフェクト
ExileDB.Affixes()        // アフィックス
ExileDB.Rarities()       // レアリティ
```

#### ⚠️ アドオンからの登録の注意点

**問題**: `ExileDB`は**Mine and Slashの初期化後**にしかアクセスできない。

```kotlin
// ❌ 間違い: init{}で直接アクセス
object MyMod {
    init {
        ExileDB.Spells().addSerializable(...)  // NPE！
    }
}

// ✅ 正しい: gatherDataイベントで登録
@SubscribeEvent
fun gatherData(event: GatherDataEvent) {
    ExileDB.Spells().addSerializable(...)  // OK
}
```

**解決策**:
- Forgeの`GatherDataEvent`で登録処理を実行
- または独自のレジストリを作成し、適切なタイミングで`ExileDB`に追加

### 3. **IGUID システム**

すべてのレジスタブルオブジェクトは`IGUID`インターフェースを実装：

```java
public interface IGUID {
    String GUID();  // 一意識別子
}
```

- **GUID（Global Unique ID）**: 文字列ベースの一意識別子
- データ参照は全てGUID経由
- タイプセーフ + 柔軟性

#### ⚠️ GUID の名前空間衝突

**問題**: GUIDは**グローバル**なので、Mine and Slash本体や他MODと衝突する可能性がある。

```kotlin
// ❌ 衝突の可能性
ExileDB.Spells().addSerializable("fireball", spell)  // 本体に同名スペルがあるかも

// ✅ プレフィックスで回避
ExileDB.Spells().addSerializable("mymod_fireball", spell)
```

**ベストプラクティス**:
- MOD IDをプレフィックスとして使用（例: `"druid_barkskin"`）
- 定数で管理して typo を防ぐ

```kotlin
object SpellRegistry {
    const val BARKSKIN = "druid_barkskin"  // 定数化

    fun register() {
        SpellBuilder.of(BARKSKIN, ...)  // typo防止
    }
}
```

### 4. **Builder Pattern（ビルダーパターン）**

複雑なオブジェクトは**Builderパターン**で構築：

```java
SpellBuilder.of(ID, PlayStyle, Configuration, Name, Tags)
    .weaponReq(CastingWeapon.MAGE_WEAPON)
    .onCast(PartBuilder.damageInAoe(...))
    .onCast(PartBuilder.playSound(...))
    .build()
```

**利点**:
- 可読性が高い
- 必須/オプションパラメータが明確
- チェーンメソッドで流暢に記述

---

## レジストリシステム

### ExileDB - 中央レジストリ

Mine and Slashの全データへのアクセスポイント：

```java
// スペル取得
Spell spell = ExileDB.Spells().get("fireball");

// Stat取得
Stat stat = ExileDB.Stats().get("strength");

// ExileEffect取得
ExileEffect effect = ExileDB.ExileEffects().get("burn");
```

### 登録タイミング

データは**起動時**に登録されます：

1. **Phase 1**: Javaコードでオブジェクトを構築
2. **Phase 2**: `.build()`でレジストリに自動登録
3. **Phase 3**: データパックJSONが生成される（開発時）
4. **Phase 4**: ゲーム実行時にJSONから読み込み

### アドオンからの登録

アドオンMODは`GatherDataEvent`で登録：

```kotlin
@SubscribeEvent
fun gatherData(event: GatherDataEvent) {
    // ここでスペル、エフェクト等を登録
    SpellRegistry.register()
    ExileEffectRegistry.register()

    // データプロバイダーを追加
    event.generator.addProvider(
        event.includeServer(),
        MnsDataProvider(output, spells, "mmorpg_spells") { ... }
    )
}
```

**重要**: Mine and Slashが初期化された**後**に登録する必要があります。

---

## データパック駆動設計

### JSONスキーマ

各ゲームオブジェクトはJSONで表現：

#### Spell JSON例

```json
{
  "identifier": "fireball",
  "config": {
    "mana_cost": { "min": 30, "max": 22.5 },
    "cooldown_ticks": 60,
    "cast_time_ticks": 0
  },
  "parts": [
    {
      "targets": [...],
      "acts": [...]
    }
  ]
}
```

#### ExileEffect JSON例

```json
{
  "id": "burn",
  "effect_type": "negative",
  "stats": [
    {
      "stat": "fire_damage_over_time",
      "value": 10
    }
  ]
}
```

### データパック生成

開発時、`MMORPG.RUN_DEV_TOOLS = true`で自動生成：

```
src/generated/resources/data/mmorpg/
├── mmorpg_spells/
├── mmorpg_exile_effect/
└── mmorpg_value_calc/
```

**本番ビルドでは必ず`RUN_DEV_TOOLS = false`に設定**

---

## クライアント/サーバー分離

### @OnlyIn アノテーション

クライアント専用コードには`@OnlyIn(Dist.CLIENT)`：

```java
@OnlyIn(Dist.CLIENT)
public class SpellHotbarRenderer {
    // クライアント専用描画ロジック
}
```

### パケット同期

クライアント/サーバー間のデータ同期は**パケット**経由：

```java
// サーバー → クライアント
MyPacket packet = new MyPacket(data);
PacketDistributor.PLAYER.with(player).send(packet);

// クライアント → サーバー
MyPacket packet = new MyPacket(data);
PacketDistributor.SERVER.noArg().send(packet);
```

### Side確認

処理前に必ず**Side確認**：

```java
if (world.isClientSide) {
    return; // サーバー専用処理
}

if (!world.isClientSide) {
    return; // クライアント専用処理
}
```

---

## ベストプラクティス

### ✅ DO

1. **既存パターンに従う**: Mine and Slashの命名規則、パッケージ構造を尊重
2. **ビルダーを使う**: `SpellBuilder`, `ExileEffectBuilder`等
3. **GUIDを使う**: データ参照は文字列GUID経由
4. **データパック生成**: 開発時に自動生成、本番では無効化
5. **翻訳キーを使う**: ハードコードせず`lang/*.json`

### ❌ DON'T

1. **直接インスタンス化しない**: ビルダーを使う
2. **データをハードコードしない**: JSONで管理
3. **クライアント/サーバーを混在させない**: `@OnlyIn`で分離
4. **レジストリを直接変更しない**: ビルダーの`.build()`に任せる
5. **セーブデータ構造を安易に変更しない**: 互換性を考慮

---

## 次のドキュメント

- [02_SpellSystem.md](./02_SpellSystem.md) - スペルシステム完全ガイド
- [03_ExileEffects.md](./03_ExileEffects.md) - ExileEffectシステム
- [04_KotlinAddon.md](./04_KotlinAddon.md) - Kotlinアドオン開発ガイド
