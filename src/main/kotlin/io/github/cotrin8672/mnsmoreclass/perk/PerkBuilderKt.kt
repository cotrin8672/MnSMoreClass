package io.github.cotrin8672.mnsmoreclass.perk

import com.robertx22.mine_and_slash.database.OptScaleExactStat
import com.robertx22.mine_and_slash.database.data.perks.Perk
import com.robertx22.mine_and_slash.mmorpg.SlashRef
import net.minecraft.resources.ResourceLocation

/**
 * Mine and Slash の `PerkBuilder` を Kotlin で再構成したユーティリティクラス。
 *
 * - ExileDB 登録やデータベース参照を行わず、純粋に `Perk` インスタンスを生成する。
 * - Mine and Slash 本体のビルダーパターンを踏襲しつつ、Kotlin DSL で柔軟に設定できる。
 */
class PerkBuilderKt private constructor(private val id: String) {
    private val stats = mutableListOf<OptScaleExactStat>()
    private var type: Perk.PerkType = Perk.PerkType.STAT
    private var iconPath: String? = null
    private var locName: String? = null
    private var oneOfAKind: String? = null
    private var isEntry: Boolean = false
    private var maxLevels: Int = 1

    fun addStat(stat: OptScaleExactStat) = apply { stats.add(stat) }

    fun addStats(vararg values: OptScaleExactStat) = apply { stats.addAll(values) }

    fun type(value: Perk.PerkType) = apply { type = value }

    fun icon(location: ResourceLocation) = apply { iconPath = location.toString() }

    fun icon(path: String) = apply { iconPath = path }

    fun locName(value: String?) = apply { locName = value }

    fun oneOfAKind(value: String?) = apply { oneOfAKind = value }

    fun entry(value: Boolean = true) = apply { isEntry = value }

    fun maxLevels(levels: Int) = apply {
        require(levels > 0) { "maxLevels must be positive" }
        maxLevels = levels
    }

    fun build(): Perk {
        val collectedStats = this@PerkBuilderKt.stats
        return Perk().apply {
            id = this@PerkBuilderKt.id
            type = this@PerkBuilderKt.type
            max_lvls = this@PerkBuilderKt.maxLevels
            is_entry = this@PerkBuilderKt.isEntry
            one_kind = this@PerkBuilderKt.oneOfAKind
            this@PerkBuilderKt.locName?.let { locname = it } // 直接フィールド代入でOK
            this@PerkBuilderKt.iconPath?.let { icon = it }

            if (collectedStats.isNotEmpty()) {
                stats = ArrayList(collectedStats) // or collectedStats.toMutableList()
            }
        }
    }

    companion object {
        fun of(id: String) = PerkBuilderKt(id)

        fun passive(id: String, maxLevel: Int, vararg statValues: OptScaleExactStat): Perk {
            require(maxLevel > 0) { "maxLevel must be positive" }
            return of(id)
                .type(Perk.PerkType.STAT)
                .icon(passiveIcon(id))
                .maxLevels(maxLevel)
                .apply { if (statValues.isNotEmpty()) addStats(*statValues) }
                .build()
        }

        fun stat(id: String, icon: ResourceLocation, vararg stats: OptScaleExactStat): Perk {
            require(stats.isNotEmpty()) { "stat perk requires at least one OptScaleExactStat" }
            return of(id)
                .type(Perk.PerkType.STAT)
                .icon(icon)
                .addStats(*stats)
                .build()
        }

        fun gameChanger(id: String, locName: String, vararg stats: OptScaleExactStat): Perk {
            require(stats.isNotEmpty()) { "game changer perk requires stats" }
            return of(id)
                .type(Perk.PerkType.MAJOR)
                .icon(gameChangerIcon(id))
                .locName(locName)
                .addStats(*stats)
                .build()
        }

        fun bigStat(id: String, locName: String, icon: ResourceLocation, vararg stats: OptScaleExactStat): Perk {
            require(stats.isNotEmpty()) { "big stat perk requires stats" }
            return of(id)
                .type(Perk.PerkType.SPECIAL)
                .icon(icon)
                .locName(locName)
                .addStats(*stats)
                .build()
        }

        fun ascPoint(id: String, vararg stats: OptScaleExactStat): Perk {
            return of(id)
                .type(Perk.PerkType.STAT)
                .icon(ascIcon(id))
                .addStats(*stats)
                .build()
        }

        private fun passiveIcon(id: String) = ResourceLocation.fromNamespaceAndPath(
            SlashRef.MODID,
            "textures/gui/spells/passives/$id.png"
        )

        private fun gameChangerIcon(id: String) = ResourceLocation.fromNamespaceAndPath(
            SlashRef.MODID,
            "textures/gui/stat_icons/game_changers/$id.png"
        )

        private fun ascIcon(id: String) = ResourceLocation.fromNamespaceAndPath(
            SlashRef.MODID,
            "textures/gui/asc_classes/perk/$id.png"
        )
    }
}

fun perk(id: String, block: PerkBuilderKt.() -> Unit): Perk {
    return PerkBuilderKt.of(id).apply(block).build()
}

