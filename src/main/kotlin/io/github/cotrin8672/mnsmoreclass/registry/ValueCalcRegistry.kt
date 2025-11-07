package io.github.cotrin8672.mnsmoreclass.registry

import com.robertx22.mine_and_slash.database.data.value_calc.ValueCalculation
import io.github.cotrin8672.mnsmoreclass.value_calc.ValueCalcBuilderKt

object ValueCalcRegistry : IMnsRegistry<ValueCalculation> by MnsRegistryDelegate() {
    fun register() {
        ValueCalcBuilderKt.of("simple_heal")
            .spellScaling(1f, 2f)
            .capScaling(1f)
            .build()
            .run(::add)
    }
}
