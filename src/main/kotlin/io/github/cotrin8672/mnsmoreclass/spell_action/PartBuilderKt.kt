package io.github.cotrin8672.mnsmoreclass.spell_action

import com.robertx22.mine_and_slash.database.data.spells.components.ComponentPart
import com.robertx22.mine_and_slash.database.data.spells.components.actions.ExileEffectAction
import com.robertx22.mine_and_slash.database.data.spells.components.actions.SpellAction
import com.robertx22.mine_and_slash.database.data.spells.components.selectors.BaseTargetSelector
import com.robertx22.mine_and_slash.database.data.spells.components.selectors.TargetSelector
import com.robertx22.mine_and_slash.uncommon.utilityclasses.AllyOrEnemy
import com.robertx22.mine_and_slash.uncommon.utilityclasses.EntityFinder

object PartBuilderKt {
    /**
     * 範囲内のターゲットのExileEffect（負のエフェクト）を解除
     */
    @JvmStatic
    fun removeExileEffectInRadius(radius: Double, allyOrEnemy: AllyOrEnemy): ComponentPart {
        val part = ComponentPart()
        part.targets.add(
            BaseTargetSelector.AOE.create(
                radius,
                EntityFinder.SelectionType.RADIUS,
                allyOrEnemy
            )
        )
        part.acts.add(SpellAction.EXILE_EFFECT.create("", ExileEffectAction.GiveOrTake.REMOVE_NEGATIVE, 1.0))
        return part
    }

    /**
     * 範囲内のターゲットの炎上を解除
     */
    @JvmStatic
    fun removeFireInRadius(radius: Double, allyOrEnemy: AllyOrEnemy): ComponentPart {
        val part = ComponentPart()
        part.targets.add(
            BaseTargetSelector.AOE.create(
                radius,
                EntityFinder.SelectionType.RADIUS,
                allyOrEnemy
            )
        )
        part.acts.add(SpellActions.REMOVE_FIRE.create())
        return part
    }

    /**
     * 範囲内のターゲットのバニラポーション（負のエフェクト）を解除
     */
    @JvmStatic
    fun removePotionEffectInRadius(
        radius: Double,
        count: Double,
        allyOrEnemy: AllyOrEnemy,
    ): ComponentPart {
        val part = ComponentPart()
        part.targets.add(
            BaseTargetSelector.AOE.create(
                radius,
                EntityFinder.SelectionType.RADIUS,
                allyOrEnemy
            )
        )
        part.acts.add(SpellAction.POTION.removeNegative(count))
        return part
    }

    /**
     * キャスターのExileEffect（負のエフェクト）を解除
     */
    @JvmStatic
    fun removeExileEffectOnCaster(): ComponentPart {
        val part = ComponentPart()
        part.targets.add(TargetSelector.CASTER.create())
        part.acts.add(SpellAction.EXILE_EFFECT.create("", ExileEffectAction.GiveOrTake.REMOVE_NEGATIVE, 1.0))
        return part
    }

    /**
     * キャスターの炎上を解除
     */
    @JvmStatic
    fun removeFireOnCaster(): ComponentPart {
        val part = ComponentPart()
        part.targets.add(TargetSelector.CASTER.create())
        part.acts.add(SpellActions.REMOVE_FIRE.create())
        return part
    }

    /**
     * キャスターのバニラポーション（負のエフェクト）を解除
     */
    @JvmStatic
    fun removePotionEffectOnCaster(count: Double): ComponentPart {
        val part = ComponentPart()
        part.targets.add(TargetSelector.CASTER.create())
        part.acts.add(SpellAction.POTION.removeNegative(count))
        return part
    }

    /**
     * 範囲内の味方の全デバフを解除（バニラポーション + ExileEffect + 炎上）
     * 便利メソッド - 個別メソッドを組み合わせたコンポジット
     */
    @JvmStatic
    fun purifyAlliesInRadius(radius: Double): List<ComponentPart> {
        return listOf(
            removePotionEffectInRadius(radius, 100.0, AllyOrEnemy.allies),
            removeExileEffectInRadius(radius, AllyOrEnemy.allies),
            removeFireInRadius(radius, AllyOrEnemy.allies)
        )
    }

    /**
     * 対象となる敵の背後へ瞬間移動
     */
    @JvmStatic
    fun teleportBehindTarget(distance: Double, searchRadius: Double): ComponentPart {
        val part = ComponentPart()
        part.targets.add(
            BaseTargetSelector.AOE.create(
                searchRadius,
                EntityFinder.SelectionType.RADIUS,
                AllyOrEnemy.enemies
            )
        )
        part.acts.add(SpellActions.TELEPORT_BEHIND_TARGET.create(distance))
        return part
    }
}
