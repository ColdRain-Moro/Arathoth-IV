package team.redrock.rain.arathoth.utils

import org.bukkit.inventory.ItemStack
import taboolib.platform.util.hasLore

/**
 * Arathoth
 * team.redrock.rain.arathoth.utils
 *
 * @author 寒雨
 * @since 2023/3/15 下午6:37
 */
val ItemStack.lore : List<String>
    get() {
        return if (hasLore()) {
            itemMeta!!.lore!!
        } else {
            listOf()
        }
    }