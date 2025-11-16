package io.github.cotrin8672.mnsmoreclass.registry

import com.robertx22.mine_and_slash.database.data.value_calc.ValueCalculation
import io.github.cotrin8672.mnsmoreclass.content.ninja.NinjaSpells
import io.github.cotrin8672.mnsmoreclass.registry.SpellRegistry
import io.github.cotrin8672.mnsmoreclass.value_calc.ValueCalcBuilderKt

object ValueCalcRegistry : IMnsRegistry<ValueCalculation> by MnsRegistryDelegate() {
    val ShadowWalk: ValueCalculation =
        ValueCalcBuilderKt.of(NinjaSpells.SHADOW_WALK)
            .attackScaling(1.0f, 2.5f)
            .capScaling(0.5f)
            .build()

    val Shuriken: ValueCalculation =
        ValueCalcBuilderKt.of(NinjaSpells.SHURIKEN)
            .spellScaling(0.6f, 1.2f)
            .build()

    val Shunjin: ValueCalculation =
        ValueCalcBuilderKt.of(NinjaSpells.SHUNJIN)
            .attackScaling(0.8f, 2.0f)
            .capScaling(0.2f)
            .build()

    val RyuuseiRanbu: ValueCalculation =
        ValueCalcBuilderKt.of(NinjaSpells.RYUUSEI_RANBU)
            .attackScaling(0.75f, 2.0f)
            .capScaling(0.25f)
            .build()

    val SwingScythe: ValueCalculation =
        ValueCalcBuilderKt.of(NinjaSpells.SWING_SCYTHE)
            .attackScaling(0.65f, 1.8f)
            .capScaling(0.2f)
            .build()

    val ShadowSecret: ValueCalculation =
        ValueCalcBuilderKt.of(NinjaSpells.SHADOW_SECRET)
            .attackScaling(0.0f, 0.0f)
            .build()

    val WeaknessAura: ValueCalculation =
        ValueCalcBuilderKt.of(SpellRegistry.WEAKNESS_AURA)
            .build()

    fun register() {
        ValueCalcBuilderKt.of(SpellRegistry.BARKSKIN)
            .spellScaling(1f, 2f)
            .capScaling(1f)
            .build()
            .add()

        // Purificationはデバフ解除なので計算不要だが、チャージシステムのため定義
        ValueCalcBuilderKt.of(SpellRegistry.PURIFICATION)
            .build()
            .add()

        ShadowWalk.add()
        Shuriken.add()
        Shunjin.add()
        RyuuseiRanbu.add()
        SwingScythe.add()
        ShadowSecret.add()
        WeaknessAura.add()
    }
}
