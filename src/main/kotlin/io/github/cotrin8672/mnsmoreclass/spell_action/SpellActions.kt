package io.github.cotrin8672.mnsmoreclass.spell_action

import com.robertx22.mine_and_slash.database.data.spells.components.actions.SpellAction

object SpellActions {
    val REMOVE_FIRE: RemoveFireAction = register(RemoveFireAction())

    private fun <T : SpellAction> register(action: T): T {
        SpellAction.MAP[action.GUID()] = action
        return action
    }

    fun init() {
        // クラスロード時に登録
    }
}
