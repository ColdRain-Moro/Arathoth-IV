package team.redrock.rain.arathoth.core.attr.loader

import org.bukkit.entity.Entity
import team.redrock.rain.arathoth.core.data.AttributeDataMap

/**
 * Arathoth
 * team.redrock.rain.team.redrock.rain.arathoth.core.attr
 *
 * @author 寒雨
 * @since 2023/3/9 下午5:22
 */
abstract class AttributeLoader {
    abstract fun load(src: Entity, entity: Entity, dataMap: AttributeDataMap)
}