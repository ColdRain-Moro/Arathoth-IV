package team.redrock.rain.arathoth

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import taboolib.common.env.RuntimeDependencies
import taboolib.common.env.RuntimeDependency
import taboolib.common.platform.Plugin
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.info
import taboolib.platform.BukkitPlugin
import team.redrock.rain.arathoth.core.event.ArathothEvents
import team.redrock.rain.arathoth.utils.coroutine.BukkitMain
import java.io.File

@RuntimeDependencies(
    RuntimeDependency(
        value = "!org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2",
        test = "kotlinx.coroutines.AwaitAll",
        relocate = ["!kotlin.", "!kotlin@kotlin_version_escape@."]
    ),
)
object Arathoth : Plugin() {

    val pluginInst = BukkitPlugin.getInstance()
    val attributesDir = pluginInst.dataFolder.resolve("attributes")
    val pluginScope = CoroutineScope(Dispatchers.BukkitMain)

    override fun onEnable() {
        if (!attributesDir.exists()) {
            attributesDir.createNewFile()
        }
        // 加载所有模块
        ArathothEvents.PluginLoad().call()
    }
}