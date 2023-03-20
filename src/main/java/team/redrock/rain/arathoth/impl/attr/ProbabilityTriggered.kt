package team.redrock.rain.arathoth.impl.attr

import org.bukkit.configuration.file.FileConfiguration
import taboolib.module.kether.ScriptContext
import team.redrock.rain.arathoth.core.event.ArathothEvents
import team.redrock.rain.arathoth.utils.executeAction

/**
 * Arathoth
 * team.redrock.rain.arathoth.impl.attr
 *
 * @author 寒雨
 * @since 2023/3/20 上午12:03
 */
interface ProbabilityTriggered {

    val config: FileConfiguration

    val victimPlayerAction: String
        get() = config.getString("script.player.victim", "")!!
    val attackerPlayerAction: String
        get() = config.getString("script.player.attacker", "")!!
    val victimAction: String
        get() = config.getString("script.victim", "")!!
    val attackerAction: String
        get() = config.getString("script.attacker", "")!!

    fun ArathothEvents.PreDamage.runActions() {
        attacker.executeAction(attackerPlayerAction, attackerAction)
        victim.executeAction(victimPlayerAction, victimAction)
    }
}