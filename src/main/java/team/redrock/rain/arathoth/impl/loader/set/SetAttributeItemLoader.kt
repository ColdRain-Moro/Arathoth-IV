package team.redrock.rain.arathoth.impl.loader.set

import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.adaptPlayer
import taboolib.common5.Coerce
import taboolib.module.kether.KetherShell
import taboolib.module.kether.ScriptContext
import taboolib.module.kether.extend
import taboolib.module.nms.getItemTag
import team.redrock.rain.arathoth.core.data.AttributeData
import team.redrock.rain.arathoth.core.data.AttributeDataMap
import team.redrock.rain.arathoth.core.data.AttributeFormat
import team.redrock.rain.arathoth.impl.loader.item.ItemAttributeLoader

/**
 * Arathoth
 * team.redrock.rain.arathoth.impl.loader.set
 *
 * @author 寒雨
 * @since 2023/3/14 下午8:18
 */
class SetAttributeItemLoader : ItemAttributeLoader {
    override fun loadPlayer(player: Player, item: ItemStack, slot: Int, dataMap: AttributeDataMap) {
        val itemTag = item.getItemTag()
        val tagList = itemTag.getDeep("arathoth.sets")
            ?.asList()
            ?.map { it.asString() } ?: return
        AttributeSet.attributeSets.forEach { (k, set) ->
            if (tagList.contains(k)) {
                // 是否通过限制条件
                set.limit?.let { limit ->
                    KetherShell.eval(limit) {
                        sender = adaptPlayer(player)
                        extend(
                            mapOf(
                                "item" to item,
                                "slot" to slot
                            )
                        )
                    }.thenAccept { pass ->
                        if (Coerce.toBoolean(pass)) {
                            loadAttrSet(set.attribute, dataMap) {
                                sender = adaptPlayer(player)
                                extend(
                                    mapOf(
                                        "item" to item,
                                        "slot" to slot
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    override fun loadEntity(livingEntity: LivingEntity, item: ItemStack, dataMap: AttributeDataMap) {
        val itemTag = item.getItemTag()
        val tagList = itemTag.getDeep("arathoth.sets")
            ?.asList()
            ?.map { it.asString() } ?: return
        AttributeSet.attributeSets.forEach { (k, set) ->
            if (tagList.contains(k)) {
                loadAttrSet(set.attribute, dataMap) {
                    extend(mapOf("item" to item))
                }
            }
        }
    }
}

private fun loadAttrSet(map: Map<String, Any>, dataMap: AttributeDataMap, ketherConfigure: ScriptContext.() -> Unit) {
    map.forEach { (k, v) ->
        fun loadAttribute(value: Pair<Double, Double>) {
            if (k.endsWith("%")) {
                val key = k.removeSuffix("%")
                dataMap[key]?.append(
                    AttributeData(AttributeFormat.SCALAR, value)
                )
            } else {
                dataMap[k]?.append(
                    AttributeData(AttributeFormat.NUMBER, value)
                )
            }
        }
        when (v) {
            is String -> {
                KetherShell.eval(v, context = ketherConfigure).thenAccept {
                    if (it is Number) {
                        val value = Coerce.toDouble(it)
                        loadAttribute(value to value)
                    } else if (it is List<*>) {
                        val value = Coerce.toDouble(it[0])
                        val value2 = it[1]?.let { v -> Coerce.toDouble(v) } ?: value
                        loadAttribute(value to value2)
                    }
                }
            }
            is Number -> {
                val value = Coerce.toDouble(v)
                loadAttribute(value to value)
            }
            is List<*> -> {
                val value = Coerce.toDouble(v[0])
                val value2 = v[1]?.let { Coerce.toDouble(it) } ?: value
                loadAttribute(value to value2)
            }
        }
    }
}

