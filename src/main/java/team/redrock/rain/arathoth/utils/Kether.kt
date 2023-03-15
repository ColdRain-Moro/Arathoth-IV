package team.redrock.rain.arathoth.utils

import org.bukkit.entity.Player
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.script

/**
 * Arathoth
 * team.redrock.rain.arathoth.utils
 *
 * @author 寒雨
 * @since 2023/3/14 上午11:54
 */
fun ScriptFrame.getPlayer(): Player {
    return script().sender?.castSafely<Player>() ?: error("No player selected.")
}

