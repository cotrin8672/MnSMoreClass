package io.github.cotrin8672.mnsmoreclass.perk

import com.robertx22.mine_and_slash.aoe_data.database.stats.DefenseStats
import com.robertx22.mine_and_slash.aoe_data.database.stats.OffenseStats
import com.robertx22.mine_and_slash.aoe_data.database.stats.SpellChangeStats
import com.robertx22.mine_and_slash.aoe_data.database.stats.old.DatapackStats
import com.robertx22.mine_and_slash.database.OptScaleExactStat
import com.robertx22.mine_and_slash.database.data.perks.Perk
import com.robertx22.mine_and_slash.database.data.stats.types.defense.DodgeRating
import com.robertx22.mine_and_slash.database.data.stats.types.generated.BonusAttackDamage
import com.robertx22.mine_and_slash.database.data.stats.types.resources.health.Health
import com.robertx22.mine_and_slash.uncommon.enumclasses.Elements
import com.robertx22.mine_and_slash.uncommon.enumclasses.ModType
import io.github.cotrin8672.mnsmoreclass.registry.StatRegistry

/**
 * Ninja 用のパッシブ Perk 定義。
 * Passive.md の効果/Lv を `OptScaleExactStat` で表現し、PerkRegistry から登録できるよう公開する。
 */
object NinjaPassives {
    const val ATTACK_SPEED_ID = "p_attack_speed_ninja"
    const val HEALTH_ID = "p_health_ninja"
    const val CRIT_RATE_ID = "p_crit_rate_ninja"
    const val DODGE_RATING_ID = "p_dodge_rating_ninja"
    const val SKILL_HASTE_ID = "p_skill_haste_ninja"
    const val WEAPON_DAMAGE_ID = "p_weapon_damage_ninja"
    const val PHYSICAL_RESIST_ID = "p_physical_resist_ninja"
    const val MOVE_SPEED_ID = "p_move_speed_ninja"

    val ATTACK_SPEED: Perk = PerkBuilderKt.passive(
        ATTACK_SPEED_ID,
        8,
        OptScaleExactStat(3f, StatRegistry.ATTACK_SPEED, ModType.FLAT)
    )

    val HEALTH: Perk = PerkBuilderKt.passive(
        HEALTH_ID,
        8,
        OptScaleExactStat(4f, Health.getInstance(), ModType.PERCENT)
    )

    val CRIT_RATE: Perk = PerkBuilderKt.passive(
        CRIT_RATE_ID,
        8,
        OptScaleExactStat(3f, OffenseStats.CRIT_CHANCE.get(), ModType.PERCENT)
    )

    val DODGE_RATING: Perk = PerkBuilderKt.passive(
        DODGE_RATING_ID,
        8,
        OptScaleExactStat(3f, DodgeRating.getInstance(), ModType.PERCENT)
    )

    val SKILL_HASTE: Perk = PerkBuilderKt.passive(
        SKILL_HASTE_ID,
        8,
        OptScaleExactStat(5f, SpellChangeStats.COOLDOWN_REDUCTION.get(), ModType.PERCENT)
    )

    val WEAPON_DAMAGE: Perk = PerkBuilderKt.passive(
        WEAPON_DAMAGE_ID,
        8,
        OptScaleExactStat(3f, BonusAttackDamage(Elements.Physical), ModType.PERCENT)
    )

    val PHYSICAL_RESIST: Perk = PerkBuilderKt.passive(
        PHYSICAL_RESIST_ID,
        8,
        OptScaleExactStat(-3f, DefenseStats.DAMAGE_RECEIVED.get(), ModType.PERCENT)
    )

    val MOVE_SPEED: Perk = PerkBuilderKt.passive(
        MOVE_SPEED_ID,
        8,
        OptScaleExactStat(2f, DatapackStats.MOVE_SPEED, ModType.MORE)
    )

    val ALL: List<Perk> = listOf(
        ATTACK_SPEED,
        HEALTH,
        CRIT_RATE,
        DODGE_RATING,
        SKILL_HASTE,
        WEAPON_DAMAGE,
        PHYSICAL_RESIST,
        MOVE_SPEED
    )
}
