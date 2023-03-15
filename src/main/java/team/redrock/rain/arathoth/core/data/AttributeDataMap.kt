package team.redrock.rain.arathoth.core.data

import team.redrock.rain.arathoth.core.attr.Attribute

/**
 * Arathoth
 * team.redrock.rain.team.redrock.rain.arathoth.core.data
 *
 * @author 寒雨
 * @since 2023/3/9 下午4:05
 */
class AttributeDataMap(map: MutableMap<String, AttributeData> = mutableMapOf())
    : MutableMap<String, AttributeData> by map, AttributeData(), MapTool {
    internal val immutable = ImmutableAttributeDataMap(this)

    override fun data(attr: Attribute): AttributeData {
        return computeIfAbsent(attr.name) { AttributeData() }
    }

    override fun append(data: AttributeData): AttributeData {
        if (data is AttributeDataMap) {
            data.forEach { (k, v) -> merge(k, v) { old, new -> old.append(new) } }
        }
        return this
    }
}

class ImmutableAttributeDataMap(private val map: AttributeDataMap)
    : Map<String, AttributeData> by map, AttributeData(), MapTool by map

interface MapTool {
    fun data(attr: Attribute): AttributeData

    fun value(attr: Attribute, format: AttributeFormat): Double {
        return data(attr).random(format)
    }

    fun gamble(attr: Attribute): Boolean {
        return data(attr).executeChance()
    }
}