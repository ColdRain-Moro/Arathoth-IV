package team.redrock.rain.arathoth.impl.loader.item

import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import team.redrock.rain.arathoth.core.data.AttributeDataMap

/**
 * Arathoth
 * team.redrock.rain.arathoth.impl.loader
 *
 * @author 寒雨
 * @since 2023/3/14 下午8:00
 */
interface ItemAttributeLoader {
    fun loadPlayer(player: Player, item: ItemStack, slot: Int, dataMap: AttributeDataMap)

    fun loadEntity(livingEntity: LivingEntity, item: ItemStack, dataMap: AttributeDataMap)
}