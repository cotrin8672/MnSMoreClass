package io.github.cotrin8672.mnsmoreclass.registry

import com.robertx22.mine_and_slash.database.data.spell_school.SpellSchool
import com.robertx22.mine_and_slash.saveclasses.PointData
import io.github.cotrin8672.mnsmoreclass.content.ninja.NinjaSpells
import io.github.cotrin8672.mnsmoreclass.perk.DruidPassives
import io.github.cotrin8672.mnsmoreclass.perk.NinjaPassives
import io.github.cotrin8672.mnsmoreclass.spell_school.SchoolBuilderKt

object SpellSchoolRegistry : IMnsRegistry<SpellSchool> by MnsRegistryDelegate() {
    fun register() {
        SchoolBuilderKt.of("druid", "Druid")
            // Row 0: 基礎ステータス
            .add(DruidPassives.HEALTH_ID, PointData(8, 1))
            .add(DruidPassives.MANA_REGEN_ID, PointData(8, 2))

            // Row 1: 攻撃支援
            .add(DruidPassives.AREA_OF_EFFECT_ID, PointData(9, 2))
            .add(DruidPassives.DOT_DAMAGE_ID, PointData(8, 5))

            // Row 2: 回復支援
            .add(DruidPassives.HEAL_STRENGTH_ID, PointData(9, 4))
            .add(DruidPassives.EFFECT_DURATION_ID, PointData(8, 3))

            // Row 3: 防御支援
            .add(DruidPassives.ARMOR_ID, PointData(9, 6))
            .add(DruidPassives.DEBUFF_RESIST_ID, PointData(8, 6))

            // 既存スペル
            .add(SpellRegistry.BARKSKIN, PointData(3, 2))
            .add(SpellRegistry.PURIFICATION, PointData(5, 2))
            .add(SpellRegistry.WEAKNESS_AURA, PointData(5, 4))
            .add(SpellRegistry.ENTANGLING_THORNS, PointData(4, 6))
            .add(SpellRegistry.SAKURA_BLOOM, PointData(5, 6))
            .add(SpellRegistry.UNDYING_BLESSING, PointData(3, 6))
            .build()
            .add()

        SchoolBuilderKt.of("ninja", "Ninja")
            // Row 0: 基礎ステータス
            .add(NinjaPassives.ATTACK_SPEED_ID, PointData(8, 0))
            .add(NinjaPassives.HEALTH_ID, PointData(9, 0))

            // Row 1: 攻撃支援
            .add(NinjaPassives.CRIT_RATE_ID, PointData(8, 2))
            .add(NinjaPassives.DODGE_RATING_ID, PointData(9, 2))

            // Row 2: クールダウン/火力支援
            .add(NinjaPassives.SKILL_HASTE_ID, PointData(8, 4))
            .add(NinjaPassives.WEAPON_DAMAGE_ID, PointData(9, 4))

            // Row 3: 防御・移動支援
            .add(NinjaPassives.PHYSICAL_RESIST_ID, PointData(8, 6))
            .add(NinjaPassives.MOVE_SPEED_ID, PointData(9, 6))

            // 既存スペル
            .add(NinjaSpells.SHADOW_WALK, PointData(0, 1))
            .add(NinjaSpells.SHURIKEN, PointData(1, 1))
            .add(NinjaSpells.SHUNJIN, PointData(2, 2))
            .add(NinjaSpells.SWING_SCYTHE, PointData(5, 2))
            .add(NinjaSpells.TOXIC_EDGE, PointData(5, 4))
            .add(NinjaSpells.RYUUSEI_RANBU, PointData(6, 3))
            .add(NinjaSpells.SHADOW_SECRET, PointData(6, 6))
            .build()
            .add()
    }
}
