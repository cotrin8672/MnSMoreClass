package io.github.cotrin8672.mnsmoreclass.spell_action

import com.robertx22.mine_and_slash.database.data.spells.components.MapHolder
import com.robertx22.mine_and_slash.database.data.spells.components.actions.SpellAction
import com.robertx22.mine_and_slash.database.data.spells.spell_classes.SpellCtx
import net.minecraft.world.entity.LivingEntity

class RemoveFireAction : SpellAction(emptyList()) {

    override fun tryActivate(
        targets: Collection<LivingEntity>,
        ctx: SpellCtx,
        data: MapHolder
    ) {
        targets.forEach { entity ->
            entity.clearFire()
        }
    }

    fun create(): MapHolder {
        val holder = MapHolder()
        holder.type = GUID()
        return holder
    }

    override fun GUID(): String = "remove_fire"
}
