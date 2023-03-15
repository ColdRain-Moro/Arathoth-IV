package team.redrock.rain.arathoth.core.internal.listener

import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit
import team.redrock.rain.arathoth.core.attr.AttributeManager

/**
 * Arathoth
 * team.redrock.rain.team.redrock.rain.arathoth.core.internal.listener
 *
 * @author 寒雨
 * @since 2023/3/10 下午2:44
 */
object ListenerRelease {
    @SubscribeEvent
    fun e(e: PlayerQuitEvent) {
        AttributeManager.release(e.player)
    }

    @SubscribeEvent
    fun e(e: ProjectileHitEvent) {
        submit { AttributeManager.release(e.entity) }
    }

    @SubscribeEvent
    fun e(e: EntityDeathEvent) {
        AttributeManager.release(e.entity)
    }
}