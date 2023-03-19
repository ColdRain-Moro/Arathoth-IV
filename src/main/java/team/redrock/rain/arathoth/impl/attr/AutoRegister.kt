package team.redrock.rain.arathoth.impl.attr

import taboolib.common.io.runningClasses
import taboolib.common.platform.event.SubscribeEvent
import taboolib.library.reflex.Reflex.Companion.getProperty
import team.redrock.rain.arathoth.core.attr.Attribute
import team.redrock.rain.arathoth.core.attr.AttributeManager
import team.redrock.rain.arathoth.core.event.ArathothEvents

/**
 * Arathoth
 * team.redrock.rain.arathoth.impl.attr
 *
 * @author 寒雨
 * @since 2023/3/15 下午9:48
 */
@Target(AnnotationTarget.CLASS)
internal annotation class AutoRegister

object AutoRegisterLoader {
    @SubscribeEvent
    internal fun e(e: ArathothEvents.PluginLoad) {
        runningClasses.forEach {
            if (it.isAnnotationPresent(AutoRegister::class.java) && Attribute::class.java.isAssignableFrom(it)) {
                it.getProperty<Attribute>("INSTANCE")?.let { attr -> AttributeManager.register(attr) }
            }
        }
    }
}
