package io.github.cotrin8672.mnsmoreclass.spell_school

import com.robertx22.mine_and_slash.database.data.spell_school.SpellSchool
import com.robertx22.mine_and_slash.saveclasses.PointData

/**
 * SpellSchoolを生成するためのKotlinビルダー
 * Mine and Slashの元のBuilderと異なり、レジストリ登録を行わない
 */
class SchoolBuilderKt private constructor(
    private val id: String,
    private val name: String,
) {
    private val perks = mutableMapOf<String, PointData>()

    /**
     * スペルまたはパークをスクールに追加
     *
     * @param id スペル/パークのID
     * @param point ツリー上の座標 (x, y)
     * @throws IllegalArgumentException 座標が範囲外、または重複している場合
     */
    fun add(id: String, point: PointData) = apply {
        require(point.x in 0..SpellSchool.MAX_X_ROWS) {
            "X coordinate must be between 0 and ${SpellSchool.MAX_X_ROWS}, but was ${point.x}"
        }
        require(point.y in 0..SpellSchool.MAX_Y_ROWS) {
            "Y coordinate must be between 0 and ${SpellSchool.MAX_Y_ROWS}, but was ${point.y}"
        }
        require(!perks.values.any { it == point }) {
            "Point ($point) is already occupied in the spell school"
        }

        perks[id] = point
    }

    /**
     * 複数のスペル/パークを一度に追加
     *
     * @param entries IDとPointDataのペアのリスト
     */
    fun addAll(vararg entries: Pair<String, PointData>) = apply {
        entries.forEach { (id, point) ->
            add(id, point)
        }
    }

    /**
     * SpellSchoolインスタンスを生成
     *
     * @return 設定された値に基づくSpellSchool
     */
    fun build(): SpellSchool {
        return SpellSchool().apply {
            this.id = this@SchoolBuilderKt.id
            this.locname = this@SchoolBuilderKt.name
            this.perks.putAll(this@SchoolBuilderKt.perks)
        }
    }

    companion object {
        /**
         * SpellSchoolBuilderKtを作成
         *
         * @param id SpellSchoolの一意なID
         * @param name SpellSchoolの表示名
         * @return 新しいSpellSchoolBuilderKtインスタンス
         */
        fun of(id: String, name: String) = SchoolBuilderKt(id, name)
    }
}

/**
 * Kotlin DSLスタイルでSpellSchoolを作成する便利関数
 *
 * 使用例:
 * ```kotlin
 * val school = spellSchool("druid", "Druid") {
 *     add("circle_of_healing", PointData(1, 0))
 *     add("thorn_bush", PointData(1, 1))
 *     add("entangling_seed", PointData(1, 2))
 * }
 * ```
 *
 * @param id SpellSchoolのID
 * @param name SpellSchoolの表示名
 * @param block ビルダーの設定ブロック
 * @return 構築されたSpellSchool
 */
fun spellSchool(id: String, name: String, block: SchoolBuilderKt.() -> Unit): SpellSchool {
    return SchoolBuilderKt.of(id, name).apply(block).build()
}
