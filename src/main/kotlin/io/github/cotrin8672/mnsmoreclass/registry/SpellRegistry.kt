package io.github.cotrin8672.mnsmoreclass.registry

import com.robertx22.mine_and_slash.aoe_data.database.spells.PartBuilder
import com.robertx22.mine_and_slash.aoe_data.database.spells.SpellBuilder
import com.robertx22.mine_and_slash.database.data.spells.components.Spell
import com.robertx22.mine_and_slash.database.data.spells.components.SpellConfiguration
import com.robertx22.mine_and_slash.database.data.spells.spell_classes.CastingWeapon
import com.robertx22.mine_and_slash.tags.all.SpellTags
import com.robertx22.mine_and_slash.uncommon.enumclasses.PlayStyle
import io.github.cotrin8672.mnsmoreclass.spell_action.PartBuilderExtensions
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.sounds.SoundEvents

object SpellRegistry : IMnsRegistry<Spell> by MnsRegistryDelegate() {
    const val BARKSKIN = "barkskin"
    const val PURIFICATION = "purification"

    fun register() {
        SpellBuilder.of(
            BARKSKIN,
            PlayStyle.INT,
            SpellConfiguration.Builder.instant(10, 10 * 20).setChargesAndRegen(BARKSKIN, 3, 20 * 20),
            "BarkSkin",
            listOf(SpellTags.BUFF, SpellTags.PHYSICAL, SpellTags.area),
        )
            .weaponReq(CastingWeapon.MAGE_WEAPON)
            .onCast(PartBuilder.giveExileEffectToAlliesInRadius(5.0, BARKSKIN, 25.0 * 20))
            .onCast(PartBuilder.playSound(SoundEvents.EVOKER_CAST_SPELL, 1.0, 1.0))
            .onCast(PartBuilder.aoeParticles(ParticleTypes.ENCHANT, 90.0, 5.0))
            .buildForEffect()
            .add()

        SpellBuilder.of(
            PURIFICATION,
            PlayStyle.INT,
            SpellConfiguration.Builder.instant(30, 0)
                .setChargesAndRegen(PURIFICATION, 3, 20 * 60),
            "Purification",
            listOf(SpellTags.heal, SpellTags.area),
        )
            .weaponReq(CastingWeapon.MAGE_WEAPON)
            .apply {
                PartBuilderExtensions.purifyAlliesInRadius(6.0).forEach { onCast(it) }
            }
            .onCast(PartBuilder.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 0.8, 1.2))
            .onCast(PartBuilder.groundEdgeParticles(ParticleTypes.HAPPY_VILLAGER, 120.0, 6.0, 0.0))
            .buildForEffect()
            .add()
    }
}
