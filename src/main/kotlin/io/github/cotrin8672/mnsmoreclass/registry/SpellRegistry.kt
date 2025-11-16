package io.github.cotrin8672.mnsmoreclass.registry

import com.robertx22.mine_and_slash.a_libraries.player_animations.SpellAnimations
import com.robertx22.mine_and_slash.aoe_data.database.spells.PartBuilder
import com.robertx22.mine_and_slash.aoe_data.database.spells.SpellBuilder
import com.robertx22.mine_and_slash.database.data.spells.components.Spell
import com.robertx22.mine_and_slash.database.data.spells.components.SpellConfiguration
import com.robertx22.mine_and_slash.database.data.spells.spell_classes.CastingWeapon
import com.robertx22.mine_and_slash.tags.all.SpellTags
import com.robertx22.mine_and_slash.uncommon.enumclasses.PlayStyle
import io.github.cotrin8672.mnsmoreclass.content.ninja.NinjaSpells
import io.github.cotrin8672.mnsmoreclass.spell_action.PartBuilderKt
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.sounds.SoundEvents

object SpellRegistry : IMnsRegistry<Spell> by MnsRegistryDelegate() {
        const val BARKSKIN = "barkskin"
        const val PURIFICATION = "purification"
        const val WEAKNESS_AURA = "weakness_aura"

        fun register() {
                SpellBuilder.of(
                                BARKSKIN,
                                PlayStyle.INT,
                                SpellConfiguration.Builder.instant(10, 10 * 20)
                                        .setChargesAndRegen(BARKSKIN, 3, 20 * 20),
                                "BarkSkin",
                                listOf(SpellTags.BUFF, SpellTags.PHYSICAL, SpellTags.area),
                        )
                        .weaponReq(CastingWeapon.MAGE_WEAPON)
                        .onCast(
                                PartBuilder.giveExileEffectToAlliesInRadius(
                                        5.0,
                                        BARKSKIN,
                                        25.0 * 20
                                )
                        )
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
                        .apply { PartBuilderKt.purifyAlliesInRadius(6.0).forEach(::onCast) }
                        .onCast(PartBuilder.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 0.8, 1.2))
                        .onCast(
                                PartBuilder.groundEdgeParticles(
                                        ParticleTypes.HAPPY_VILLAGER,
                                        120.0,
                                        6.0,
                                        0.0
                                )
                        )
                        .buildForEffect()
                        .add()

                SpellBuilder.of(
                                WEAKNESS_AURA,
                                PlayStyle.INT,
                                SpellConfiguration.Builder.instant(15, 20 * 35),
                                "Weakness Aura",
                                listOf(SpellTags.magic, SpellTags.area, SpellTags.curse, SpellTags.LIGHTNING),
                        )
                        .weaponReq(CastingWeapon.MAGE_WEAPON)
                        .manualDesc(
                                "Project a weakening aura, inflicting enemies in front with -30% Armor for 8 seconds."
                        )
                        .onCast(PartBuilder.playSound(SoundEvents.SLIME_ATTACK, 1.0, 0.7))
                        .onCast(PartBuilder.groundEdgeParticles(ParticleTypes.SPORE_BLOSSOM_AIR, 100.0, 4.0, 1.0))
                        .onCast(PartBuilder.addExileEffectToEnemiesInFront(WEAKNESS_AURA, 7.0, 4.0, 160.0))
                        .animations(SpellAnimations.HAND_UP_CAST, SpellAnimations.CAST_FINISH)
                        .buildForEffect()
                        .add()

                NinjaSpells.getAll().onEach { it.add() }
        }
}
