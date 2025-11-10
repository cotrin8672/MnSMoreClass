package io.github.cotrin8672.mnsmoreclass.registry

import com.robertx22.mine_and_slash.aoe_data.database.stats.DefenseStats
import com.robertx22.mine_and_slash.aoe_data.database.stats.base.EffectCtx
import com.robertx22.mine_and_slash.database.data.exile_effects.EffectType
import com.robertx22.mine_and_slash.database.data.exile_effects.ExileEffect
import com.robertx22.mine_and_slash.database.data.stats.types.resources.health.HealthRegen
import com.robertx22.mine_and_slash.tags.all.EffectTags
import com.robertx22.mine_and_slash.uncommon.enumclasses.Elements
import com.robertx22.mine_and_slash.uncommon.enumclasses.ModType
import io.github.cotrin8672.mnsmoreclass.exile_effect.ExileEffectBuilderKt

object ExileEffectRegistry : IMnsRegistry<ExileEffect> by MnsRegistryDelegate() {
    fun register() {
        ExileEffectBuilderKt.of(EffectCtx(SpellRegistry.BARKSKIN, "barkskin", Elements.Physical, EffectType.beneficial))
            .stat(-5f, -15f, DefenseStats.DAMAGE_RECEIVED.get(), ModType.MORE)
            .stat(1f, 2f, HealthRegen.getInstance(), ModType.FLAT)
            .maxStacks(3)
            .addTags(EffectTags.positive, EffectTags.defensive)
            .build()
            .add()
    }
}
