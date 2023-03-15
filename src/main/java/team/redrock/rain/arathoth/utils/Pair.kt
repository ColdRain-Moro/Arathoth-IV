package team.redrock.rain.arathoth.utils

import kotlin.math.max
import kotlin.math.min

/**
 * Arathoth
 * team.redrock.rain.team.redrock.rain.arathoth.utils
 *
 * @author 寒雨
 * @since 2023/3/9 下午4:14
 */
fun Pair<Double, Double>.max(): Double {
    return max(first, second)
}

fun Pair<Double, Double>.min(): Double {
    return min(first, second)
}