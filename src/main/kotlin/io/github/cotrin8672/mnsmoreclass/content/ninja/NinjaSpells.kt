package io.github.cotrin8672.mnsmoreclass.content.ninja

import com.robertx22.mine_and_slash.a_libraries.player_animations.AnimationHolder
import com.robertx22.mine_and_slash.a_libraries.player_animations.SpellAnimations
import com.robertx22.mine_and_slash.aoe_data.database.ailments.Ailments
import com.robertx22.mine_and_slash.aoe_data.database.spells.PartBuilder
import com.robertx22.mine_and_slash.aoe_data.database.spells.SpellBuilder
import com.robertx22.mine_and_slash.database.data.spells.components.SpellConfiguration
import com.robertx22.mine_and_slash.database.data.spells.components.actions.SpellAction
import com.robertx22.mine_and_slash.database.data.spells.map_fields.MapField
import com.robertx22.mine_and_slash.database.data.spells.spell_classes.CastingWeapon
import com.robertx22.mine_and_slash.mmorpg.registers.common.SlashEntities
import com.robertx22.mine_and_slash.tags.all.SpellTags
import com.robertx22.mine_and_slash.uncommon.enumclasses.Elements
import com.robertx22.mine_and_slash.uncommon.enumclasses.PlayStyle
import io.github.cotrin8672.mnsmoreclass.init.ModIdentity
import io.github.cotrin8672.mnsmoreclass.registry.ItemRegistry
import io.github.cotrin8672.mnsmoreclass.registry.ValueCalcRegistry
import io.github.cotrin8672.mnsmoreclass.spell_action.PartBuilderKt
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.sounds.SoundEvents

object NinjaSpells {
    const val SHADOW_WALK = "shadow_walk"
    const val SHURIKEN = "shuriken"
    const val TOXIC_EDGE = "toxic_edge"
    const val RYUUSEI_RANBU = "ryuusei_ranbu"
    const val SHUNJIN = "shunjin"
    const val SWING_SCYTHE = "swing_scythe"
    const val SHADOW_SECRET = "shadow_secret"

    val ShadowWalk =
        SpellBuilder.of(
            SHADOW_WALK,
            PlayStyle.STR,
            SpellConfiguration.Builder.instant(25, 10 * 20),
            "ShadowWalk",
            listOf(
                SpellTags.PHYSICAL,
                SpellTags.damage,
                SpellTags.melee,
                SpellTags.weapon_skill
            ),
        )
            .manualDesc(
                "Blink behind your target, deal " +
                        ValueCalcRegistry.ShadowWalk.getLocDmgTooltip(
                            Elements.Physical
                        ) +
                        " physical damage, and briefly increase your dodge chance."
            )
            .weaponReq(CastingWeapon.MELEE_WEAPON)
            .onCast(PartBuilderKt.teleportBehindTarget(0.5, 5.0))
            .onCast(PartBuilder.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0, 1.0))
            .onCast(PartBuilder.giveSelfExileEffect(SHADOW_WALK, 20.0 * 8))
            .onCast(
                PartBuilder.damageInFront(
                    ValueCalcRegistry.ShadowWalk,
                    Elements.Physical,
                    2.0,
                    2.0
                )
            )
            .buildForEffect()

    val Shuriken =
        SpellBuilder.of(
            SHURIKEN,
            PlayStyle.DEX,
            SpellConfiguration.Builder.instant(10, 20 * 5)
                .setChargesAndRegen(SHURIKEN, 3, 20 * 10)
                .applyCastSpeedToCooldown(),
            "Shuriken",
            listOf(
                SpellTags.projectile,
                SpellTags.damage,
                SpellTags.chaining,
                SpellTags.PHYSICAL
            ),
        )
            .manualDesc(
                "Throw a shuriken that deals " +
                        ValueCalcRegistry.Shuriken.getLocDmgTooltip(Elements.Physical) +
                        " physical damage and inflicts Bleed for 5 seconds."
            )
            .animations(SpellAnimations.THROW, AnimationHolder.none())
            .weaponReq(CastingWeapon.MELEE_WEAPON)
            .onCast(PartBuilder.playSound(SoundEvents.SNOWBALL_THROW, 1.0, 1.0))
            .onCast(
                PartBuilder.justAction(
                    SpellAction.SUMMON_PROJECTILE
                        .create(
                            ItemRegistry.SHURIKEN.get(),
                            1.0,
                            1.0,
                            SlashEntities.SIMPLE_PROJECTILE.get(),
                            50.0,
                            false
                        )
                        .put(MapField.ITEM, "${ModIdentity.MOD_ID}:$SHURIKEN")
                        .put(MapField.CHAIN_COUNT, 5.0)
                )
            )
            .onHit(PartBuilder.exileEffect(Ailments.BLEED.GUID(), 20.0 * 5))
            .onTick(PartBuilder.particleOnTick(1.0, ParticleTypes.CRIT, 10.0, 0.01))
            .onExpire(
                PartBuilder.damageInAoe(
                    ValueCalcRegistry.Shuriken,
                    Elements.Physical,
                    1.0
                )
            )
            .onExpire(PartBuilder.aoeParticles(ParticleTypes.CRIT, 50.0, 0.5))
            .onExpire(PartBuilder.playSound(SoundEvents.TRIDENT_HIT, 1.0, 1.0))
            .levelReq(1)
            .buildForEffect()

    val Shunjin =
        SpellBuilder.of(
            SHUNJIN,
            PlayStyle.DEX,
            SpellConfiguration.Builder.instant(12, 20 * 6)
                .setChargesAndRegen(SHUNJIN, 3, 20 * 10),
            "Shunjin",
            listOf(SpellTags.damage, SpellTags.PHYSICAL, SpellTags.melee, SpellTags.weapon_skill)
        )
            .manualDesc(
                "Perform an instant strike, dealing " +
                        ValueCalcRegistry.Shunjin.getLocDmgTooltip(Elements.Physical) +
                        " physical damage and granting 30% attack speed for 5 seconds."
            )
            .animations(SpellAnimations.MELEE_SLASH, AnimationHolder.none())
            .weaponReq(CastingWeapon.MELEE_WEAPON)
            .onCast(PartBuilder.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.0, 1.0))
            .onCast(PartBuilder.aoeParticles(ParticleTypes.CRIT, 20.0, 0.75))
            .onCast(PartBuilder.damageInFront(ValueCalcRegistry.Shunjin, Elements.Physical, 2.5, 2.0))
            .onCast(PartBuilder.giveSelfExileEffect(SHUNJIN, 20.0 * 5))
            .levelReq(6)
            .buildForEffect()

    val ToxicEdge =
        SpellBuilder.of(
            TOXIC_EDGE,
            PlayStyle.DEX,
            SpellConfiguration.Builder.instant(15, 20 * 8),
            "Toxic Edge",
            listOf(SpellTags.BUFF, SpellTags.weapon_skill)
        )
            .manualDesc(
                "Imbue your weapon with 5 stacks of Toxic Edge, causing your next hits to poison enemies."
            )
            .weaponReq(CastingWeapon.MELEE_WEAPON)
            .onCast(PartBuilder.playSound(SoundEvents.ENCHANTMENT_TABLE_USE, 1.0, 1.2))
            .onCast(PartBuilder.giveSelfExileEffect(TOXIC_EDGE, 20.0 * 300))
            .onCast(PartBuilder.giveSelfExileEffect(TOXIC_EDGE, 20.0 * 300))
            .onCast(PartBuilder.giveSelfExileEffect(TOXIC_EDGE, 20.0 * 300))
            .onCast(PartBuilder.giveSelfExileEffect(TOXIC_EDGE, 20.0 * 300))
            .onCast(PartBuilder.giveSelfExileEffect(TOXIC_EDGE, 20.0 * 300))
            .buildForEffect()

    val RyuuseiRanbu =
        SpellBuilder.of(
            RYUUSEI_RANBU,
            PlayStyle.DEX,
            SpellConfiguration.Builder.multiCast(28, 20 * 16, 10, 5),
            "Ryuusei Ranbu",
            listOf(SpellTags.damage, SpellTags.PHYSICAL, SpellTags.melee, SpellTags.weapon_skill)
        )
            .manualDesc(
                "Unleash a flurry of blades around you, dealing " +
                        ValueCalcRegistry.RyuuseiRanbu.getLocDmgTooltip(Elements.Physical) +
                        " physical damage per strike in a 3 block radius. Hits 4 times."
            )
            .animations(SpellAnimations.MELEE_SLASH, AnimationHolder.none())
            .weaponReq(CastingWeapon.MELEE_WEAPON)
            .onCast(PartBuilder.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.0, 1.2))
            .onCast(PartBuilder.aoeParticles(ParticleTypes.SWEEP_ATTACK, 25.0, 0.0))
            .onCast(PartBuilder.damageInAoe(ValueCalcRegistry.RyuuseiRanbu, Elements.Physical, 3.0))
            .buildForEffect()

    val SwingScythe =
        SpellBuilder.of(
            SWING_SCYTHE,
            PlayStyle.DEX,
            SpellConfiguration.Builder.multiCast(24, 20 * 18, 12, 5),
            "Swing Scythe",
            listOf(SpellTags.damage, SpellTags.PHYSICAL, SpellTags.area, SpellTags.weapon_skill)
        )
            .manualDesc(
                "Spin your chain-sickle to pull enemies in and deal " +
                        ValueCalcRegistry.SwingScythe.getLocDmgTooltip(Elements.Physical) +
                        " physical damage five times."
            )
            .animations(SpellAnimations.SPIN, SpellAnimations.CAST_FINISH)
            .weaponReq(CastingWeapon.MELEE_WEAPON)
            .onCast(PartBuilder.playSound(SoundEvents.CHAIN_BREAK, 1.0, 1.0))
            .onCast(PartBuilder.groundEdgeParticles(ParticleTypes.SWEEP_ATTACK, 40.0, 2.0, 0.2))
            .onCast(
                PartBuilder.damageInAoe(ValueCalcRegistry.SwingScythe, Elements.Physical, 3.0)
                    .addPerEntityHit(PartBuilder.justAction(SpellAction.TP_TARGET_TO_SELF.create()))
                    .addPerEntityHit(PartBuilder.groundEdgeParticles(ParticleTypes.CRIT, 20.0, 1.5, 0.1))
            )
            .levelReq(22)
            .buildForEffect()

    val ShadowSecret =
        SpellBuilder.of(
            SHADOW_SECRET,
            PlayStyle.DEX,
            SpellConfiguration.Builder.instant(45, 20 * 90),
            "Shadow Secret",
            listOf(SpellTags.BUFF, SpellTags.weapon_skill)
        )
            .manualDesc(
                "Unleash the ultimate shadow technique: instantly shave 30 seconds off spell cooldowns and gain 50% attack speed and 30% crit chance for 12 seconds."
            )
            .animations(SpellAnimations.HAND_UP_CAST, SpellAnimations.CAST_FINISH)
            .weaponReq(CastingWeapon.MELEE_WEAPON)
            .onCast(PartBuilder.playSound(SoundEvents.WARDEN_ROAR, 1.0, 1.3))
            .onCast(PartBuilder.aoeParticles(ParticleTypes.SMOKE, 80.0, 2.5))
            .onCast(PartBuilder.groundEdgeParticles(ParticleTypes.CRIT, 40.0, 3.0, 0.2))
            .onCast(PartBuilder.giveSelfExileEffect(SHADOW_SECRET, 20.0 * 12))
            .buildForEffect()

    fun getAll() = listOf(ShadowWalk, Shuriken, Shunjin, ToxicEdge, RyuuseiRanbu, SwingScythe, ShadowSecret)
}
