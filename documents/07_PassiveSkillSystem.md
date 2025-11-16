# Mine and Slash パッシブスキル徹底ガイド

**最終更新**: 2025-11-13  
**対象バージョン**: Mine and Slash Rework 1.20 (Forge)

---

## 1. システム概要

Mine and Slash のパッシブスキルは `Perk` クラスを単位として構成され、才能ツリー (Talent Tree) 上に配置されます。各 Perk は **スタット増減や特殊処理を表す `OptScaleExactStat` の配列**で定義され、プレイヤーがポイントを割り振ることで累積的に適用されます。

主な構成要素は以下のとおりです。

| 層               | 役割                   | 代表クラス                                 |
| ---------------- | ---------------------- | ------------------------------------------ |
| データ定義       | Perk の静的設定        | `Perk`, `PerkBuilder`, `OptScaleExactStat` |
| ツリー構造       | グリッド配置と接続情報 | `TalentTree`, `TalentGrid`                 |
| プレイヤーデータ | 割り振り状況と検証     | `TalentsData`, `SchoolData`                |
| 実行時反映       | スタットへの適用文脈   | `ExactStatData`, `StatContext`             |

---

## 2. Perk クラスの詳細

`Perk` は Json シリアライズ対象の基本エンティティです。

```java
class Perk implements JsonExileRegistry<Perk>, IAutoGson<Perk>, IAutoLocName {
    PerkType type;        // 表示・演出に利用される種別
    String id;            // 一意 ID
    String icon;          // GUI 用アイコンパス
    int max_lvls = 1;     // レベル上限 (パッシブのみ複数レベル)
    List<OptScaleExactStat> stats = new ArrayList<>();
    boolean is_entry;     // ツリー開始ノード判定
    String one_kind;      // 同系統排他指定
}
```

### 2.1 PerkType

| 値        | 用途                           | 特徴                                        |
| --------- | ------------------------------ | ------------------------------------------- |
| `STAT`    | 通常パッシブ／スペルアンロック | もっとも一般的。`passive` や `spell` で利用 |
| `SPECIAL` | 強化版ステータス (Big Stat 等) | 枠線・アイコンが特別色になる                |
| `MAJOR`   | ゲームチェンジャー級           | UI で赤色枠、複合的なトレードオフ           |
| `START`   | ツリー開始ノード               | `is_entry` と併用される                     |
| `ASC`     | アセンション系統               | 別ポイントプールを消費                      |

### 2.2 スタット構成

`stats` フィールドに格納される `OptScaleExactStat` が **Perk の実際の効果そのもの**です。スペル習得 Perk の場合も、`LearnSpellStat` を格納することで実現します。

> ⚠️ Perk に直接ロジックを書くことはなく、特殊挙動が必要な場合は **カスタム Stat (または StatEffect)** を実装して `OptScaleExactStat` 経由で付与します。

---

## 3. PerkBuilder のバリエーション

`PerkBuilder` は Perk 定義を簡潔に記述するためのユーティリティです。内部では `Perk` インスタンスを生成し、`stats` や `icon` などを設定した後 `addToSerializables` で登録します。

| メソッド                                         | 主用途                   | 特記事項                                                           |
| ------------------------------------------------ | ------------------------ | ------------------------------------------------------------------ |
| `spell(id)`                                      | スペル習得               | `LearnSpellStat(spell)` を自動追加し、最大レベルをスペルに合わせる |
| `passive(id, maxLvl, OptScaleExactStat...)`      | 複数レベルの一般パッシブ | パスは `textures/gui/spells/passives/{id}.png`                     |
| `stat(id, OptScaleExactStat...)`                 | 単発ステータス強化       | 最初の Stat アイコンを流用                                         |
| `bigStat(...)`                                   | SPECIAL タイプの大型バフ | 表示名を個別設定可能                                               |
| `gameChanger(id, locname, OptScaleExactStat...)` | MAJOR パーク             | トレードオフ構成に最適                                             |
| `ascPoint(id, OptScaleExactStat...)`             | アセンションツリー用     | アイコンは `textures/gui/asc_classes/perk/{id}.png`                |
| `socket()`                                       | ジュエルスロット追加     | `JewelSocketStat` を付与                                           |

---

## 4. OptScaleExactStat と派生計算

`OptScaleExactStat` は Perk が付与するスタットの “1 レベルあたり” の値を表します。

```java
OptScaleExactStat(float value, Stat stat, ModType type)
    .scale() // レベル依存スケールを有効化
```

- `ModType` で加算種別を切り替え (`FLAT`, `PERCENT`, `MORE`)。
- `scale()` を呼ぶとキャラクターのレベルに応じて値が指数的に調整されます (`ExactStatData.levelScaled`)。
- 複数同一 Stat が積まれる場合は `OptScaleExactStat.combine` で自動マージされます。

### 4.1 異なるステータスを参照するバフ

**Perk 自体は数値を持つだけ**ですが、Stat 側で以下のような処理を持たせることで間接バフが可能です。

- `NumberProvider.ofPercentOfStat("stat_id")` : 所持ステータス値の％を新しい値として使用。
- `NumberProvider.ofStatData()` : 付与済み Stat の値をそのまま参照。
- `NumberProvider.ofPercentOfDataNumber(eventField)` : イベントで計算されたダメージなどから％換算。

こうした `NumberProvider` は `StatEffects` 内のリーチ／回復処理で多用されており、Perk にリーチ強化 Stat を追加すれば “与ダメの〇％を吸収” などを実現できます。

---

## 5. Talent Tree との結合

### 5.1 TalentTree 定義

- `perks`: カンマ区切りの 2D グリッド文字列。`TalentGrid` が解析。
- `calcData.perks`: `PointData → perk_id` のマップ。
- `calcData.connections`: 各ノード間の接続セット。隣接＋コネクタ種別に応じて自動生成。
- `school_type`: `TALENTS` / `ASCENDANCY`。使用ポイントプールと `StatContext` の種別を決定。

### 5.2 グリッド解析の流れ

1. グリッド文字列を `TalentGrid` が行列に分解。
2. Talents (Perk ノード) と接続線 (Connector) を判別。
3. 中心ノード (`center`) を特定。
4. 各 Talents 同士を距離・コネクタ条件で接続 (`addConnection`)。

### 5.3 表示・分類情報

Perk は種別に応じた枠線・インジケータを持ちます。`PerkType` の `getColorTexture` / `getBorderTexture` が GUI 表示を制御します。

---

## 6. ランタイム処理 (`TalentsData`)

プレイヤー情報は `TalentsData` に保存され、以下の責務を担います。

- **ポイント管理**: `hasFreePoints` が `PlayerPointsType` を参照して残ポイントを判定。
- **取得判定**: `canAllocate` が接続状態・排他 (`one_kind`)・ポイントの有無を検証。
- **解除判定**: `canRemove` が再配置可能か BFS (`hasPathToStart`) で確認。
- **Stat反映**: `getStatAndContext` が各 SchoolType の取得 Perk を走査し、`OptScaleExactStat.toExactStat` を呼び出して `StatContext` を組み立て。

> これにより、Perk の効果は装備やエフェクトと同じ Stat パイプラインを通って最終ステータスに加算されます。

---

## 7. カスタム Perk 作成フロー

1. **Stat / StatEffect の準備**  
   - 既存 Stat を使うか、特殊挙動が必要なら新しい `Stat` を実装。必要に応じて `NumberProvider` や `StatEffect` を定義。

2. **Perk の定義**  
   - `PerkBuilder` を用いて `OptScaleExactStat` を設定。  
   - アイコンファイル（32×32推奨）を `textures/gui/spells/passives/` 等に配置。

3. **TalentTree へ配置**  
   - `perks` グリッドに perk ID を配置。接続子 (例: `-`, `|`, `:`) を調整し、`TalentGrid` による自動接続を活用。

4. **ローカライズ**  
   - `mmorpg.perk.{id}` で名前を、必要であれば説明テキストも追加。

5. **テスト**  
   - 取得可能か (`canAllocate`) / 解除可能か (`canRemove`) をチェック。  
   - 実際に振った際のステータス変化を `Debug Stat Screen` 等で検証。

---

## 8. 応用パターン集

| パターン         | 仕組み                                                      | 実装ポイント                                   |
| ---------------- | ----------------------------------------------------------- | ---------------------------------------------- |
| レベル比例バフ   | `OptScaleExactStat(...).scale()`                            | キャラクターレベルに応じて伸びる耐性などに有効 |
| 別ステータス参照 | カスタム Stat + `NumberProvider.ofPercentOfStat`            | 「最大体力の〇％を魔法ダメに変換」など         |
| リーチ／吸収     | `RestoreResourceAction` 系 StatEffect                       | ダメージ→回復変換を行う Stat を付与            |
| 排他系 Perk      | `perk.one_kind = "tag"`                                     | 同カテゴリから 1 つだけ取らせたい場合に使用    |
| アセンション限定 | `PerkBuilder.ascPoint` + `TalentTree.SchoolType.ASCENDANCY` | 別ポイントプールを共有するクラス専用ツリー     |

---

## 9. デバッグとメンテナンス Tips

- `MMORPG.RUN_DEV_TOOLS` を有効化すると未登録 Perk を検出するログが出力されます。
- `OptScaleExactStat.combine` の仕様上、同一 Stat を複数追加する場合は `scale_to_lvl` と `ModType` が一致しているか確認。
- `TalentTree` の `shouldGenerateJson()` は false が推奨。手書きグリッドのほうが調整しやすいためです。
- `Perk.status` を UI で表示する場合は `TalentsData.getStatus()` を利用すると BLOCKED / POSSIBLE / CONNECTED を判定できます。

---

## 10. 参考リンク

- `src/main/java/com/robertx22/mine_and_slash/database/data/perks/Perk.java`
- `src/main/java/com/robertx22/mine_and_slash/aoe_data/database/perks/PerkBuilder.java`
- `src/main/java/com/robertx22/mine_and_slash/database/OptScaleExactStat.java`
- `src/main/java/com/robertx22/mine_and_slash/database/data/talent_tree/TalentTree.java`
- `src/main/java/com/robertx22/mine_and_slash/saveclasses/perks/TalentsData.java`
- `src/main/java/com/robertx22/mine_and_slash/aoe_data/database/stat_effects/StatEffects.java`

> 既存の `05_PerkSystem.md` では API / 実装パターンを中心に整理しています。本ドキュメントは内部コードを補完し、カスタム Perk 設計時に参照できるシステム面の詳細をまとめています。
