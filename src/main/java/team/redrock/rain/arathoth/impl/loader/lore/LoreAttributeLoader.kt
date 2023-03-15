package team.redrock.rain.arathoth.impl.loader.lore

import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.event.SubscribeEvent
import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration
import taboolib.platform.util.isNotAir
import team.redrock.rain.arathoth.core.attr.AttributeManager
import team.redrock.rain.arathoth.core.data.AttributeDataMap
import team.redrock.rain.arathoth.core.event.ArathothEvents
import team.redrock.rain.arathoth.impl.Marco.expansionRegexMarco
import team.redrock.rain.arathoth.impl.loader.item.InventoryItemsAttributeLoader
import java.util.regex.Pattern

/**
 * Arathoth
 * team.redrock.rain.arathoth.impl.loader
 *
 * @author 寒雨
 * @since 2023/3/12 下午11:15
 */
class LoreAttributeLoader : InventoryItemsAttributeLoader(LoreItemAttributeLoader()) {

    override fun load(livingEntity: LivingEntity, dataMap: AttributeDataMap) {
        if (livingEntity is Player) {
            scanInventoryItems.getItems(livingEntity).forEach { (slot, item) ->
                if (item.isNotAir()) {
                    loader.loadPlayer(livingEntity, item, slot, dataMap)
                }
            }
        } else {
            super.load(livingEntity, dataMap)
        }
    }

    companion object {
        @Config("loader/lore/config.yml")
        lateinit var config: Configuration
            private set

        val enabled: Boolean
            get() = config.getBoolean("enable")

        internal lateinit var scanInventoryItems: ScanInventoryItems
        private val _attributePatterns: MutableMap<String, List<Pattern>> = mutableMapOf()
        private val _conditions: MutableMap<String, Condition> = mutableMapOf()
        val attributePatterns: Map<String, List<Pattern>>
            get() = _attributePatterns
        val conditions: Map<String, Condition>
            get() = _conditions

        @SubscribeEvent
        fun load(e: ArathothEvents.PluginLoad) {
            scanInventoryItems = config["scan-inventory-items"]?.let {
                if (it.toString().lowercase() == "all") {
                    return@let ScanInventoryItems.All
                }
                if (it is List<*>) {
                    return@let it.map { obj ->
                        when (obj) {
                            "main-hand" -> ItemSlot.MainHand
                            "offhand" -> ItemSlot.OffHand
                            is Int -> ItemSlot.Slot(obj)
                            else -> error("wrong params for scan-inventory-items: $obj")
                        }
                    }.let { list -> ScanInventoryItems.Slots(list) }
                }
                null
            } ?: error("scan-inventory-items not configured or incorrect configured.")
            _attributePatterns.clear()
            _conditions.clear()
            config.getConfigurationSection("attributes")?.let { section ->
                section.getKeys(false).associateWith { key ->
                    when (val obj = section[key]) {
                        is List<*> -> obj.map { Pattern.compile(it.toString().expansionRegexMarco()) }
                        else -> listOf(Pattern.compile(obj.toString().expansionRegexMarco()))
                    }
                }
            }?.let { _attributePatterns.putAll(it) }
            config.getConfigurationSection("conditions")?.let { section ->
                section.getKeys(false).associateWith { key ->
                    val pattern = section.getString("$key.pattern").let {
                        // 不将正则表达式整体看作捕获组
                        Pattern.compile("(?:$it)".expansionRegexMarco())
                    }
                    Condition(key, pattern, section.getString("$key.condition")!!)
                }
            }?.let { _conditions.putAll(it) }
            if (enabled) {
                AttributeManager.registerLoader(LoreAttributeLoader())
            }
        }
    }
}

// 我测 我好想念 rust 的枚举
sealed interface ScanInventoryItems {

    fun getItems(player: Player): Map<Int, ItemStack>

    object All : ScanInventoryItems {
        override fun getItems(player: Player): Map<Int, ItemStack> {
            return player.inventory
                .mapIndexed { index, itemStack -> index to itemStack }
                .toMap()
        }
    }

    data class Slots(val slots: List<ItemSlot>) : ScanInventoryItems {
        override fun getItems(player: Player): Map<Int, ItemStack> {
            return slots
                .mapNotNull { slot -> slot.getSlot(player) to (slot.getItem(player) ?: return@mapNotNull null) }
                .toMap()
        }
    }
}

sealed interface ItemSlot {
    fun getItem(player: Player): ItemStack?

    fun getSlot(player: Player): Int

    object MainHand : ItemSlot {
        override fun getItem(player: Player): ItemStack {
            return player.inventory.itemInMainHand
        }

        override fun getSlot(player: Player): Int {
            return player.inventory.heldItemSlot
        }
    }

    object OffHand : ItemSlot {
        override fun getItem(player: Player): ItemStack {
            return player.inventory.itemInOffHand
        }

        override fun getSlot(player: Player): Int {
            return 40
        }
    }

    class Slot(private val slot: Int) : ItemSlot {
        override fun getItem(player: Player): ItemStack? {
            return player.inventory.getItem(slot)
        }

        override fun getSlot(player: Player): Int {
            return slot
        }
    }
}