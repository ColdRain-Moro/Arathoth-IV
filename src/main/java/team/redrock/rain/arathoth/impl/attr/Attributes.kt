package team.redrock.rain.arathoth.impl.attr

import org.bukkit.attribute.AttributeModifier
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Mob
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.attribute.Attribute as BukkitAttribute
import taboolib.common.platform.function.submit
import team.redrock.rain.arathoth.Arathoth
import team.redrock.rain.arathoth.core.attr.Attribute
import team.redrock.rain.arathoth.core.attr.Damageable
import team.redrock.rain.arathoth.core.attr.attrDataMap
import team.redrock.rain.arathoth.core.data.AttributeFormat
import team.redrock.rain.arathoth.core.event.ArathothEvents
import team.redrock.rain.arathoth.utils.heal
import team.redrock.rain.arathoth.utils.modifyAttribute
import team.redrock.rain.arathoth.utils.positive
import team.redrock.rain.arathoth.utils.pseudoRandom

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
object DamageFinal : Attribute(), Damageable {

    override val property: Int = 0

    override fun onAttack(event: ArathothEvents.PreDamage) {
        event.bukkitEvent.damage += event.attackerData.value(DamageFinal, AttributeFormat.NUMBER)
        event.bukkitEvent.damage *= (1 + event.attackerData.value(DamageFinal, AttributeFormat.SCALAR) / 100)
        event.bukkitEvent.damage -= event.victimData.value(ArmorFinal, AttributeFormat.NUMBER)
        event.bukkitEvent.damage *= (1 - event.victimData.value(ArmorFinal, AttributeFormat.SCALAR) / 100)
    }
}

@AutoRegister
object DamagePercentAll : Attribute(), Damageable {

    override val property: Int = 40

    override fun onAttack(event: ArathothEvents.PreDamage) {
        val rate = event.attackerData.value(DamagePercentAll, AttributeFormat.SCALAR)
        event.bukkitEvent.damage += (rate / 100) * event.victim.maxHealth
    }
}

@AutoRegister
object DamagePercentCurrent : Attribute(), Damageable {

    override val property: Int = 40

    override fun onAttack(event: ArathothEvents.PreDamage) {
        val rate = event.attackerData.value(DamagePercentCurrent, AttributeFormat.SCALAR)
        event.bukkitEvent.damage += (rate / 100) * event.victim.health
    }
}

@AutoRegister
object AttackRange : Attribute() {
    @EventHandler
    fun e(e: ArathothEvents.PostDamage) {
        if (e.eventPre.attacker.isBypass()) {
            return
        }
        e.eventPre.attacker.setBypass(true)
        val value = e.eventPre.attackerData.value(AttackRange, AttributeFormat.NUMBER)
        e.eventPre.attacker.getNearbyEntities(value, value, value)
            .filterNot { it.uniqueId == e.eventPre.attacker.uniqueId }
            .filterNot { it.uniqueId == e.eventPre.victim.uniqueId }
            .filterIsInstance<LivingEntity>()
            .onEach {
                it.damage(0.0, e.eventPre.attacker)
            }
        submit { e.eventPre.attacker.setBypass(false) }
    }

    private fun LivingEntity.setBypass(bool: Boolean) {
        if (bool) {
            setMetadata("Arathoth|AttackRange", FixedMetadataValue(Arathoth.pluginInst, true))
        } else {
            removeMetadata("Arathoth|AttackRange", Arathoth.pluginInst)
        }
    }

    private fun LivingEntity.isBypass(): Boolean {
        return hasMetadata("Arathoth|AttackRange")
    }
}

@AutoRegister
object DamageTrue : Attribute() {
    @EventHandler
    fun e(e: ArathothEvents.PostDamage) {
        val value = e.eventPre.attackerData.data(DamageTrue).calculate()
        if (value > 0) {
            e.eventPre.victim.heal(-value)
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
object CriticalDamage : Attribute(), Damageable, ProbabilityTriggered {

    override val property: Int = 30
    
    override fun FileConfiguration.initConfig() {
        set("script.player.victim", "subtitle color \"&7遭暴击\" by 10 20 10")
        set("script.player.attacker", "subtitle color \"&e暴击\" by 10 20 10")
        set("script.victim", "")
        set("script.attacker", "")
    }

    override fun onAttack(event: ArathothEvents.PreDamage) {
        val chance = event.attackerData.value(CriticalChance, AttributeFormat.SCALAR) -
                event.victimData.value(CriticalDefense, AttributeFormat.SCALAR)
        if (pseudoRandom(chance / 100)) {
            var value = event.attackerData.value(CriticalDamage) -
                    event.victimData.value(CriticalDefense, AttributeFormat.NUMBER)
            value *= 1 - event.attackerData.value(CriticalDefense, AttributeFormat.SCALAR) / 100
            event.bukkitEvent.damage += value.positive()
            event.runActions()
        }
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
            event.attacker.heal(resultValue)
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
object ArmorFinal : Attribute()

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
object Dodge : Attribute(), Damageable, ProbabilityTriggered {
    override val property: Int = 100

    override fun FileConfiguration.initConfig() {
        set("script.player.victim", "subtitle color \"&b闪避\" by 10 20 10")
        set("script.player.attacker", "subtitle color \"&7遭闪避\" by 10 20 10")
        set("script.victim", "")
        set("script.attacker", "")
    }

    override fun onAttack(event: ArathothEvents.PreDamage) {
        val rate = event.victimData.value(Dodge, AttributeFormat.SCALAR) - event.victimData.value(Accuracy, AttributeFormat.SCALAR)
        if (pseudoRandom(rate / 100)) {
            event.isCancelled = true
            event.runActions()
        }
    }
}

@AutoRegister
object Accuracy : Attribute()

@AutoRegister
object Health : Attribute() {

    @EventHandler
    fun e(e: ArathothEvents.PostLoadAll) {
        val entity = e.entity
        if (entity is LivingEntity) {
            val number = entity.attrDataMap.value(Health, AttributeFormat.NUMBER)
            val scalar = entity.attrDataMap.value(Health, AttributeFormat.SCALAR)
            entity.modifyAttribute(BukkitAttribute.GENERIC_MAX_HEALTH, number, AttributeModifier.Operation.ADD_NUMBER)
            entity.modifyAttribute(BukkitAttribute.GENERIC_MAX_HEALTH, scalar, AttributeModifier.Operation.ADD_SCALAR)
        }
    }
}
