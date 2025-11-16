package io.github.cotrin8672.mnsmoreclass.registry

import com.tterrag.registrate.util.entry.ItemEntry
import io.github.cotrin8672.mnsmoreclass.MnsMoreClass
import net.minecraft.world.item.Item

object ItemRegistry {
    val SHURIKEN: ItemEntry<Item> = MnsMoreClass.Registrate
        .item<Item>("shuriken") { Item(Item.Properties()) }
        .register()

    fun register() {
        // 呼び出し側から明示的に初期化を行うためのダミーメソッド
    }
}
