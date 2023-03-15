package team.redrock.rain.arathoth.utils

import org.bukkit.Bukkit
import taboolib.module.chat.colored

/**
 * Arathoth
 * team.redrock.rain.team.redrock.rain.arathoth.utils
 *
 * @author 寒雨
 * @since 2023/3/9 下午4:58
 */
const val INFO_PREFIX = "&7&l[&8&lArathoth&7&l] &fINFO &8| &f"
const val WARN_PREFIX = "&7&l[&8&lArathoth&7&l] &6WARN &8| &f"
const val ERROR_PREFIX = "&7&l[&8&lArathoth&7&l] &cERROR &8| &f"

fun sendInfo(msg: String) {
    Bukkit.getConsoleSender().sendMessage((INFO_PREFIX + msg).colored())
}

fun sendWarn(msg: String) {
    Bukkit.getConsoleSender().sendMessage((WARN_PREFIX + msg).colored())
}

fun sendError(msg: String) {
    Bukkit.getConsoleSender().sendMessage((ERROR_PREFIX + msg).colored())
}

