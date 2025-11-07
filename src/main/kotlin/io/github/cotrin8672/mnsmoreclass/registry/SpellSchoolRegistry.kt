package io.github.cotrin8672.mnsmoreclass.registry

import com.robertx22.mine_and_slash.database.data.spell_school.SpellSchool
import com.robertx22.mine_and_slash.saveclasses.PointData
import io.github.cotrin8672.mnsmoreclass.spell_school.SchoolBuilderKt

object SpellSchoolRegistry : IMnsRegistry<SpellSchool> by MnsRegistryDelegate() {
    fun register() {
        SchoolBuilderKt.of("druid", "Druid")
            .add("simple_heal", PointData(1, 0))
            .build()
            .run(::add)
    }
}
