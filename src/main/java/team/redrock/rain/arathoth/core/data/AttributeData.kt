package team.redrock.rain.arathoth.core.data

import taboolib.common.util.random
import team.redrock.rain.arathoth.utils.max
import team.redrock.rain.arathoth.utils.min

/**
 * Arathoth
 * team.redrock.rain.team.redrock.rain.arathoth.core.data
 *
 * @author 寒雨
 * @since 2023/3/9 下午4:05
 */
open class AttributeData(
    private val values: MutableMap<AttributeFormat, Pair<Double, Double>>,
) {
    constructor() : this(mutableMapOf())
    constructor(format: AttributeFormat, value: Pair<Double, Double>): this(mutableMapOf(format to value))
    constructor(format: AttributeFormat, value: Double): this(format, value to value)

    open fun append(data: AttributeData): AttributeData {
        AttributeFormat.values()
            .associateWith { (min(it) + data.min(it) to max(it) + data.max(it)) }
            .also { values.putAll(it) }
        return this
    }

    fun min(format: AttributeFormat): Double {
        return values.computeIfAbsent(format) { 0.0 to 0.0 }.min()
    }

    fun max(format: AttributeFormat): Double {
        return values.computeIfAbsent(format) { 0.0 to 0.0 }.max()
    }

    fun random(format: AttributeFormat): Double {
        return random(min(format), max(format))
    }

    fun executeChance(): Boolean {
        return random(AttributeFormat.SCALAR) > random(0.0, 100.0)
    }
}

enum class AttributeFormat {
    NUMBER, SCALAR
}