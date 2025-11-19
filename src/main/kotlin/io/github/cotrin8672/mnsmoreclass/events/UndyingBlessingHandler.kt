package io.github.cotrin8672.mnsmoreclass.events

import com.robertx22.mine_and_slash.database.registry.ExileDB
import com.robertx22.mine_and_slash.uncommon.datasaving.Load
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraftforge.event.entity.living.LivingDeathEvent
import net.minecraftforge.eventbus.api.SubscribeEvent

object UndyingBlessingHandler {
    private const val EFFECT_ID = "undying_blessing"

    @SubscribeEvent
    fun onLivingDeath(event: LivingDeathEvent) {
        val entity = event.entity
        if (entity.level().isClientSide) {
            return
        }

        val living = entity ?: return
        val effect = ExileDB.ExileEffects().get(EFFECT_ID) ?: return
        val statusData = Load.Unit(living).statusEffectsData

        if (!statusData.has(effect)) {
            return
        }

        val instance = statusData.get(effect) ?: return
        if (instance.stacks <= 0) {
            statusData.delete(effect)
            return
        }

        event.isCanceled = true

        instance.stacks -= 1
        if (instance.stacks <= 0) {
            statusData.delete(effect)
        }

        living.level().playSound(
            null,
            living.x,
            living.y,
            living.z,
            SoundEvents.TOTEM_USE,
            SoundSource.PLAYERS,
            1.0f,
            1.0f
        )

        val targetHealth = (living.maxHealth * 0.5f).let { if (it < 1f) 1f else it }
        living.health = targetHealth
        Load.Unit(living).equipmentCache.STATUS.setDirty()
    }
}
