package team.redrock.rain.arathoth.impl.loader.item

import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import taboolib.platform.util.isNotAir
import team.redrock.rain.arathoth.core.attr.loader.LivingEntityAttributeLoader
import team.redrock.rain.arathoth.core.data.AttributeDataMap

/**
 * Arathoth
 * team.redrock.rain.arathoth.impl.loader.item
 *
 * @author 寒雨
 * @since 2023/3/14 下午8:04
 */
open class InventoryItemsAttributeLoader(
    protected val loader: ItemAttributeLoader
) : LivingEntityAttributeLoader() {
    override fun load(livingEntity: LivingEntity, dataMap: AttributeDataMap) {
        if (livingEntity is Player) {
            (0 until livingEntity.inventory.size).forEach { slot ->
                livingEntity.inventory.getItem(slot)?.let { item ->
                    if (item.isNotAir()) {
                        loader.loadPlayer(livingEntity, item, slot, dataMap)
                    }
                }
            }
        } else {
            livingEntity.equipment?.let {
                it.armorContents.toMutableList().apply {
                    add(it.itemInMainHand)
                    add(it.itemInOffHand)
                }
            }?.filter { it.isNotAir() }
             ?.forEach { item -> loader.loadEntity(livingEntity, item, dataMap) }
        }
    }
}