package team.redrock.rain.arathoth.core.event

import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.event.entity.EntityDamageByEntityEvent
import taboolib.platform.type.BukkitProxyEvent
import taboolib.platform.util.attacker
import team.redrock.rain.arathoth.core.attr.attrDataMap
import team.redrock.rain.arathoth.core.attr.loader.AttributeLoader
import team.redrock.rain.arathoth.core.data.AttributeDataMap
import team.redrock.rain.arathoth.core.data.ImmutableAttributeDataMap

/**
 * Arathoth
 * team.redrock.rain.team.redrock.rain.arathoth.core.event
 *
 * @author 寒雨
 * @since 2023/3/9 下午10:03
 */
sealed class ArathothEvents : BukkitProxyEvent() {
    /**
     * 伤害事件
     */
    class PreDamage(
        val bukkitEvent: EntityDamageByEntityEvent,
        val attacker: LivingEntity,
        val victim: LivingEntity,
        val attackerData: ImmutableAttributeDataMap,
        val victimData: ImmutableAttributeDataMap
    ) : ArathothEvents() {

        override fun setCancelled(value: Boolean) {
            bukkitEvent.isCancelled = true
            super.setCancelled(value)
        }

        companion object {
            fun fromBukkit(bukkitEvent: EntityDamageByEntityEvent): PreDamage? {
                return bukkitEvent.attacker?.let { attacker ->
                    PreDamage(
                        bukkitEvent,
                        attacker,
                        bukkitEvent.entity as LivingEntity,
                        bukkitEvent.damager.attrDataMap,
                        bukkitEvent.entity.attrDataMap
                    )
                }
            }
        }
    }

    /**
     * 伤害事件已经处理完成后 1 tick 会唤起该事件
     *
     * 该事件中的 eventPre 已经结束，对其进行修改已无意义
     *
     * 但你可以藉此直接获取到一次伤害事件的最终伤害
     */
    class PostDamage(
        val eventPre: PreDamage
    ): ArathothEvents() {
        override val allowCancelled: Boolean = false
    }

    /**
     * 属性加载，可取消
     *
     * 为了降低对 tps 的影响，Arathoth 维护了一条单线程池进行属性的异步加载。
     *
     * 所以该事件在异步线程上唤起
     */
    class PreLoad(
        val src: Entity,
        val entity: Entity,
        val dataMap: AttributeDataMap,
        val loader: AttributeLoader
    ) : ArathothEvents()

    /**
     * 属性加载完成，不可取消
     *
     * 为了降低对 tps 的影响，Arathoth 维护了一条单线程池进行属性的异步加载。
     *
     * 所以该事件在异步线程上唤起
     */
    class PostLoad(
        val src: Entity,
        val entity: Entity,
        val dataMap: AttributeDataMap,
        val loader: AttributeLoader
    ) : ArathothEvents() {
        override val allowCancelled: Boolean = false
    }

    /**
     * 所有属性加载完成后回调该事件
     *
     * 该事件在主线程上唤起
     *
     */
    class PostLoadAll(
        val src: Entity,
        val entity: Entity,
        val dataMap: AttributeDataMap,
    ) : ArathothEvents() {
        override val allowCancelled: Boolean = false
    }

    /**
     * 唤起该事件可以触发一个实体的属性加载
     * 亦可监听该事件进行实体状态更新
     */
    class Update(val src: Entity, val entity: Entity) : ArathothEvents() {
        override val allowCancelled: Boolean = false
    }

    /**
     * 插件重载回调
     */
    class PluginLoad : ArathothEvents() {
        override val allowCancelled: Boolean = false
    }
}