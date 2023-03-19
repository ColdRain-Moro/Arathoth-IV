package team.redrock.rain.arathoth.impl.attr

import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Mob
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageEvent
import team.redrock.rain.arathoth.core.attr.Attribute
import team.redrock.rain.arathoth.core.attr.Damageable
import team.redrock.rain.arathoth.core.attr.attrDataMap
import team.redrock.rain.arathoth.core.data.AttributeFormat
import team.redrock.rain.arathoth.core.event.ArathothEvents

/**
 * Arathoth
 * team.redrock.rain.arathoth.impl.attr
 *
 * @author 寒雨
 * @since 2023/3/15 下午8:16
 */
@AutoRegister
object Damage : Attribute(), Damageable {

    override val property: Int = 10

    override fun onAttack(event: ArathothEvents.PreDamage) {
        val damageData = event.attackerData.data(Damage).cloneData()
        val armorData = event.victimData.data(Armor).cloneData()
        val armorPenData = event.attackerData.data(ArmorPenetration).cloneData()
        when (event.attacker) {
            is Player -> {
                armorData.append(event.victimData.data(ArmorPlayer))
            }
            is Mob -> {
                armorData.append(event.victimData.data(ArmorMonster))
            }
        }
        when (event.victim) {
            is Player -> {
                damageData.append(event.attackerData.data(DamagePlayer))
                armorPenData.append(event.attackerData.data(ArmorPenetrationPlayer))
            }
            is Mob -> {
                damageData.append(event.attackerData.data(DamageMonster))
                armorPenData.append(event.attackerData.data(ArmorPenetrationMonster))
            }
        }
        event.bukkitEvent.damage += damageData.calculate()
            - (armorData.calculate() - armorPenData.random(AttributeFormat.NUMBER)) * (1 - armorPenData.random(AttributeFormat.SCALAR) / 100)
    }
}

@AutoRegister
object DamagePlayer : Attribute()

@AutoRegister
object DamageMonster : Attribute()

@AutoRegister
object DamageAll : Attribute(), Damageable {

    override val property: Int = 0

    override fun onAttack(event: ArathothEvents.PreDamage) {
        event.bukkitEvent.damage += event.attackerData.value(DamageAll, AttributeFormat.NUMBER)
        event.bukkitEvent.damage *= (1 + event.attackerData.value(DamageAll, AttributeFormat.SCALAR) / 100)
        event.bukkitEvent.damage -= event.attackerData.value(ArmorAll, AttributeFormat.NUMBER)
        event.bukkitEvent.damage *= (1 - event.attackerData.value(ArmorAll, AttributeFormat.SCALAR) / 100)
    }
}

@AutoRegister
object DamageTrue : Attribute() {
    @EventHandler
    fun e(e: ArathothEvents.PostDamage) {
        val value = e.eventPre.attackerData.data(DamageTrue).calculate()
        if (value > 0) {
            e.eventPre.victim.health -= value
        }
    }
}

@AutoRegister
object ArmorPenetration : Attribute()

@AutoRegister
object ArmorPenetrationPlayer : Attribute()

@AutoRegister
object ArmorPenetrationMonster : Attribute()

@AutoRegister
object CriticalDamage : Attribute(), Damageable {
    override fun onAttack(event: ArathothEvents.PreDamage) {

    }
}

@AutoRegister
object CriticalChance : Attribute()

@AutoRegister
object CriticalDefense : Attribute()

@AutoRegister
object LifeSteal : Attribute(), Damageable {

    override val property: Int = 20

    override fun onAttack(event: ArathothEvents.PreDamage) {
        val value = event.attackerData.data(LifeSteal).calculate()
        val defenseNumber = event.victimData.value(LifeStealDefense, AttributeFormat.NUMBER)
        val defenseScalar = event.victimData.value(LifeStealDefense, AttributeFormat.SCALAR)
        val resultValue = (value - defenseNumber) * (1 - defenseScalar / 100)
        if (resultValue > 0) {
            event.bukkitEvent.damage += resultValue
            event.attacker.health = (event.attacker.health + resultValue).coerceAtMost(event.attacker.maxHealth)
        }
    }
}

@AutoRegister
object LifeStealDefense : Attribute()

@AutoRegister
object Armor : Attribute()

@AutoRegister
object ArmorMonster : Attribute()

@AutoRegister
object ArmorPlayer : Attribute()

@AutoRegister
object ArmorAll : Attribute()

@AutoRegister
object ArmorMagic : Attribute() {
    @EventHandler
    fun e(e: EntityDamageEvent) {
        if (e.cause == EntityDamageEvent.DamageCause.MAGIC) {
            val value = (e.entity as LivingEntity).attrDataMap.data(ArmorMagic).calculate()
            e.damage -= value
        }
    }
}

@AutoRegister
object Health : Attribute() {
    @EventHandler
    fun e(e: ArathothEvents.Update) {
        val entity = e.entity
        if (entity is LivingEntity) {
            val value = entity.attrDataMap.data(Health).calculate()
            entity.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH)
                ?.baseValue = (value + 20).coerceAtLeast(1.0)
        }
    }
}
