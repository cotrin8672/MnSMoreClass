# 🤖 Claude Code - Mine and Slash 開発ガイド

## 📂 プロジェクト構造

### メインプロジェクト
- **パス**: `C:\Users\gummy\IdeaProjects\Mine-And-Slash-Rework`
- **説明**: Mine and Slash mod本体のコードベース

### 拡張プロジェクト (このリポジトリ)
- **パス**: `C:\Users\gummy\IdeaProjects\MnSMoreClass`
- **説明**: 新しいSpellSchool(クラス)を追加するプロジェクト

---

## 🔍 コード検索方法

### 1. CodeContextMCP (セマンティック検索)

#### インデックス化
```
プロジェクトを初めて検索する前に:
- ツール: mcp__code-context__index_codebase
- パス: C:\Users\gummy\IdeaProjects\Mine-And-Slash-Rework
- splitter: ast
```

#### 検索例
```
自然言語で検索可能:
- "SpellSchool class definition and main implementation"
- "how to register and create new spell schools"
- "SpellSchoolsData player data saving and perk allocation"
```

### 2. Serena (シンボリック検索)

#### プロジェクトのアクティベート
```
最初に必要:
- ツール: mcp__serena__activate_project
- プロジェクト名: "Mine-And-Slash-Rework"
```

#### パターン検索
```
正規表現でコード検索:
- パターン: "SpellSchool"
- paths_include_glob: "**/*.java"
- restrict_search_to_code_files: true
```

#### シンボル検索
```
クラス/メソッドを効率的に探索:
- get_symbols_overview: ファイルの構造を取得
- find_symbol: 特定のシンボルを検索
- find_referencing_symbols: 使用箇所を検索
```

---

## 🗺️ 重要なファイルパス

### SpellSchool関連
```
コアクラス:
src/main/java/com/robertx22/mine_and_slash/database/data/spell_school/SpellSchool.java

プレイヤーデータ:
src/main/java/com/robertx22/mine_and_slash/saveclasses/spells/SpellSchoolsData.java

登録・ビルダー:
src/main/java/com/robertx22/mine_and_slash/aoe_data/database/spell_schools/SpellSchoolsAdder.java
src/main/java/com/robertx22/mine_and_slash/aoe_data/database/spell_schools/SchoolBuilder.java

Perk (スキル):
src/main/java/com/robertx22/mine_and_slash/database/data/perks/Perk.java
src/main/java/com/robertx22/mine_and_slash/aoe_data/database/perks/PerkBuilder.java
```

### スペル関連
```
スペル登録:
src/main/java/com/robertx22/mine_and_slash/aoe_data/database/spells/Spells.java

各属性スペル:
src/main/java/com/robertx22/mine_and_slash/aoe_data/database/spells/schools/NatureSpells.java
src/main/java/com/robertx22/mine_and_slash/aoe_data/database/spells/schools/HolySpells.java
src/main/java/com/robertx22/mine_and_slash/aoe_data/database/spells/schools/SummonSpells.java
... (他の属性も同様)
```

### GUI関連
```
SpellSchool画面:
src/main/java/com/robertx22/mine_and_slash/gui/screens/spell/SpellSchoolScreen.java

ボタン類:
src/main/java/com/robertx22/mine_and_slash/gui/screens/spell/LearnClassPointButton.java
src/main/java/com/robertx22/mine_and_slash/gui/screens/spell/SchoolButton.java
```

### ネットワーク
```
パケット:
src/main/java/com/robertx22/mine_and_slash/vanilla_mc/packets/AllocateClassPointPacket.java
```

---

## 🏗️ SpellSchool実装の基本構造

### 1. グリッドシステム
- **サイズ**: 10 (X) × 7 (Y)
- **レベル要件**: Y座標ごとに `[1, 5, 10, 15, 20, 25, 30]`

### 2. 既存のSpellSchool (6種)
1. **Sorcerer** - 火/氷/ゴーレム
2. **Warlock** - 召喚/カオス/呪い
3. **Minstrel** - ソング/ヒール
4. **Hunter** - 弓/罠
5. **Shaman** - 雷/トーテム
6. **Warrior** - 近接戦闘

### 3. ポイントシステム
- **SPELL** ポイント: アクティブスペル用
- **PASSIVE** ポイント: パッシブスキル用
- プレイヤーは**最大2つのスクール**を選択可能

---

## 📝 新しいSpellSchool追加の手順

### Step 1: スペルの定義
```java
// 新しいスペルクラスを作成
public class DruidSpells implements ExileRegistryInit {
    public static String HEALING_BREEZE = "healing_breeze";
    public static String LEAF_STORM = "leaf_storm";
    // ...
}
```

### Step 2: Perkの作成
```java
// PerkBuilder使用
PerkBuilder.spell("healing_breeze").build();
PerkBuilder.passive("resonance_with_nature", stats).build();
```

### Step 3: SpellSchoolの登録
```java
// SpellSchoolsAdder.java に追加
SchoolBuilder.of("druid", "Druid")
    .add(DruidSpells.HEALING_BREEZE, new PointData(1, 0))
    .add(DruidSpells.LEAF_STORM, new PointData(2, 0))
    .add(DruidSpells.SPIRIT_BLOOM, new PointData(1, 1))
    // ... グリッド配置
    .build();
```

### Step 4: リソース追加
```
アイコン:
assets/mine_and_slash/textures/gui/asc_classes/class/druid.png

背景:
assets/mine_and_slash/textures/gui/asc_classes/background/druid.png

言語ファイル:
assets/mine_and_slash/lang/en_us.json
```

---

## 🎯 ドルイドクラス実装計画

詳細は `druid.md` を参照:
- **アクティブスキル**: 10種 (回復/召喚/変身/拘束)
- **パッシブスキル**: 8種 (自然強化/精霊強化/生存性)
- **役割**: 純サポート/ヒーラー
- **武器**: 杖

---

## 💡 開発Tips

### Serenaツールの活用
- **全ファイル読み込みは避ける**: `get_symbols_overview` → `find_symbol` の順で
- **パターン検索を活用**: 正規表現で効率的に検索
- **シンボル編集ツール使用**: `replace_symbol_body`, `insert_after_symbol` など

### CodeContextMCPの活用
- **自然言語クエリ**: 「○○の実装方法」などで検索可能
- **セマンティックランキング**: 関連度の高い順に結果表示
- **大規模検索に最適**: パターンが不明確な場合に有効

### 検索の使い分け
- **具体的な名前がわかる**: Serenaのシンボル検索
- **概念的に探したい**: CodeContextMCPのセマンティック検索
- **正規表現で絞り込む**: Serenaのパターン検索

---

## 🔗 参考リソース

- Mine and Slash Wiki: プロジェクト内の `docs/` フォルダ
- 既存スペル実装: `aoe_data/database/spells/schools/` 配下
- 既存SpellSchool: `SpellSchoolsAdder.java` の6クラス定義

---

**最終更新**: 2025-11-04
**作成者**: Claude Code
