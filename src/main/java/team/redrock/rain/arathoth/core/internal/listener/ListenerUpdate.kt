package team.redrock.rain.arathoth.core.internal.listener

import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerItemBreakEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import taboolib.common.platform.event.SubscribeEvent
import team.redrock.rain.arathoth.core.attr.AttributeManager
import team.redrock.rain.arathoth.core.event.ArathothEvents

/**
 * Arathoth
 * team.redrock.rain.team.redrock.rain.arathoth.core.internal.listener
 *
 * @author 寒雨
 * @since 2023/3/10 下午2:26
 */
object ListenerUpdate {

    @SubscribeEvent
    fun onUpdate(e: ArathothEvents.Update) {
        AttributeManager.load(e.src, e.entity)
    }

    @SubscribeEvent
    fun e(e: PlayerItemHeldEvent) {
        e.player.inventory.heldItemSlot
        ArathothEvents.Update(e.player, e.player).call()
    }

    @SubscribeEvent
    fun e(e: InventoryCloseEvent) {
        ArathothEvents.Update(e.player, e.player).call()
    }

    @SubscribeEvent
    fun e(e: PlayerSwapHandItemsEvent) {
        ArathothEvents.Update(e.player, e.player).call()
    }

    @SubscribeEvent
    fun e(e: PlayerItemBreakEvent) {
        ArathothEvents.Update(e.player, e.player).call()
    }

    @SubscribeEvent
    fun e(e: PlayerJoinEvent) {
        ArathothEvents.Update(e.player, e.player).call()
    }

    @SubscribeEvent
    fun e(e: EntitySpawnEvent) {
        ArathothEvents.Update(e.entity, e.entity).call()
    }

    @SubscribeEvent
    fun e(e: EntityShootBowEvent) {
        ArathothEvents.Update(e.entity, e.projectile).call()
    }
}