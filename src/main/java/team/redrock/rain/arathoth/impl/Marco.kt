package team.redrock.rain.arathoth.impl

import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common5.util.replace
import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration
import team.redrock.rain.arathoth.core.event.ArathothEvents

/**
 * Arathoth
 * team.redrock.rain.arathoth.impl
 *
 * @author 寒雨
 * @since 2023/3/12 上午12:24
 */
object Marco {
    @Config("marco.yml")
    lateinit var config: Configuration
        private set
    private val regexMarcos = hashMapOf<String, String>()
    internal val ketherMarcos = hashMapOf<String, String>()

    // 宏的加载需要在其他模块加载之前
    @SubscribeEvent(priority = EventPriority.LOWEST)
    fun load(e: ArathothEvents.PluginLoad) {
        regexMarcos.clear()
        ketherMarcos.clear()
        config.getConfigurationSection("regex")?.let { section ->
            section.getKeys(false)
                .forEach { key ->
                    regexMarcos[key] = section.getString(key)!!
                }
        }
        config.getConfigurationSection("kether")?.let { section ->
            section.getKeys(false)
                .forEach { key ->
                    regexMarcos[key] = section.getString(key)!!
                }
        }
    }

    internal fun String.expansionRegexMarco(): String {
        return this.replace(
            *regexMarcos.mapKeys { (k, _) -> "[$k]" }
                .toList()
                .toTypedArray()
        )
    }
}