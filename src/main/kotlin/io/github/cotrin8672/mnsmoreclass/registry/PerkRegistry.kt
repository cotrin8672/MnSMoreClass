package io.github.cotrin8672.mnsmoreclass.registry

import com.robertx22.mine_and_slash.database.OptScaleExactStat
import com.robertx22.mine_and_slash.database.data.perks.Perk
import com.robertx22.mine_and_slash.database.data.stats.types.LearnSpellStat
import com.robertx22.mine_and_slash.mmorpg.SlashRef
import io.github.cotrin8672.mnsmoreclass.perk.DruidPassives
import io.github.cotrin8672.mnsmoreclass.perk.NinjaPassives
import io.github.cotrin8672.mnsmoreclass.perk.perk
import net.minecraft.resources.ResourceLocation

object PerkRegistry : IMnsRegistry<Perk> by MnsRegistryDelegate() {
    fun register() {
        SpellRegistry.getAll().forEach { spell ->
            val perk = perk(spell.identifier) {
                icon(
                    ResourceLocation.fromNamespaceAndPath(
                        SlashRef.MODID,
                        "textures/gui/spells/icons/${spell.identifier}.png"
                    )
                )
                maxLevels(spell.max_lvl)
                addStat(OptScaleExactStat(1F, LearnSpellStat(spell)))
            }
            perk.add()
        }

        DruidPassives.ALL.forEach { it.add() }
        NinjaPassives.ALL.forEach { it.add() }
    }
}
