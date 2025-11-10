package io.github.cotrin8672.mnsmoreclass.registry

import com.robertx22.mine_and_slash.database.OptScaleExactStat
import com.robertx22.mine_and_slash.database.data.perks.Perk
import com.robertx22.mine_and_slash.database.data.stats.types.LearnSpellStat
import com.robertx22.mine_and_slash.mmorpg.SlashRef
import net.minecraft.resources.ResourceLocation

object PerkRegistry : IMnsRegistry<Perk> by MnsRegistryDelegate() {
    fun register() {
        SpellRegistry.getAll().forEach { spell ->
            val perk = Perk().apply {
                id = spell.identifier
                type = Perk.PerkType.STAT
                icon = ResourceLocation.fromNamespaceAndPath(
                    SlashRef.MODID,
                    "textures/gui/spells/icons/${spell.identifier}.png"
                ).toString()
                max_lvls = spell.max_lvl
                stats = listOf(OptScaleExactStat(1F, LearnSpellStat(spell)))
            }
            perk.add()
        }
    }
}
