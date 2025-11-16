package io.github.cotrin8672.mnsmoreclass.registry

import com.robertx22.mine_and_slash.aoe_data.database.spells.PartBuilder
import com.robertx22.mine_and_slash.aoe_data.database.spells.SpellBuilder
import com.robertx22.mine_and_slash.aoe_data.database.stats.DefenseStats
import com.robertx22.mine_and_slash.aoe_data.database.stats.OffenseStats
import com.robertx22.mine_and_slash.aoe_data.database.stats.SpellChangeStats
import com.robertx22.mine_and_slash.aoe_data.database.stats.base.EffectCtx
import com.robertx22.mine_and_slash.database.data.exile_effects.EffectType
import com.robertx22.mine_and_slash.database.data.exile_effects.ExileEffect
import com.robertx22.mine_and_slash.database.data.exile_effects.VanillaStatData
import com.robertx22.mine_and_slash.database.data.spells.map_fields.MapField
import com.robertx22.mine_and_slash.database.data.stats.types.defense.Armor
import com.robertx22.mine_and_slash.database.data.stats.types.defense.DodgeRating
import com.robertx22.mine_and_slash.database.data.stats.types.resources.health.HealthRegen
import com.robertx22.mine_and_slash.tags.all.EffectTags
import com.robertx22.mine_and_slash.uncommon.enumclasses.Elements
import com.robertx22.mine_and_slash.uncommon.enumclasses.ModType
import io.github.cotrin8672.mnsmoreclass.content.ninja.NinjaSpells
import io.github.cotrin8672.mnsmoreclass.exile_effect.ExileEffectBuilderKt
import net.minecraft.world.entity.ai.attributes.Attributes
import java.util.*

object ExileEffectRegistry : IMnsRegistry<ExileEffect> by MnsRegistryDelegate() {
    fun register() {
        ExileEffectBuilderKt.of(
            EffectCtx(
                SpellRegistry.BARKSKIN,
                "barkskin",
                Elements.Physical,
                EffectType.beneficial
            )
        )
            .stat(-5f, -15f, DefenseStats.DAMAGE_RECEIVED.get(), ModType.MORE)
            .stat(1f, 2f, HealthRegen.getInstance(), ModType.FLAT)
            .maxStacks(3)
            .addTags(EffectTags.positive, EffectTags.defensive)
            .build()
            .add()

        ExileEffectBuilderKt.of(
            EffectCtx(
                NinjaSpells.SHADOW_WALK,
                "shadow_walk",
                Elements.Physical,
                EffectType.beneficial
            )
        )
            .stat(8f, 20f, DodgeRating.getInstance(), ModType.PERCENT)
            .maxStacks(1)
            .addTags(EffectTags.positive, EffectTags.defensive)
            .build()
            .add()

        ExileEffectBuilderKt.of(
            EffectCtx(
                NinjaSpells.SHUNJIN,
                "shunjin",
                Elements.Physical,
                EffectType.beneficial
            )
        )
            .vanillaStat(
                VanillaStatData.create(
                    Attributes.ATTACK_SPEED,
                    0.3f,
                    ModType.MORE,
                    SHUNJIN_ATTACK_SPEED_UUID
                )
            )
            .maxStacks(1)
            .addTags(EffectTags.positive, EffectTags.offensive)
            .build()
            .add()

        ExileEffectBuilderKt.of(
            EffectCtx(
                "toxic_edge",
                "toxic_edge",
                Elements.Nature,
                EffectType.beneficial
            )
        )
            .maxStacks(5)
            .desc("次の攻撃時に毒を付与するスタックバフ。攻撃ごとに1スタック消費する")
            .addTags(EffectTags.positive, EffectTags.offensive)
            .build()
            .add()

        ExileEffectBuilderKt.of(
            EffectCtx(
                NinjaSpells.SHADOW_SECRET,
                "shadow_secret",
                Elements.Shadow,
                EffectType.beneficial
            )
        )
            .vanillaStat(
                VanillaStatData.create(
                    Attributes.ATTACK_SPEED,
                    0.5f,
                    ModType.MORE,
                    SHADOW_SECRET_ATTACK_SPEED_UUID
                )
            )
            .stat(30f, 30f, OffenseStats.CRIT_CHANCE.get(), ModType.FLAT)
            .stat(600f, 600f, SpellChangeStats.COOLDOWN_TICKS.get(), ModType.FLAT)
            .maxStacks(1)
            .addTags(EffectTags.positive, EffectTags.offensive)
            .build()
            .add()

        ExileEffectBuilderKt.of(
            EffectCtx(
                "weakness_aura",
                "weakness_aura",
                Elements.Nature,
                EffectType.negative
            )
        )
            .stat(-30f, -30f, Armor.getInstance(), ModType.MORE)
            .maxStacks(1)
            .addTags(EffectTags.negative, EffectTags.offensive)
            .build()
            .add()

        ExileEffectBuilderKt.of(
            EffectCtx(
                SpellRegistry.ENTANGLING_THORNS,
                "entangling_thorns",
                Elements.Nature,
                EffectType.negative
            )
        )
            .vanillaStat(
                VanillaStatData.create(
                    Attributes.MOVEMENT_SPEED,
                    -1f,
                    ModType.MORE,
                    ENTANGLING_THORNS_MOVEMENT_SPEED_UUID
                )
            )
            .vanillaStat(
                VanillaStatData.create(
                    Attributes.KNOCKBACK_RESISTANCE,
                    1.0f,
                    ModType.FLAT,
                    ENTANGLING_THORNS_KNOCKBACK_RESIST_UUID
                )
            )
            .spell(
                SpellBuilder.forEffect()
                    .onTick(
                        PartBuilder.damage(
                            ValueCalcRegistry.EntanglingThornsDot,
                            Elements.Elemental
                        ).apply {
                            acts.forEach { holder ->
                                holder.put(MapField.DMG_EFFECT_TYPE, "dot")
                            }
                        }
                            .tick(20.0)
                    )
                    .buildForEffect()
            )
            .maxStacks(1)
            .addTags(EffectTags.negative, EffectTags.immobilizing)
            .build()
            .add()

        ExileEffectBuilderKt.of(
            EffectCtx(
                SpellRegistry.SAKURA_BLOOM,
                "sakura_bloom",
                Elements.Nature,
                EffectType.beneficial
            )
        )
            .vanillaStat(
                VanillaStatData.create(
                    Attributes.ATTACK_SPEED,
                    0.15f,
                    ModType.MORE,
                    SAKURA_BLOOM_ATTACK_SPEED_UUID
                )
            )
            .maxStacks(1)
            .addTags(EffectTags.positive, EffectTags.offensive)
            .build()
            .add()
    }

    private val SHUNJIN_ATTACK_SPEED_UUID: UUID =
        UUID.fromString("61ad1e2c-3b16-4d8f-9fa0-d95d8f1792f6")
    private val SHADOW_SECRET_ATTACK_SPEED_UUID: UUID =
        UUID.fromString("99441a65-e0f3-46e3-8b5f-8437b3d09f51")
    private val ENTANGLING_THORNS_MOVEMENT_SPEED_UUID: UUID =
        UUID.fromString("893c46ab-7bda-43dd-81ec-d0f71aafdbb2")
    private val ENTANGLING_THORNS_KNOCKBACK_RESIST_UUID: UUID =
        UUID.fromString("0e58c71a-aee5-4d0f-94ba-98a183f6c465")
    private val SAKURA_BLOOM_ATTACK_SPEED_UUID: UUID =
        UUID.fromString("f0a612fe-1c02-4c9c-ac6b-6f6a41cc0f5b")
}
