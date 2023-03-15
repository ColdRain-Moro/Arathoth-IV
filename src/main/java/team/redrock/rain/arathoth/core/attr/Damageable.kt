package team.redrock.rain.arathoth.core.attr

import team.redrock.rain.arathoth.core.event.ArathothEvents


/**
 * Arathoth
 * team.redrock.rain.team.redrock.rain.arathoth.core.attr
 *
 * @author 寒雨
 * @since 2023/3/9 下午4:29
 */
interface Damageable {
    fun onAttack(event: ArathothEvents.PreDamage)
}