package team.redrock.rain.arathoth.impl.loader.lore

import java.util.regex.Pattern

/**
 * Arathoth
 * team.redrock.rain.arathoth.impl.loader.lore
 *
 * @author 寒雨
 * @since 2023/3/15 上午12:27
 */
data class Condition(
    val name: String,
    val pattern: Pattern,
    val condition: String
)
