package io.github.cotrin8672.mnsmoreclass.registry

import com.robertx22.mine_and_slash.database.data.spell_school.SpellSchool
import com.robertx22.mine_and_slash.saveclasses.PointData
import io.github.cotrin8672.mnsmoreclass.content.ninja.NinjaSpells
import io.github.cotrin8672.mnsmoreclass.spell_school.SchoolBuilderKt

object SpellSchoolRegistry : IMnsRegistry<SpellSchool> by MnsRegistryDelegate() {
    fun register() {
        SchoolBuilderKt.of("druid", "Druid")
            .add(SpellRegistry.BARKSKIN, PointData(1, 0))
            .add(SpellRegistry.PURIFICATION, PointData(3, 1))
            .add(SpellRegistry.WEAKNESS_AURA, PointData(5, 3))
            .add(SpellRegistry.ENTANGLING_THORNS, PointData(4, 6))
            .add(SpellRegistry.SAKURA_BLOOM, PointData(5, 4))
            .build()
            .add()

        SchoolBuilderKt.of("ninja", "Ninja")
            .add(NinjaSpells.SHADOW_WALK, PointData(1, 0))
            .add(NinjaSpells.SHURIKEN, PointData(2, 0))
            .add(NinjaSpells.SHUNJIN, PointData(3, 1))
            .add(NinjaSpells.SWING_SCYTHE, PointData(4, 1))
            .add(NinjaSpells.TOXIC_EDGE, PointData(4, 3))
            .add(NinjaSpells.RYUUSEI_RANBU, PointData(5, 2))
            .add(NinjaSpells.SHADOW_SECRET, PointData(5, 6))
            .build()
            .add()
    }
}
