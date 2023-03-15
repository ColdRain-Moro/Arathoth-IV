package team.redrock.rain.arathoth.core.attr

import org.bukkit.entity.Entity
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import team.redrock.rain.arathoth.core.attr.loader.AttributeLoader
import team.redrock.rain.arathoth.core.data.AttributeDataMap
import team.redrock.rain.arathoth.core.data.ImmutableAttributeDataMap
import team.redrock.rain.arathoth.core.event.ArathothEvents
import team.redrock.rain.arathoth.utils.sendError
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Arathoth
 * team.redrock.rain.team.redrock.rain.arathoth.core.attr
 *
 * @author 寒雨
 * @since 2023/3/9 下午4:51
 */
object AttributeManager {
    internal val registry: MutableMap<String, Attribute> = mutableMapOf()
    val dataRegistry: MutableMap<UUID, AttributeDataMap> = ConcurrentHashMap()
    private val loaderRegistry: MutableSet<AttributeLoader> = mutableSetOf()
    private val attributeLoaderExecutor: ExecutorService = Executors.newSingleThreadExecutor()

    @SubscribeEvent(priority = EventPriority.LOWEST)
    fun load(e: ArathothEvents.PluginLoad) {
        unregisterAll()
        dataRegistry.clear()
        loaderRegistry.clear()
    }

    fun register(attr: Attribute) {
        if (registry[attr.name] != null) {
            sendError("存在重名属性: ${attr.name}, 拒绝注册后者")
            return
        }
        registry[attr.name] = attr
    }

    private fun unregisterAll() {
        registry.forEach { (_, attr) ->
            attr.onUnload()
        }
        registry.clear()
    }

    fun registerLoader(loader: AttributeLoader) {
        loaderRegistry.add(loader)
    }

    internal fun load(src: Entity, entity: Entity) {
        attributeLoaderExecutor.submit {
            entity.mutableAttrDataMap.also { dataMap ->
                dataMap.clear()
                loaderRegistry.forEach { loader ->
                    if (ArathothEvents.PreLoad(src, entity, dataMap, loader).call()) {
                        loader.load(src, entity, dataMap)
                        ArathothEvents.PostLoad(src, entity, dataMap, loader).call()
                    }
                }
            }
        }
    }

    internal fun load(entity: Entity) {
        load(entity, entity)
    }

    @Awake(LifeCycle.DISABLE)
    internal fun onDisable() {
        unregisterAll()
        attributeLoaderExecutor.shutdown()
    }

    internal fun release(e: Entity) {
        dataRegistry.remove(e.uniqueId)
    }
}

internal val Entity.mutableAttrDataMap: AttributeDataMap
    get() = AttributeManager.dataRegistry.computeIfAbsent(uniqueId) { AttributeDataMap() }

val Entity.attrDataMap: ImmutableAttributeDataMap
    get() = mutableAttrDataMap.immutable

