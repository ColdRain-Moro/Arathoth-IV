package team.redrock.rain.arathoth.core.internal.listener

import org.bukkit.event.entity.EntityDamageByEntityEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit
import team.redrock.rain.arathoth.core.attr.AttributeManager
import team.redrock.rain.arathoth.core.attr.Damageable
import team.redrock.rain.arathoth.core.event.ArathothEvents

/**
 * Arathoth
 * team.redrock.rain.team.redrock.rain.arathoth.core.internal.listener
 *
 * @author 寒雨
 * @since 2023/3/11 下午10:01
 */
object ListenerAttribute {
    @SubscribeEvent
    fun e(e: EntityDamageByEntityEvent) {
        val preDamage = ArathothEvents.PreDamage.fromBukkit(e)
        if (preDamage?.call() == true) {
            submit {
                ArathothEvents.PostDamage(preDamage).call()
            }
        }
    }

    @SubscribeEvent
    fun e(e: ArathothEvents.PreDamage) {
        AttributeManager.registry
            .values
            // 优先级越高越后计算
            .sortedBy { it.property }
            .filterIsInstance<Damageable>()
            .onEach { if (!e.isCancelled) it.onAttack(e) }
    }
}