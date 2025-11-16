package io.github.cotrin8672.mnsmoreclass.spell_action

import com.robertx22.mine_and_slash.database.data.spells.components.MapHolder
import com.robertx22.mine_and_slash.database.data.spells.components.actions.SpellAction
import com.robertx22.mine_and_slash.database.data.spells.map_fields.MapField
import com.robertx22.mine_and_slash.database.data.spells.spell_classes.SpellCtx
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3

class TeleportBehindTargetAction : SpellAction(listOf(MapField.DISTANCE)) {
    fun create(distance: Double): MapHolder {
        return MapHolder().apply {
            type = GUID()
            put(MapField.DISTANCE, distance)
        }
    }

    override fun GUID(): String {
        return GUID_VALUE
    }

    companion object {
        private const val GUID_VALUE = "teleport_behind_target"
    }

    override fun tryActivate(targets: MutableCollection<LivingEntity>, spellCtx: SpellCtx, mapHolder: MapHolder) {
        val caster = spellCtx.caster
        val target = targets.minByOrNull { it.distanceToSqr(caster) } ?: return

        val direction = computeHorizontalDirection(target, caster)
        if (direction.lengthSqr() <= 1.0E-6) {
            return
        }

        val distance = mapHolder.get(MapField.DISTANCE)
            ?: throw IllegalArgumentException("Distance not provided for teleport_behind_target action")
        val offset = direction.scale(distance)

        val targetCenter = target.boundingBox.center
        val destination = targetCenter.subtract(offset)

        caster.teleportTo(destination.x, destination.y, destination.z)
        alignCasterRotation(caster, target)
    }

    private fun computeHorizontalDirection(target: LivingEntity, caster: LivingEntity): Vec3 {
        val look = target.lookAngle
        val horizontal = Vec3(look.x, 0.0, look.z)

        if (horizontal.lengthSqr() > 1.0E-6) {
            return horizontal.normalize()
        }

        val fallback = Vec3(target.x - caster.x, 0.0, target.z - caster.z)
        return if (fallback.lengthSqr() > 1.0E-6) {
            fallback.normalize()
        } else {
            Vec3(0.0, 0.0, 0.0)
        }
    }

    private fun alignCasterRotation(caster: LivingEntity, target: LivingEntity) {
        caster.yRot = target.yRot
        caster.xRot = target.xRot

        caster.yHeadRot = target.yHeadRot
        caster.yBodyRot = target.yBodyRot
        caster.yRot = target.yRot
        caster.xRot = target.xRot
        caster.setYBodyRot(target.yBodyRot)
        caster.setYHeadRot(target.yHeadRot)
    }

    private fun isSafePosition(entity: LivingEntity, basePosition: Vec3, halfHeight: Double): Boolean {
        val center = basePosition.add(0.0, halfHeight, 0.0)
        val aabb = AABB.ofSize(center, entity.bbWidth.toDouble(), entity.bbHeight.toDouble(), entity.bbWidth.toDouble())
        return entity.level().noCollision(entity, aabb)
    }
}
