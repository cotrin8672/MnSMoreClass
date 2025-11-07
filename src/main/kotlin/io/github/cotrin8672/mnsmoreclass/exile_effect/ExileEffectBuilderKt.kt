package io.github.cotrin8672.mnsmoreclass.exile_effect

import com.robertx22.mine_and_slash.aoe_data.database.stats.base.EffectCtx
import com.robertx22.mine_and_slash.database.data.StatMod
import com.robertx22.mine_and_slash.database.data.exile_effects.EffectType
import com.robertx22.mine_and_slash.database.data.exile_effects.ExileEffect
import com.robertx22.mine_and_slash.database.data.exile_effects.VanillaStatData
import com.robertx22.mine_and_slash.database.data.spells.components.Spell
import com.robertx22.mine_and_slash.database.data.stats.Stat
import com.robertx22.mine_and_slash.tags.all.EffectTags
import com.robertx22.mine_and_slash.tags.imp.EffectTag
import com.robertx22.mine_and_slash.tags.imp.SpellTag
import com.robertx22.mine_and_slash.uncommon.enumclasses.ModType

/**
 * ExileEffectを生成するためのKotlinビルダー
 * Mine and Slashの元のBuilderと異なり、レジストリ登録を行わない
 */
class ExileEffectBuilderKt private constructor(private val ctx: EffectCtx) {
    private val effect = ExileEffect().apply {
        this.type = ctx.type
        this.id = ctx.resourcePath
        this.locName = ctx.locname
    }

    init {
        // 自動的にポジティブ/ネガティブタグを追加
        when (ctx.type) {
            EffectType.beneficial -> addTags(EffectTags.positive)
            EffectType.negative -> addTags(EffectTags.negative)
            else -> {}
        }
    }

    /**
     * エフェクトタグを追加
     *
     * @param tags 追加するEffectTag
     */
    fun addTags(vararg tags: EffectTag) = apply {
        tags.forEach { tag ->
            if (!effect.tags.contains(tag.GUID())) {
                effect.tags.add(tag)
            }
        }
    }

    /**
     * スペルタグを追加
     *
     * @param tags 追加するSpellTag
     */
    fun addSpellTags(vararg tags: SpellTag) = apply {
        tags.forEach { tag ->
            if (!effect.spell_tags.contains(tag.GUID())) {
                effect.spell_tags.add(tag)
            }
        }
    }

    /**
     * エフェクトの説明文を設定
     *
     * @param desc 説明文
     */
    fun desc(desc: String) = apply {
        effect.locdesc = desc
    }

    /**
     * one_of_a_kind IDを設定（同系統のエフェクトで上書き）
     *
     * @param kind 系統ID
     */
    fun oneOfAKind(kind: String) = apply {
        effect.one_of_a_kind_id = kind
    }

    /**
     * 特定のタグを持つスペルキャスト時にエフェクトを削除
     *
     * @param tag スペルタグ
     */
    fun removeOnSpellCastWithTag(tag: SpellTag) = apply {
        effect.remove_on_spell_cast = tag
    }

    /**
     * ステータス修正を追加（StatMod形式）
     *
     * @param stat StatModインスタンス
     */
    fun stat(stat: StatMod) = apply {
        effect.stats.add(stat)
    }

    /**
     * ステータス修正を追加（個別パラメータ形式）
     *
     * @param first 最小レベルでの値
     * @param second 最大レベルでの値
     * @param stat 対象ステータス
     * @param type 修正タイプ（FLAT/PERCENT/MORE）
     */
    fun stat(first: Float, second: Float, stat: Stat, type: ModType = ModType.FLAT) = apply {
        effect.stats.add(StatMod(first, second, stat, type))
    }

    /**
     * バニラMinecraftのステータス修正を追加
     *
     * @param stat VanillaStatDataインスタンス
     */
    fun vanillaStat(stat: VanillaStatData) = apply {
        effect.mc_stats.add(stat)
    }

    /**
     * 最大スタック数を設定
     *
     * @param stacks 最大スタック数
     */
    fun maxStacks(stacks: Int) = apply {
        effect.max_stacks = stacks
    }

    /**
     * エフェクトに関連付けるスペルを設定
     *
     * @param spell Spellインスタンス
     */
    fun spell(spell: Spell) = apply {
        effect.spell = spell.attached
    }

    /**
     * スタックによるステータスバフの増幅を無効化
     */
    fun disableStackingStatBuff() = apply {
        effect.stacks_affect_stats = false
    }

    /**
     * ExileEffectインスタンスを生成
     *
     * @return 設定された値に基づくExileEffect
     */
    fun build(): ExileEffect {
        // レジストリ登録を呼び出さない（addToSerializablesを呼ばない）
        return effect
    }

    companion object {
        /**
         * ExileEffectBuilderKtを作成
         *
         * @param ctx EffectCtxインスタンス
         * @return 新しいExileEffectBuilderKtインスタンス
         */
        fun of(ctx: EffectCtx) = ExileEffectBuilderKt(ctx)

        /**
         * 食べ物専用のExileEffectBuilderKtを作成
         * 自動的にfoodタグが付与され、maxStacks(1)が設定される
         *
         * @param ctx EffectCtxインスタンス
         * @return 新しいExileEffectBuilderKtインスタンス（foodタグ付き）
         */
        fun food(ctx: EffectCtx) = of(ctx).apply {
            addTags(EffectTags.food)
            maxStacks(1)
        }
    }
}

/**
 * Kotlin DSLスタイルでExileEffectを作成する便利関数
 *
 * 使用例:
 * ```kotlin
 * val effect = exileEffect(BARKSKIN) {
 *     stat(-70f, -70f, DefenseStats.DAMAGE_RECEIVED.get(), ModType.MORE)
 *     stat(1f, 2f, HealthRegen.getInstance())
 *     maxStacks(1)
 * }
 * ```
 *
 * @param ctx EffectCtx
 * @param block ビルダーの設定ブロック
 * @return 構築されたExileEffect
 */
fun exileEffect(ctx: EffectCtx, block: ExileEffectBuilderKt.() -> Unit): ExileEffect {
    return ExileEffectBuilderKt.of(ctx).apply(block).build()
}
