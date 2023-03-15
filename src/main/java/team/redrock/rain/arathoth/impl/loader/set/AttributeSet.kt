package team.redrock.rain.arathoth.impl.loader.set

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.releaseResourceFile
import taboolib.module.kether.KetherShell
import taboolib.module.kether.Script
import taboolib.module.kether.parseKetherScript
import team.redrock.rain.arathoth.Arathoth
import team.redrock.rain.arathoth.core.attr.AttributeManager
import team.redrock.rain.arathoth.core.attr.loader.AttributeLoader
import team.redrock.rain.arathoth.core.event.ArathothEvents

/**
 * Arathoth
 * team.redrock.rain.arathoth.impl.loader.set
 *
 * @author 寒雨
 * @since 2023/3/14 下午1:28
 */
data class AttributeSet(
    val name: String,
    val limit: String?,
    val attribute: Map<String, Any>
) {

    companion object {

        private val _attributeSets: MutableMap<String, AttributeSet> = mutableMapOf()
        val attributeSets: Map<String, AttributeSet>
            get() = _attributeSets

        private val setDir = Arathoth.pluginInst
            .dataFolder
            .resolve("loader")
            .resolve("set")

        @SubscribeEvent
        fun load(e: ArathothEvents.PluginLoad) {
            _attributeSets.clear()
            if (!setDir.exists()) {
                releaseResourceFile("loader/set/def.yml")
            }
            setDir.listFiles()
                ?.filter { it.name.endsWith(".yml") }
                ?.map { file -> YamlConfiguration.loadConfiguration(file) }
                ?.flatMap { conf -> fromConfiguration(conf) }
                ?.onEach { attributeSet -> _attributeSets[attributeSet.name] = attributeSet }
            // 注册属性加载器
            AttributeManager.registerLoader(SetAttributeLoader())
        }

        private fun fromConfiguration(conf: FileConfiguration): List<AttributeSet> {
            return conf.getKeys(false).map { key ->
                AttributeSet(
                    key,
                    // 预编译
                    conf.getString("$key.limit"),
                    conf.getConfigurationSection("$key.attribute")?.let { section ->
                        section.getKeys(false).associateWith { k -> section[k]!! }
                    } ?: emptyMap()
                )
            }
        }
    }
}