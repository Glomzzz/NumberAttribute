package com.skillw.numberatt.api.read

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.attribute.Attribute
import com.skillw.attsystem.api.attribute.status.Status
import com.skillw.attsystem.api.read.ReadPattern
import com.skillw.attsystem.taboolib.module.chat.TellrawJson
import com.skillw.attsystem.util.Format.real
import com.skillw.numberatt.NumberAttribute
import com.skillw.numberatt.api.operation.Operation
import com.skillw.numberatt.api.operation.Plus
import com.skillw.numberatt.api.status.NumberStatus
import com.skillw.numberatt.internal.manager.NAConfig
import com.skillw.numberatt.internal.manager.NAConfig.statPlaceholderValue
import com.skillw.pouvoir.api.map.LowerMap
import com.skillw.pouvoir.util.CalculationUtils.resultDouble
import com.skillw.pouvoir.util.MessageUtils.wrong
import com.skillw.pouvoir.util.StringUtils.toStringWithNext
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.entity.LivingEntity
import taboolib.common5.Coerce
import taboolib.module.chat.colored
import taboolib.module.chat.uncolored
import java.util.regex.Pattern

private val keyPattern = Pattern.compile("<(?<key>.*?)>")

class ReadGroup(
    key: String,
    private val totalFormula: String,
    private val patternStrings: MutableList<String>,
    private val placeholders: LowerMap<String>
) : ReadPattern(key), ConfigurationSerializable {
    val groupKeys = HashSet<String>()
    val operations = LowerMap<Operation>()
    private val patterns = LinkedHashMap<Pattern, HashSet<String>>()

    init {
        val numberPattern = NAConfig.numberPattern
        for (patternStr in patternStrings) {
            val matcher = keyPattern.matcher(patternStr)
            val stringBuffer = StringBuffer()
            val groupKeys = HashSet<String>()
            while (matcher.find()) {
                var groupKey = matcher.group("key") ?: continue
                if (groupKey == "name") {
                    matcher.appendReplacement(stringBuffer, "(?<name>.*)")
                    continue
                }
                var operation: Operation? = null
                if (groupKey.contains(":")) {
                    val array = groupKey.split(":")
                    operation = NumberAttribute.operationManager[array[0]]
                    if (operation != null) {
                        groupKey = array[1]
                    }
                }
                if (operation == null) operation = Plus
                operations.put(groupKey, operation)
                groupKeys.add(groupKey)
                matcher.appendReplacement(stringBuffer, numberPattern.replace("value", groupKey))
            }
            val pattern = Pattern.compile(matcher.appendTail(stringBuffer).toString())
            patterns[pattern] = groupKeys
            this.groupKeys.addAll(groupKeys)
        }
    }

    companion object {
        @JvmStatic
        fun deserialize(section: ConfigurationSection): ReadGroup? {
            return try {
                val placeholders = LowerMap<String>()
                val placeholderSection = section.getConfigurationSection("placeholder")
                placeholderSection?.getConfigurationSection("placeholder")?.getKeys(false)?.forEach {
                    placeholders[it] = placeholderSection[it].toString()
                }
                val readGroup = ReadGroup(
                    section.name,
                    section.getString("total").toString(),
                    section.getStringList("patterns"),
                    placeholders
                )
                readGroup.release = true
                return readGroup
            } catch (e: Exception) {
                wrong("An error occurred while loading ReadGroup ${section.name} ！")
                wrong("Cause: ${e.cause.toString()}")
                null
            }
        }
    }

    override fun register() {
        AttributeSystem.readPatternManager.register(this)
    }

    override fun read(
        string: String,
        attribute: Attribute,
        livingEntity: LivingEntity?,
        slot: String
    ): NumberStatus? {
        if ((attribute.names.none { string.contains(it) })) return null
        val attributeStatus = NumberStatus(this)
        val temp = string.uncolored().replace(Regex("§#.{6}"), "")
        val keyMap = LowerMap<String>()
        patternList@ for ((pattern, groupKeys) in patterns) {
            val matcher = pattern.matcher(temp)
            if (!matcher.find()) continue
            groupKeys.forEach {
                keyMap[it] = matcher.group(it)
            }
            break@patternList
        }
        if (keyMap.isEmpty()) return null
        try {
            for ((key, valueStr) in keyMap) {
                val value = Coerce.asDouble(valueStr)
                if (!value.isPresent) {
                    wrong("The value &d$valueStr &ein &6$temp &emust be a Double!")
                    continue
                }
                attributeStatus.operation(key, value.get(), operations[key] ?: Plus)
            }
        } catch (e: Exception) {
            wrong("Can't read the attribute &d${this.key} &ein &6$temp &e!(Wrong format / Wrong read group)")
            return attributeStatus
        }
        return attributeStatus
    }

    private fun calculate(
        formula: String,
        status: NumberStatus,
        livingEntity: LivingEntity?
    ): Double {
        var formulaReplaced = formula.replace("<total>", totalFormula)
        val matcher = keyPattern.matcher(formulaReplaced)
        val stringBuffer = StringBuffer()
        while (matcher.find()) {
            val key = matcher.group("key") ?: continue
            matcher.appendReplacement(stringBuffer, status[key].real())
        }
        formulaReplaced = matcher.appendTail(stringBuffer).toString()
        return formulaReplaced.resultDouble(livingEntity)
    }


    override fun placeholder(
        key: String,
        attribute: Attribute,
        status: Status,
        livingEntity: LivingEntity?
    ): Any {
        if (status !is NumberStatus) return 0.0
        if (key == "total") {
            return calculate(totalFormula, status, livingEntity).real()
        }
        val formula = placeholders[key] ?: return 0.0
        return calculate(formula, status, livingEntity).real()
    }


    override fun serialize(): MutableMap<String, Any> {
        val map = LinkedHashMap<String, Any>()
        map["total"] = totalFormula
        map["patterns"] = patternStrings
        return map
    }

    override fun stat(attribute: Attribute, status: Status, livingEntity: LivingEntity?): TellrawJson {
        val json = TellrawJson()
        if (status !is NumberStatus) return json
        val readGroup = attribute.readPattern as? ReadGroup ?: return json
        json.append(
            NAConfig.attributeFormat.replace("{name}", attribute.names[0])
                .replace("{value}", readGroup.placeholder("total", attribute, status, livingEntity).toString())
                .colored()
        ).hoverText(
            ("${NAConfig.statStatus} \n" +
                    status.map {
                        NAConfig.statStatusValue.replace("{key}", it.key).replace("{value}", it.value.real())
                    }.run { this.ifEmpty { listOf(NAConfig.statNone) } }.toStringWithNext()
                    + "\n \n"
                    + "${NAConfig.statPlaceholder} \n"
                    + readGroup.placeholders.keys.map {
                statPlaceholderValue.replace("{key}", it)
                    .replace("{value}", readGroup.placeholder(it, attribute, status, livingEntity).toString())
            }.run { this.ifEmpty { listOf(NAConfig.statNone) } }.toStringWithNext()).colored()
        )
        return json
    }

}

