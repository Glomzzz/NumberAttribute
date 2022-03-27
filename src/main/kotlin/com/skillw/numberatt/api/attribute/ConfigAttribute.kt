package com.skillw.numberatt.api.attribute

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.attribute.Attribute
import com.skillw.numberatt.api.read.ReadGroup
import com.skillw.pouvoir.api.`object`.BaseObject
import com.skillw.pouvoir.util.MessageUtils.wrong
import org.bukkit.Bukkit
import org.bukkit.configuration.ConfigurationSection
import taboolib.platform.util.sendLang

class ConfigAttribute(
    override val key: String,
    override val priority: Int,
    val names: List<String>,
    val readGroup: ReadGroup,
    val isEntity: Boolean
) : BaseObject {
    override fun register() {
        AttributeSystem.attributeManager.register(
            Attribute.createAttribute(key, readGroup) {
                priority = this@ConfigAttribute.priority
                release = true
                entity = this@ConfigAttribute.isEntity
                names.addAll(this@ConfigAttribute.names)
            }
        )
    }

    companion object {
        @JvmStatic
        fun deserialize(section: ConfigurationSection): ConfigAttribute? {
            try {
                val key = section.name
                val priority = section["priority"].toString().toInt()
                val names = section.getStringList("names")
                val isEntity = (section["oriented"]?.toString()?.lowercase() ?: "entity") == "entity"
                val readGroup = AttributeSystem.readPatternManager[section.getString("read-group") ?: "Default"]
                if (readGroup == null || readGroup !is ReadGroup) {
                    wrong("The ReadPattern of &6$key &eis not a ReadGroup!")
                    return null
                }
                return ConfigAttribute(key, priority, names, readGroup, isEntity)
            } catch (e: Exception) {
                Bukkit.getConsoleSender().sendLang("error.attribute-load", section["key"].toString())
                e.printStackTrace()
            }
            return null
        }
    }

    override fun serialize(): MutableMap<String, Any> {
        return linkedMapOf(
            "priority" to priority,
            "names" to names,
            "read-group" to readGroup
        )
    }
}