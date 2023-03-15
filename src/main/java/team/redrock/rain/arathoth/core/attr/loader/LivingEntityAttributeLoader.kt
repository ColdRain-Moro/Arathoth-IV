package team.redrock.rain.arathoth.core.attr.loader

import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import team.redrock.rain.arathoth.core.data.AttributeDataMap

/**
 * Arathoth
 * team.redrock.rain.team.redrock.rain.arathoth.core.attr.loader
 *
 * @author 寒雨
 * @since 2023/3/9 下午5:31
 */
abstract class LivingEntityAttributeLoader : AttributeLoader() {
    override fun load(src: Entity, entity: Entity, dataMap: AttributeDataMap) {
        if (entity is LivingEntity) load(livingEntity = entity, dataMap)
    }

    abstract fun load(livingEntity: LivingEntity, dataMap: AttributeDataMap)
}