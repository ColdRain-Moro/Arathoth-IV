package team.redrock.rain.arathoth.core.attr.loader

import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import team.redrock.rain.arathoth.core.data.AttributeDataMap

/**
 * Arathoth
 * team.redrock.rain.team.redrock.rain.arathoth.core.attr.loader
 *
 * @author 寒雨
 * @since 2023/3/9 下午5:28
 */
abstract class PlayerAttributeLoader : LivingEntityAttributeLoader() {
    override fun load(livingEntity: LivingEntity, dataMap: AttributeDataMap) {
        if (livingEntity is Player) load(player = livingEntity, dataMap)
    }

    abstract fun load(player: Player, dataMap: AttributeDataMap)
}