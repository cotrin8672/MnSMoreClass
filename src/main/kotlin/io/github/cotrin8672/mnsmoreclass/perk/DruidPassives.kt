package io.github.cotrin8672.mnsmoreclass.perk

import com.robertx22.mine_and_slash.aoe_data.database.stats.*
import com.robertx22.mine_and_slash.database.OptScaleExactStat
import com.robertx22.mine_and_slash.database.data.perks.Perk
import com.robertx22.mine_and_slash.database.data.stats.types.defense.Armor
import com.robertx22.mine_and_slash.database.data.stats.types.resources.health.Health
import com.robertx22.mine_and_slash.database.data.stats.types.resources.mana.ManaRegen
import com.robertx22.mine_and_slash.uncommon.enumclasses.ModType

/**
 * Druid 用のパッシブ Perk 定義。
 * Passive.md の効果/Lv を `OptScaleExactStat` で表現し、PerkRegistry から登録できるよう公開する。
 */
object DruidPassives {
    const val HEALTH_ID = "p_health_druid"
    const val MANA_REGEN_ID = "p_mana_regen_druid"
    const val AREA_OF_EFFECT_ID = "p_area_druid"
    const val DOT_DAMAGE_ID = "p_dot_dmg_druid"
    const val HEAL_STRENGTH_ID = "p_heal_str_druid"
    const val EFFECT_DURATION_ID = "p_effect_dur_druid"
    const val ARMOR_ID = "p_armor_druid"
    const val DEBUFF_RESIST_ID = "p_debuff_resist_druid"

    val HEALTH: Perk = PerkBuilderKt.passive(
        HEALTH_ID,
        8,
        OptScaleExactStat(4f, Health.getInstance(), ModType.PERCENT)
    )

    val MANA_REGEN: Perk = PerkBuilderKt.passive(
        MANA_REGEN_ID,
        8,
        OptScaleExactStat(5f, ManaRegen.getInstance(), ModType.PERCENT)
    )

    val AREA_OF_EFFECT: Perk = PerkBuilderKt.passive(
        AREA_OF_EFFECT_ID,
        8,
        OptScaleExactStat(4f, SpellChangeStats.INCREASED_AREA.get())
    )

    val DOT_DAMAGE: Perk = PerkBuilderKt.passive(
        DOT_DAMAGE_ID,
        8,
        OptScaleExactStat(4f, OffenseStats.DOT_DAMAGE.get())
    )

    val HEAL_STRENGTH: Perk = PerkBuilderKt.passive(
        HEAL_STRENGTH_ID,
        8,
        OptScaleExactStat(3f, ResourceStats.HEAL_STRENGTH.get())
    )

    val EFFECT_DURATION: Perk = PerkBuilderKt.passive(
        EFFECT_DURATION_ID,
        8,
        OptScaleExactStat(5f, EffectStats.EFFECT_DURATION_YOU_CAST.get())
    )

    val ARMOR: Perk = PerkBuilderKt.passive(
        ARMOR_ID,
        8,
        OptScaleExactStat(3f, Armor.getInstance(), ModType.PERCENT)
    )

    val DEBUFF_RESIST: Perk = PerkBuilderKt.passive(
        DEBUFF_RESIST_ID,
        8,
        OptScaleExactStat(-4f, DefenseStats.DAMAGE_RECEIVED.get())
    )

    val ALL: List<Perk> = listOf(
        HEALTH,
        MANA_REGEN,
        AREA_OF_EFFECT,
        DOT_DAMAGE,
        HEAL_STRENGTH,
        EFFECT_DURATION,
        ARMOR,
        DEBUFF_RESIST
    )
}
