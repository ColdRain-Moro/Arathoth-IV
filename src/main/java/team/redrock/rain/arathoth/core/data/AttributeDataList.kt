package team.redrock.rain.arathoth.core.data

/**
 * Arathoth
 * team.redrock.rain.team.redrock.rain.arathoth.core.data
 *
 * @author 寒雨
 * @since 2023/3/10 下午2:21
 */
class AttributeDataList(list: MutableList<AttributeData>): MutableList<AttributeData> by list, AttributeData() {
    internal val immutable = ImmutableAttributeDataList(this)
}

class ImmutableAttributeDataList(mutable: AttributeDataList): List<AttributeData> by mutable, AttributeData()