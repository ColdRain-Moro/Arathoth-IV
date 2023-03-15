package team.redrock.rain.arathoth.core.attr

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import team.redrock.rain.arathoth.Arathoth
import java.io.File

/**
 * Arathoth
 * team.redrock.rain.team.redrock.rain.arathoth.core.attr
 *
 * @author 寒雨
 * @since 2023/3/9 下午4:24
 */
abstract class Attribute {
    open val configFile: File by lazy {
        Arathoth.attributesDir.resolve("$name.yml")
    }

    protected val config: FileConfiguration by lazy {
        if (!configFile.exists()) {
            configFile.createNewFile()
            YamlConfiguration.loadConfiguration(configFile)
        } else {
            YamlConfiguration.loadConfiguration(configFile).apply {
                initConfig()
                save(configFile)
            }
        }
    }

    open val name: String = this::class.simpleName ?: error("匿名属性对象必须重写name字段")

    open val property: Int
        get() = config.getInt("property")

    open fun FileConfiguration.initConfig() {
        set("property", 0)
    }

    open fun onLoad() {  }

    open fun onUnload() {  }

    internal fun load() {
        config.load(configFile)
        onLoad()
    }
}