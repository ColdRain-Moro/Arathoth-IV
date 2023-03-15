package team.redrock.rain.arathoth.utils

import taboolib.common5.Coerce
import java.util.regex.Matcher

/**
 * Arathoth
 * team.redrock.rain.arathoth.utils
 *
 * @author 寒雨
 * @since 2023/3/15 下午7:54
 */
fun Matcher.attributeRange(): Pair<Double, Double> {
    val min = Coerce.toDouble(group("min"))
    val max = group("max")?.let { Coerce.toDouble(it) } ?: min
    return min to max
}