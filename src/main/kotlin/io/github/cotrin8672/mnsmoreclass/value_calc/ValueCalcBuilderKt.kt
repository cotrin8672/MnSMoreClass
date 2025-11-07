package io.github.cotrin8672.mnsmoreclass.value_calc

import com.robertx22.mine_and_slash.database.data.stats.Stat
import com.robertx22.mine_and_slash.database.data.stats.types.offense.WeaponDamage
import com.robertx22.mine_and_slash.database.data.stats.types.resources.health.Health
import com.robertx22.mine_and_slash.database.data.value_calc.LeveledValue
import com.robertx22.mine_and_slash.database.data.value_calc.ScalingCalc
import com.robertx22.mine_and_slash.database.data.value_calc.ValueCalculation

/**
 * ValueCalculationを生成するためのKotlinビルダー
 * Mine and Slashの元のBuilderと異なり、レジストリ登録を行わない
 */
class ValueCalcBuilderKt private constructor(private val id: String) {
    private var baseMin: Float = 0f
    private var baseMax: Float = 0f
    private var spellScalingMin: Float = 0f
    private var spellScalingMax: Float = 0f
    private val statScalings = mutableListOf<Pair<Stat, Pair<Float, Float>>>()
    private var capToWeaponDamage: Float = 1000f

    /**
     * スペルスケーリング（魔法攻撃用）
     *
     * @param min 最小レベルでのスケーリング倍率
     * @param max 最大レベルでのスケーリング倍率
     */
    fun spellScaling(min: Float, max: Float) = apply {
        this.spellScalingMin = min
        this.spellScalingMax = max

        // 自動的にベース値を設定（Mine and Slashの実装に準拠）
        if (baseMin == 0f && baseMax == 0f) {
            baseMin = 2 * min
            baseMax = 6 * max
        }

        // 武器ダメージスケーリングを追加
        statScalings.add(WeaponDamage.getInstance() to (min to max))
    }

    /**
     * 攻撃スケーリング（物理攻撃用）
     * spellScaling と同じ実装だが、意味的に物理攻撃用として使用
     *
     * @param min 最小レベルでのスケーリング倍率
     * @param max 最大レベルでのスケーリング倍率
     */
    fun attackScaling(min: Float, max: Float) = apply {
        // spellScalingと同じロジック
        this.spellScalingMin = min
        this.spellScalingMax = max

        if (baseMin == 0f && baseMax == 0f) {
            baseMin = 2 * min
            baseMax = 6 * max
        }

        statScalings.add(WeaponDamage.getInstance() to (min to max))
    }

    /**
     * 特定のステータスによるスケーリングを追加
     *
     * @param stat スケーリングの基準となるステータス（Energy, Manaなど）
     * @param min 最小レベルでのスケーリング係数
     * @param max 最大レベルでのスケーリング係数
     */
    fun statScaling(stat: Stat, min: Float, max: Float) = apply {
        statScalings.add(stat to (min to max))
    }

    /**
     * 武器ダメージに対するスケーリングのキャップを設定
     *
     * @param cap キャップ倍率（デフォルト1000 = 実質無制限）
     */
    fun capScaling(cap: Float) = apply {
        this.capToWeaponDamage = cap

        // Mine and Slashの実装に準拠：キャップの半分をdmg_effectivenessに加算
        // （これはbuild()で適用される）
    }

    /**
     * ベース値を手動設定
     * 通常は自動設定されるため、特殊なケースでのみ使用
     *
     * @param min 最小レベルでのベース値
     * @param max 最大レベルでのベース値
     */
    fun baseValue(min: Float, max: Float) = apply {
        this.baseMin = min
        this.baseMax = max
    }

    /**
     * ValueCalculationインスタンスを生成
     *
     * @return 設定された値に基づくValueCalculation
     */
    fun build(): ValueCalculation {
        val scaling = spellScalingMin to spellScalingMax

        return ValueCalculation().apply {
            this.id = this@ValueCalcBuilderKt.id
            this.base = LeveledValue(baseMin, baseMax)

            // dmg_effectivenessの設定
            val effectivenessMax = scaling.second + (capToWeaponDamage / 2f)
            val effectivenessMin = scaling.first + (capToWeaponDamage / 2f)
            this.dmg_effectiveness = ScalingCalc(
                Health.getInstance(),
                LeveledValue(effectivenessMin, effectivenessMax)
            )

            // 各ステータスのスケーリングを追加
            this@ValueCalcBuilderKt.statScalings.forEach { (stat, range) ->
                this.stat_scalings.add(
                    ScalingCalc(stat, LeveledValue(range.first, range.second))
                )
            }

            this.cap_to_wep_dmg = capToWeaponDamage
        }
    }

    companion object {
        /**
         * ValueCalcBuilderKtを作成
         *
         * @param id ValueCalculationの一意なID
         * @return 新しいValueCalcBuilderKtインスタンス
         */
        fun of(id: String) = ValueCalcBuilderKt(id)
    }
}

/**
 * Kotlin DSLスタイルでValueCalculationを作成する便利関数
 *
 * 使用例:
 * ```kotlin
 * val calc = valueCalc("my_spell") {
 *     spellScaling(1.0f, 2.0f)
 *     statScaling(Energy.getInstance(), 0.05f, 0.05f)
 *     capScaling(1f)
 * }
 * ```
 *
 * @param id ValueCalculationのID
 * @param block ビルダーの設定ブロック
 * @return 構築されたValueCalculation
 */
fun valueCalc(id: String, block: ValueCalcBuilderKt.() -> Unit): ValueCalculation {
    return ValueCalcBuilderKt.of(id).apply(block).build()
}
