package team.redrock.rain.arathoth.impl.loader.lore

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.adaptPlayer
import taboolib.common5.Coerce
import taboolib.module.kether.KetherShell
import taboolib.module.kether.extend
import team.redrock.rain.arathoth.Arathoth
import team.redrock.rain.arathoth.core.data.AttributeData
import team.redrock.rain.arathoth.core.data.AttributeDataMap
import team.redrock.rain.arathoth.core.data.AttributeFormat
import team.redrock.rain.arathoth.impl.loader.item.ItemAttributeLoader
import team.redrock.rain.arathoth.utils.attributeRange
import team.redrock.rain.arathoth.utils.await
import team.redrock.rain.arathoth.utils.lore

/**
 * Arathoth
 * team.redrock.rain.arathoth.impl.loader.lore
 *
 * @author 寒雨
 * @since 2023/3/14 下午11:11
 */
class LoreItemAttributeLoader : ItemAttributeLoader, CoroutineScope by Arathoth.pluginScope {
    override fun loadPlayer(player: Player, item: ItemStack, slot: Int, dataMap: AttributeDataMap) {
        launch {
            // 玩家的话需要先判断condition
            val pass = LoreAttributeLoader.conditions
                .filter { (_, v) -> v.pattern.matcher(item.lore.joinToString("\n")).find() }
                // 没有条件的话默认不加载
                .ifEmpty { return@launch }
                .all { (_, v) ->
                    val params = item.lore.joinToString("\n").let { lore ->
                        v.pattern.matcher(lore)
                            .takeIf { it.find() }
                            ?.let { matcher ->
                                (0 until matcher.groupCount()).map { matcher.group(it) }
                            } ?: emptyList()
                    }
                    KetherShell.eval(v.condition) {
                        sender = adaptPlayer(player)
                        extend(
                            mapOf(
                                "slot" to slot,
                                "item" to item,
                                "params" to params
                            )
                        )
                    }.await().let { Coerce.toBoolean(it) }
                }
            if (pass) {
                loadItemAttribute(item, dataMap)
            }
        }
    }

    override fun loadEntity(livingEntity: LivingEntity, item: ItemStack, dataMap: AttributeDataMap) {
        loadItemAttribute(item, dataMap)
    }
}

private fun loadItemAttribute(item: ItemStack, dataMap: AttributeDataMap) {
    LoreAttributeLoader.attributePatterns.forEach { (key, patterns) ->
        fun loadAttribute(value: Pair<Double, Double>) {
            if (key.endsWith("%")) {
                val k = key.removeSuffix("%")
                dataMap[k]?.append(
                    AttributeData(AttributeFormat.SCALAR, value)
                )
            } else {
                dataMap[key]?.append(
                    AttributeData(AttributeFormat.NUMBER, value)
                )
            }
        }
        patterns.forEach { pattern ->
            pattern.matcher(item.lore.joinToString("\n"))
                .takeIf { it.find() }
                ?.attributeRange()
                ?.let { loadAttribute(it) }
        }
    }
}