package io.github.cotrin8672.mnsmoreclass.registry

import com.robertx22.mine_and_slash.a_libraries.player_animations.SpellAnimations
import com.robertx22.mine_and_slash.aoe_data.database.spells.PartBuilder
import com.robertx22.mine_and_slash.aoe_data.database.spells.SpellBuilder
import com.robertx22.mine_and_slash.database.data.spells.components.Spell
import com.robertx22.mine_and_slash.database.data.spells.components.SpellConfiguration
import com.robertx22.mine_and_slash.database.data.spells.components.actions.SpellAction
import com.robertx22.mine_and_slash.database.data.spells.components.selectors.BaseTargetSelector
import com.robertx22.mine_and_slash.database.data.spells.map_fields.MapField
import com.robertx22.mine_and_slash.database.data.spells.spell_classes.CastingWeapon
import com.robertx22.mine_and_slash.mmorpg.registers.common.SlashEntities
import com.robertx22.mine_and_slash.tags.all.SpellTags
import com.robertx22.mine_and_slash.uncommon.enumclasses.PlayStyle
import com.robertx22.mine_and_slash.uncommon.utilityclasses.AllyOrEnemy
import com.robertx22.mine_and_slash.uncommon.utilityclasses.EntityFinder
import io.github.cotrin8672.mnsmoreclass.content.ninja.NinjaSpells
import io.github.cotrin8672.mnsmoreclass.spell_action.PartBuilderKt
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.sounds.SoundEvents

object SpellRegistry : IMnsRegistry<Spell> by MnsRegistryDelegate() {
    const val BARKSKIN = "barkskin"
    const val PURIFICATION = "purification"
    const val WEAKNESS_AURA = "weakness_aura"
    const val ENTANGLING_THORNS = "entangling_thorns"

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
            listOf(
                SpellTags.magic,
                SpellTags.area,
                SpellTags.curse,
                SpellTags.LIGHTNING
            ),
        )
            .weaponReq(CastingWeapon.MAGE_WEAPON)
            .manualDesc(
                "Project a weakening aura, inflicting enemies in front with -30% Armor for 8 seconds."
            )
            .onCast(PartBuilder.playSound(SoundEvents.SLIME_ATTACK, 1.0, 0.7))
            .onCast(
                PartBuilder.groundEdgeParticles(
                    ParticleTypes.SPORE_BLOSSOM_AIR,
                    100.0,
                    4.0,
                    1.0
                )
            )
            .onCast(PartBuilder.addExileEffectToEnemiesInFront(WEAKNESS_AURA, 7.0, 4.0, 160.0))
            .animations(SpellAnimations.HAND_UP_CAST, SpellAnimations.CAST_FINISH)
            .buildForEffect()
            .add()

        SpellBuilder.of(
            ENTANGLING_THORNS,
            PlayStyle.INT,
            SpellConfiguration.Builder.instant(18, 20 * 40),
            "Entangling Thorns",
            listOf(
                SpellTags.magic,
                SpellTags.damage,
                SpellTags.area,
                SpellTags.LIGHTNING
            ),
        )
            .weaponReq(CastingWeapon.MAGE_WEAPON)
            .manualDesc(
                "Anchor a thornfield ahead that snares enemies with Entangling Thorns while spores vent for 20 seconds."
            )
            .onCast(PartBuilder.playSound(SoundEvents.FROGSPAWN_HATCH, 1.0, 0.9))
            .onCast(
                PartBuilder.justAction(
                    SpellAction.SUMMON_AT_SIGHT.create(
                        SlashEntities.SIMPLE_PROJECTILE.get(),
                        20.0 * 20,
                        0.0
                    ).apply {
                        put(MapField.ENTITY_NAME, "thorn_zone")
                        put(MapField.RADIUS, 5.0)
                    }
                )
            )
            .onTick(
                "thorn_zone",
                PartBuilder.groundEdgeParticles(ParticleTypes.END_ROD, 140.0, 5.0, 0.0)
                    .tick(20.0)
            )
            .onTick(
                "thorn_zone",
                PartBuilder.cloudParticles(ParticleTypes.WARPED_SPORE, 140.0, 5.0, 2.5)
                    .tick(2.0)
            )
            .onTick(
                "thorn_zone",
                PartBuilder.exileEffect(ENTANGLING_THORNS, 160.0).apply {
                    targets.clear()
                    targets.add(
                        BaseTargetSelector.AOE.create(
                            5.0,
                            EntityFinder.SelectionType.RADIUS,
                            AllyOrEnemy.enemies
                        )
                    )
                }
                    .tick(20.0)
            )
            .animations(SpellAnimations.HAND_UP_CAST, SpellAnimations.CAST_FINISH)
            .buildForEffect()
            .add()

        NinjaSpells.getAll().onEach { it.add() }
    }
}
