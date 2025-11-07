package io.github.cotrin8672.mnsmoreclass.registry

import com.robertx22.mine_and_slash.aoe_data.database.spells.SpellBuilder
import com.robertx22.mine_and_slash.database.data.spells.components.Spell
import com.robertx22.mine_and_slash.database.data.spells.components.SpellConfiguration
import com.robertx22.mine_and_slash.database.data.spells.spell_classes.CastingWeapon
import com.robertx22.mine_and_slash.uncommon.enumclasses.PlayStyle

object SpellRegistry : IMnsRegistry<Spell> by MnsRegistryDelegate() {
    fun register() {
        SpellBuilder.of(
            "simple_heal",
            PlayStyle.INT,
            SpellConfiguration.Builder.instant(30, 0),
            "Simple Heal",
            listOf(),
        )
            .weaponReq(CastingWeapon.ANY_WEAPON)
            .manualDesc("simple heal")
            .buildForEffect()
            .run(::add)
    }
}
