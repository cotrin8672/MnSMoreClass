package io.github.cotrin8672.mnsmoreclass

import com.robertx22.mine_and_slash.mmorpg.SlashRef
import io.github.cotrin8672.mnsmoreclass.datagen.MnsGsonDataProvider
import io.github.cotrin8672.mnsmoreclass.datagen.MnsSerializableDataProvider
import io.github.cotrin8672.mnsmoreclass.events.ToxicEdgeHandler
import io.github.cotrin8672.mnsmoreclass.events.UndyingBlessingHandler
import io.github.cotrin8672.mnsmoreclass.init.ModIdentity
import io.github.cotrin8672.mnsmoreclass.registrate.KotlinRegistrate
import io.github.cotrin8672.mnsmoreclass.registry.*
import io.github.cotrin8672.mnsmoreclass.spell_action.SpellActions
import net.minecraft.resources.ResourceLocation
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.data.event.GatherDataEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import thedarkcolour.kotlinforforge.forge.MOD_BUS

/**
 * Sophisticated MAS MODのエントリーポイント
 *
 * Mine and SlashとSophisticated Backpacksを統合するアドオンMOD。 バックパック内のMASギアの自動サルベージと、 作業台への直接アクセス機能を提供します。
 */
@Mod(ModIdentity.MOD_ID)
object MnsMoreClass {
    /** MODロガー */
    val LOGGER: Logger = LogManager.getLogger(ModIdentity.LOGGER_NAME)

    /** Registrateインスタンス アイテム、ブロック、その他のレジストリオブジェクトの登録を管理します。 */
    val Registrate = KotlinRegistrate.create(ModIdentity.MOD_ID)

    init {
        LOGGER.info("${ModIdentity.MOD_NAME} is initializing...")
        LOGGER.info("Registrate instance created for MOD ID: ${ModIdentity.MOD_ID}")
        Registrate.registerEventListeners(MOD_BUS)
        MOD_BUS.addListener(this::gatherData)
        MOD_BUS.addListener(this::onFMLCommonSetupEvent)

        // カスタムSpellActionを登録
        SpellActions.init()
        ItemRegistry.register()

        MinecraftForge.EVENT_BUS.register(ToxicEdgeHandler)
        MinecraftForge.EVENT_BUS.register(UndyingBlessingHandler)
    }

    @SubscribeEvent
    fun onFMLCommonSetupEvent(event: FMLCommonSetupEvent) {
        event.enqueueWork {
            SpellSchoolRegistry.register()
            SpellRegistry.register()
            PerkRegistry.register()
            ValueCalcRegistry.register()
            ExileEffectRegistry.register()
            StatRegistry.register()
        }
    }

    @SubscribeEvent
    fun gatherData(event: GatherDataEvent) {
        val output = event.generator.packOutput

        // Register all content here to ensure Mine and Slash is initialized
        SpellSchoolRegistry.register()
        SpellRegistry.register()
        PerkRegistry.register()
        ValueCalcRegistry.register()
        ExileEffectRegistry.register()
        StatRegistry.register()

        event.generator.addProvider(
            event.includeServer(),
            MnsGsonDataProvider(output, SpellSchoolRegistry.getAll(), "mmorpg_spell_school") { school ->
                ResourceLocation.fromNamespaceAndPath(SlashRef.MODID, school.id)
            }
        )

        event.generator.addProvider(
            event.includeServer(),
            MnsGsonDataProvider(output, SpellRegistry.getAll(), "mmorpg_spells") { spell ->
                ResourceLocation.fromNamespaceAndPath(SlashRef.MODID, spell.identifier)
            }
        )

        event.generator.addProvider(
            event.includeServer(),
            MnsGsonDataProvider(output, PerkRegistry.getAll(), "mmorpg_perk") { perk ->
                ResourceLocation.fromNamespaceAndPath(SlashRef.MODID, perk.id)
            }
        )

        event.generator.addProvider(
            event.includeServer(),
            MnsGsonDataProvider(output, ValueCalcRegistry.getAll(), "mmorpg_value_calc") {
                    valueCalc,
                ->
                ResourceLocation.fromNamespaceAndPath(SlashRef.MODID, valueCalc.id)
            }
        )

        event.generator.addProvider(
            event.includeServer(),
            MnsGsonDataProvider(output, ExileEffectRegistry.getAll(), "mmorpg_exile_effect") { exileEffect ->
                ResourceLocation.fromNamespaceAndPath(SlashRef.MODID, exileEffect.id)
            }
        )

        event.generator.addProvider(
            event.includeServer(),
            MnsSerializableDataProvider(output, StatRegistry.getAll(), "mmorpg_stat") { stat ->
                ResourceLocation.fromNamespaceAndPath(SlashRef.MODID, stat.GUID())
            }
        )
    }
}
