package team.redrock.rain.arathoth.impl.loader

import org.bukkit.entity.Entity
import org.bukkit.entity.Projectile
import team.redrock.rain.arathoth.core.attr.attrDataMap
import team.redrock.rain.arathoth.core.attr.loader.AttributeLoader
import team.redrock.rain.arathoth.core.data.AttributeDataMap

/**
 * Arathoth
 * team.redrock.rain.team.redrock.rain.arathoth.core.attr.loader
 *
 * @author 寒雨
 * @since 2023/3/9 下午5:30
 */
class ProjectileAttributeLoader : AttributeLoader() {
    override fun load(src: Entity, entity: Entity, dataMap: AttributeDataMap) {
        if (entity is Projectile) {
            dataMap.putAll(src.attrDataMap)
        }
    }
}