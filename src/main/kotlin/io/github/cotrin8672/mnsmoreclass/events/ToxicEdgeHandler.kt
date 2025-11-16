package io.github.cotrin8672.mnsmoreclass.events

import com.robertx22.mine_and_slash.aoe_data.database.ailments.Ailments
import com.robertx22.mine_and_slash.database.data.stats.types.ailment.AilmentChance
import com.robertx22.mine_and_slash.database.registry.ExileDB
import com.robertx22.mine_and_slash.uncommon.datasaving.Load
import net.minecraft.world.damagesource.DamageTypes
import net.minecraft.world.entity.LivingEntity
import net.minecraftforge.event.entity.living.LivingAttackEvent
import net.minecraftforge.eventbus.api.SubscribeEvent

object ToxicEdgeHandler {
    private const val EFFECT_ID = "toxic_edge"

    @SubscribeEvent
    fun onLivingAttack(event: LivingAttackEvent) {
        event.source.type()
        val attacker = event.source.entity as? LivingEntity ?: return
        if (event.source.directEntity != attacker) {
            return
        }
        val isMelee = event.source.`is`(DamageTypes.PLAYER_ATTACK)

        if (!isMelee) return

        if (attacker.level().isClientSide || event.entity.level().isClientSide) {
            return
        }

        val target = event.entity ?: return

        val toxicEdge = ExileDB.ExileEffects().get(EFFECT_ID) ?: return
        val statusData = Load.Unit(attacker).statusEffectsData
        if (!statusData.has(toxicEdge)) {
            return
        }

        val instance = statusData.get(toxicEdge)
        if (instance.stacks <= 0) {
            statusData.delete(toxicEdge)
            return
        }

        val damage = event.amount
        if (damage > 0f) {
            AilmentChance.activate(damage, Ailments.POISON, attacker, target, null)
        }

        instance.stacks -= 1
        if (instance.stacks <= 0) {
            statusData.delete(toxicEdge)
        }

        Load.Unit(attacker).equipmentCache.STATUS.setDirty()
    }
}
