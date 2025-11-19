package io.github.cotrin8672.mnsmoreclass.registry

import com.robertx22.mine_and_slash.database.data.stats.datapacks.base.BaseDatapackStat
import com.robertx22.mine_and_slash.database.data.stats.datapacks.stats.AttributeStat
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import java.util.*

object StatRegistry : IMnsRegistry<BaseDatapackStat> by MnsRegistryDelegate() {
    val ATTACK_SPEED: AttributeStat = AttributeStat(
        "attack_speed",
        "Attack Speed",
        UUID.fromString("4f7a890b-86d5-4ec8-84f4-8e498871f5e1"),
        Attributes.ATTACK_SPEED,
        true,
        AttributeModifier.Operation.MULTIPLY_TOTAL,
        true
    )

    private val allStats: List<BaseDatapackStat> = listOf(ATTACK_SPEED)

    fun register() {
        allStats
            .filterNot { getAll().contains(it) }
            .forEach { it.add() }
    }
}
