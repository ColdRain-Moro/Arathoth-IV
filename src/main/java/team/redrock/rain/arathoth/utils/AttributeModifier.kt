package team.redrock.rain.arathoth.utils

import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.attribute.AttributeModifier.Operation
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.event.SubscribeEvent
import team.redrock.rain.arathoth.utils.ArathothModifier.Companion.modifiersMap
import java.util.UUID

/**
 * Arathoth
 * team.redrock.rain.arathoth.utils
 *
 * @author 寒雨
 * @since 2023/3/20 下午1:11
 */
internal class ArathothModifier(
    val attribute: Attribute,
    value: Double,
    op: Operation
) : AttributeModifier("Arathoth:${attribute}:${op}", value, op) {
    companion object {
        internal val modifiersMap = mutableMapOf<UUID, MutableSet<ArathothModifier>>()

        @SubscribeEvent
        fun e(e: EntityDeathEvent) {
            modifiersMap.remove(e.entity.uniqueId)?.let {
                it.forEach { attr -> e.entity.getAttribute(attr.attribute)?.removeModifier(attr) }
            }
        }

        @SubscribeEvent
        fun e(e: PlayerQuitEvent) {
            modifiersMap.remove(e.player.uniqueId)?.let {
                it.forEach { attr -> e.player.getAttribute(attr.attribute)?.removeModifier(attr) }
            }
        }
    }
}

fun LivingEntity.modifyAttribute(attribute: Attribute, value: Double, op: Operation = Operation.ADD_NUMBER) {
    val modifier = ArathothModifier(attribute, value, op)
    modifiersMap.computeIfAbsent(uniqueId) { mutableSetOf() }
        .apply {
            filter { it.name == modifier.name }
                .onEach { getAttribute(it.attribute)?.removeModifier(it) }
            removeIf { it.name == modifier.name }
            add(modifier)
        }
    getAttribute(modifier.attribute)?.addModifier(modifier)
}