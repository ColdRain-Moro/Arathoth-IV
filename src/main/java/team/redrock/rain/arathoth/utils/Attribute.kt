package team.redrock.rain.arathoth.utils

import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import taboolib.common.platform.function.adaptPlayer
import taboolib.common.util.randomDouble
import taboolib.module.kether.KetherShell
import taboolib.module.kether.ScriptContext

/**
 * Arathoth
 * team.redrock.rain.arathoth.utils
 *
 * @author 寒雨
 * @since 2023/3/19 下午11:09
 */
fun LivingEntity.heal(value: Double) {
    health = (health + value)
        .coerceAtMost(maxHealth)
        .coerceAtLeast(0.0)
}

fun pseudoRandom(rate: Double): Boolean {
    return rate > randomDouble()
}

fun Double.positive(): Double {
    return coerceAtLeast(0.0)
}

internal fun LivingEntity.executeAction(
    playerAction: String? = null,
    entityAction: String? = null,
    configure: ScriptContext.() -> Unit = {}
) {
    playerAction?.let { action ->
        if (this is Player) {
            KetherShell.eval(action) {
                sender = adaptPlayer(this)
                configure()
            }
        }
    }
    entityAction?.let { action ->
        KetherShell.eval(action, context = configure)
    }
}