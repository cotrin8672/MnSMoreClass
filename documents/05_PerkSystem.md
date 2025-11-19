# Mine and Slash SpellSchool パッシブ Perk ガイド

**最終更新**: 2025年11月18日  
**対象バージョン**: Mine and Slash Rework 1.20 (Forge)

---

## 1. このドキュメントの目的

- 本稿は **SpellSchool に紐づくパッシブ Perk**（スペルツリー内で取得するポイント）を対象とする。
- Talent ツリー（`TalentTree` 系、別ポイントプール）は [07_PassiveSkillSystem.md](./07_PassiveSkillSystem.md) を参照。
- 情報源は Mine and Slash 本体ソースの該当クラスを確認し、仕様を整合させている。

---

## 2. 用語整理

| 用語 | 説明 |
| ---- | ---- |
| Perk | パッシブ効果やスペル習得を表すデータエントリ |
| SpellSchool | アセンションクラスごとのツリー。Perk が座標と紐づく |
| SpellSchoolsData | プレイヤーが SpellSchool Perk に投資したレベルを保持するクラス |
| PointType | `SPELL` / `PASSIVE`。消費ポイント種別とリセット処理を決定 |

---

## 3. データフロー概要

```
PerkBuilder → Perk 登録 → SpellSchool 配置 → SpellSchoolsData (プレイヤー状態)
```

1. `PerkBuilder` で Perk を生成・登録。@Mine-And-Slash-Rework/src/main/java/com/robertx22/mine_and_slash/aoe_data/database/perks/PerkBuilder.java#30-167
2. 生成した ID を `SchoolBuilder.add(id, PointData)` で SpellSchool の座標へ割り当て。@Mine-And-Slash-Rework/src/main/java/com/robertx22/mine_and_slash/aoe_data/database/spell_schools/SchoolBuilder.java#21-34
3. プレイヤーは `SpellSchoolsData` に投資レベルが保存され、`StatContext` に変換される。@Mine-And-Slash-Rework/src/main/java/com/robertx22/mine_and_slash/saveclasses/spells/SpellSchoolsData.java#23-185

---

## 4. Perk データモデル

`Perk` クラスの主要フィールド。@Mine-And-Slash-Rework/src/main/java/com/robertx22/mine_and_slash/database/data/perks/Perk.java#32-156

- `type` : 表示や枠線に利用する `PerkType`
- `id` : 一意識別子
- `icon` : GUI 用アイコンパス
- `max_lvls` : SpellSchool で投資できる最大レベル
- `stats` : `OptScaleExactStat` のリスト（効果本体）
- `is_entry` : ツリー開始ノードかどうか
- `one_kind` : 同系統排他タグ（同じタグを持つ Perk を同時取得不可）

### 4.1 SpellSchool における PerkType

| PerkType | SpellSchool での役割 | 備考 |
| -------- | ------------------- | ---- |
| `STAT` | 通常パッシブ／スペル習得 | 白枠表示 |
| `SPECIAL` | Big Stat／ソケット等 | 紫枠。大型表示 |
| `MAJOR` | 大型パッシブ（トレードオフなど） | 赤枠。`locname` を個別設定 |
| `ASC` | アセンション専用 Perk | Ascendancy ポイントを消費 |

`Perk#getPointType()` が `SPELL` か `PASSIVE` を返し、SpellSchool UI のポイント消費ロジックで利用される。@Mine-And-Slash-Rework/src/main/java/com/robertx22/mine_and_slash/database/data/perks/Perk.java#48-70

---

## 5. PerkBuilder の実用パターン

| メソッド | 主用途 | 自動設定される内容 |
| -------- | ------ | ------------------ |
| `spell(id)` | スペル習得ノード | `LearnSpellStat`, スペル最大レベル, スペルアイコン |
| `passive(id, maxLvl, stats...)` | 複数レベルのパッシブ | `PerkType.STAT`, `textures/gui/spells/passives/{id}.png` |
| `stat(id, stats...)` | 単発ステータス強化 | 最初の Stat アイコンを流用 |
| `bigStat(id, locname, stats...)` | SPECIAL 枠の大型強化 | `PerkType.SPECIAL` と表示名を設定 |
| `gameChanger(id, locname, stats...)` | MAJOR 枠 | `PerkType.MAJOR`, 専用アイコン |
| `socket()` | Jewel ソケット | JewelSocketStat を自動付与 |
| `ascPoint(id, stats...)` | Ascendancy 用ノード | `textures/gui/asc_classes/perk/{id}.png` |

複数 Stat を渡した場合は順に `stats` リストへ追加され、後段のツールチップ整列処理へ渡る。@Mine-And-Slash-Rework/src/main/java/com/robertx22/mine_and_slash/aoe_data/database/perks/PerkBuilder.java#46-147

---

## 6. OptScaleExactStat の扱い

- 1 レベルあたりのスタット変化を表現。`value`・`stat`・`ModType` を指定する。
- SpellSchool ではプレイヤーレベル依存の指数スケールは基本的に使用せず、`SpellSchoolsData` 側でレベル差分のパーセンテージを加算している。@Mine-And-Slash-Rework/src/main/java/com/robertx22/mine_and_slash/saveclasses/spells/SpellSchoolsData.java#170-184
- スペル習得 Perk も `OptScaleExactStat(1, LearnSpellStat(spell))` として定義される。@Mine-And-Slash-Rework/src/main/java/com/robertx22/mine_and_slash/aoe_data/database/perks/PerkBuilder.java#30-44

---

## 7. SpellSchool への配置とレベル要求

`SpellSchool` は Perk ID と `PointData`（x,y）をマップに保持し、y 座標に応じてレベル要求を決定する。@Mine-And-Slash-Rework/src/main/java/com/robertx22/mine_and_slash/database/data/spell_school/SpellSchool.java#30-53

### 7.1 レベル要求ロジック

- `lvl_reqs` デフォルト: `[1, 5, 10, 15, 20, 25, 30]`
- スペルランクアップ時は `GameBalanceConfig.player_points[PointType.SPELLS].points_per_lvl` による追加要求を計算。@Mine-And-Slash-Rework/src/main/java/com/robertx22/mine_and_slash/database/data/spell_school/SpellSchool.java#45-52

### 7.2 SchoolBuilder の利用

```kotlin
SchoolBuilder.of("druid", "Druid")
    .add("p_health_druid", PointData(1, 1))
    .add("barkskin", PointData(2, 1))
    .build()
```

`MAX_X_ROWS` / `MAX_Y_ROWS` の範囲チェックが行われ、同一座標の重複も弾かれる。@Mine-And-Slash-Rework/src/main/java/com/robertx22/mine_and_slash/aoe_data/database/spell_schools/SchoolBuilder.java#21-27

---

## 8. プレイヤーデータとポイント管理

`SpellSchoolsData` が取得状況を管理し、ポイント消費／リセット／ステータス適用を担当。@Mine-And-Slash-Rework/src/main/java/com/robertx22/mine_and_slash/saveclasses/spells/SpellSchoolsData.java#23-185

### 8.1 学習チェック

- ポイント残数、キャラクターレベル、SpellSchool のレベル要求、最大ランクを判定。@Mine-And-Slash-Rework/src/main/java/com/robertx22/mine_and_slash/saveclasses/spells/SpellSchoolsData.java#115-136
- Spell と Passive でポイントプールが分かれる (`PlayerPointsType.SPELLS` / `PASSIVES`)。@Mine-And-Slash-Rework/src/main/java/com/robertx22/mine_and_slash/saveclasses/spells/SpellSchoolsData.java#80-113

### 8.2 ステータス適用

- 投資レベル数に応じて `percentIncrease = (投資レベル-1) * 100` を設定し、`increaseByAddedPercent()` で累積倍率を更新。@Mine-And-Slash-Rework/src/main/java/com/robertx22/mine_and_slash/saveclasses/spells/SpellSchoolsData.java#170-184
- Spell Perk の場合は `LearnSpellStat` 経由で `SpellCastingData` にリンクし、ツールチップで習得ランクを表示する。@Mine-And-Slash-Rework/src/main/java/com/robertx22/mine_and_slash/database/data/perks/Perk.java#147-155

### 8.3 リセット

- `canUnlearn` はレベルやリセットポイントを確認し、可能な場合に 1 レベル減算。@Mine-And-Slash-Rework/src/main/java/com/robertx22/mine_and_slash/saveclasses/spells/SpellSchoolsData.java#138-168
- リセット後は不要なエントリを `removeUnlearnedPerks()` で除去。@Mine-And-Slash-Rework/src/main/java/com/robertx22/mine_and_slash/saveclasses/spells/SpellSchoolsData.java#41-77

---

## 9. 実装フロー（SpellSchool 向けパッシブ追加）

1. **Stat / StatEffect 準備**: 既存 Stat を利用するか、特殊挙動が必要なら追加実装。
2. **Perk を生成**: `PerkBuilder.passive` 等で効果と最大レベルを設定し登録。
3. **SpellSchool へ配置**: `SchoolBuilder.add` で座標割り当て。y 座標でレベル要求を調整。
4. **アセット配置**: `textures/gui/spells/passives/{perk_id}.png`（32×32 推奨）を追加。
5. **ローカライズ**: `mmorpg.talent.{perk_id}`（`Perk#locNameGroup = Talents`）で名前を登録。@Mine-And-Slash-Rework/src/main/java/com/robertx22/mine_and_slash/database/data/perks/Perk.java#171-183
6. **検証**: Dev Tools ログで未登録エラーがないか確認し、ゲーム内でポイント消費・リセット挙動をテスト。

---

## 10. アイコンとローカライズ

- **アイコン**
  - パッシブ: `SlashRef.MODID/textures/gui/spells/passives/{id}.png`
  - スペル: 対象スペルのアイコンを使用
  - MAJOR: `textures/gui/stat_icons/game_changers/{id}.png`
- **推奨解像度**: 32×32 px
- **表示名**: `mmorpg.talent.{id}` キーに追加（英語/日本語双方）。

---

## 11. デバッグ Tips

- 表示されない: SpellSchool の `perks` に ID が登録されているか確認。
- レベル条件を満たしているのに取得不可: `SpellSchool.isLevelEnoughForSpellLevelUp` が現在ランクと追加要求を計算しているため、プレイヤーレベル不足がないか確認。@Mine-And-Slash-Rework/src/main/java/com/robertx22/mine_and_slash/database/data/spell_school/SpellSchool.java#41-52
- 効果が反映されない: `OptScaleExactStat` の `stat` が ExileDB に登録済みか、ModType の整合性を再確認。

---

## 12. 参考リファレンス

- `Perk`: @Mine-And-Slash-Rework/src/main/java/com/robertx22/mine_and_slash/database/data/perks/Perk.java#32-275
- `PerkBuilder`: @Mine-And-Slash-Rework/src/main/java/com/robertx22/mine_and_slash/aoe_data/database/perks/PerkBuilder.java#17-167
- `SpellSchool`: @Mine-And-Slash-Rework/src/main/java/com/robertx22/mine_and_slash/database/data/spell_school/SpellSchool.java#21-98
- `SchoolBuilder`: @Mine-And-Slash-Rework/src/main/java/com/robertx22/mine_and_slash/aoe_data/database/spell_schools/SchoolBuilder.java#8-34
- `SpellSchoolsData`: @Mine-And-Slash-Rework/src/main/java/com/robertx22/mine_and_slash/saveclasses/spells/SpellSchoolsData.java#23-188
- 併読推奨: [06_SpellSchool.md](./06_SpellSchool.md), [07_PassiveSkillSystem.md](./07_PassiveSkillSystem.md)
